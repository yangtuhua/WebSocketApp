package web.tuhua.com.websocketapp.http;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 此拦截器用于添加公共参数 requestType = app
 * <p>
 * Created by yangtufa on 2017/3/27.
 */
public class ParamsInterceptor implements Interceptor {
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request oldRequest = chain.request();
        // 添加requestType = app参数
        HttpUrl.Builder requestTypeBuilder = oldRequest.url()
                .newBuilder()
                .scheme(oldRequest.url().scheme())
                .host(oldRequest.url().host())
                .addQueryParameter(HttpConfig.REQUEST_TYPE_KEY, HttpConfig.REQUEST_TYPE_VALUE);

        // 新的请求
        Request newRequest = oldRequest.newBuilder()
                .method(oldRequest.method(), oldRequest.body())
                .addHeader("User-Agent", "Android-jobEnterprise")
                .url(requestTypeBuilder.build())
                .build();
        return chain.proceed(newRequest);
    }
}
