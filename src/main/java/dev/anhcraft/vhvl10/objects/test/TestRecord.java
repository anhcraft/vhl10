package dev.anhcraft.vhvl10.objects.test;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Path;
import dev.anhcraft.config.annotations.Setting;

@Configurable
public class TestRecord {
    @Setting
    @Path("score")
    private int score;

    @Setting
    @Path("time")
    private long time;

    @Setting
    @Path("date")
    private long date;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
