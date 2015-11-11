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
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.ScoresContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;

/**
 * Remote adapter that binds to the list view in the scores collection widget.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ScoresWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final String LOG_TAG = ScoresWidgetRemoteViewsFactory.class.getSimpleName();

    // Projection for getting scores data from the scores table.
    private static final String[] SCORES_PROJECTION = {
            ScoresContract.scores_table.MATCH_ID,
            ScoresContract.scores_table.DATE_COL,
            ScoresContract.scores_table.TIME_COL,
            ScoresContract.scores_table.HOME_COL,
            ScoresContract.scores_table.AWAY_COL,
            ScoresContract.scores_table.HOME_GOALS_COL,
            ScoresContract.scores_table.AWAY_GOALS_COL
    };

    // Column indices matching the projection.
    private static final int INDEX_MATCH_ID = 0;
    private static final int INDEX_DATE = 1;
    private static final int INDEX_TIME = 2;
    private static final int INDEX_HOME = 3;
    private static final int INDEX_AWAY = 4;
    private static final int INDEX_HOME_GOALS = 5;
    private static final int INDEX_AWAY_GOALS = 6;

    // Store the cursor containing football scores data.
    private Cursor mCursor = null;
    // Store the context.
    private Context mContext;
    // Store the intent.
    private Intent mIntent;

    ScoresWidgetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
    }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, mContext.getString(R.string.on_create));
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, mContext.getString(R.string.on_destroy));

        // Check and close the cursor.
        if(mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
    }

    @Override
    public int getCount() {
        // Return the number of rows in the cursor.
        return mCursor != null ? mCursor.getCount() : 0;
    }

    @Override
    public int getViewTypeCount() {
        // Only one view type.
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_scores_list_item);
    }

    @Override
    public long getItemId(int i) {
        // Check if cursor is valid and has the requested row.
        if(mCursor != null && mCursor.moveToPosition(i)) {
           return mCursor.getLong(INDEX_MATCH_ID);
        }

        return i;
    }

    @Override
    public RemoteViews getViewAt(int i) {
        Log.d(LOG_TAG, mContext.getString(R.string.get_view_at));

        // Check if it's a valid position and cursor exists.
        if(i == AdapterView.INVALID_POSITION || mCursor == null || !mCursor.moveToPosition(i)) {
            return null;
        }

        // Create the remote view corresponding to the list view item.
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_scores_list_item);

        // Extract relevant match details.
        int matchId = mCursor.getInt(INDEX_MATCH_ID);
        String date = mCursor.getString(INDEX_DATE);
        String time = mCursor.getString(INDEX_TIME);
        String homeTeam = mCursor.getString(INDEX_HOME);
        String awayTeam = mCursor.getString(INDEX_AWAY);
        int homeGoals = mCursor.getInt(INDEX_HOME_GOALS);
        int awayGoals = mCursor.getInt(INDEX_AWAY_GOALS);

        String score;
        // Adjust score direction based on whether layout direction is rtl.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && Utilities.isRtl(mContext)) {
            score = Utilities.getScores(mContext, awayGoals, homeGoals);
        } else {
            score = Utilities.getScores(mContext, homeGoals, awayGoals);
        }

        // Set the match data onto the list item views.
        remoteViews.setTextViewText(R.id.widget_home_team, homeTeam);
        remoteViews.setTextViewText(R.id.widget_away_team, awayTeam);
        remoteViews.setTextViewText(R.id.widget_match_score, score);
        remoteViews.setTextViewText(R.id.widget_match_time, time);

        // Check if the platform version is level 15 or higher. Only if it is, then
        // set the content descriptions for the remote views.
        if(Build.VERSION.SDK_INT >= 15) {
            remoteViews.setContentDescription(R.id.widget_home_team,
                    mContext.getString(R.string.a11y_home_name, homeTeam));
            remoteViews.setContentDescription(R.id.widget_away_team,
                    mContext.getString(R.string.a11y_away_name, awayTeam));
            remoteViews.setContentDescription(R.id.widget_match_time,
                    mContext.getString(R.string.a11y_match_time, time));
            // Check if match score is available and set content description accordingly.
            if(score.equals(mContext.getString(R.string.dash))) {
                remoteViews.setContentDescription(R.id.widget_match_score,
                        mContext.getString(R.string.a11y_no_score));
            } else {
                remoteViews.setContentDescription(R.id.widget_match_score,
                        mContext.getString(R.string.a11y_match_score, score));
            }
        }

        // Create and set the fill in intent.
        final Intent fillInIntent = new Intent();
        fillInIntent.putExtra(mContext.getString(R.string.extra_match_id), matchId);
        fillInIntent.putExtra(mContext.getString(R.string.extra_item_position), i);
        remoteViews.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

        return remoteViews;
    }

    @Override
    public void onDataSetChanged() {
        Log.d(LOG_TAG, mContext.getString(R.string.on_dataset_changed));

        // Check and close cursor.
        if(mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        // Clear the calling identity (app widget host).
        final long callingIdentityToken = Binder.clearCallingIdentity();

        // Get today's date and convert to string.
        Date todayDate = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(mContext.getString(R.string.date_format));
        String[] dateArray = {dateFormat.format(todayDate)};

        // Sort on the time column in ascending order.
        String sortOrder = ScoresContract.scores_table.TIME_COL +
                mContext.getString(R.string.ascending_sort_order);

        // Query the content provider to fetch today's matches.
        mCursor = mContext.getContentResolver().query(
                ScoresContract.scores_table.buildScoreWithDate(),
                SCORES_PROJECTION,
                null,
                dateArray,
                sortOrder);

        // Restore the calling identity.
        Binder.restoreCallingIdentity(callingIdentityToken);
    }
}
