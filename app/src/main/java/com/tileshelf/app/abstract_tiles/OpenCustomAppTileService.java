package com.tileshelf.app.abstract_tiles;

import android.content.Intent;
import android.net.Uri;
import android.service.quicksettings.Tile;

import com.tileshelf.app.R;
import com.tileshelf.app.utils.SharedPreferencesUtil;

public abstract class OpenCustomAppTileService extends IntentTileService {

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        updateState();
    }

    @Override
    public void onStartListening() {
        updateState();
    }

    @Override
    public Intent createIntent() {
        String link = SharedPreferencesUtil.getCustomPackage(this, getPreferencesKey() + "_link");
        if (link != null && !link.trim().isEmpty()) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse(link.trim()));
        }
        String packageName = SharedPreferencesUtil.getCustomPackage(this, getPreferencesKey());
        if (packageName == null || packageName.trim().isEmpty()) {
            return null;
        }
        return getPackageManager().getLaunchIntentForPackage(packageName);
    }

    private void updateState() {
        Tile tile = getQsTile();
        String packageName = SharedPreferencesUtil.getCustomPackage(this, getPreferencesKey());
        String link = SharedPreferencesUtil.getCustomPackage(this, getPreferencesKey() + "_link");
        if (packageName != null || link != null) {
            tile.setLabel(SharedPreferencesUtil.getCustomPackage(this, getLabelPreferenceKey()));
        } else {
            tile.setLabel(getString(R.string.custom_app));
        }
        tile.updateTile();
    }

    public abstract String getPreferencesKey();

    public abstract String getLabelPreferenceKey();
}
