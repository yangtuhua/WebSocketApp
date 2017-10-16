package web.tuhua.com.websocketapp.dagger.module;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import web.tuhua.com.websocketapp.WSApplication;
import web.tuhua.com.websocketapp.http.EntApi;
import web.tuhua.com.websocketapp.http.RetrofitHelper;

/**
 * Created by yangtufa on 2017/7/7.
 */
@Module
public class ApplicationModule {

    private final WSApplication bookApplication;

    public ApplicationModule(WSApplication bookApplication) {
        this.bookApplication = bookApplication;
    }

    @Singleton
    @Provides
    WSApplication providesApplication() {
        return bookApplication;
    }

    @Singleton
    @Provides
    RetrofitHelper providesRetrofitHelper(EntApi entApi) {
        return new RetrofitHelper(entApi);
    }
}
