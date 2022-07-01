package com.geolocation.service;

import com.geolocation.cache.CacheStore;
import com.geolocation.dao.GeoLocationDAO;
import com.geolocation.entity.GeoLocation;
import com.geolocation.exceptions.GeoLocationNotFoundException;

import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class GeolocationService {
    private static Logger LOGGER = LoggerFactory.getLogger(GeolocationService.class);
    private GeoLocationDAO geoLocationDAO;
    private Client client;
    CacheStore<GeoLocation> cacheStore;

    public GeolocationService(GeoLocationDAO dao,Client client,CacheStore<GeoLocation> cacheStore) {
        this.geoLocationDAO = dao;
        this.client = client;
        this.cacheStore = cacheStore;
    }
    @UnitOfWork
    public GeoLocation getGeoLocationByName(String ipAddress) {
        GeoLocation geoLocation;
        geoLocation = cacheStore.get(ipAddress);
        if(geoLocation == null) {
            LOGGER.info("not in cache",ipAddress);
            try {
                geoLocation = geoLocationDAO.findByName(ipAddress);
            }catch (NoResultException e){
                LOGGER.info("No data Found On for "+ipAddress+" "+e.getLocalizedMessage());
            }
            if(geoLocation==null) {
                LOGGER.info("not in db",ipAddress);
                try {
                    WebTarget webTarget = client.target("http://ip-api.com/json/" + ipAddress);
                    Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
                    Response response = invocationBuilder.get();
                    geoLocation = response.readEntity(GeoLocation.class);
                }catch (Exception e){
                    throw new GeoLocationNotFoundException("No Data Found for this IP:"+ipAddress);
                }
                LOGGER.info("from api call"+ipAddress);
                try {
                    geoLocationDAO.insert(geoLocation);
                }catch (Exception e){
                    throw e;
                }
            }
            cacheStore.add(ipAddress, geoLocation);
        }
        return geoLocation;
    }
}
