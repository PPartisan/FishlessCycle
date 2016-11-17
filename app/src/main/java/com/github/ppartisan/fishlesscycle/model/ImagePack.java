package com.github.ppartisan.fishlesscycle.model;

public final class ImagePack {

    private final CharSequence[] titles, paths;

    public ImagePack(CharSequence[] titles, CharSequence[] paths) {
        this.titles = titles;
        this.paths = paths;
    }

    public CharSequence[] getTitles() {
        return titles;
    }

    public CharSequence[] getPaths() {
        return paths;
    }

}
