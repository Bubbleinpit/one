package me.lijpeng.one.util;

import android.graphics.Bitmap;

/**
 * Created by 李计芃 on 2017/5/26.
 */

public class OneContent {
    private String pictureAuthor;
    private String oneText;
    private String vol;
    private String time;
    private Bitmap bitmap;

    public String getPictureAuthor() {
        return pictureAuthor;
    }

    public void setPictureAuthor(String pictureAuthor) {
        this.pictureAuthor = pictureAuthor;
    }

    public String getOneText() {
        return oneText;
    }

    public void setOneText(String oneText) {
        this.oneText = oneText;
    }

    public String getVol() {
        return vol;
    }

    public void setVol(String vol) {
        this.vol = vol;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
