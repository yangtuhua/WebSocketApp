package web.tuhua.com.websocketapp;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

/**
 * Created by yangtufa on 2017/10/14.
 */

public class WSApplication extends Application {

    private static boolean IS_DATABASE_LOGGING_ENABLED = false;

    @Override
    public void onCreate() {
        super.onCreate();
        initActiveAndroid();
    }

    /***初始化activeAndroid数据库框架*/
    private void initActiveAndroid() {
        if (BuildConfig.DEBUG) {
            IS_DATABASE_LOGGING_ENABLED = true;
        }
        Configuration.Builder configurationBuilder = new Configuration.Builder(this);
        //TODO 注:所有新增的表,都需要在此处注册
        configurationBuilder.addModelClasses();
        ActiveAndroid.initialize(configurationBuilder.create(), IS_DATABASE_LOGGING_ENABLED);
    }
}
