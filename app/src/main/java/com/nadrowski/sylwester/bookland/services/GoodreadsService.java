package com.nadrowski.sylwester.bookland.services;

import android.util.Log;
import android.util.Xml;

import com.android.volley.Request;
import com.android.volley.Response;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nadrowski.sylwester.bookland.models.Data;
import com.nadrowski.sylwester.bookland.models.GoodreadsResponse;
import com.nadrowski.sylwester.bookland.models.SearchResponse;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
//import retrofit2.Response;


/**
 * Created by korSt on 08.12.2016.
 */

public class GoodreadsService {

    private GoodreadsServiceAPI bookServiceAPI = Data.retrofitGR.create(GoodreadsServiceAPI.class);

    public void searchBooks(String title) {
        Call<GoodreadsResponse> books = bookServiceAPI.getBooks("aqNRg8X7TIUBvboElhdg", title);
//        books.enqueue(new Callback<GoodreadsResponse>() {
//            @Override
//            public void onResponse(Call<GoodreadsResponse> call, Response<GoodreadsResponse> response) {
//
//                if (response.isSuccessful()) {
////                    BusProvider.getInstance().post(new NewBooksEvent(response));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GoodreadsResponse> call, Throwable t) {
//
//            }
//        });
    }

    public static void getSearchBook(String title, RequestQueue rq, final BookListener listener) {
        String t = title.replaceAll(" ", "%20");

        String url2Call = "https://www.goodreads.com/search.xml?key=aqNRg8X7TIUBvboElhdg&q="+ t;

        StringRequest req = new StringRequest(Request.Method.GET, url2Call, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                List<SearchResponse> result = parseCityResponse(s);
                listener.onBookResponse(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("error response", "error");
            }
        });
        rq.add(req);
    }

    private static List<SearchResponse> parseCityResponse(String response) {
        SearchResponse result = new SearchResponse();
        List<SearchResponse> responses = new ArrayList<>();
        try {
            XmlPullParser parser = Xml.newPullParser();
//            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            parser.setInput(new StringReader(response));
            Log.d("Swa", "XML Parser ok");

            String tagName = null;
            String currentTag = null;

            int event = parser.getEventType();
//           // We start parsing the XML
            while (event != XmlPullParser.END_DOCUMENT) {
                tagName = parser.getName();

                if (event == XmlPullParser.START_TAG) {
                    if (tagName.equals("work")) {
                        result = new SearchResponse();
                    }
                        currentTag = tagName;
                }
                else if (event == XmlPullParser.TEXT) {
                    // We found some text. let's see the tagName to know the tag related to the text
                    if("name".equals(currentTag)) {
                        result.setAuthor(parser.getText());
                    }
                    if ("image_url".equals(currentTag)) {
                        result.setPath(parser.getText());
                    }
                    if("title".equals(currentTag)) {
                        result.setTitle(parser.getText());
                    }


//                    cty.setCountry(parser.getText());

                    // We don't want to analyze other tag at the moment
                }
                else if (event == XmlPullParser.END_TAG) {
                    if ("work".equals(tagName)){
                        responses.add(result);

                    }
//                        Log.d("res", parser.getText());

//                        result.add(cty);
                    event = parser.next();

                }
                event = parser.next();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            Log.e("Error in getCityList", t.getMessage());
        }
        return responses;
    }

    public interface BookListener {
        void onBookResponse(List<SearchResponse> book);

    }

}
