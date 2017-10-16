package web.tuhua.com.websocketapp.dagger.module;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import okhttp3.CipherSuite;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import web.tuhua.com.websocketapp.BuildConfig;
import web.tuhua.com.websocketapp.dagger.qualifiter.EntUrl;
import web.tuhua.com.websocketapp.http.CookieHolder;
import web.tuhua.com.websocketapp.http.EntApi;
import web.tuhua.com.websocketapp.http.HttpConfig;
import web.tuhua.com.websocketapp.http.ParamsInterceptor;

/**
 * 网络请求模块的module
 * Created by yangtufa on 2017/3/27.
 */
@Module
public class HttpModule {

    @Singleton
    @Provides
    Retrofit.Builder provideRetrofitBuilder() {
        return new Retrofit.Builder();
    }

    @Singleton
    @Provides
    OkHttpClient.Builder provideOkHttpBuilder() {
        return new OkHttpClient.Builder();
    }

    @Singleton
    @Provides
    @EntUrl
    Retrofit provideStuRetrofit(Retrofit.Builder builder, OkHttpClient client) {
        return createRetrofit(builder, client, EntApi.HOST);
    }

    @Singleton
    @Provides
    EntApi provideStuApiService(@EntUrl Retrofit retrofit) {
        return retrofit.create(EntApi.class);
    }

    @Singleton
    @Provides
    OkHttpClient provideClient(OkHttpClient.Builder builder) {
        //添加日志拦截器
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }

        //缓存
//        File cacheFile = new File(HttpConfig.DEFAULT_REQUEST_CACHE_PATH);
//        final Cache cache = new Cache(cacheFile, HttpConfig.MAX_CACHE_SIZE);
//        HttpCacheInterceptor cacheInterceptor = new HttpCacheInterceptor();
//        builder.cache(cache);
//
//        builder.interceptors().add(cacheInterceptor);//添加本地缓存拦截器，用来拦截本地缓存
//        builder.addNetworkInterceptor(cacheInterceptor);
//        builder.networkInterceptors().add(cacheInterceptor);//添加网络拦截器，用来拦截网络数据
        builder.addInterceptor(new ParamsInterceptor());//自定义拦截器,用于添加功能参数requestType = app

        //cookie持久化
        builder.cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
                CookieHolder.setCooKieList(cookies);
            }

            @Override
            public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
                return CookieHolder.getCookies();
            }
        });

        //设置超时
        builder.connectTimeout(HttpConfig.DEFAULT_TIMER_OUT, TimeUnit.SECONDS);
        builder.readTimeout(HttpConfig.DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(HttpConfig.DEFAULT_WRITE_TIME_OUT, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(HttpConfig.RETRY_ON_CONNECTION_WHEN_FAIL);

//        //TODO 需进一步调试
//        try {
//            List<CipherSuite> customCipherSuites = Arrays.asList(
//                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
//                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
//                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,
//                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
//            );
//            final ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
//                    .cipherSuites(customCipherSuites.toArray(new CipherSuite[3]))
//                    .build();
//
//            X509TrustManager trustManager = defaultTrustManager();
//            SSLSocketFactory sslSocketFactory = defaultSslSocketFactory(trustManager);
//            SSLSocketFactory customSslSocketFactory = new DelegatingSSLSocketFactory(sslSocketFactory) {
//                @Override
//                protected SSLSocket configureSocket(SSLSocket socket) throws IOException {
//                    socket.setEnabledCipherSuites(javaNames(spec.cipherSuites()));
//                    return socket;
//                }
//            };
//            builder.connectionSpecs(Collections.singletonList(spec)).sslSocketFactory(customSslSocketFactory, trustManager);
//        } catch (GeneralSecurityException e) {
//            e.printStackTrace();
//        }
//        if (EntApi.HOST.startsWith("https://")) {
//            ConnectionSpec specTLS1_2 = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
//                    .cipherSuites(CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
//                    .build();
//            ConnectionSpec specTLSAll = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
//                    .build();
//            List<ConnectionSpec> specs = new ArrayList<>();
//            specs.add(specTLS1_2);
//            specs.add(specTLSAll);
//            builder.connectionSpecs(specs);
//        }
        return builder.build();
    }

    private Retrofit createRetrofit(Retrofit.Builder builder, OkHttpClient client, String url) {
        return builder
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Returns the VM's default SSL socket factory, using {@code trustManager} for trusted root
     * certificates.
     */
    private SSLSocketFactory defaultSslSocketFactory(X509TrustManager trustManager) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{trustManager}, null);
        return sslContext.getSocketFactory();
    }

    /**
     * Returns a trust manager that trusts the VM's default certificate authorities.
     */
    private X509TrustManager defaultTrustManager() throws GeneralSecurityException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }

    private String[] javaNames(List<CipherSuite> cipherSuites) {
        String[] result = new String[cipherSuites.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = cipherSuites.get(i).javaName();
        }
        return result;
    }

    /**
     * An SSL socket factory that forwards all calls to a delegate. Override {@link #configureSocket}
     * to customize a created socket before it is returned.
     */
    private static class DelegatingSSLSocketFactory extends SSLSocketFactory {
        final SSLSocketFactory delegate;

        DelegatingSSLSocketFactory(SSLSocketFactory delegate) {
            this.delegate = delegate;
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return delegate.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return delegate.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
            return configureSocket((SSLSocket) delegate.createSocket(socket, host, port, autoClose));
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException {
            return configureSocket((SSLSocket) delegate.createSocket(host, port));
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
            return configureSocket((SSLSocket) delegate.createSocket(host, port, localHost, localPort));
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return configureSocket((SSLSocket) delegate.createSocket(host, port));
        }

        @Override
        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
            return configureSocket((SSLSocket) delegate.createSocket(address, port, localAddress, localPort));
        }

        protected SSLSocket configureSocket(SSLSocket socket) throws IOException {
            return socket;
        }
    }
}
