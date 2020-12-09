package com.shashanksrikanth.multinotes;

import java.io.Serializable;
import java.util.Date;

public class Note implements Comparable<Note>, Serializable {
    private String title;
    private String text;
    private Date lastEdit;

    public Note(String title, String text) {
        this.title = title;
        this.text = text;
        this.lastEdit = new Date();
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public Date getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit (long lastTimeMilliseconds) {
        lastEdit = new Date(lastTimeMilliseconds);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int compareTo(Note n) {
        if(lastEdit.before(n.lastEdit)) return 1;
        else if (lastEdit.after(n.lastEdit)) return -1;
        else return 0;
    }
}
