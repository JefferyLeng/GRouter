package cc.guider.architeature.minecenter.debug;

import android.app.Application;
import android.util.Log;

public class MineCenter_DebugApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("minecenter", "onCreate: >>> mine center application execute");
    }
}
