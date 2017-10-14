package web.tuhua.com.websocketapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private WebSocketClient webSocketClient;

    private boolean isConnected;//是否已经连接

    private static final int MAX_RECONNECT_COUNT = 5;//最多尝试重新连接5次

    private int tryConnectCount;//已经尝试连接的次数
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectToServer();
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

                break;
            case R.id.menu_disconnect:
                break;
        }
        return true;
    }

    /***连接到服务器*/
    private void connectToServer() {
        webSocketClient = new WebSocketClient(URI.create("ws://push.mysise.org/websocket")) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.e("info", "连接成功...");
                isConnected = true;
            }

            @Override
            public void onMessage(String s) {
                pushMsgToStatusBar(s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                isConnected = false;
                Log.e("info", "连接关闭！");

                //尝试重新连接
                if (tryConnectCount <= MAX_RECONNECT_COUNT && !isConnected) {
                    tryConnectCount++;
                    Toast.makeText(MainActivity.this, "尝试第" + tryConnectCount + "次重新连接", Toast.LENGTH_LONG).show();
                    webSocketClient.connect();
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
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
}
