package web.tuhua.com.websocketapp.dagger.component;


import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;
import web.tuhua.com.websocketapp.WSApplication;
import web.tuhua.com.websocketapp.dagger.module.ApplicationModule;
import web.tuhua.com.websocketapp.dagger.module.HttpModule;
import web.tuhua.com.websocketapp.http.RetrofitHelper;

/**
 * Created by yangtufa on 2017/7/7.
 */
@Singleton
@Component(modules = {ApplicationModule.class, HttpModule.class})
public interface ApplicationComponent {
    WSApplication getApplication();

    RetrofitHelper getRetrofitHelper();

    OkHttpClient getOkhttpClient();
}
