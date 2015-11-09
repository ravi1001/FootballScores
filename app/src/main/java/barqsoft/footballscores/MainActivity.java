package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import barqsoft.footballscores.widget.ScoresWidgetProvider;
import barqsoft.footballscores.widget.ScoresWidgetRemoteViewsFactory;

public class MainActivity extends ActionBarActivity
{
    // Flag indicating whether widget list item was clicked.
    public static boolean isWidgetItemClicked = false;
    // Match id corresponding to the widget list item that was clicked.
    public static int widgetItemClickedMatchId = -1;
    // Position of the widget list item that was clicked.
    public static int widgetItemClickedPosition = -1;

    public static int selected_match_id;
    public static int current_fragment = 2;
    public static String LOG_TAG = MainActivity.class.getSimpleName();
    private final String save_tag = "Save Test";
    private PagerFragment my_main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "onCreate()");

        if (savedInstanceState == null) {
            // Check if the widget list item was clicked.
            Intent intent = getIntent();
            if(intent != null && intent.getAction() != null
                    && intent.getAction().equals(ScoresWidgetProvider.ACTION_ITEM_CLICKED)) {
                Log.d(LOG_TAG, "onCreate(): ACTION_ITEM_CLICKED");

                // Set the widget list item clicked flag.
                isWidgetItemClicked = true;

                // Extract the match id and position from the intent.
                widgetItemClickedMatchId = intent.getIntExtra(ScoresWidgetRemoteViewsFactory.EXTRA_MATCH_ID, -1);
                widgetItemClickedPosition = intent.getIntExtra(ScoresWidgetRemoteViewsFactory.EXTRA_ITEM_POSITION, -1);

                Log.d(LOG_TAG, "onCreate(): Match Id: " + ((Integer)widgetItemClickedMatchId).toString());
                Log.d(LOG_TAG, "onCreate(): Position: " + ((Integer)widgetItemClickedPosition).toString());
            }

            my_main = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, my_main)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about)
        {
            Intent start_about = new Intent(this,AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.v(save_tag,"will save");
        Log.v(save_tag,"fragment: "+String.valueOf(my_main.mPagerHandler.getCurrentItem()));
        Log.v(save_tag,"selected id: "+selected_match_id);
        outState.putInt("Pager_Current",my_main.mPagerHandler.getCurrentItem());
        outState.putInt("Selected_match",selected_match_id);
        getSupportFragmentManager().putFragment(outState,"my_main",my_main);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.v(save_tag,"will retrive");
        Log.v(save_tag,"fragment: "+String.valueOf(savedInstanceState.getInt("Pager_Current")));
        Log.v(save_tag,"selected id: "+savedInstanceState.getInt("Selected_match"));
        current_fragment = savedInstanceState.getInt("Pager_Current");
        selected_match_id = savedInstanceState.getInt("Selected_match");
        my_main = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState,"my_main");
        super.onRestoreInstanceState(savedInstanceState);
    }
}
