package model;

import java.time.LocalDate;

public class Answer {
    private int id;
    private int userId;
    private int questionId;
    private int answerValue;
    private LocalDate answerDate;

    public Answer() {}

    public Answer(int userId, int questionId, int answerValue) {
        this.userId = userId;
        this.questionId = questionId;
        this.answerValue = answerValue;
        this.answerDate = LocalDate.now();
    }

    public Answer(int userId, int questionId, int answerValue, LocalDate answerDate) {
        this.userId = userId;
        this.questionId = questionId;
        this.answerValue = answerValue;
        this.answerDate = answerDate;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getUserId() {return userId;}
    public void setUserId(int userId) {this.userId = userId;}

    public int getQuestionId() {return questionId;}
    public void setQuestionId(int questionId) {this.questionId = questionId;}

    public int getAnswerValue() {return answerValue;}
    public void setAnswerValue(int answerValue) {this.answerValue = answerValue;}

    public LocalDate getAnswerDate() {return answerDate;}
    public void setAnswerDate(LocalDate answerDate) {this.answerDate = answerDate;}

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", userId=" + userId +
                ", questionId=" + questionId +
                ", answerValue=" + answerValue +
                ", answerDate=" + answerDate +
                '}';
    }
}