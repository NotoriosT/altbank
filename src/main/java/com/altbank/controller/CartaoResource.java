package com.altbank.controller;

import com.altbank.dto.AtivarCartaoDTO;
import com.altbank.dto.CriarCartaoDTO;
import com.altbank.dto.ReemissaoCartaoDTO;
import com.altbank.model.Cartao;
import com.altbank.service.CartaoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/cartoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartaoResource {

    @Inject
    CartaoService cartaoService;

    @POST
    public Cartao criarCartao(CriarCartaoDTO dto) {
        return cartaoService.criarCartao(dto);
    }

    @PUT
    @Path("/ativar")
    public void ativarCartao(AtivarCartaoDTO dto) {
        cartaoService.ativarCartao(dto);
    }

    @PUT
    @Path("/{id}/bloquear")
    public void bloquearCartao(@PathParam("id") Long id) {
        cartaoService.bloquearCartao(id);
    }

    @PUT
    @Path("/reemitir")
    public Cartao reemitirCartao(ReemissaoCartaoDTO dto) {
        return cartaoService.solicitarReemissaoCartao(dto);
    }
}
