package com.geolocation.exceptions;

public class GeoLocationNotFoundException extends RuntimeException{
    public GeoLocationNotFoundException (String msg){
        super(msg);
    }
}
