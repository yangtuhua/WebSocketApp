package web.tuhua.com.websocketapp.dagger.module;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;
import web.tuhua.com.websocketapp.dagger.scope.ActivityScope;

/**
 * Created by yangtufa on 2017/7/7.
 */
@Module
public class ActivityModule {

    private Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    Activity provideActivity() {
        return activity;
    }
}
