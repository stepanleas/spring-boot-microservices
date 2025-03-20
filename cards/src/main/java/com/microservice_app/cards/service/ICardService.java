package com.microservice_app.cards.service;

import com.microservice_app.cards.dto.CardDto;

public interface ICardService {
    void createCard(String mobileNumber);
    CardDto fetchCard(String mobileNumber);
    boolean updateCard(CardDto cardDto);
    boolean deleteCard(String mobileNumber);
}
