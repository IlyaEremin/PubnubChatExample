package com.ilyaeremin.pubnubchatexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNPushType;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;
import com.pubnub.api.models.consumer.push.PNPushRemoveChannelResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_NAME_TOP      = "top_chat";
    private static final String CHANNEL_NAME_TAK_SEBE = "tak_sebe_chat";

    private String  currentChatId;
    private boolean userInChat;

    private PubNub pubNub;
    private Gson   gson;
    private static final String USERNAME = String.valueOf(new Random().nextInt());

    @Bind(R.id.message)        EditText uiTextToSend;
    @Bind(R.id.chat)           TextView uiChat;
    @Bind(R.id.enter_top_chat) View     uiTopChat;
    @Bind(R.id.enter_tak_sebe) View     uiTakSebeChat;
    @Bind(R.id.comment_bar)    View     uiCommentBar;
    private                    MenuItem uiLeaveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initializePubnub();
        gson = new Gson();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        uiLeaveBtn = menu.findItem(R.id.leave_chat);
        uiLeaveBtn.setVisible(false);
        uiLeaveBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override public boolean onMenuItemClick(MenuItem item) {
                leaveChat();
                return true;
            }
        });
        return true;
    }

    private void leaveChat() {
        pubNub.removePushNotificationsFromChannels()
            .channels(Arrays.asList(currentChatId))
            .deviceId(FirebaseInstanceId.getInstance().getToken())
            .pushType(PNPushType.GCM)
            .async(new PNCallback<PNPushRemoveChannelResult>() {
                @Override
                public void onResponse(PNPushRemoveChannelResult result, PNStatus status) {
                    Log.i("PUSH removed", result + " " + status);
                }
            });
        pubNub.unsubscribeAll();
        uiChat.setText("");
        uiTopChat.setVisibility(View.VISIBLE);
        uiTakSebeChat.setVisibility(View.VISIBLE);
        uiCommentBar.setVisibility(View.GONE);
        uiChat.setVisibility(View.VISIBLE);
        setTitle("Choose chat");
    }


    private void initializePubnub() {
        PNConfiguration pnConfiguration = new PNConfiguration()
            .setSubscribeKey("sub-c-4b97fdc8-01d5-11e7-aba5-0619f8945a4f")
            .setPublishKey("pub-c-4ee225db-df51-48a8-8df9-bec21df7d418")
            .setReconnectionPolicy(PNReconnectionPolicy.LINEAR);
        pubNub = new PubNub(pnConfiguration);
        pubNub.addListener(new SubscribeCallback() {
            @Override public void status(PubNub pubnub, PNStatus status) {
            }

            @Override public void message(PubNub pubnub, PNMessageResult message) {
                final Message msg = gson.fromJson(message.getMessage().getAsJsonObject().get("message"), Message.class);
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        uiChat.setText(uiChat.getText().toString() + '\n' + msg.getAuthor() + ": " + msg.getText());
                    }
                });
            }

            @Override public void presence(PubNub pubnub, PNPresenceEventResult presence) {
            }
        });
    }

    @OnClick(R.id.enter_top_chat) void onEnterChatClick() {
        enterChat(CHANNEL_NAME_TOP);
    }

    @OnClick(R.id.enter_tak_sebe) void onTakSebeClick() {
        enterChat(CHANNEL_NAME_TAK_SEBE);
    }

    private void enterChat(String chatId) {
        this.currentChatId = chatId;
        this.userInChat = true;
        uiLeaveBtn.setVisible(true);
        pubNub.addPushNotificationsOnChannels()
            .channels(Arrays.asList(currentChatId))
            .pushType(PNPushType.GCM)
            .deviceId(FirebaseInstanceId.getInstance().getToken())
            .async(new PNCallback<PNPushAddChannelResult>() {
                @Override public void onResponse(PNPushAddChannelResult result, PNStatus status) {
                    Log.i("PUSHES ADDED RESULT", "add pushes: " + result + status);
                }
            });
        pubNub.subscribe().channels(Arrays.asList(currentChatId)).execute();
        uiTopChat.setVisibility(View.GONE);
        uiTakSebeChat.setVisibility(View.GONE);
        uiCommentBar.setVisibility(View.VISIBLE);
        uiChat.setVisibility(View.VISIBLE);
        setTitle(currentChatId);
    }

    @OnClick(R.id.send_message) void onSendMessageClick() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("pn_gcm", new AndroidPushes("New Incoming Message", USERNAME + ": " + uiTextToSend.getText().toString()));
        payload.put("message", new Message(uiTextToSend.getText().toString(), USERNAME));
        pubNub.publish()
            .message(payload)
            .channel(currentChatId)
            .async(new PNCallback<PNPublishResult>() {
                @Override public void onResponse(PNPublishResult result, PNStatus status) {
                    Toast.makeText(MainActivity.this, "Message has been sent", Toast.LENGTH_SHORT).show();
                    Log.i("MSG SENT", result + " " + status);
                }
            });
        uiTextToSend.getText().clear();
    }
}
