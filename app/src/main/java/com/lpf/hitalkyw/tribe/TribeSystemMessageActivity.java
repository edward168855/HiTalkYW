package com.lpf.hitalkyw.tribe;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.conversation.IYWMessageListener;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWSystemConversation;
import com.alibaba.mobileim.lib.model.message.YWSystemMessage;
import com.alibaba.mobileim.tribe.IYWTribeService;
import com.lpf.hitalkyw.R;
import com.lpf.hitalkyw.helper.LoginSampleHelper;

import java.util.ArrayList;
import java.util.List;

public class TribeSystemMessageActivity extends Activity {

    private YWIMKit mIMKit;
    private TribeSystemMessageAdapter mAdapter;
    private ListView mListView;
    private IYWTribeService mTribeService;
    private YWSystemConversation mConversation;
    private List<YWMessage> mList = new ArrayList<YWMessage>();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_system_message);
        init();
    }

    private void init(){
        mIMKit = LoginSampleHelper.getInstance().getIMKit();
        mTribeService = mIMKit.getTribeService();
        mConversation = mIMKit.getConversationService().getSystemConversation();
        mIMKit.getConversationService().markReaded(mConversation);
        initTitle();
        mList = mConversation.getMessageLoader().loadMessage(20, null);
        mListView = (ListView) findViewById(R.id.message_list);
        mAdapter = new TribeSystemMessageAdapter(this, mList);
        mListView.setAdapter(mAdapter);
        //添加新消息到达监听,监听到有新消息到达的时候或者消息类别有变更的时候应该更新adapter
        mConversation.getMessageLoader().addMessageListener(mMessageListener);
    }

    private void initTitle() {
        RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.title_bar);
        titleBar.setBackgroundColor(Color.parseColor("#00b4ff"));
        titleBar.setVisibility(View.VISIBLE);

        TextView titleView = (TextView) findViewById(R.id.title_self_title);
        TextView leftButton = (TextView) findViewById(R.id.left_button);
        leftButton.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.demo_common_back_btn_white, 0, 0, 0);
        leftButton.setTextColor(Color.WHITE);
        leftButton.setText("返回");
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleView.setTextColor(Color.WHITE);
        titleView.setText("群系统消息");


        TextView rightButton = (TextView) findViewById(R.id.right_button);
        rightButton.setText("清空");
        rightButton.setTextColor(Color.WHITE);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConversation.getMessageLoader().deleteAllMessage();
            }
        });
    }

    private void refreshAdapter(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.refreshData(mList);
            }
        });
    }

    IYWMessageListener mMessageListener = new IYWMessageListener() {
        @Override
        public void onItemUpdated() {  //消息列表变更，例如删除一条消息，修改消息状态，加载更多消息等等
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChangedWithAsyncLoad();
                }
            });
        }

        @Override
        public void onItemComing() { //收到新消息
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChangedWithAsyncLoad();
                }
            });
        }

        @Override
        public void onInputStatus(byte status) {

        }
    };

    public void acceptToJoinTribe(final YWMessage message) {
        final YWSystemMessage msg = (YWSystemMessage) message;
        mConversation.accept(msg, new IWxCallback() {
            @Override
            public void onSuccess(Object... result) {
                refreshAdapter();
            }

            @Override
            public void onError(int code, String info) {

            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    public void refuseToJoinTribe(YWMessage message) {
        final YWSystemMessage msg = (YWSystemMessage) message;
        mConversation.reject(msg, new IWxCallback() {
            @Override
            public void onSuccess(Object... result) {
                refreshAdapter();
            }

            @Override
            public void onError(int code, String info) {

            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        mConversation.getMessageLoader().removeMessageListener(mMessageListener);
        super.onDestroy();
    }
}