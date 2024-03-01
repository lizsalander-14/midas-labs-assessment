package com.midas.app.activities;

import com.midas.app.models.Account;
import com.midas.app.models.ProviderType;
import com.midas.app.providers.payment.CreateAccount;
import com.midas.app.repositories.AccountRepository;
import com.midas.app.services.PaymentProviderFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountActivityImpl implements AccountActivity {

  @Autowired private AccountRepository accountRepository;

  @Override
  public Account saveAccount(Account account) {
    return accountRepository.save(account);
  }

  @Override
  public Account createPaymentAccount(Account account) {
    var request = new CreateAccount();
    BeanUtils.copyProperties(account, request);
    return PaymentProviderFactory.getService(ProviderType.STRIPE.getName()).createAccount(request);
  }
}
