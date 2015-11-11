/*
 * Copyright (C) 2015 Ravi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.service.ScoresFetchService;

/**
 * Provides football scores collection widgets.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ScoresWidgetProvider extends AppWidgetProvider {

    private final String LOG_TAG = ScoresWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, context.getString(R.string.on_update));

        // Update each app widget instance with remote view and corresponding remote adapter.
        for(int appWidgetId : appWidgetIds) {
            // Create the remote views object for the widget instance.
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_scores);

            // Create the intent that starts the service which will provide the views for this collection.
            Intent serviceIntent = new Intent(context, ScoresWidgetRemoteViewsService.class);

            // Add the app widget id to the intent extras.
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            // Set the remote adapter onto the scores list view in the remote view.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                remoteView.setRemoteAdapter(R.id.widget_list_view, serviceIntent);
            } else {
                remoteView.setRemoteAdapter(appWidgetId, R.id.widget_list_view, serviceIntent);
            }

            // Create an intent to launch the main activity and set it on the remote view's title.
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteView.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Create and add the list item click pending intent template.
            Intent listItemClickIntent = new Intent(context, MainActivity.class);
            listItemClickIntent.setAction(context.getString(R.string.action_item_clicked))
                    .setData(Uri.parse(listItemClickIntent.toUri(Intent.URI_INTENT_SCHEME)));

            // Set the pending intent template.
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(listItemClickIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteView.setPendingIntentTemplate(R.id.widget_list_view, clickPendingIntentTemplate);

            // Set an empty view in case of no data.
            remoteView.setEmptyView(R.id.widget_list_view, R.id.widget_empty);

            // Call app widget manager to update the app widget instance.
            appWidgetManager.updateAppWidget(appWidgetId, remoteView);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        // Get the intent action.
        String action = intent.getAction();
        Log.d(LOG_TAG, context.getString(R.string.on_receive) + action);

        // Notify widgets to refresh their views if any of the following events have occurred:
        // data has been updated, date has changed, time was set/reset, timezone changed.
        if(context.getString(R.string.action_data_updated).equals(action) ||
                Intent.ACTION_DATE_CHANGED.equals(action) ||
                Intent.ACTION_TIMEZONE_CHANGED.equals(action) ||
                Intent.ACTION_TIME_CHANGED.equals(action)) {

            // Get the app widget manager.
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            // Get app widget ids from the app widget manager.
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));

            // Notify the app widget manager that the underlying widget data has changed.
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
        }

        // Set up a repeating alarm to start the scores fetch service. Do this only ONCE when the
        // first app widget instance is enabled.
        if(AppWidgetManager.ACTION_APPWIDGET_ENABLED.equals(action)) {
            // Set up an inexact repeating alarm to start service to refresh data.
            Intent serviceIntent = new Intent(context, ScoresFetchService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, serviceIntent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    AlarmManager.INTERVAL_HALF_HOUR, AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
        }

        // Cancel the repeating alarm after all the app widget instances have been disabled.
        if(AppWidgetManager.ACTION_APPWIDGET_DISABLED.equals(action)) {
            // Cancel the alarm.
            Intent serviceIntent = new Intent(context, ScoresFetchService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, serviceIntent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }

        // If the device has booted, recreate the alarm to start the scores fetch service.
        if(Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            // Set up an inexact repeating alarm to start service to refresh data.
            Intent serviceIntent = new Intent(context, ScoresFetchService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, serviceIntent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    AlarmManager.INTERVAL_HALF_HOUR, AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
        }
    }
}

