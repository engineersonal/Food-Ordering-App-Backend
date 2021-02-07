package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

public class CouponService {

    @Autowired
    private CouponDao couponDao;

    @Transactional
    public CouponEntity getCouponByUuid(final String couponUuid) {
        return couponDao.getCouponByUuid(couponUuid);
    }

}
