package web.tuhua.com.websocketapp.http;


/**
 * retrofit网络请求参数配置
 * <p>
 * Created by yangtufa on 2017/3/27.
 */

public class HttpConfig {

    //网络请求模块公共参数requestType = app
    public static final String REQUEST_TYPE_KEY = "requestType";

    //网络请求模块公共参数requestType = app
    public static final String REQUEST_TYPE_VALUE = "app";

    //是否允许失败后重新请求
    public static final boolean RETRY_ON_CONNECTION_WHEN_FAIL = true;

    //请求缓存路径
    public static final String DEFAULT_REQUEST_CACHE_PATH = AppConfig.BASE_CACHE_PATH + "/HTTP_CACHE";

    //请求缓存空间大小
    public static final int MAX_CACHE_SIZE = 10 * 1024 * 1024;

    //超时时间
    public static final int DEFAULT_TIMER_OUT = 30;

    //超时时间
    public static final int DEFAULT_READ_TIME_OUT = 30;

    //超时时间
    public static final int DEFAULT_WRITE_TIME_OUT = 30;

}
