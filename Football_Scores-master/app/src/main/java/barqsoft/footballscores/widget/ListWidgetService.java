package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by vezikon on 10/24/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }


    public static class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private static final int mCount = 5;
        private Cursor cursor;
        private Context mContext;
        private int mAppWidgetId;

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


        public ListRemoteViewsFactory(Context context, Intent intent) {

            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            mContext = context;
        }

        @Override
        public void onCreate() {


        }

        @Override
        public void onDataSetChanged() {
            if (cursor != null) {
                cursor.close();
            }
            //getting today's date
            Date todayDate = new Date(System.currentTimeMillis());
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");

            //add today's to selections args
            String[] selectionArgs = {mformat.format(todayDate)};

            cursor = mContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                    null, null, selectionArgs, null);

        }

        @Override
        public void onDestroy() {
            if (cursor != null) {
                cursor.close();
            }
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            // Construct a RemoteViews item based on the app widget item XML file, and set the
            // text based on the position.
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.scores_list_item);

            if (cursor.moveToPosition(position)) {

                Log.d("home team", cursor.getString(COL_HOME));
                rv.setTextViewText(R.id.home_name, cursor.getString(COL_HOME));
                rv.setTextViewText(R.id.away_name, cursor.getString(COL_AWAY));
                rv.setTextViewText(R.id.data_textview, cursor.getString(COL_MATCHTIME));
                rv.setTextViewText(R.id.score_textview, Utilies.getScores(cursor.getInt(COL_HOME_GOALS)
                        , cursor.getInt(COL_AWAY_GOALS)));

            }

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
