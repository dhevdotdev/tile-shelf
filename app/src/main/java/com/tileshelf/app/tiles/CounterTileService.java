package com.tileshelf.app.tiles;

import android.service.quicksettings.Tile;

import com.tileshelf.app.abstract_tiles.BaseTileService;
import com.tileshelf.app.utils.SharedPreferencesUtil;

public class CounterTileService extends BaseTileService {

    @Override
    public void onStartListening() {
        updateTileResources();
    }

    @Override
    public void onClick() {
        SharedPreferencesUtil.triggerCounter(this);
        updateTileResources();
    }

    private void updateTileResources() {
        Tile tile = getQsTile();
        tile.setLabel("" + SharedPreferencesUtil.getCounter(this));
        tile.updateTile();
    }

    @Override
    public void reset() {
        SharedPreferencesUtil.resetCounter(this);
    }
}
