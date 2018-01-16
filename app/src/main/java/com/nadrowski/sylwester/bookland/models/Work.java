package com.nadrowski.sylwester.bookland.models;

import org.simpleframework.xml.Element;

/**
 * Created by korSt on 08.12.2016.
 */

public class Work {

    public Work() {

    }

    public int getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(int averageRating) {
        this.averageRating = averageRating;
    }

    @Element(name = "average_rating")
    int averageRating;

}
