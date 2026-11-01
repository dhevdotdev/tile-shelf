package com.flxholle.quicktiles.abstract_tiles;

import android.content.Intent;
import android.widget.Toast;

import com.flxholle.quicktiles.R;
import com.flxholle.quicktiles.catalog.TileIntents;

public abstract class IntentTileService extends BaseTileService {

    @Override
    public final void onClick() {
        Intent intent = createIntent();
        if (intent == null) {
            Toast.makeText(this, R.string.no_app_found, Toast.LENGTH_LONG).show();
            return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TileIntents.collapse(this, intent);
    }

    @Override
    public final void reset() {
    }

    public abstract Intent createIntent();
}
