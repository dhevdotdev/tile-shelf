package com.tileshelf.app.intent_tiles;

import android.content.Context;
import android.media.AudioManager;

import com.tileshelf.app.abstract_tiles.BaseTileService;

public class OpenVolumePanelTileService extends BaseTileService {

    @Override
    public void onClick() {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
    }

    @Override
    public void reset() {
    }
}
