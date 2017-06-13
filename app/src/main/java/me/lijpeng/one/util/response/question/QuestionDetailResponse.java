package me.lijpeng.one.util.response.question;

/**
 * Created by ljp on 2017/6/13.
 */

public class QuestionDetailResponse {
    private String question_title;
    private String question_content;
    private String answer_content;
    private String charge_edt;
    private String charge_email;
    private AskerOrAnswerer answerer;
    private AskerOrAnswerer asker;

    public String getQuestion_title() {
        return question_title;
    }

    public void setQuestion_title(String question_title) {
        this.question_title = question_title;
    }

    public String getQuestion_content() {
        return question_content;
    }

    public void setQuestion_content(String question_content) {
        this.question_content = question_content;
    }

    public String getAnswer_content() {
        return answer_content;
    }

    public void setAnswer_content(String answer_content) {
        this.answer_content = answer_content;
    }

    public String getCharge_edt() {
        return charge_edt;
    }

    public void setCharge_edt(String charge_edt) {
        this.charge_edt = charge_edt;
    }

    public String getCharge_email() {
        return charge_email;
    }

    public void setCharge_email(String charge_email) {
        this.charge_email = charge_email;
    }

    public AskerOrAnswerer getAnswerer() {
        return answerer;
    }

    public void setAnswerer(AskerOrAnswerer answerer) {
        this.answerer = answerer;
    }

    public AskerOrAnswerer getAsker() {
        return asker;
    }

    public void setAsker(AskerOrAnswerer asker) {
        this.asker = asker;
    }
}
