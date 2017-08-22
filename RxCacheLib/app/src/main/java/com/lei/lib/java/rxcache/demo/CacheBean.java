package com.lei.lib.java.rxcache.demo;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rymyz on 2017/8/22.
 */

public class CacheBean {
    private String name;
    private long time;
    private List<String> hobbies;
    private int[] scores;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    public int[] getScores() {
        return scores;
    }

    public void setScores(int[] scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "CacheBean{" +
                "name='" + name + '\'' +
                ", time=" + time +
                ", hobbies=" + hobbies +
                ", scores=" + Arrays.toString(scores) +
                '}';
    }
}
