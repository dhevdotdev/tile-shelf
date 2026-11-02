package com.tileshelf.app.intent_tiles;

import android.content.Intent;
import android.os.BatteryManager;
import android.service.quicksettings.Tile;

import com.tileshelf.app.abstract_tiles.IntentTileService;

public class BatteryTileService extends IntentTileService {

    @Override
    public Intent createIntent() {
        return new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
    }

    @Override
    public void onStartListening() {
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        Tile tile = getQsTile();
        tile.setLabel(batLevel + " %");
        tile.updateTile();
    }
}
