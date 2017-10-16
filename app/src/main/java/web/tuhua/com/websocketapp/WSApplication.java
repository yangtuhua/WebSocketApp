package web.tuhua.com.websocketapp;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.Utils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import web.tuhua.com.websocketapp.dagger.component.ApplicationComponent;
import web.tuhua.com.websocketapp.dagger.component.DaggerApplicationComponent;
import web.tuhua.com.websocketapp.dagger.module.ApplicationModule;

/**
 * Created by yangtufa on 2017/10/14.
 */

public class WSApplication extends Application {

    private static WSApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        Utils.init(this);
    }

    //static 代码段可以防止内存泄露,初始化下来刷新的样式
    static {
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.color_f3f5f0, R.color.color_333);//全局设置主题颜色
                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                return new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
    }

    /****向外提供获取 {@link ApplicationComponent}的方法*/
    public static ApplicationComponent getApplicationComponent() {
        return DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(mInstance)).build();
    }
}
