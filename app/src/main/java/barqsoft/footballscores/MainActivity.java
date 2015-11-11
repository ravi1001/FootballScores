package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity
{
    public static String LOG_TAG = MainActivity.class.getSimpleName();

    // Flag indicating whether widget list item was clicked.
    public static boolean isWidgetItemClicked = false;
    // Match id corresponding to the widget list item that was clicked.
    public static int widgetItemClickedMatchId = -1;
    // Position of the widget list item that was clicked.
    public static int widgetItemClickedPosition = -1;
    public static int selected_match_id;
    public static int current_fragment = 2;
    private PagerFragment my_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, getString(R.string.on_create));

        if (savedInstanceState == null) {
            // Check if the widget list item was clicked.
            Intent intent = getIntent();
            if(intent != null && intent.getAction() != null
                    && intent.getAction().equals(getString(R.string.action_item_clicked))) {
                Log.d(LOG_TAG, getString(R.string.on_create_item_clicked));

                // Set the widget list item clicked flag.
                isWidgetItemClicked = true;

                // Extract the match id and position from the intent.
                widgetItemClickedMatchId = intent.getIntExtra(getString(R.string.extra_match_id), -1);
                widgetItemClickedPosition = intent.getIntExtra(getString(R.string.extra_item_position), -1);

                Log.d(LOG_TAG, getString(R.string.on_create_match_id) + ((Integer)widgetItemClickedMatchId).toString());
                Log.d(LOG_TAG, getString(R.string.on_create_position) + ((Integer)widgetItemClickedPosition).toString());
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
        Log.v(LOG_TAG,getString(R.string.will_save));
        Log.v(LOG_TAG,getString(R.string.fragment)+String.valueOf(my_main.mPagerHandler.getCurrentItem()));
        Log.v(LOG_TAG,getString(R.string.selected_id)+selected_match_id);
        outState.putInt(getString(R.string.pager_current), my_main.mPagerHandler.getCurrentItem());
        outState.putInt(getString(R.string.selected_match),selected_match_id);
        getSupportFragmentManager().putFragment(outState,getString(R.string.my_main),my_main);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.v(LOG_TAG,getString(R.string.will_retrieve));
        Log.v(LOG_TAG,getString(R.string.fragment)+String.valueOf(savedInstanceState.getInt(getString(R.string.pager_current))));
        Log.v(LOG_TAG,getString(R.string.selected_id)+savedInstanceState.getInt(getString(R.string.selected_match)));
        current_fragment = savedInstanceState.getInt(getString(R.string.pager_current));
        selected_match_id = savedInstanceState.getInt(getString(R.string.selected_match));
        my_main = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState,getString(R.string.my_main));
        super.onRestoreInstanceState(savedInstanceState);
    }
}
