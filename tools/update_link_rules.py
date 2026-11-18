#!/usr/bin/env python3
"""Refresh the bundled link cleaning rules from their upstream projects.

Writes app/src/main/assets/link-rules/. Running it again without upstream
changes leaves every file byte-identical, so a diff means upstream changed.
Set GITHUB_TOKEN to raise the GitHub API rate limit.
"""

import json
import os
import pathlib
import urllib.request

ROOT = pathlib.Path(__file__).resolve().parent.parent
OUT = ROOT / "app" / "src" / "main" / "assets" / "link-rules"

FIREFOX = "mozilla-firefox/firefox"
FIREFOX_DIR = "toolkit/components/antitracking/StripOnShareLists"
CLEARURLS = "ClearURLs/Rules"
MPL_TEXT = "https://www.mozilla.org/media/MPL/2.0/index.txt"


def fetch(url, api=False):
    request = urllib.request.Request(url, headers={"User-Agent": "tile-shelf-rules-updater"})
    token = os.environ.get("GITHUB_TOKEN")
    if api and token:
        request.add_header("Authorization", f"Bearer {token}")
    with urllib.request.urlopen(request, timeout=60) as response:
        return response.read()


def last_commit(repo, path):
    url = f"https://api.github.com/repos/{repo}/commits?path={path}&per_page=1"
    commits = json.loads(fetch(url, api=True))
    if not commits:
        raise SystemExit(f"no commit found for {repo}/{path}")
    return commits[0]["sha"]


def raw(repo, sha, path):
    return fetch(f"https://raw.githubusercontent.com/{repo}/{sha}/{path}")


def write(name, data):
    target = OUT / name
    if target.exists() and target.read_bytes() == data:
        return False
    target.write_bytes(data)
    return True


def clearurls_redirects(data):
    providers = json.loads(data)["providers"]
    redirects = []
    for name in sorted(providers):
        provider = providers[name]
        if name.lower().startswith("clearurlstest") or not provider.get("redirections"):
            continue
        redirects.append({
            "name": name,
            "urlPattern": provider["urlPattern"],
            "exceptions": provider.get("exceptions", []),
            "redirections": provider["redirections"],
        })
    return redirects


def main():
    OUT.mkdir(parents=True, exist_ok=True)
    mpl_path = f"{FIREFOX_DIR}/MPL2/StripOnShare.json"
    lgpl_path = f"{FIREFOX_DIR}/LGPL/StripOnShareLGPL.json"
    mpl_sha = last_commit(FIREFOX, mpl_path)
    lgpl_sha = last_commit(FIREFOX, lgpl_path)
    clear_sha = last_commit(CLEARURLS, "data.min.json")

    redirects = clearurls_redirects(raw(CLEARURLS, clear_sha, "data.min.json"))
    files = {
        "firefox-strip-on-share.json": raw(FIREFOX, mpl_sha, mpl_path),
        "firefox-strip-on-share-lgpl.json": raw(FIREFOX, lgpl_sha, lgpl_path),
        "clearurls-redirects.json": (json.dumps(redirects, indent=1, ensure_ascii=False) + "\n").encode(),
        "LICENSE-MPL-2.0.txt": fetch(MPL_TEXT),
        "LICENSE-LGPL-3.0.txt": raw(FIREFOX, lgpl_sha, f"{FIREFOX_DIR}/LGPL/LICENSE"),
    }
    sources = [
        {"file": "firefox-strip-on-share.json", "license": "MPL-2.0",
         "source": f"https://github.com/{FIREFOX}/blob/{mpl_sha}/{mpl_path}"},
        {"file": "firefox-strip-on-share-lgpl.json", "license": "LGPL-3.0",
         "source": f"https://github.com/{FIREFOX}/blob/{lgpl_sha}/{lgpl_path}"},
        {"file": "clearurls-redirects.json", "license": "LGPL-3.0",
         "source": f"https://github.com/{CLEARURLS}/blob/{clear_sha}/data.min.json",
         "note": "The redirections of each ClearURLs provider, with its urlPattern and exceptions."},
    ]
    files["sources.json"] = (json.dumps(sources, indent=1) + "\n").encode()

    changed = [name for name, data in files.items() if write(name, data)]
    print("changed: " + ", ".join(changed) if changed else "no changes")


if __name__ == "__main__":
    main()
