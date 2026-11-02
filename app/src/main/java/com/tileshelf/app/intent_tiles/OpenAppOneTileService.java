package com.tileshelf.app.intent_tiles;

import com.tileshelf.app.abstract_tiles.OpenCustomAppTileService;
import com.tileshelf.app.utils.SharedPreferencesUtil;

public class OpenAppOneTileService extends OpenCustomAppTileService {
    @Override
    public String getPreferencesKey() {
        return SharedPreferencesUtil.CUSTOM_PACKAGE_ONE;
    }

    @Override
    public String getLabelPreferenceKey() {
        return SharedPreferencesUtil.CUSTOM_PACKAGE_ONE_LABEL;
    }
}
