package com.tileshelf.app.intent_tiles;

import android.content.ComponentName;
import android.content.Intent;
import android.provider.Settings;

import com.tileshelf.app.abstract_tiles.IntentTileService;

public class OpenDataUsageTileService extends IntentTileService {

    @Override
    public Intent createIntent() {
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            intent = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
            Intent settingsApp = new Intent(intent).setPackage("com.android.settings");
            if (settingsApp.resolveActivity(getPackageManager()) != null) {
                intent = settingsApp;
            }
        } else {
            intent = new Intent();
            intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
        }
        return intent;
    }
}
