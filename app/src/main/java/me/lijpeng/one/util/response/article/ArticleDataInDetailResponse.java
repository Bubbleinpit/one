package me.lijpeng.one.util.response.article;

import me.lijpeng.one.util.ArticleAuthorDetail;

/**
 * Created by ljp on 2017/5/28.
 */

public class ArticleDataInDetailResponse {
    private String hp_title;
    private String sub_title;
    private String hp_author;
    private String auth_it;
    private String hp_author_introduce;
    private String hp_content;
    private String guideWord;
    private String copyright;
    private ArticleAuthorDetail[] authorDetail;

    public String getHp_title() {
        return hp_title;
    }

    public void setHp_title(String hp_title) {
        this.hp_title = hp_title;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getHp_author() {
        return hp_author;
    }

    public void setHp_author(String hp_author) {
        this.hp_author = hp_author;
    }

    public String getAuth_it() {
        return auth_it;
    }

    public void setAuth_it(String auth_it) {
        this.auth_it = auth_it;
    }

    public String getHp_author_introduce() {
        return hp_author_introduce;
    }

    public void setHp_author_introduce(String hp_author_introduce) {
        this.hp_author_introduce = hp_author_introduce;
    }

    public String getHp_content() {
        return hp_content;
    }

    public void setHp_content(String hp_content) {
        this.hp_content = hp_content;
    }

    public String getGuideWord() {
        return guideWord;
    }

    public void setGuideWord(String guideWord) {
        this.guideWord = guideWord;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public ArticleAuthorDetail[] getAuthorDetail() {
        return authorDetail;
    }

    public void setAuthorDetail(ArticleAuthorDetail[] authorDetail) {
        this.authorDetail = authorDetail;
    }
}
