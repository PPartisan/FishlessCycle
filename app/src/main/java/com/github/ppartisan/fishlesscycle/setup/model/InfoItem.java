package com.github.ppartisan.fishlesscycle.setup.model;

import android.support.annotation.Nullable;

public abstract class InfoItem {

    public final String content;

    public abstract boolean isHeader();
    public abstract boolean isChild();
    public abstract boolean hasChild();
    public abstract Child getChild();

    private InfoItem(String content) {
        this.content = content;
    }

    public static class ParentBuilder {

        private final String content;
        private Child child;

        public ParentBuilder(String content) {
            this.content = content;
        }

        public ParentBuilder setChild(String childContent) {
            this.child = new Child(childContent);
            return this;
        }

        public Parent build() {
            return new Parent(content, child);
        }

    }

    public static final class Child extends InfoItem {

        private boolean isVisible = false;

        private Child(String content) {
            super(content);
        }

        @Override
        public boolean isHeader() {
            return false;
        }

        @Override
        public boolean isChild() {
            return true;
        }

        @Override
        public boolean hasChild() {
            return false;
        }

        @Override
        public Child getChild() {
            return null;
        }

        public boolean isVisible() {
            return isVisible;
        }

        public void setVisible(boolean isVisible) {
            this.isVisible = isVisible;
        }

    }

    private static final class Parent extends InfoItem {

        @Nullable private final Child mChild;

        private Parent(String content, @Nullable Child child) {
            super(content);
            this.mChild = child;
        }

        @Override
        public boolean isHeader() {
            return false;
        }

        @Override
        public boolean isChild() {
            return false;
        }

        @Override
        public boolean hasChild() {
            return (mChild != null);
        }

        public Child getChild() {
            return mChild;
        }

    }

    public static final class Header extends InfoItem {

        public Header(String content) {
            super(content);
        }

        @Override
        public boolean isHeader() {
            return true;
        }

        @Override
        public boolean isChild() {
            return false;
        }

        @Override
        public boolean hasChild() {
            return false;
        }

        @Override
        public Child getChild() {
            return null;
        }
    }

}
