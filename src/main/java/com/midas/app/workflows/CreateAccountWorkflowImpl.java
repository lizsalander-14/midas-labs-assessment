package com.midas.app.workflows;

import com.midas.app.activities.AccountActivity;
import com.midas.app.models.Account;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class CreateAccountWorkflowImpl implements CreateAccountWorkflow {

  private final AccountActivity accountActivity =
      Workflow.newActivityStub(
          AccountActivity.class,
          ActivityOptions.newBuilder()
              .setStartToCloseTimeout(Duration.ofMinutes(5))
              .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
              .build());

  @Override
  public Account createAccount(Account details) {
    Account paymentProviderDetails = accountActivity.createPaymentAccount(details);
    return accountActivity.saveAccount(paymentProviderDetails);
  }
}
