package com.microservice_app.accounts.service.impl;

import com.microservice_app.accounts.dto.AccountDto;
import com.microservice_app.accounts.dto.CardDto;
import com.microservice_app.accounts.dto.CustomerDetailsDto;
import com.microservice_app.accounts.dto.LoanDto;
import com.microservice_app.accounts.entity.Account;
import com.microservice_app.accounts.entity.Customer;
import com.microservice_app.accounts.exception.ResourceNotFoundException;
import com.microservice_app.accounts.mapper.AccountMapper;
import com.microservice_app.accounts.mapper.CustomerMapper;
import com.microservice_app.accounts.repository.AccountRepository;
import com.microservice_app.accounts.repository.CustomerRepository;
import com.microservice_app.accounts.service.ICustomerService;
import com.microservice_app.accounts.service.client.CardFeignClient;
import com.microservice_app.accounts.service.client.LoanFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;
    private CardFeignClient cardFeignClient;
    private LoanFeignClient loanFeignClient;

    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));

        Account account = accountRepository.findByCustomerId(customer.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString()));

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountDto(AccountMapper.mapToAccountDto(account, new AccountDto()));

        ResponseEntity<LoanDto> loanDtoResponseEntity = loanFeignClient.fetchLoanDetails(mobileNumber);
        customerDetailsDto.setLoanDto(loanDtoResponseEntity.getBody());

        ResponseEntity<CardDto> cardDtoResponseEntity = cardFeignClient.fetchCardDetails(mobileNumber);
        customerDetailsDto.setCardDto(cardDtoResponseEntity.getBody());

        return customerDetailsDto;
    }
}
