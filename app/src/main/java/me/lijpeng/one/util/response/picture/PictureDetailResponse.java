package me.lijpeng.one.util.response.picture;

/**
 * Created by ljp on 2017/5/26.
 */

public class PictureDetailResponse {
    private String res;
    private PictureDataInDetailResponse data;

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public PictureDataInDetailResponse getData() {
        return data;
    }

    public void setData(PictureDataInDetailResponse data) {
        this.data = data;
    }
}
