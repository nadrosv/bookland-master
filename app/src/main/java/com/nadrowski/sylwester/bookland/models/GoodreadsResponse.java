package com.nadrowski.sylwester.bookland.models;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by korSt on 08.12.2016.
 */

@Root(strict=false)
public class GoodreadsResponse {

    public GoodreadsResponse() {

    }

    @ElementList
    @Path("GoodreadsResponse/search")
    private List<Work> results;

    public List<Work> getResults() {
        return results;
    }

    public void setResults(List<Work> results) {
        this.results = results;
    }
}
