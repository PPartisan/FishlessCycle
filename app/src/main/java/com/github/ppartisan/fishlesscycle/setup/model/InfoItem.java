package com.github.ppartisan.fishlesscycle.setup.model;

import android.support.annotation.Nullable;

public abstract class InfoItem {

    public final String content;
    public final boolean isOptional;

    public abstract boolean isHeader();
    public abstract boolean isChild();
    public abstract boolean hasChild();
    public abstract Child getChild();

    public InfoItem(String content, boolean isOptional) {
        this.content = content;
        this.isOptional = isOptional;
    }

    public static Parent buildParent(String parentContent, boolean isParentOptional, String childContent, boolean isOptional) {
        return new Parent(parentContent, isOptional, new Child(childContent, isOptional));
    }

    public static class ParentBuilder {

        private final String content;
        private final boolean isOptional;
        private Child child;

        public ParentBuilder(String content, boolean isOptional) {
            this.content = content;
            this.isOptional = isOptional;
        }

        public ParentBuilder setChild(Child child) {
            this.child = child;
            return this;
        }

        public ParentBuilder setChild(String childContent, boolean isChildOptional) {
            this.child = new Child(childContent, isChildOptional);
            return this;
        }

        public Parent build() {
            return new Parent(content, isOptional, child);
        }

    }

    public static final class Child extends InfoItem {

        private boolean isVisible = false;

        private Child(String content, boolean isOptional) {
            super(content, isOptional);
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

        private Parent(String content, boolean isOptional, @Nullable Child child) {
            super(content, isOptional);
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
            super(content, false);
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
