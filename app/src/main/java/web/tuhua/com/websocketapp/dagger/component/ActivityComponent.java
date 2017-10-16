package web.tuhua.com.websocketapp.dagger.component;

import android.app.Activity;

import dagger.Component;
import web.tuhua.com.websocketapp.MainActivity;
import web.tuhua.com.websocketapp.dagger.module.ActivityModule;
import web.tuhua.com.websocketapp.dagger.scope.ActivityScope;

/**
 * Created by yangtufa on 2017/7/7.
 */
@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class})
public interface ActivityComponent {

    Activity getActivity();

    void inject(MainActivity activity);
}
