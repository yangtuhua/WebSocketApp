package web.tuhua.com.websocketapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by yangtufa on 2017/10/16.
 */

public class MsgDetailActivity extends AppCompatActivity {

    public static final String MSG_CONTENT = "msg_content";

    private TextView tvContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_detail);
        tvContent = (TextView) findViewById(R.id.tv_content);
        getIntentData();
    }

    private void getIntentData() {
        String content = getIntent().getStringExtra(MSG_CONTENT);
        tvContent.setText(content);
        tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
