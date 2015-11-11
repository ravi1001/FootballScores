package barqsoft.footballscores;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilities
{
    public static final int SERIE_A = 357;
    public static final int PREMIER_LEGAUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;
    public static String getLeague(Context context, int league_num)
    {
        switch (league_num)
        {
            case SERIE_A : return context.getString(R.string.seriaa);
            case PREMIER_LEGAUE : return context.getString(R.string.premierleague);
            case CHAMPIONS_LEAGUE : return context.getString(R.string.champions_league);
            case PRIMERA_DIVISION : return context.getString(R.string.primeradivison);
            case BUNDESLIGA : return context.getString(R.string.bundesliga);
            default: return context.getString(R.string.unknown_league);
        }
    }
    public static String getMatchDay(Context context, int match_day,int league_num)
    {
        if(league_num == CHAMPIONS_LEAGUE)
        {
            if (match_day <= 6)
            {
                return context.getString(R.string.group_stage_text) +
                        context.getString(R.string.matchday_text) +
                        String.valueOf(match_day);
            }
            else if(match_day == 7 || match_day == 8)
            {
                return context.getString(R.string.first_knockout_round);
            }
            else if(match_day == 9 || match_day == 10)
            {
                return context.getString(R.string.quarter_final);
            }
            else if(match_day == 11 || match_day == 12)
            {
                return context.getString(R.string.semi_final);
            }
            else
            {
                return context.getString(R.string.final_text);
            }
        }
        else
        {
            return context.getString(R.string.matchday_text) + String.valueOf(match_day);
        }
    }

    public static String getScores(Context context, int home_goals,int awaygoals)
    {
        if(home_goals < 0 || awaygoals < 0)
        {
            return context.getString(R.string.dash);
        }
        else
        {
            return String.valueOf(home_goals) + context.getString(R.string.dash)
                    + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName(Context context, String teamname)
    {
        // Extract team names from strings resource.
        if(teamname == null) {
            return R.drawable.no_icon;
        } else if(teamname.equals(context.getString(R.string.arsenal_london_fc))) {
            return R.drawable.arsenal;
        } else if(teamname.equals(context.getString(R.string.manchester_united_fc))) {
            return R.drawable.manchester_united;
        } else if(teamname.equals(context.getString(R.string.swansea_city))) {
            return R.drawable.swansea_city_afc;
        } else if(teamname.equals(context.getString(R.string.leicester_city))) {
            return R.drawable.leicester_city_fc_hd_logo;
        } else if(teamname.equals(context.getString(R.string.everton_fc))) {
            return R.drawable.everton_fc_logo1;
        } else if(teamname.equals(context.getString(R.string.west_ham_united_fc))) {
            return R.drawable.west_ham;
        } else if(teamname.equals(context.getString(R.string.tottenham_hotspur_fc))) {
            return R.drawable.tottenham_hotspur;
        } else if(teamname.equals(context.getString(R.string.west_bromwich_albion))) {
            return R.drawable.west_bromwich_albion_hd_logo;
        } else if(teamname.equals(context.getString(R.string.sunderland_afc))) {
            return R.drawable.sunderland;
        } else if(teamname.equals(context.getString(R.string.stoke_city_fc))) {
            return R.drawable.stoke_city;
        } else {
            return R.drawable.no_icon;
        }
    }

    /**
     * Checks if the layout direction is RTL.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isLayoutDirectionRtl(View view) {
        return view.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    /**
     * Reverses the adapter position to support RTL.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int reversePositionForRtl(int position, int total)
    {
        return total - position - 1; // Thanks to Udacity student josen for the input.
    }

    /**
     * Checks and returns if device is in rtl mode.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isRtl(Context context) {
        return context.getResources().getBoolean(R.bool.is_right_to_left);
    }
}
