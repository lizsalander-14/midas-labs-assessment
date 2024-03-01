package com.midas.app.providers.external.stripe;

import com.midas.app.models.Account;
import com.midas.app.models.ProviderType;
import com.midas.app.providers.payment.CreateAccount;
import com.midas.app.providers.payment.PaymentProvider;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.net.RequestOptions;
import com.stripe.param.CustomerCreateParams;
import io.temporal.activity.Activity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
public class StripePaymentProvider implements PaymentProvider {
  private final Logger logger = LoggerFactory.getLogger(StripePaymentProvider.class);

  private final StripeConfiguration configuration;

  /** providerName is the name of the payment provider */
  @Override
  public String providerName() {
    return ProviderType.STRIPE.getName();
  }

  /**
   * createAccount creates a new account in the payment provider.
   *
   * @param details is the details of the account to be created.
   * @return Account
   */
  @Override
  public Account createAccount(CreateAccount details) {
    var requestOptions = RequestOptions.builder().setApiKey(configuration.getApiKey()).build();
    CustomerCreateParams params =
        CustomerCreateParams.builder()
            .setName(details.getFirstName() + " " + details.getLastName())
            .setEmail(details.getEmail())
            .build();
    try {
      var customer = Customer.create(params, requestOptions);
      var account = new Account();
      BeanUtils.copyProperties(details, account);
      account.setProviderType(ProviderType.STRIPE);
      account.setProviderId(customer.getId());
      return account;
    } catch (StripeException e) {
      logger.error("Error while creating stripe customer for email: {}", details.getEmail(), e);
      throw Activity.wrap(e);
    }
  }
}
