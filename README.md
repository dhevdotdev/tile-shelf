# Tile Shelf

Tile Shelf is a fork of [QuickTiles](https://gitlab.com/flxholle/quick-tiles) by flxholle, based on version 1.8 (versionCode 18, `com.asdoi.quicktiles`). Most of the tile code is theirs and the full upstream history is kept in this repository. This fork adds quick settings tiles on Android and installs beside the original app. The launcher name is Tile Shelf. The application id is `com.tileshelf.app`. There are no ads, no analytics, and no advertising id.

This is not the official QuickTiles app and is not affiliated with its author. This program is free software under GPL-3.0-or-later. See `LICENSE`.

The screens use Material 3 Expressive (`androidx.compose.material3:material3:1.5.0-beta02`). The new tile in this version is Clear clipboard. A caffeine tile, a sync tile, and home-screen widgets are not in this version.

## Build

You need JDK 21 and Android SDK platform 37.

`compileSdk` is 37 because `androidx.compose.material3:material3:1.5.0-beta02` and the Compose 1.12 libraries it pulls require compileSdk 37. `checkDebugAarMetadata` fails on compileSdk 36. `targetSdk` stays 36. `minSdk` stays 24, so the debug APK still installs on Android 15, including a OnePlus 13R.

```sh
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
./gradlew :app:recordRoborazziDebug
```

The debug APK is `app/build/outputs/apk/debug/app-debug.apk`. Roborazzi writes `app/screenshots/matrix/`.

Turning a tile on in the app publishes it to the quick settings editor. You still add it to the shade yourself. The app does not place tiles in the active row.

## Tiles

Tiles with no extra grant need nothing beyond the app. Clear clipboard is one of those. It calls `ClipboardManager.clearPrimaryClip()` from the tile service on API 28 and above. On older APIs, and if that call throws, it sets an empty clip. The tile shows "Clipboard cleared" on its subtitle, or on its label below API 29. Keyboard clipboard histories, including Gboard and the OnePlus and Samsung keyboards, keep their own copies. An app cannot clear those.

| Tile | Grant |
| --- | --- |
| Play/Pause, Next, Previous, Media volume, Volume panel | None |
| New alarm, New timer, New event, Make call, Take photo, Record video, Calculator, File manager | None |
| Custom app, five slots | None. Pick the app in Tile Shelf before the tile can open it. |
| Search settings, Languages, Personal dictionary, Privacy, Display, Home app, Notification history, VPN, Data usage, Screen cast, Connected devices, About phone, Developer options, Battery | None |
| Clear clipboard | None |
| Counter | None. Reset the count from the tile's detail screen. |
| Screen timeout, Adjust brightness, Adaptive brightness, Vibrate on calls | One-time Modify system settings |
| Keep screen on | Modify system settings and an adb grant of `WRITE_SECURE_SETTINGS`. The detail screen chooses AC, USB, wireless, or any power source. |
| Switch volume modes, Switch between loud and silence | One-time Do Not Disturb access |
| USB debugging, Heads-up, Force rotation, Fix rotation, Animator duration, Disable all animations, Always finish activities | adb grant of `android.permission.WRITE_SECURE_SETTINGS` |
| Demo mode | adb grants of `WRITE_SECURE_SETTINGS` and `DUMP` |
| Screenshot, Lock screen, Split screen, Power menu | Enable the Tile Shelf accessibility service. Screenshot and lock screen appear from API 28. |
| Grayscale | adb `WRITE_SECURE_SETTINGS`. The row is hidden from API 30 up unless it was already on, in which case you can only turn it off. |
| Show taps | Not offered. A stored on value can only be turned off. Writing `show_touches` throws on this targetSdk. |

The adb form is:

```sh
adb shell pm grant com.tileshelf.app android.permission.WRITE_SECURE_SETTINGS
adb shell pm grant com.tileshelf.app android.permission.DUMP
```

The upstream wireless USB debugging tile needs root. This fork does not include it and does not request root.

## Limits

Publishing a tile does not add it to the shade. After you turn it on, add it from the quick settings editor.

Clear clipboard does not clear keyboard clipboard history.

Turning a tile off does not undo the system setting that tile last wrote.

A failed grant leaves the switch off. Grant the permission, then turn the tile on again.

## Next step

Add a caffeine keep-screen-awake tile with a foreground service, and note how OnePlus battery optimisation treats that service. That tile is deferred from this version.

## Credits

Icons. The app icon is original to this fork. The split-screen icon is by Smashicons on Flaticon. The upstream README also credits a later icon to DALL-E 3.

Code used by the original project:

- Dev-Tiles by Mustafa Ali, Apache-2.0
- Dev-QuickSettings by Adrián García, Apache-2.0
- Adaptive Brightness Tile by rascarlo, GPL-3.0
- PowerButtonTile by rascarlo, GPL-3.0
- Greyscale by fei-ke, Apache-2.0
