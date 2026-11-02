package com.tileshelf.app.intent_tiles;

import android.content.Intent;

import com.tileshelf.app.abstract_tiles.IntentTileService;

public class OpenVpnTileService extends IntentTileService {

    @Override
    public Intent createIntent() {
        return new Intent("android.net.vpn.SETTINGS");
    }
}
