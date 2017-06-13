package me.lijpeng.one.util.response.picture;

/**
 * Created by ljp on 2017/5/26.
 */

public class PictureResponse {
    private String res;
    private PictureDetailResponse data;

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public PictureDetailResponse getData() {
        return data;
    }

    public void setData(PictureDetailResponse data) {
        this.data = data;
    }
}
