package com.growthbeat.glroutable;

/**
 * Created by tabatakatsutoshi on 2015/08/04.
 */
public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(String message) {
        super(message);
    }
}
