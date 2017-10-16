package web.tuhua.com.websocketapp.http;


import io.reactivex.Observable;
import retrofit2.http.GET;
import web.tuhua.com.websocketapp.MsgBean;

/**
 * 网络请求api
 * Created by yangtufa on 2017/7/24.
 */

public interface EntApi {
    String HOST = "http://push.mysise.org/";


    @GET("/MsgPushCtrl/getHistoryPush")
    Observable<FeedResult<PagerResult<MsgBean>>> getHistory();
}
