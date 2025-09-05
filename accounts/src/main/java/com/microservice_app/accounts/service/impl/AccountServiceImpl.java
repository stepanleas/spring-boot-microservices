package com.microservice_app.accounts.service.impl;

import com.microservice_app.accounts.constants.AccountConstant;
import com.microservice_app.accounts.dto.AccountDto;
import com.microservice_app.accounts.dto.AccountMessageDto;
import com.microservice_app.accounts.dto.CustomerDto;
import com.microservice_app.accounts.entity.Account;
import com.microservice_app.accounts.entity.Customer;
import com.microservice_app.accounts.exception.CustomerAlreadyExistsException;
import com.microservice_app.accounts.exception.ResourceNotFoundException;
import com.microservice_app.accounts.mapper.AccountMapper;
import com.microservice_app.accounts.mapper.CustomerMapper;
import com.microservice_app.accounts.repository.AccountRepository;
import com.microservice_app.accounts.repository.CustomerRepository;
import com.microservice_app.accounts.service.IAccountService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements IAccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;
    private final StreamBridge streamBridge;

    @Override
    public void createAccount(CustomerDto customerDto) {
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Optional<Customer> optionalCustomer = customerRepository.findByMobileNumber(customerDto.getMobileNumber());
        if (optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already registered with given mobile number" + customerDto.getMobileNumber());
        }

        Customer savedCustomer = customerRepository.save(customer);
        Account savedAccount = accountRepository.save(createNewAccount(savedCustomer));
        sendCommunication(savedAccount, savedCustomer);
    }

    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));

        Account account = accountRepository.findByCustomerId(customer.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString()));

        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        customerDto.setAccountDto(AccountMapper.mapToAccountDto(account, new AccountDto()));

        return customerDto;
    }

    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        AccountDto accountDto = customerDto.getAccountDto();

        if (accountDto != null) {
            Account account = accountRepository.findById(accountDto.getAccountNumber()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "AccountNumber", accountDto.getAccountNumber().toString())
            );
            AccountMapper.mapToAccount(accountDto, account);
            Account savedAccount = accountRepository.save(account);

            Customer customer = customerRepository.findById(savedAccount.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "CustomerID", savedAccount.getCustomerId().toString())
            );
            CustomerMapper.mapToCustomer(customerDto, customer);
            customerRepository.save(customer);

            return true;
        }

        return false;
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
            () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );

        accountRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());

        return true;
    }

    @Override
    public boolean updateCommunicationStatus(Long accountNumber) {
        boolean isUpdated = false;
        if (accountNumber != null) {
            Account account = accountRepository.findById(accountNumber).orElseThrow(
                () -> new ResourceNotFoundException("Account", "AccountNumber", accountNumber.toString())
            );
            account.setCommunicationSw(true);
            accountRepository.save(account);
            isUpdated = true;
        }

        return isUpdated;
    }

    private Account createNewAccount(Customer customer) {
        Account newAccount = new Account();
        newAccount.setCustomerId(customer.getCustomerId());

        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);
        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountConstant.SAVINGS);
        newAccount.setBranchAddress(AccountConstant.ADDRESS);

        return newAccount;
    }

    private void sendCommunication(Account account, Customer customer) {
        AccountMessageDto accountMessageDto = new AccountMessageDto(
            account.getAccountNumber(),
            customer.getName(),
            customer.getEmail(),
            customer.getMobileNumber()
        );

        logger.info("Sending communication request for details: {}", accountMessageDto);
        boolean result = streamBridge.send("sendCommunication-out-0", accountMessageDto);
        logger.info("Communication request sent with result: {}", result);
    }
}
