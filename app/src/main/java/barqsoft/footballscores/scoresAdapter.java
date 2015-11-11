package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter
{
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;
    public double detail_match_id = 0;
    public ScoresAdapter(Context context, Cursor cursor, int flags)
    {
        super(context,cursor,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        //Log.v(FetchScoreTask.LOG_TAG,"new View inflated");
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor)
    {
        final ViewHolder mHolder = (ViewHolder) view.getTag();
        mHolder.home_name.setText(cursor.getString(COL_HOME));
        mHolder.home_name.setContentDescription(context.getString(R.string.a11y_home_name,
                cursor.getString(COL_HOME)));
        mHolder.away_name.setText(cursor.getString(COL_AWAY));
        mHolder.away_name.setContentDescription(context.getString(R.string.a11y_away_name,
                cursor.getString(COL_AWAY)));
        mHolder.date.setText(cursor.getString(COL_MATCHTIME));
        mHolder.date.setContentDescription(context.getString(R.string.a11y_match_time,
                cursor.getString(COL_MATCHTIME)));

        String matchScore;
        // Adjust score direction based on whether layout direction is rtl.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && Utilities.isRtl(context)) {
            matchScore = Utilities.getScores(mContext, cursor.getInt(COL_AWAY_GOALS),
                    cursor.getInt(COL_HOME_GOALS));
        } else {
            matchScore = Utilities.getScores(mContext, cursor.getInt(COL_HOME_GOALS),
                    cursor.getInt(COL_AWAY_GOALS));
        }
        mHolder.score.setText(matchScore);

        // Check if match score is available and set content description accordingly.
        if(matchScore.equals(context.getString(R.string.dash))) {
            mHolder.score.setContentDescription(context.getString(R.string.a11y_no_score));
        } else {
            mHolder.score.setContentDescription(context.getString(R.string.a11y_match_score, matchScore));
        }
        mHolder.match_id = cursor.getDouble(COL_ID);
        mHolder.home_crest.setImageResource(Utilities.getTeamCrestByTeamName(mContext,
                cursor.getString(COL_HOME)));
        mHolder.home_crest.setContentDescription(context.getString(R.string.a11y_home_crest,
                cursor.getString(COL_HOME)));
        mHolder.away_crest.setImageResource(Utilities.getTeamCrestByTeamName(mContext,
                cursor.getString(COL_AWAY)));
        mHolder.away_crest.setContentDescription(context.getString(R.string.a11y_away_crest,
                cursor.getString(COL_AWAY)));
        //Log.v(FetchScoreTask.LOG_TAG,mHolder.home_name.getText() + " Vs. " + mHolder.away_name.getText() +" id " + String.valueOf(mHolder.match_id));
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(detail_match_id));
        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
        if(mHolder.match_id == detail_match_id)
        {
            //Log.v(FetchScoreTask.LOG_TAG,"will insert extraView");

            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Utilities.getMatchDay(mContext, cursor.getInt(COL_MATCHDAY),
                    cursor.getInt(COL_LEAGUE)));
            match_day.setContentDescription(Utilities.getMatchDay(mContext, cursor.getInt(COL_MATCHDAY),
                    cursor.getInt(COL_LEAGUE)));
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utilities.getLeague(mContext, cursor.getInt(COL_LEAGUE)));
            league.setContentDescription(context.getString(R.string.a11y_league,
                    Utilities.getLeague(mContext, cursor.getInt(COL_LEAGUE))));
            Button share_button = (Button) v.findViewById(R.id.share_button);
            share_button.setContentDescription(context.getString(R.string.a11y_share_button));
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    //add Share Action
                    context.startActivity(createShareForecastIntent(mHolder.home_name.getText()+" "
                    +mHolder.score.getText()+" "+mHolder.away_name.getText() + " "));
                }
            });
        }
        else
        {
            container.removeAllViews();
        }
    }

    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType(mContext.getString(R.string.intent_type_text_plain));
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                ShareText + mContext.getString(R.string.scores_hashtag));
        return shareIntent;
    }
}
