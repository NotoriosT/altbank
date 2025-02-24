package com.altbank.model;

import com.altbank.model.enums.StatusCartao;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
@Entity
public class Cartao extends PanacheEntity {

    @Column(nullable = false, unique = true)
    private String numero;

    @Column(nullable = false)
    private String nomeTitular;

    @Column(nullable = false)
    private String cvv;

    @Column(nullable = false)
    private LocalDate dataValidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCartao status = StatusCartao.PENDENTE;

    @ManyToOne
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;

    @Column(nullable = false)
    private BigDecimal limite;

    @Column(nullable = false, unique = true)
    private String trackingId;

    public Cartao() {
        this.trackingId = gerarTrackingId(); // Gera trackingId automaticamente
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNomeTitular() {
        return nomeTitular;
    }

    public void setNomeTitular(String nomeTitular) {
        this.nomeTitular = nomeTitular;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public StatusCartao getStatus() {
        return status;
    }

    public void setStatus(StatusCartao status) {
        this.status = status;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public BigDecimal getLimite() {
        return limite;
    }

    public void setLimite(BigDecimal limite) {
        this.limite = limite;
    }

    public void ativar() {
        this.status = StatusCartao.ATIVO;
    }

    public void bloquear() {
        this.status = StatusCartao.BLOQUEADO;
    }

    public void atualizarCvv() {
        this.cvv = String.format("%03d", new Random().nextInt(1000));
    }

    private String gerarTrackingId() {
        return "TRK-" + System.currentTimeMillis() + "-" + new Random().nextInt(9999);
    }
}
