package com.midas.app.services;

import com.midas.app.models.Account;
import com.midas.app.repositories.AccountRepository;
import com.midas.app.workflows.CreateAccountWorkflow;
import com.midas.app.workflows.UpdateAccountWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
  private final Logger logger = Workflow.getLogger(AccountServiceImpl.class);

  private final WorkflowClient workflowClient;

  private final AccountRepository accountRepository;

  /**
   * createAccount creates a new account in the system or provider.
   *
   * @param details is the details of the account to be created.
   * @return Account
   */
  @Override
  public Account createAccount(Account details) {
    var options =
        WorkflowOptions.newBuilder()
            .setTaskQueue(CreateAccountWorkflow.QUEUE_NAME)
            .setWorkflowId(details.getEmail())
            .setWorkflowRunTimeout(Duration.ofMinutes(10))
            .setWorkflowTaskTimeout(Duration.ofMinutes(5))
            .build();

    logger.info("initiating workflow to create account for email: {}", details.getEmail());

    var workflow = workflowClient.newWorkflowStub(CreateAccountWorkflow.class, options);

    return workflow.createAccount(details);
  }

  /**
   * getAccounts returns a list of accounts.
   *
   * @return List<Account>
   */
  @Override
  public List<Account> getAccounts() {
    return accountRepository.findAll();
  }

  /**
   * updateAccount updates existing account in the system.
   *
   * @param details of the account to be updated
   * @return Account
   */
  @Override
  public Account updateAccount(Account details) {
    var options =
        WorkflowOptions.newBuilder()
            .setTaskQueue(UpdateAccountWorkflow.QUEUE_NAME)
            .setWorkflowId(String.valueOf(details.getId()))
            .setWorkflowRunTimeout(Duration.ofMinutes(10))
            .setWorkflowTaskTimeout(Duration.ofMinutes(5))
            .build();

    logger.info("initiating workflow to update account for request: {}", details);

    var workflow = workflowClient.newWorkflowStub(UpdateAccountWorkflow.class, options);

    var existingDetails = accountRepository.findById(details.getId());
    existingDetails.setFirstName(details.getFirstName());
    existingDetails.setLastName(details.getLastName());
    existingDetails.setEmail(details.getEmail());
    return workflow.updateAccount(existingDetails);
  }
}
