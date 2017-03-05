package com.ilyaeremin.pubnubchatexample;

import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Arrays;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_NAME = "top_friends_chat";

    private PubNub pubNub;
    private Gson   gson;
    private static final String USERNAME = String.valueOf(new Random().nextInt());

    @Bind(R.id.message) EditText uiTextToSend;
    @Bind(R.id.chat)    TextView uiChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initializePubnub();
        gson = new Gson();
    }

    private void initializePubnub() {
        PNConfiguration pnConfiguration = new PNConfiguration()
            .setSubscribeKey("sub-c-4b97fdc8-01d5-11e7-aba5-0619f8945a4f")
            .setPublishKey("pub-c-4ee225db-df51-48a8-8df9-bec21df7d418")
            .setReconnectionPolicy(PNReconnectionPolicy.LINEAR);
        pubNub = new PubNub(pnConfiguration);
    }

    @OnClick(R.id.enter_chat) void onEnterChatClick() {
        pubNub.subscribe().channels(Arrays.asList(CHANNEL_NAME)).execute();
        pubNub.addListener(new SubscribeCallback() {
            @Override public void status(PubNub pubnub, PNStatus status) {

            }

            @Override public void message(PubNub pubnub, PNMessageResult message) {
                final Message msg = gson.fromJson(message.getMessage(), Message.class);
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        uiChat.setText(uiChat.getText().toString() + '\n' + msg.getAuthor() + ": " + msg.getText());
                    }
                });
            }

            @Override public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });
        findViewById(R.id.enter_chat).setVisibility(View.GONE);
    }

    @OnClick(R.id.send_message) void onSendMessageClick() {
        pubNub.publish().message(new Message(uiTextToSend.getText().toString(), USERNAME))
            .channel(CHANNEL_NAME)
            .async(new PNCallback<PNPublishResult>() {
                @Override public void onResponse(PNPublishResult result, PNStatus status) {
                    Toast.makeText(MainActivity.this, "Message has been sent", Toast.LENGTH_SHORT).show();
                }
            });
        uiTextToSend.getText().clear();
    }
}
