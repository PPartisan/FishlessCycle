package com.github.ppartisan.fishlesscycle.model;

public final class Data {

    public final long id, date;
    public final float ammonia, nitrite, nitrate;
    private String note;

    public Data(long id, long date, float ammonia, float nitrite, float nitrate) {
        this.id = id;
        this.date = date;
        this.ammonia = ammonia;
        this.nitrite = nitrite;
        this.nitrate = nitrate;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

}
