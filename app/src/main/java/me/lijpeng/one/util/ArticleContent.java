package me.lijpeng.one.util;

import android.graphics.Bitmap;

/**
 * Created by ljp on 2017/5/27.
 */

public class ArticleContent {
    private String title;
    private String subTitle;    //不一定有
    private String author;
    private String authorInfo;  //不一定有
    private String guideWord;
    private String authorWeibo; //不一定有
    private String editorInfo;
    private String articleContent;
    private String copyright;   //转载来源，不一定有
    private Bitmap authorPhoto;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorInfo() {
        return authorInfo;
    }

    public void setAuthorInfo(String authorInfo) {
        this.authorInfo = authorInfo;
    }

    public String getGuideWord() {
        return guideWord;
    }

    public void setGuideWord(String guideWord) {
        this.guideWord = guideWord;
    }

    public String getAuthorWeibo() {
        return authorWeibo;
    }

    public void setAuthorWeibo(String authorWeibo) {
        this.authorWeibo = authorWeibo;
    }

    public String getEditorInfo() {
        return editorInfo;
    }

    public void setEditorInfo(String editorInfo) {
        this.editorInfo = editorInfo;
    }

    public String getArticleContent() {
        return articleContent;
    }

    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public Bitmap getAuthorPhoto() {
        return authorPhoto;
    }

    public void setAuthorPhoto(Bitmap authorPhoto) {
        this.authorPhoto = authorPhoto;
    }
}
