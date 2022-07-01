package com.geolocation.dao;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.geolocation.entity.GeoLocation;

public class GeoLocationDAO extends AbstractDAO<GeoLocation> {

    public GeoLocationDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    public GeoLocation findByID(){
        return findByID();
    }

    public GeoLocation insert(GeoLocation geoLocation) {
        return persist(geoLocation);
    }

    public GeoLocation findByName(String ipAddress){
        Query query = currentSession().getNamedQuery(GeoLocation.GET_GEOLOCATION_BY_IP).setParameter("query", ipAddress);
        return (GeoLocation) query.getSingleResult();
    }

}
