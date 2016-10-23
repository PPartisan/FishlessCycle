package com.github.ppartisan.fishlesscycle.model;

public final class Data {

    public final long id, date;
    public final double ammonia, nitrite, nitrate;
    private String note;

    public Data(long id, long date, double ammonia, double nitrite, double nitrate) {
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
