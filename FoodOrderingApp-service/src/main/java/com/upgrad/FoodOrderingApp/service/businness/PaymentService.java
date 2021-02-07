package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.List;

public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;


    @Transactional
    public PaymentEntity getPaymentByUuid(final String paymentUuid) {
        return paymentDao.getPaymentByUuid(paymentUuid);
    }

    @Transactional
    public List<PaymentEntity> getAllPaymentMethods() {
        return paymentDao.getAllPaymentMethods();
    }
}
