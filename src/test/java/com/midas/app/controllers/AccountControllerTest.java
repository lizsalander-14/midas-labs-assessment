package com.midas.app.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.app.models.Account;
import com.midas.app.models.ProviderType;
import com.midas.app.services.AccountService;
import com.midas.generated.model.AccountDto;
import com.midas.generated.model.CreateAccountDto;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerTest {

  @InjectMocks private AccountController accountController;

  @Mock private AccountService accountService;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  private static final String firstName = "test";
  private static final String lastName = "xyz";
  private static final String email = "a@b.com";
  private static final String stripeProvider = "stripe";
  private static final String providerId = "cus_123abc";
  private static final UUID accountId = UUID.randomUUID();

  @Before
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    objectMapper = new ObjectMapper();
  }

  @Test
  public void createUserAccountTest_success() throws Exception {
    CreateAccountDto request = new CreateAccountDto();
    request.setFirstName(firstName);
    request.setLastName(lastName);
    request.setEmail(email);
    Account details = new Account();
    BeanUtils.copyProperties(request, details);
    Mockito.when(accountService.createAccount(details))
        .thenReturn(
            Account.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .providerType(ProviderType.STRIPE)
                .providerId(providerId)
                .build());

    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/accounts")
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andReturn();

    AccountDto response =
        objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
    Assert.assertNotNull(response);
    Assert.assertEquals(firstName, response.getFirstName());
    Assert.assertEquals(lastName, response.getLastName());
    Assert.assertEquals(email, response.getEmail());
    Assert.assertEquals(stripeProvider, response.getProviderType());
    Assert.assertEquals(providerId, response.getProviderId());
    Mockito.verify(accountService).createAccount(details);
  }

  @Test
  public void getUserAccountsTest_success() throws Exception {
    Mockito.when(accountService.getAccounts())
        .thenReturn(
            Collections.singletonList(
                Account.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .providerType(ProviderType.STRIPE)
                    .providerId(providerId)
                    .build()));

    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/accounts")
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    List<AccountDto> response =
        objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
    Assert.assertEquals(1, response.size());
    Mockito.verify(accountService).getAccounts();
  }

  @Test
  public void updateUserAccountTest_success() throws Exception {
    AccountDto request = new AccountDto();
    request.setFirstName(firstName);
    request.setLastName(lastName);
    request.setEmail(email);
    Account details = new Account();
    BeanUtils.copyProperties(request, details);
    details.setId(accountId);
    Mockito.when(accountService.updateAccount(details))
        .thenReturn(
            Account.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .providerType(ProviderType.STRIPE)
                .providerId(providerId)
                .build());

    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.put("/accounts/{accountId}", String.valueOf(accountId))
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    AccountDto response =
        objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
    Assert.assertNotNull(response);
    Assert.assertEquals(firstName, response.getFirstName());
    Assert.assertEquals(lastName, response.getLastName());
    Assert.assertEquals(email, response.getEmail());
    Assert.assertEquals(stripeProvider, response.getProviderType());
    Assert.assertEquals(providerId, response.getProviderId());
    Mockito.verify(accountService).updateAccount(details);
  }

  @After
  public void destroy() {
    Mockito.verifyNoMoreInteractions(accountService);
  }
}
