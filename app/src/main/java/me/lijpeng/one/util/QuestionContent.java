package me.lijpeng.one.util;
/*
 * Created by ljp on 2017/6/13.
 */

import android.graphics.Bitmap;

public class QuestionContent {
    private String questionTitle;
    private String questionContent;
    private String answerContent;
    private String editorInfo;
    private String askerName;
    private String answererName;
    private Bitmap askerHead;
    private Bitmap answererHead;

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public String getEditorInfo() {
        return editorInfo;
    }

    public void setEditorInfo(String editorInfo) {
        this.editorInfo = editorInfo;
    }

    public String getAskerName() {
        return askerName;
    }

    public void setAskerName(String askerName) {
        this.askerName = askerName;
    }

    public String getAnswererName() {
        return answererName;
    }

    public void setAnswererName(String answererName) {
        this.answererName = answererName;
    }

    public Bitmap getAskerHead() {
        return askerHead;
    }

    public void setAskerHead(Bitmap askerHead) {
        this.askerHead = askerHead;
    }

    public Bitmap getAnswererHead() {
        return answererHead;
    }

    public void setAnswererHead(Bitmap answererHead) {
        this.answererHead = answererHead;
    }
}
