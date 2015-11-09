package barqsoft.footballscores;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import barqsoft.footballscores.service.ScoresFetchService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private final String LOG_TAG = MainScreenFragment.class.getSimpleName();
    public ScoresAdapter mAdapter;
    public static final int SCORES_LOADER = 0;
    private String[] fragmentdate = new String[1];
    private int last_selected_item = -1;
    private ListView mScoresList;

    // Sort order for the query.
    private static final String SORT_ORDER = " ASC";

    public MainScreenFragment()
    {
    }

    private void update_scores()
    {
        Intent service_start = new Intent(getActivity(), ScoresFetchService.class);
        getActivity().startService(service_start);
    }
    public void setFragmentDate(String date)
    {
        fragmentdate[0] = date;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView()");

        update_scores();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mScoresList = (ListView) rootView.findViewById(R.id.scores_list);
        mAdapter = new ScoresAdapter(getActivity(),null,0);
        mScoresList.setAdapter(mAdapter);
        getLoaderManager().initLoader(SCORES_LOADER,null,this);
        mAdapter.detail_match_id = MainActivity.selected_match_id;
        mScoresList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder selected = (ViewHolder) view.getTag();
                mAdapter.detail_match_id = selected.match_id;
                MainActivity.selected_match_id = (int) selected.match_id;
                mAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
//        return new CursorLoader(getActivity(),DatabaseContract.scores_table.buildScoreWithDate(),
//                null,null,fragmentdate,null);

        // Sort on the time column in ascending order.
        String sortOrder = DatabaseContract.scores_table.TIME_COL + SORT_ORDER;

        return new CursorLoader(getActivity(),
                DatabaseContract.scores_table.buildScoreWithDate(),
                null,
                null,
                fragmentdate,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        //Log.v(FetchScoreTask.LOG_TAG,"loader finished");
        //cursor.moveToFirst();
        /*
        while (!cursor.isAfterLast())
        {
            Log.v(FetchScoreTask.LOG_TAG,cursor.getString(1));
            cursor.moveToNext();
        }
        */

        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            i++;
            cursor.moveToNext();
        }
        //Log.v(FetchScoreTask.LOG_TAG,"Loader query: " + String.valueOf(i));
        mAdapter.swapCursor(cursor);
        //mAdapter.notifyDataSetChanged();


        Log.d(LOG_TAG, "onLoadFinished()");

        // Check if widget list item was clicked.
        if(MainActivity.isWidgetItemClicked) {
            Log.d(LOG_TAG, "onLoadFinished(): Widget item clicked");

            // Set the match id clicked into the adapter.
            mAdapter.detail_match_id = MainActivity.widgetItemClickedMatchId;
            MainActivity.selected_match_id = MainActivity.widgetItemClickedMatchId;
            mAdapter.notifyDataSetChanged();

            // Scroll to widget item click position.
            if(MainActivity.widgetItemClickedPosition != ListView.INVALID_POSITION) {
                mScoresList.setSelection(MainActivity.widgetItemClickedPosition);
            }

            // Reset the widget list item clicked flag and match id.
            MainActivity.isWidgetItemClicked = false;
            MainActivity.widgetItemClickedMatchId = -1;
            MainActivity.widgetItemClickedPosition = -1;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        mAdapter.swapCursor(null);
    }
}
