package me.lijpeng.one.util.response.article;

/**
 * Created by ljp on 2017/5/28.
 */

public class ArticleDetailResponse {
    private String res;
    private ArticleDataInDetailResponse data;

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public ArticleDataInDetailResponse getData() {
        return data;
    }

    public void setData(ArticleDataInDetailResponse data) {
        this.data = data;
    }
}
