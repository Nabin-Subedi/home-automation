package com.example.bishal.smartoutlet;

/**
 * Created by bishal on 2/8/17.
 */


public class Post {

    public String status;
    public String temperature;
    public String timestamp;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String st, String te, String time) {
        this.status = st;
        this.temperature = te;

        this.timestamp = time;
    }
}
