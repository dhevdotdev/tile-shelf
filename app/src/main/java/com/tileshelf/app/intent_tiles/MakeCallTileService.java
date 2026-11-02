package com.tileshelf.app.intent_tiles;

import android.content.Intent;

import com.tileshelf.app.abstract_tiles.IntentTileService;

public class MakeCallTileService extends IntentTileService {

    @Override
    public Intent createIntent() {
        return new Intent(Intent.ACTION_DIAL);
    }
}
