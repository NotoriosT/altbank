package com.altbank.dto;

import java.time.LocalDateTime;

public class CvvWebhookDTO {

    private Long accountId;
    private Long cardId;
    private String nextCvv;
    private LocalDateTime expirationDate;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getNextCvv() {
        return nextCvv;
    }

    public void setNextCvv(String nextCvv) {
        this.nextCvv = nextCvv;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }
}
