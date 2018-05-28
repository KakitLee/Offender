package com.project.zhi.tigerapp.FaceUtils;

import android.support.annotation.NonNull;

import com.project.zhi.tigerapp.Entities.Entities;

import java.io.Serializable;

public class MatchedImage implements Comparable<MatchedImage> {
    private Float score;
    private String image;

    public MatchedImage(Float score, String image){
        this.score = score;
        this.image = image;
    }

    public Float getScore() {
        return this.score;
    }

    public String getImage() {
        return this.image;
    }

    @Override
    public int compareTo(MatchedImage o) {
       if(this.score<o.getScore()){
                return -1;

        }
        else{
            return 1;
        }
        //return (int)(compareSocre - this.score);

    }

    @Override
    public String toString() {
        return "image=" + this.image + ", score=" + this.score;
    }


}
