package com.tileshelf.app.intent_tiles;

import com.tileshelf.app.abstract_tiles.OpenCustomAppTileService;
import com.tileshelf.app.utils.SharedPreferencesUtil;

public class OpenAppThreeTileService extends OpenCustomAppTileService {
    @Override
    public String getPreferencesKey() {
        return SharedPreferencesUtil.CUSTOM_PACKAGE_THREE;
    }

    @Override
    public String getLabelPreferenceKey() {
        return SharedPreferencesUtil.CUSTOM_PACKAGE_THREE_LABEL;
    }
}
