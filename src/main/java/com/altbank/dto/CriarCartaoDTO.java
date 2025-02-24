package com.altbank.dto;

import java.math.BigDecimal;

public class CriarCartaoDTO {

    private BigDecimal renda;
    private Long idConta;

    public BigDecimal getRenda() {
        return renda;
    }

    public void setRenda(BigDecimal renda) {
        this.renda = renda;
    }

    public Long getIdConta() {
        return idConta;
    }

    public void setIdConta(Long idConta) {
        this.idConta = idConta;
    }
}
