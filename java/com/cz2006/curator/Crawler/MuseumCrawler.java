package com.cz2006.curator.Crawler;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.cz2006.curator.Constants.MuseumConstants;
import com.cz2006.curator.Objects.Museum;
import com.cz2006.curator.Objects.Review;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * MuseumCrawler is a class for crawling museum detail from JSON file of Google Place API.
 * The class implements MuseumCrawlerInterface as interface between manager to this class.
 * The class send JSON request through Google Place API and extract specific data.
 * One museum is saved in one Museum object.
 * The process run in asynchronous manner.
 */
public class MuseumCrawler extends AsyncTask<String, Void, Museum> implements MuseumCrawlerInterface {

    /**
     * This is an object for specifying callback function
     * when a crawler finish processing data.
     */
    public AsyncRespond asyncRespond;
    private GoogleApiClient mGoogleApiClient;
    private String placeId;

    /**
     * This is a constructor of MuseumCrawler
     * @param placeId This is place id of a museum
     * @param mGoogleApiClient This is client class of Google Places API
     * @param asyncRespond This is a class for specifying callback function
     */
    public MuseumCrawler(String placeId, GoogleApiClient mGoogleApiClient, AsyncRespond asyncRespond) {
        this.asyncRespond = asyncRespond;
        this.placeId = placeId;
        this.mGoogleApiClient = mGoogleApiClient;
    }

    /**
     * This method is for starting fetching and processing data in a crawler.
     * This process run in asynchronous manner.
     */
    @Override
    public void refresh() {
        this.execute(MuseumConstants.URL + placeId);
    }

    @Override
    protected Museum doInBackground(String... params) {

        Museum museum = null;

        String jsonStr = getJSONFromUrl(params[0]);

        JSONObject jsonObject = null;
        JSONObject result = null;
        String address = null;
        String phone = null;
        Double rating = 0.0;
        String name = null;
        JSONObject openingHoursObject = null;
        JSONArray weekdayText = null;
        ArrayList<String> openingHours = null;
        Bitmap image = null;
        JSONArray reviews = null;
        ArrayList<Review> reviewList = null;
        String museumUrl = null;


        if(jsonStr != null) {
            try {
                jsonObject = new JSONObject(jsonStr);
            } catch(Exception e) {
                jsonObject = null;
            }
            result = getJsonObject(jsonObject, "result");

            address = getString(result, "formatted_address");
            phone = getString(result, "formatted_phone_number");
            rating = getDouble(result, "rating");
            name = getString(result, "name");
            openingHoursObject = getJsonObject(result, "opening_hours");
            weekdayText = getJsonArray(openingHoursObject, "weekday_text");
            openingHours = new ArrayList<>();

            int openingLength = 0;

            if(weekdayText != null) {
                openingLength = weekdayText.length();
            }

            for(int i = 0; i < openingLength; i++) {
                openingHours.add(getString(weekdayText, i));
            }

            image = getImage();

            reviews = getJsonArray(result, "reviews");
            reviewList = new ArrayList<>();

            int reviewLength = 0;
            if(reviews != null) {
                reviewLength = reviews.length();
            }

            for(int i = 0; i < reviewLength; i++) {
                JSONObject review = getJsonObject(reviews, i);

                String authorName = getString(review, "author_name");
                Double ratingReview = getDouble(review, "rating");
                String text = getString(review, "text");

                Long dateInSecond = getLong(review,"time");
                Calendar calendar = Calendar.getInstance();
                Date date = null;
                if(dateInSecond != null) {
                    calendar.setTimeInMillis(dateInSecond*1000);
                    date = calendar.getTime();
                }

                reviewList.add(new Review(authorName, date, ratingReview, text
                ));
            }

            museumUrl = getString(result, "website");


            Log.e("MuseumCrawler", name);
            Log.e("MuseumCrawler", address);
            Log.e("MuseumCrawler", phone);
            Log.e("MuseumCrawler", rating + "");
            //Log.e("MuseumCrawler", openingHours.get(0));
//            Log.e("MuseumCrawler", image.getHeight()+"" );
            Log.e("MuseumCrawler", reviewList.toString());
            Log.e("MuseumCrawler", museumUrl);

            if(rating == null)
                rating = 0.0;

            museum = new Museum(
                    name,
                    0.0,
                    0.0,
                    address,
                    rating,
                    openingHours,
                    phone
                    , "", "", museumUrl, reviewList, image,""
            );
        }

        return museum;
    }

    @Override
    protected void onPostExecute(Museum museum) {
        asyncRespond.processFinish(museum);
    }

    private String getJSONFromUrl(String add) {

        String json = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;

        try {
            // defaultHttpClient
            URL url = new URL(add);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.getResponseCode();

            is = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String inputLine = "";

            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }

            json = sb.toString();

        } catch (UnsupportedEncodingException e) {
            Log.e("MuseumCrawler", e.getMessage());
        } catch (IOException e) {
            Log.e("MuseumCrawler", e.getMessage());
        } catch (Exception e) {
            Log.e("MuseumCrawler", e.getMessage());
        }
        // return json
        return json;
    }

    private Bitmap getImage() {

        Bitmap photoBitmap = null;

        PlacePhotoMetadataResult result = Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId).await();

        if(result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            if(photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                photoBitmap = photo.getPhoto(mGoogleApiClient).await().getBitmap();
            }
            photoMetadataBuffer.release();
        }

        return photoBitmap;
    }

    private JSONObject getJsonObject(JSONObject jsonObject, String s) {
        try {
            return jsonObject.getJSONObject(s);
        }catch (Exception e) {
            return null;
        }
    }

    private JSONArray getJsonArray(JSONObject jsonObject, String s) {
        try {
            return jsonObject.getJSONArray(s);
        } catch(Exception e) {
            return null;
        }
    }

    private String getString(JSONObject jsonObject, String s) {
        try {
            return jsonObject.getString(s);
        } catch (Exception e) {
            return null;
        }
    }

    private Double getDouble(JSONObject jsonObject, String s) {
        try {
            return jsonObject.getDouble(s);
        }catch (Exception e) {
            return null;
        }
    }

    private String getString(JSONArray jsonArray, int i) {
        try {
            return jsonArray.getString(i);
        }catch (Exception e) {
            return null;
        }
    }

    private  JSONObject getJsonObject(JSONArray jsonArray, int i) {
        try {
            return jsonArray.getJSONObject(i);
        } catch(Exception e) {
            return null;
        }
    }

    private Long getLong(JSONObject jsonObject, String s) {
        try {
            return jsonObject.getLong(s);
        }catch(Exception e) {
            return null;
        }
    }

}
