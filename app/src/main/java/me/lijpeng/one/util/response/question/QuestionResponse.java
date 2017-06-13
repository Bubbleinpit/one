package me.lijpeng.one.util.response.question;

/**
 * Created by ljp on 2017/6/13.
 */

public class QuestionResponse {
    private String res;
    private QuestionDetailResponse data;

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public QuestionDetailResponse getData() {
        return data;
    }

    public void setData(QuestionDetailResponse data) {
        this.data = data;
    }
}
