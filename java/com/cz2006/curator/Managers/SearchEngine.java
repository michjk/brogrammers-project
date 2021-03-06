package com.cz2006.curator.Managers;

import com.cz2006.curator.Objects.Museum;
import com.cz2006.curator.Objects.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * SearchEngine is a class to control and manage SearchUI. The class use data from PlaceCrawler to
 * generate a list of museum with filtering feature and sorting feature.
 */
public class SearchEngine{
    //attribute(s)
    private ArrayList<Museum> result;

    //user
    private User user;

    //constructor
    public SearchEngine(ArrayList<Museum> arr, User u) {
        user = u;
        result = arr;
    }

    public SearchEngine() {
    }

    //getters & setters
    public ArrayList<Museum> getResult() {
        return result;
    }

    public void setResult(ArrayList<Museum> result) {
        this.result = result;
    }

    /**
     * This method is to sort list of museum based on proximity.
     * @return a sorted museum based on proximity.
     */
    public ArrayList<Museum> byProximity() {
        Collections.sort(result,new CmpByProximity());
        return result;
    }

    /**
     * This method is to sort list of museum based on rating.
     * @return a sorted museum based on rating.
     */
    public ArrayList<Museum> byRating() {
        Collections.sort(result, new CmpByRating());
        return result;
    }

    /**
     * This method is for filtering list of museum based on key words.
     * @param arr array of museum that need to be filtered.
     * @param q key words for filtering.
     * @return filtered array list of museum.
     */
    public ArrayList<Museum> filter(ArrayList<Museum> arr, String q){
        ArrayList<Museum> ret = new ArrayList<>();
        for(Museum m:arr)
            if(m.getName().toLowerCase().contains(q.toLowerCase()))
                ret.add(m);
        return ret;
    }

    /**
     * CmpByProximity is a comparator class for sorting based on proximity
     */
    public class CmpByProximity implements Comparator<Museum> {


        public CmpByProximity() {
        }

        /**
         * This is a method for calculating distance between 2 place with consideration of earth's radius.
         *
         * @param lat1 latitude of place 1
         * @param lon1 longitude of place 1
         * @param lat2 latitude of place 2
         * @param lon2 longitude of place 2
         * @return distance between 2 places with consideration of earth's radius.
         */
        public double dist(double lat1, double lon1, double lat2, double lon2){
            if(lat1 < -90 || lat1 > 90) return 1e15;
		    if(lon1 < -180 || lon1 > 180) return 1e15;
		    if(lat2 < -90 || lat2 > 90) return 1e15;
		    if(lon2 < -180 || lon2 > 180) return 1e15;
            double R = 6371;
            double p1 = Math.toRadians(lat1);
            double p2 = Math.toRadians(lat2);
            double dp = Math.toRadians(lat2-lat1);
            double dl = Math.toRadians(lon2-lon1);

            double a = Math.sin(dp/2.0) * Math.sin(dp/2.0)
                    + Math.cos(p1) * Math.cos(p2) * Math.sin(dl/2.0) * Math.sin(dl/2.0);

            double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

            return R * c;
        }

        @Override
        public int compare(Museum a, Museum b) {
            double d1 = dist(a.getLatitude(),a.getLongitude(),user.getLatitude(),user.getLongitude());
            double d2 = dist(b.getLatitude(),b.getLongitude(),user.getLatitude(),user.getLongitude());

            return ((d1 < d2)?(-1):(1));
        }
    }

    /**
     * CmpByRating is a comparator class for sorting based on proximity
     */
    public class CmpByRating implements Comparator<Museum>{
        @Override
        public int compare(Museum a, Museum b) {
            return ((a.getRating() > b.getRating())?(-1):(1));
        }
    }
}
