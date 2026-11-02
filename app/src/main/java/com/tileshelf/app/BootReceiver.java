package com.tileshelf.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tileshelf.app.catalog.Publication;

import java.util.Objects;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
            Publication.republishTrue(context);
        }
    }
}
