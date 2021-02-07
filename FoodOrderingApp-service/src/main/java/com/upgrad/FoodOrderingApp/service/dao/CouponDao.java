package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

public class CouponDao {

    @PersistenceContext
    private EntityManager entityManager;

    public CouponEntity getCouponByUuid(String couponId) {
        try {
            return entityManager.createNamedQuery("couponByUuid", CouponEntity.class).setParameter("uuid", couponId)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }
}
