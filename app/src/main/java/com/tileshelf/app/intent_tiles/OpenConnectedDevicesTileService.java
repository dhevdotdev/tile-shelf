package com.tileshelf.app.intent_tiles;

import android.content.ComponentName;
import android.content.Intent;

import com.tileshelf.app.abstract_tiles.IntentTileService;

public class OpenConnectedDevicesTileService extends IntentTileService {

    @Override
    public Intent createIntent() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$ConnectedDeviceDashboardActivity"));
        return intent;
    }
}
