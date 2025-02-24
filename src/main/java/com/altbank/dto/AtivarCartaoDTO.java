package com.altbank.dto;

public class AtivarCartaoDTO {

    private Long idCartao;
    private String ultimosQuatroDigitos;

    public Long getIdCartao() {
        return idCartao;
    }

    public void setIdCartao(Long idCartao) {
        this.idCartao = idCartao;
    }

    public String getUltimosQuatroDigitos() {
        return ultimosQuatroDigitos;
    }

    public void setUltimosQuatroDigitos(String ultimosQuatroDigitos) {
        this.ultimosQuatroDigitos = ultimosQuatroDigitos;
    }
}
