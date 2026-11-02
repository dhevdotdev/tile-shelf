package com.tileshelf.app.intent_tiles;

import android.content.Intent;
import android.provider.Settings;

import com.tileshelf.app.abstract_tiles.IntentTileService;

public class OpenCastTileService extends IntentTileService {

    @Override
    public Intent createIntent() {
        return new Intent(Settings.ACTION_CAST_SETTINGS);
    }
}
