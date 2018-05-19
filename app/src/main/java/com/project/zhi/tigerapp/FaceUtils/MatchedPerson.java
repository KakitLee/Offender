package com.project.zhi.tigerapp.FaceUtils;

import com.project.zhi.tigerapp.Entities.Entities;

import java.io.Serializable;

public class MatchedPerson implements Comparable<MatchedPerson>, Serializable {
    private float score;
    private Entities entity;

    public MatchedPerson(float score,Entities entity){
        this.score = score;
        this.entity = entity;
    }

    public float getScore() {
        return score;
    }

    public Entities getEntity() {
        return entity;
    }


    public int compareTo(MatchedPerson comparePerson) {
        float compareSocre=((MatchedPerson)comparePerson).getScore();
        /* For Ascending order*/
        return (int)(this.score-compareSocre);

    }

    @Override
    public String toString() {
        return "entity=" + this.entity.getId() + ", score=" + this.score;
    }

}
