package me.lijpeng.one.preload;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Objects;

import static me.lijpeng.one.SplashActivity.client;

/**
 * Created by ljp on 2017/6/12.
 */

public class BaseData {
    private static String pictureId;
    private static String articleId;
    private static String questionID;

    public static String getPictureId() {
        return pictureId;
    }

    public static void setPictureId(String pictureId) {
        BaseData.pictureId = pictureId;
    }

    public static String getArticleId() {
        return articleId;
    }

    public static void setArticleId(String articleId) {
        BaseData.articleId = articleId;
    }

    public static String getQuestionID() {
        return questionID;
    }

    public static void setQuestionID(String questionID) {
        BaseData.questionID = questionID;
    }

    public static int getBaseData() {
        Request getTodayContent = new Request.Builder()
                .url("http://www.wufazhuce.com")
                .get()
                .build();
        Response response;
        String result;
        try {
            response = client.newCall(getTodayContent).execute();
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        Document parse = Jsoup.parse(result);
        if (!parse.title().equals("「ONE · 一个」"))
            return -1;
        pictureId = parse.select("a[href^=http://wufazhuce.com/one/]").get(0).attr("href").trim().substring(25);
        articleId = parse.select("a[href^=http://wufazhuce.com/article/]").get(0).attr("href").trim().substring(29);
        questionID = parse.select("a[href^=http://wufazhuce.com/question/]").get(0).attr("href").trim().substring(30);
        return 0;
    }
}
