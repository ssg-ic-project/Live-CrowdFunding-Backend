package com.crofle.livecrowdfunding.service;

import net.minidev.json.JSONObject;

public interface PaymentService {
    void insertPaymentHistory(JSONObject jsonObject, String address);

}
