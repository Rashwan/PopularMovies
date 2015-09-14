package com.example.rashwan.popularmovies;

/**
 * Created by rashwan on 9/11/15.
 */
public class Review {
    private String author;
    private String content;

    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
