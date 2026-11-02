package com.tileshelf.app.intent_tiles;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.tileshelf.app.abstract_tiles.IntentTileService;

import java.util.Locale;

public class OpenCalculatorTileService extends IntentTileService {

    @Override
    public Intent createIntent() {
        PackageManager packageManager = getPackageManager();
        Intent selector = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_CALCULATOR);
        if (selector.resolveActivity(packageManager) != null) {
            return selector;
        }
        Intent launcher = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        for (ResolveInfo info : packageManager.queryIntentActivities(launcher, 0)) {
            String packageName = info.activityInfo.packageName;
            if (packageName.toLowerCase(Locale.ROOT).contains("calculator")) {
                return packageManager.getLaunchIntentForPackage(packageName);
            }
        }
        return selector;
    }
}
