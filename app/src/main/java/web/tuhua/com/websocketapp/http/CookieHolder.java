package web.tuhua.com.websocketapp.http;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import okhttp3.Cookie;

/**
 * 此类用于保存用户的 {@link Cookie}信息
 * 在请求网络返回时,HttpModule 类中保存cookie信息
 * <p>
 * Created by yangtufa on 2017/4/5.
 */

public class CookieHolder {
    private static CopyOnWriteArrayList<Cookie> mCookies;

    /***向外保存cookie的方法*/
    public static void setCooKieList(List<Cookie> cookies) {
    }

    /***向外提供获取cookie的方法*/
    public static List<Cookie> getCookies() {
        if (mCookies == null) {
            return new ArrayList<>();
        }
        return mCookies;
    }

    /***向外提供清楚cookie的方法*/
    public static void clearCookie() {
        if (mCookies != null) {
            mCookies.clear();
        }
    }

    public static String getCookieString() {
        StringBuilder builder = new StringBuilder();
        if (mCookies != null) {
            for (int i = 0; i < mCookies.size(); i++) {
                Cookie cookie = mCookies.get(i);
                if (cookie.name().contains("JSESSIONID")) {
                    builder.append("JSESSIONID=").append(cookie.value());
                    if (i != mCookies.size() - 1) {
                        builder.append(",");
                    }
                }
            }
        }
        return builder.toString();
    }
}
