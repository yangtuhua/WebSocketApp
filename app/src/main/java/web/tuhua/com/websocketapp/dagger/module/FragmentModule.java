package web.tuhua.com.websocketapp.dagger.module;

import android.app.Activity;
import android.support.v4.app.Fragment;

import dagger.Module;
import dagger.Provides;
import web.tuhua.com.websocketapp.dagger.scope.FragmentScope;

/**
 * Created by yangtufa on 2017/7/7.
 */
@Module
public class FragmentModule {
    private Fragment fragment;

    public FragmentModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    @FragmentScope
    Activity providesActivity() {
        return fragment.getActivity();
    }

}
