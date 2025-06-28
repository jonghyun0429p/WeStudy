package com.westudy.post.enums;

public enum PostCategory {
    FREE("자유"),
    STUDY("스터디"),
    QNA("질문"),
    NOTICE("공지사항");

    private final String category;

    PostCategory(String category){
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}
