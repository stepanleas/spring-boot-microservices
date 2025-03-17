package com.microservice_app.cards.mapper;

import com.microservice_app.cards.dto.CardDto;
import com.microservice_app.cards.entity.Card;

public class CardMapper {
    public static CardDto mapToCardDto(Card card, CardDto cardDto) {
        cardDto.setCardNumber(card.getCardNumber());
        cardDto.setCardType(card.getCardType());
        cardDto.setMobileNumber(card.getMobileNumber());
        cardDto.setTotalLimit(card.getTotalLimit());
        cardDto.setAvailableAmount(card.getAvailableAmount());
        cardDto.setAmountUsed(card.getAmountUsed());

        return cardDto;
    }

    public static Card mapToCard(CardDto cardDto, Card card) {
        card.setCardNumber(cardDto.getCardNumber());
        card.setCardType(cardDto.getCardType());
        card.setMobileNumber(cardDto.getMobileNumber());
        card.setTotalLimit(cardDto.getTotalLimit());
        card.setAvailableAmount(cardDto.getAvailableAmount());
        card.setAmountUsed(cardDto.getAmountUsed());

        return card;
    }
}
