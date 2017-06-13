package me.lijpeng.one.util.response.article;

/**
 * Created by ljp on 2017/5/28.
 */

public class ArticleDetailResponse {
    private String hp_title;    //标题
    private String sub_title;   //副标题
    private String hp_author;   //作者名
    private String auth_it;     //作者签名/简介
    private String hp_author_introduce; //责任编辑信息
    private String hp_content;  //webview内容
    private String guide_word;  //导语
    private String editor_email;    // 责任编辑电子邮箱
    private String copyright;   //转载声明
    private ArticleAuthorDetail[] author;   //作者详细信息

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

    public String getGuide_word() {
        return guide_word;
    }

    public void setGuide_word(String guideWord) {
        this.guide_word = guideWord;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public ArticleAuthorDetail[] getAuthor() {
        return author;
    }

    public void setAuthor(ArticleAuthorDetail[] authorDetail) {
        this.author = authorDetail;
    }

    public String getEditor_email() {
        return editor_email;
    }

    public void setEditor_email(String editor_email) {
        this.editor_email = editor_email;
    }
}
