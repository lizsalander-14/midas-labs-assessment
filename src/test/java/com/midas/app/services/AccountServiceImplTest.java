package com.midas.app.services;

import com.midas.app.models.Account;
import com.midas.app.models.ProviderType;
import com.midas.app.repositories.AccountRepository;
import com.midas.app.workflows.CreateAccountWorkflow;
import io.temporal.client.ActivityCompletionFailureException;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceImplTest {

  @InjectMocks private AccountServiceImpl accountService;

  @Mock private WorkflowClient workflowClient;
  @Mock private AccountRepository accountRepository;
  @Mock private CreateAccountWorkflow createAccountWorkflow;

  private static final String firstName = "test";
  private static final String lastName = "xyz";
  private static final String email = "a@b.com";
  private static final String providerId = "cus_123abc";

  @Captor ArgumentCaptor<WorkflowOptions> workflowOptionsArgumentCaptor;

  @Test
  public void createAccountTest_success() {
    Account details =
        Account.builder().firstName(firstName).lastName(lastName).email(email).build();
    Mockito.when(
            workflowClient.newWorkflowStub(
                Mockito.eq(CreateAccountWorkflow.class), Mockito.any(WorkflowOptions.class)))
        .thenReturn(createAccountWorkflow);
    Mockito.when(createAccountWorkflow.createAccount(details))
        .thenReturn(
            Account.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .providerType(ProviderType.STRIPE)
                .providerId(providerId)
                .build());

    Account response = accountService.createAccount(details);

    Assert.assertNotNull(response);
    Assert.assertEquals(firstName, response.getFirstName());
    Assert.assertEquals(lastName, response.getLastName());
    Assert.assertEquals(email, response.getEmail());
    Assert.assertEquals(ProviderType.STRIPE, response.getProviderType());
    Assert.assertEquals(providerId, response.getProviderId());
    Mockito.verify(workflowClient)
        .newWorkflowStub(
            Mockito.eq(CreateAccountWorkflow.class), workflowOptionsArgumentCaptor.capture());
    WorkflowOptions options = workflowOptionsArgumentCaptor.getValue();
    Assert.assertEquals(CreateAccountWorkflow.QUEUE_NAME, options.getTaskQueue());
    Assert.assertEquals(email, options.getWorkflowId());
    Mockito.verify(createAccountWorkflow).createAccount(details);
  }

  @Test
  public void createAccountTest_stripeError() {
    Account details =
        Account.builder().firstName(firstName).lastName(lastName).email(email).build();
    Mockito.when(
            workflowClient.newWorkflowStub(
                Mockito.eq(CreateAccountWorkflow.class), Mockito.any(WorkflowOptions.class)))
        .thenReturn(createAccountWorkflow);
    Mockito.when(createAccountWorkflow.createAccount(details))
        .thenThrow(
            new ActivityCompletionFailureException(
                email, new RuntimeException("stripe exception")));

    try {
      accountService.createAccount(details);
    } catch (ActivityCompletionFailureException e) {
      Mockito.verify(workflowClient)
          .newWorkflowStub(
              Mockito.eq(CreateAccountWorkflow.class), workflowOptionsArgumentCaptor.capture());
      WorkflowOptions options = workflowOptionsArgumentCaptor.getValue();
      Assert.assertEquals(CreateAccountWorkflow.QUEUE_NAME, options.getTaskQueue());
      Assert.assertEquals(email, options.getWorkflowId());
      Mockito.verify(createAccountWorkflow).createAccount(details);
    }
  }

  @After
  public void destroy() {
    Mockito.verifyNoMoreInteractions(workflowClient, accountRepository, createAccountWorkflow);
  }
}
