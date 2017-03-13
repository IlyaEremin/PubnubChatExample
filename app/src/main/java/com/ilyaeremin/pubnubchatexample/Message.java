package com.ilyaeremin.pubnubchatexample;

/**
 * Created by ereminilya on 5/3/17.
 */

public class Message {

    private String text;
    private String author;

    private AndroidPushes pn_gcm;

    public Message() {
    }

    public Message(String text, String author) {
        this.text = text;
        this.author = author;
        this.pn_gcm = new AndroidPushes("New Incomming Message suchek", author + ": " + text);
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {

        return text;
    }

}
