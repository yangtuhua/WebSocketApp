package web.tuhua.com.websocketapp;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.Random;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermission();
        } else {
            getData();
        }
    }

    private void getData() {
        initView();

        connectToServer(false);
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
        refreshLayout.autoRefresh();
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
                protected void convert(BaseViewHolder baseViewHolder, final MsgBean msgBean) {
                    baseViewHolder.setText(R.id.tv_content, msgBean.getMsg());

                    String dateStr = TimeUtils.date2String(new Date(msgBean.getCreat_time()), new SimpleDateFormat("MM-dd HH:mm"));
                    baseViewHolder.setText(R.id.tv_time, dateStr);
                    baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //详情
                            Intent intent = new Intent(MainActivity.this, MsgDetailActivity.class);
                            intent.putExtra(MsgDetailActivity.MSG_CONTENT, msgBean.getMsg());
                            startActivity(intent);
                        }
                    });
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showLong("连接成功");
                    }
                });
                closeByUser = false;
                tryConnectCount = 0;
                isConnected = true;
            }

            @Override
            public void onMessage(final String s) {
                LogUtils.e("收到消息：" + s);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pushMsgToStatusBar(s);
                        isRefresh = true;
                        getHistory();
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                isConnected = false;
                Log.e("info", "连接关闭！");
                //TODO 重新连接
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showLong("连接断开");
                        if (tryConnectCount <= MAX_RECONNECT_COUNT && !closeByUser) {
                            tryConnectCount++;
                            ToastUtils.showLong("尝试第" + tryConnectCount + "次连接");
                            connectToServer(true);
                        }
                    }
                });
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
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.mContentTitle = "新消息";
        mBuilder.mContentText = msg;
        mBuilder.setSmallIcon(R.mipmap.ic_msg);
        mBuilder.setWhen(System.currentTimeMillis());


        mNotificationManager.notify(new Random(100000).nextInt(), mBuilder.build());
    }

    @Override
    public void onPermissionsGranted(int i, List<String> list) {
        if (i == REQUEST_GET_PERMISSION && list.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            getData();
        }
    }

    @Override
    public void onPermissionsDenied(int i, List<String> list) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
