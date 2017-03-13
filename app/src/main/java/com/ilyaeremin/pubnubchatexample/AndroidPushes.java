package com.ilyaeremin.pubnubchatexample;

/**
 * Created by ereminilya on 13/3/17.
 */
class AndroidPushes {

    private Wrapper data;

    public AndroidPushes() {
    }

    public AndroidPushes(String title, String body) {
        this.data = new Wrapper();
        this.data.title = title;
        this.data.body = body;
    }

    public static class Wrapper {
        private String title;
        private String body;
    }
}