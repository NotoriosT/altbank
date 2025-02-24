package com.altbank.dto;

import com.altbank.dto.enums.MotivoReemissao;

public class ReemissaoCartaoDTO {

    private Long idCartao;
    private MotivoReemissao motivo;

    public Long getIdCartao() {
        return idCartao;
    }

    public void setIdCartao(Long idCartao) {
        this.idCartao = idCartao;
    }

    public MotivoReemissao getMotivo() {
        return motivo;
    }

    public void setMotivo(MotivoReemissao motivo) {
        this.motivo = motivo;
    }
}
