package com.tileshelf.app.intent_tiles;

import android.content.Context;
import android.media.AudioManager;
import android.view.KeyEvent;

import com.tileshelf.app.abstract_tiles.BaseTileService;

public class PreviousSongTileService extends BaseTileService {

    @Override
    public void onClick() {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        am.dispatchMediaKeyEvent(downEvent);

        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        am.dispatchMediaKeyEvent(upEvent);
    }

    @Override
    public void reset() {
    }
}
