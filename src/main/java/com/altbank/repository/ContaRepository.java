package com.altbank.repository;

import com.altbank.model.Conta;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ContaRepository implements PanacheRepository<Conta> {
    public Conta findByCpf(String cpf) {
        return find("cpf", cpf).firstResult();
    }
}
