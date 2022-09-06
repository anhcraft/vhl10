package dev.anhcraft.vhvl10.objects.test;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Path;
import dev.anhcraft.config.annotations.Setting;
import dev.anhcraft.config.annotations.Validation;

import java.util.ArrayList;
import java.util.List;

@Configurable
public class TestSession {
    @Setting
    @Path("question_list")
    @Validation(notNull = true, silent = true)
    private List<Integer> questionList = new ArrayList<>();

    @Setting
    @Path("question_current")
    private int currentQuestion;

    @Setting
    @Path("question_count")
    private int questionCount;

    @Setting
    @Path("time_start")
    private long timeStart;

    @Setting
    @Path("time_end")
    private long timeEnd;

    @Setting
    @Path("score")
    private int score;

    @Deprecated
    public TestSession() {
    }

    public TestSession(long time) {
        timeStart = System.currentTimeMillis();
        this.timeEnd = timeStart + time;
    }

    public List<Integer> getQuestionList() {
        return questionList;
    }

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(int currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean nextQuestion() {
        if (questionCount < questionList.size()) {
            currentQuestion = questionList.get(questionCount);
            questionCount++;
            return true;
        }
        return false;
    }
}
