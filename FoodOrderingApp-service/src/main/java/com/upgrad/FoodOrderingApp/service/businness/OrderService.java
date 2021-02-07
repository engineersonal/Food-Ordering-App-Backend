package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.api.model.ItemQuantity;
import com.upgrad.FoodOrderingApp.api.model.SaveOrderRequest;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderItemDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private CustomerService CustomerService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private RestaurantBusinessService restaurantBusinessService;

    @Autowired
    private CustomerAddressDao customerAddressDao;

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderItemDao orderItemDao;



    @Transactional
    public CouponEntity getCouponByName(String couponName, final String authorizationToken) throws AuthorizationFailedException {

        // Validates the access token retrieved from database
        CustomerService.getCustomer(authorizationToken);
        return orderDao.getCouponByName(couponName);
    }

    @Transactional
    public List<OrdersEntity> getCustomerOrders(final CustomerEntity customerEntity) {

        return orderDao.getCustomerOrders(customerEntity);
    }

    @Transactional
    public OrdersEntity saveOrder(final SaveOrderRequest saveOrderRequest, final String authorizationToken)
            throws AuthorizationFailedException, CouponNotFoundException, AddressNotFoundException,
            PaymentMethodNotFoundException, RestaurantNotFoundException, ItemNotFoundException {

        // Validates the provided access token
        CustomerService.getCustomer(authorizationToken);

        // Gets the customerAuthToken details from customerDao
        CustomerAuthEntity customerAuthTokenEntity = CustomerService.getCustomerAuthToken(authorizationToken);

        // Gets the customer details from customerAuthTokenEntity
        CustomerEntity customerEntity = customerAuthTokenEntity.getCustomer();

        // Gets the address details from addressService
        AddressEntity addressEntity = addressService.getAddressByUUID(saveOrderRequest.getAddressId());

        // Gets the Customer address details from customerAddressDao
        CustomerAddressEntity customerAddressEntity = customerAddressDao.getCustAddressByCustIdAddressId(customerAuthTokenEntity.getCustomer(), addressEntity);

        // Gets the coupon details from couponService
        CouponEntity couponEntity = couponService.getCouponByUuid(saveOrderRequest.getCouponId().toString());

        // Gets the payment details from paymentService
        PaymentEntity paymentEntity = paymentService.getPaymentByUuid(saveOrderRequest.getPaymentId().toString());

        // Gets the restaurant details from restaurantService
        RestaurantEntity restaurantEntity = restaurantBusinessService.getRestaurantByUUId(saveOrderRequest.getRestaurantId().toString());

        // Throws CouponNotFoundException if coupon not found
        if (couponEntity == null) {
            throw new CouponNotFoundException("CPF-002", "No coupon by this id");
            // Throws AddressNotFoundException if address not found
        } else if (addressEntity == null) {
            throw new AddressNotFoundException("ANF-003", "No address by this id");
            // Throws PaymentMethodNotFoundException id payment method not found
        } else if (paymentEntity ==  null) {
            throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
            // Throws RestaurantNotFoundException if restaurant not found
        } else if (restaurantEntity == null) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
            // Throws AuthorizationFailedException if customer provides some other's address
        } else if (customerAddressEntity == null) {
            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        // Loads the ordersEntity with all the obtained details
        OrdersEntity ordersEntity = new OrdersEntity();
        ordersEntity.setUuid(UUID.randomUUID().toString());
        ordersEntity.setCoupon(couponEntity);
        ordersEntity.setRestaurant(restaurantEntity);
        ordersEntity.setPayment(paymentEntity);
        ordersEntity.setCustomer(customerEntity);
        ordersEntity.setAddress(addressEntity);
        ordersEntity.setBill(saveOrderRequest.getBill());
        ordersEntity.setDiscount(saveOrderRequest.getDiscount());
        ordersEntity.setDate(now);

        // Saves the order by calling saveOrder
        OrdersEntity savedOrderEntity = orderDao.saveOrder(ordersEntity);

        // Loops thru getItemQuantities and loads the OrderItemEntity
        for (ItemQuantity itemQuantity : saveOrderRequest.getItemQuantities()) {
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setOrder(savedOrderEntity);
            orderItemEntity.setItem(itemService.getItemEntityByUuid(itemQuantity.getItemId().toString()));
            orderItemEntity.setQuantity(itemQuantity.getQuantity());
            orderItemEntity.setPrice(itemQuantity.getPrice());

            // Saves the order item by calling createOrderItemEntity of orderItemDao
            orderItemDao.createOrderItemEntity(orderItemEntity);
        }

        // Returns the savedOrderEntity from the orderDao
        return orderDao.saveOrder(savedOrderEntity);
    }

}
