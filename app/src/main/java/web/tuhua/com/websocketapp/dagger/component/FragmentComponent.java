package web.tuhua.com.websocketapp.dagger.component;

import android.app.Activity;

import dagger.Component;
import web.tuhua.com.websocketapp.dagger.module.FragmentModule;
import web.tuhua.com.websocketapp.dagger.scope.FragmentScope;

/**
 * Created by yangtufa on 2017/7/7.
 */
@FragmentScope
@Component(dependencies = ApplicationComponent.class, modules = {FragmentModule.class})
public interface FragmentComponent {
    Activity getActivity();

}
