package com.ilyaeremin.pubnubchatexample;

/**
 * Created by ereminilya on 5/3/17.
 */

public class Message {

    private String text;
    private String author;

    public Message() {
    }

    public Message(String text, String author) {
        this.text = text;
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {

        return text;
    }
}
