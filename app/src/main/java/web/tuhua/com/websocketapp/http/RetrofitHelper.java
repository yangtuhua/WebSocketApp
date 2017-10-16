package web.tuhua.com.websocketapp.http;


import java.util.Map;

import io.reactivex.Observable;
import web.tuhua.com.websocketapp.MsgBean;

/**
 * Created by yangtufa on 2017/7/24.
 */

public class RetrofitHelper {
    private EntApi entApi;

    public RetrofitHelper(EntApi entApi) {
        this.entApi = entApi;
    }


    /*********************在此处添加api方法**************************/

    public Observable<FeedResult<PagerResult<MsgBean>>> getHistory(Map<String,Object> params) {
        return entApi.getHistory(params);
    }
}
