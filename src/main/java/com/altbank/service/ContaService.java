package com.altbank.service;

import com.altbank.model.Cartao;
import com.altbank.model.Conta;

import com.altbank.model.enums.ContaStatus;
import com.altbank.repository.CartaoRepository;
import com.altbank.repository.ContaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class ContaService {

    @Inject
    ContaRepository contaRepository;
    @Inject
    CartaoRepository cartaoRepository;


    public List<Conta> listarContas() {
        return contaRepository.listAll();
    }

    public Conta buscarPorCpf(String cpf) {
        return contaRepository.findByCpf(cpf);
    }

    @Transactional
    public void criarConta(Conta conta) {
        contaRepository.persist(conta);
    }

    @Transactional
    public void inativarConta(Long id) {
        Conta conta = contaRepository.findById(id);
        if (conta == null) {
            throw new RuntimeException("Conta não encontrada.");
        }

        if (conta.getContaStatus() == ContaStatus.INATIVA) {
            throw new RuntimeException("A conta já está inativa.");
        }

        conta.inativar();
    }


    public List<Cartao> listarCartoesPorConta(Long contaId) {
        Conta conta = contaRepository.findById(contaId);
        if (conta == null) {
            throw new RuntimeException("Conta não encontrada.");
        }
        return cartaoRepository.list("conta", conta);
    }
}
