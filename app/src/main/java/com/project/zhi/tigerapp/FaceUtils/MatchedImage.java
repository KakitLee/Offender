package com.project.zhi.tigerapp.FaceUtils;

import com.project.zhi.tigerapp.Entities.Entities;

import java.io.Serializable;

public class MatchedImage implements Comparable<MatchedImage> {
    private float score;
    private String image;

    public MatchedImage(float score, String image){
        this.score = score;
        this.image = image;
    }

    public float getScore() {
        return this.score;
    }

    public String getImage() {
        return this.image;
    }


    public int compareTo(MatchedImage comparePerson) {
        float compareSocre=((MatchedImage)comparePerson).getScore();
        /* For Ascending order*/
        return (int)(this.score-compareSocre);

    }

    @Override
    public String toString() {
        return "image=" + this.image + ", score=" + this.score;
    }

}
