package me.lijpeng.one.util.response.article;

/**
 * Created by ljp on 2017/5/28.
 */

public class ArticleResponse {
    private String res;
    private ArticleDetailResponse data;

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public ArticleDetailResponse getData() {
        return data;
    }

    public void setData(ArticleDetailResponse data) {
        this.data = data;
    }
}
