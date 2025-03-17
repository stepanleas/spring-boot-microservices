package com.microservice_app.loans.service;

import com.microservice_app.loans.dto.LoanDto;

public interface ILoanService {
    void createLoan(String mobileNumber);
    LoanDto fetchLoan(String mobileNumber);
    boolean updateLoan(LoanDto loanDto);
    boolean deleteLoan(String mobileNumber);
}
