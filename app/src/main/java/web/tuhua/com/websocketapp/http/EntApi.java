package web.tuhua.com.websocketapp.http;


import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import web.tuhua.com.websocketapp.MsgBean;

/**
 * 网络请求api
 * Created by yangtufa on 2017/7/24.
 */

public interface EntApi {
    String HOST = "http://push.mysise.org/";


    @POST("/MsgPushCtrl/getHistoryPush")
    @FormUrlEncoded
    Observable<FeedResult<PagerResult<MsgBean>>> getHistory(@FieldMap Map<String, Object> params);
}
