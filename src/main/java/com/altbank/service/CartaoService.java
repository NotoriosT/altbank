package com.altbank.service;

import com.altbank.dto.*;
import com.altbank.model.Cartao;
import com.altbank.model.Conta;
import com.altbank.model.enums.StatusCartao;
import com.altbank.repository.CartaoRepository;
import com.altbank.repository.ContaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Random;

@ApplicationScoped
public class CartaoService {

    @Inject
    CartaoRepository cartaoRepository;

    @Inject
    ContaRepository contaRepository;

    @Transactional
    public Cartao criarCartao(CriarCartaoDTO dto) {
        Conta conta = contaRepository.findById(dto.getIdConta());
        if (conta == null) {
            throw new RuntimeException("Conta não encontrada.");
        }

        if (dto.getRenda() == null || dto.getRenda().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Renda inválida.");
        }

        BigDecimal limite = dto.getRenda().multiply(new BigDecimal("1.5"));

        Cartao cartao = new Cartao();
        cartao.setNumero(gerarNumeroCartao());
        cartao.setNomeTitular(conta.getNome());
        cartao.setCvv(gerarCvv());
        cartao.setDataValidade(LocalDate.now().plusYears(5));
        cartao.setConta(conta);
        cartao.setStatus(StatusCartao.PENDENTE);
        cartao.setLimite(limite);

        // TrackingId agora é gerado automaticamente no construtor do Cartao

        cartaoRepository.persist(cartao);
        return cartao;
    }


    @Transactional
    public void ativarCartao(AtivarCartaoDTO dto) {
        Cartao cartao = cartaoRepository.findById(dto.getIdCartao());
        if (cartao == null) {
            throw new RuntimeException("Cartão não encontrado.");
        }

        if (cartao.getNumero() == null || cartao.getNumero().length() < 4) {
            throw new RuntimeException("Número do cartão inválido.");
        }

        // Pegando os últimos 4 dígitos corretamente
        String ultimosQuatro = cartao.getNumero().substring(cartao.getNumero().length() - 4);
        if (!ultimosQuatro.equals(dto.getUltimosQuatroDigitos())) {
            throw new RuntimeException("Os últimos 4 dígitos não correspondem ao cartão cadastrado.");
        }

        cartao.ativar();
    }


    @Transactional
    public void bloquearCartao(Long id) {
        Cartao cartao = cartaoRepository.findById(id);
        if (cartao != null) {
            cartao.bloquear();
        } else {
            throw new RuntimeException("Cartão não encontrado.");
        }
    }

    private String gerarNumeroCartao() {
        return "400000" + (1000000000L + (long) (Math.random() * 9000000000L));
    }

    private String gerarCvv() {
        return String.format("%03d", new Random().nextInt(1000));
    }








    @Transactional
    public Cartao solicitarReemissaoCartao(ReemissaoCartaoDTO dto) {
        Cartao cartaoAntigo = cartaoRepository.findById(dto.getIdCartao());
        if (cartaoAntigo == null) {
            throw new RuntimeException("Cartão não encontrado.");
        }

        if (cartaoAntigo.getStatus() == StatusCartao.BLOQUEADO) {
            throw new RuntimeException("Cartão já bloqueado. Não é possível reemitir.");
        }

        if (dto.getMotivo() == null) {
            throw new RuntimeException("Motivo de reemissão deve ser informado.");
        }

        // Bloqueia o cartão antigo
        cartaoAntigo.bloquear();

        // Cria um novo cartão com os mesmos dados do titular e conta
        Cartao novoCartao = new Cartao();
        novoCartao.setNumero(gerarNumeroCartao());
        novoCartao.setNomeTitular(cartaoAntigo.getNomeTitular());
        novoCartao.setCvv(gerarCvv());
        novoCartao.setDataValidade(LocalDate.now().plusYears(5));
        novoCartao.setConta(cartaoAntigo.getConta());
        novoCartao.setStatus(StatusCartao.PENDENTE);
        novoCartao.setLimite(cartaoAntigo.getLimite());

        cartaoRepository.persist(novoCartao);
        return novoCartao;
    }

    @Transactional
    public void processarEntregaCartao(DeliveryWebhookDTO dto) {
        Optional<Cartao> cartaoOpt = cartaoRepository.find("trackingId", dto.getTrackingId()).firstResultOptional();

        if (cartaoOpt.isEmpty()) {
            throw new RuntimeException("Cartão não encontrado com o tracking ID: " + dto.getTrackingId());
        }

        Cartao cartao = cartaoOpt.get();

        if ("ENTREGUE".equalsIgnoreCase(dto.getDeliveryStatus())) {
            cartao.ativar();
        } else {
            System.out.println("Cartão não entregue. Motivo: " + dto.getDeliveryReturnReason());
        }
    }


    @Transactional
    public void atualizarCvvCartao(CvvWebhookDTO dto) {
        Cartao cartao = cartaoRepository.findById(dto.getCardId());
        if (cartao == null) {
            throw new RuntimeException("Cartão não encontrado.");
        }

        if (cartao.getStatus() != StatusCartao.ATIVO) {
            throw new RuntimeException("O cartão não está ativo, não é possível atualizar o CVV.");
        }

        cartao.setCvv(dto.getNextCvv());
    }



}
