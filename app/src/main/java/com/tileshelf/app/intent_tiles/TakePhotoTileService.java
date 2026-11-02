package com.tileshelf.app.intent_tiles;

import android.content.Intent;
import android.provider.MediaStore;

import com.tileshelf.app.abstract_tiles.IntentTileService;

public class TakePhotoTileService extends IntentTileService {

    @Override
    public Intent createIntent() {
        return new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
    }
}
