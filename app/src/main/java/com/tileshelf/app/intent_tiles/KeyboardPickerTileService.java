package com.tileshelf.app.intent_tiles;

import android.content.Intent;

import com.tileshelf.app.KeyboardPickerActivity;
import com.tileshelf.app.abstract_tiles.IntentTileService;

public class KeyboardPickerTileService extends IntentTileService {

    @Override
    public Intent createIntent() {
        return new Intent(this, KeyboardPickerActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    }
}
