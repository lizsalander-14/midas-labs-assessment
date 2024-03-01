package com.midas.app.services;

import com.midas.app.providers.payment.PaymentProvider;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentProviderFactory {

  @Autowired List<PaymentProvider> paymentProviderList;

  private static final Map<String, PaymentProvider> paymentProviderServiceCache = new HashMap<>();

  @PostConstruct
  public void initServiceCache() {
    for (PaymentProvider service : paymentProviderList) {
      paymentProviderServiceCache.put(service.providerName(), service);
    }
  }

  public static PaymentProvider getService(String type) {
    PaymentProvider service = paymentProviderServiceCache.get(type);
    if (Objects.isNull(service)) {
      throw new RuntimeException("Unknown service type : {}" + type);
    }
    return service;
  }
}
