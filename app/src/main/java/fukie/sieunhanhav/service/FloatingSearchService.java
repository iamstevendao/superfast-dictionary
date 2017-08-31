package fukie.sieunhanhav.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import fukie.sieunhanhav.R;
import fukie.sieunhanhav.activity.FloatingSearchActivity;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

/**
 * Created by Fukie on 25/03/2016.
 */
public class FloatingSearchService extends Service implements FloatingViewListener {
    private IBinder mChatHeadServiceBinder;
    private FloatingViewManager mFloatingViewManager;
    Context context;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mFloatingViewManager != null) {
            return START_STICKY;
        }

        final DisplayMetrics metrics = new DisplayMetrics();
        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mChatHeadServiceBinder = new ChatHeadServiceBinder(this);
        final LayoutInflater inflater = LayoutInflater.from(this);
        final ImageView iconView = (ImageView) inflater.inflate(R.layout.widget_chathead, null, false);
        context = getApplication().getApplicationContext();
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!FloatingSearchActivity.active) {
                    Intent intent = new Intent(context, FloatingSearchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    FloatingSearchActivity.getInstance().finish();
                }
            }
        });

        mFloatingViewManager = new FloatingViewManager(this, this);
        mFloatingViewManager.setFixedTrashIconImage(R.drawable.ic_trash_fixed);
        mFloatingViewManager.setActionTrashIconImage(R.drawable.ic_trash_action);
        final FloatingViewManager.Options options = new FloatingViewManager.Options();
        options.shape = FloatingViewManager.SHAPE_CIRCLE;
        options.overMargin = (int) (16 * metrics.density);
        mFloatingViewManager.addViewToWindow(iconView, options);


        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        destroy();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mChatHeadServiceBinder;
    }

    @Override
    public void onFinishFloatingView() {
        stopSelf();
    }

    private void destroy() {
        if (mFloatingViewManager != null) {
            FloatingSearchActivity.getInstance().finish();
            mFloatingViewManager.removeAllViewToWindow();
            mFloatingViewManager = null;
        }
    }

    private static class ChatHeadServiceBinder extends Binder {

        private final WeakReference<FloatingSearchService> mService;

        ChatHeadServiceBinder(FloatingSearchService service) {
            mService = new WeakReference<>(service);
        }

        public FloatingSearchService getService() {
            return mService.get();
        }
    }

}

