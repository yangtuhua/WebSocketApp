package web.tuhua.com.websocketapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import web.tuhua.com.websocketapp.http.FeedResult;
import web.tuhua.com.websocketapp.http.PagerResult;
import web.tuhua.com.websocketapp.http.RetrofitHelper;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_GET_PERMISSION = 100;
    private WebSocketClient webSocketClient;

    private boolean isConnected;//是否已经连接

    private static final int MAX_RECONNECT_COUNT = 5;//最多尝试重新连接5次

    private int tryConnectCount;//已经尝试连接的次数
    private RetrofitHelper retrofitHelper;

    private int currentPage = 1;

    private static final int pageSize = 15;
    private RecyclerView recyclerView;
    private boolean isRefresh;
    private long total;

    private boolean closeByUser;//是否由用户关闭


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();
    }

    private void getData() {
        initView();

        connectToServer(false);

        getHistory();
    }

    /***权限获取*/
    @AfterPermissionGranted(REQUEST_GET_PERMISSION)
    private void getPermission() {
        String[] perms = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            getData();
        } else {
            EasyPermissions.requestPermissions(this, "为了保证正常使用，请允许应用获取相应的权限", REQUEST_GET_PERMISSION, perms);
        }
    }

    private void initView() {
        RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(0);
                isRefresh = true;
                currentPage = 1;
                getHistory();
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                isRefresh = false;
                if (total <= pageSize * currentPage) {
                    Toast.makeText(MainActivity.this, "没有更多了", Toast.LENGTH_LONG).show();
                } else {
                    currentPage++;
                    getHistory();
                }
                refreshlayout.finishLoadmore(0);
            }
        });
    }

    private void getHistory() {
        if (retrofitHelper == null) {
            retrofitHelper = WSApplication.getApplicationComponent().getRetrofitHelper();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("pageSize", pageSize);
        params.put("pages", currentPage);
        retrofitHelper.getHistory(params).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<FeedResult<PagerResult<MsgBean>>>() {
            @Override
            public void accept(FeedResult<PagerResult<MsgBean>> pagerResultFeedResult) throws Exception {
                total = pagerResultFeedResult.getResult().getTotalRow();

                showMsgList(pagerResultFeedResult.getResult().getList());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtils.e("出错了：" + throwable);
            }
        });
    }

    private BaseQuickAdapter<MsgBean, BaseViewHolder> commonAdapter;

    private void showMsgList(List<MsgBean> list) {

        if (commonAdapter == null) {
            commonAdapter = new BaseQuickAdapter<MsgBean, BaseViewHolder>(R.layout.item_msg, list) {
                @Override
                protected void convert(BaseViewHolder baseViewHolder, MsgBean msgBean) {
                    baseViewHolder.setText(R.id.tv_content, msgBean.getMsg());

                    String dateStr = TimeUtils.date2String(new Date(msgBean.getCreat_time()), new SimpleDateFormat("MM-dd HH:mm"));
                    baseViewHolder.setText(R.id.tv_time, dateStr);
                }
            };
            recyclerView.setAdapter(commonAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
        } else {
            if (isRefresh) {
                commonAdapter.setNewData(list);
            } else {
                commonAdapter.addData(list);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_connect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                if (webSocketClient != null) {
                    if (webSocketClient.isConnecting()) {
                        ToastUtils.showLong("您已经连接了服务器！");
                        return true;
                    } else {
                        connectToServer(false);
                    }
                } else {
                    connectToServer(false);
                }
                break;
            case R.id.menu_disconnect:
                if (webSocketClient != null) {
                    closeByUser = true;
                    webSocketClient.close();
                }
                break;
        }
        return true;
    }

    /***连接到服务器*/
    private void connectToServer(boolean isTryReconnect) {
        if (webSocketClient != null && webSocketClient.isConnecting() && isTryReconnect) {
            webSocketClient.close();
            webSocketClient = null;
        }
        webSocketClient = new WebSocketClient(URI.create("ws://push.mysise.org/websocket")) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.e("info", "连接成功...");
                closeByUser = false;
                tryConnectCount = 0;
                isConnected = true;
            }

            @Override
            public void onMessage(String s) {
                LogUtils.e("收到消息：" + s);
                pushMsgToStatusBar(s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                isConnected = false;
                Log.e("info", "连接关闭！");

                //TODO 重新连接
                if (tryConnectCount <= MAX_RECONNECT_COUNT && !closeByUser) {
                    tryConnectCount++;
                    connectToServer(true);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("info", "连接错误：" + e.getMessage());
                isConnected = false;
            }
        };
        webSocketClient.connect();
    }

    /***将消息推送至通知栏*/
    private void pushMsgToStatusBar(String msg) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 创建一个新的Notification对象，并添加图标
        Notification n = new Notification();
        n.icon = R.mipmap.ic_launcher;
        n.tickerText = msg;
        n.when = System.currentTimeMillis();
        //n.flags=Notification.FLAG_ONGOING_EVENT;
//        Intent intent = new Intent(context, SendNotification.class);
//        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
//        n.setLatestEventInfo(context, "title", "content", pi);
        mNotificationManager.notify(1, n);
    }

    @Override
    public void onPermissionsGranted(int i, List<String> list) {
        getData();
    }

    @Override
    public void onPermissionsDenied(int i, List<String> list) {

    }
}
