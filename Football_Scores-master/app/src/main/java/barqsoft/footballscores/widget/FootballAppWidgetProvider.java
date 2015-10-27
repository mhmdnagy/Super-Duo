package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Random;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;

/**
 * Created by vezikon on 10/23/15.
 */
public class FootballAppWidgetProvider extends AppWidgetProvider {


    private static HandlerThread sWorkerThread;
    private static Handler sWorkerQueue;
    private static FootballDataObserver sDataObserver;


    public FootballAppWidgetProvider() {
        // Start the worker thread
        sWorkerThread = new HandlerThread("WeatherWidgetProvider-worker");
        sWorkerThread.start();
        sWorkerQueue = new Handler(sWorkerThread.getLooper());
    }

    @Override
    public void onEnabled(Context context) {
        // Register for external updates to the data to trigger an update of the widget.  When using
        // content providers, the data is often updated via a background service, or in response to
        // user interaction in the main app.  To ensure that the widget always reflects the current
        // state of the data, we must listen for changes and update ourselves accordingly.
        final ContentResolver r = context.getContentResolver();
        if (sDataObserver == null) {
            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            final ComponentName cn = new ComponentName(context, FootballAppWidgetProvider.class);
            sDataObserver = new FootballDataObserver(mgr, cn, sWorkerQueue);
            r.registerContentObserver(DatabaseContract.BASE_CONTENT_URI, true, sDataObserver);
        }
    }


    @Override
    public void onReceive(Context ctx, Intent intent) {


        super.onReceive(ctx, intent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // update each of the app widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {

            // Sets up the intent that points to the StackViewService that will
            // provide the views for this collection.
            Intent intent = new Intent(context, ListWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.appwidget_view);
            rv.setRemoteAdapter(appWidgetIds[i], R.id.widget_list_view, intent);

            // The empty view is displayed when the collection has no items. It should be a sibling
            // of the collection view.
            rv.setEmptyView(R.id.widget_list_view, R.id.empty_view);

//            // This section makes it possible for items to have individualized behavior.
//            // It does this by setting up a pending intent template. Individuals items of a collection
//            // cannot set up their own pending intents. Instead, the collection as a whole sets
//            // up a pending intent template, and the individual items set a fillInIntent
//            // to create unique behavior on an item-by-item basis.
//            Intent toastIntent = new Intent(context, ListWidgetService.class);
//            // Set the action for the intent.
//            // When the user touches a particular view, it will have the effect of
//            // broadcasting TOAST_ACTION.
//            toastIntent.setAction(ListWidgetService.TOAST_ACTION);
//            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
//            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
//            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//            rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }

    }
}
