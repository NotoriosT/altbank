package com.altbank.controller;

import com.altbank.model.Cartao;
import com.altbank.model.Conta;
import com.altbank.service.ContaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/contas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ContaResource {

    @Inject
    ContaService contaService;

    @GET
    public Response listarContas() {
        List<Conta> contas = contaService.listarContas();
        return Response.ok(contas).build();
    }

    @GET
    @Path("/{cpf}")
    public Response buscarPorCpf(@PathParam("cpf") String cpf) {
        Conta conta = contaService.buscarPorCpf(cpf);
        if (conta == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Conta não encontrada.").build();
        }
        return Response.ok(conta).build();
    }

    @POST
    public Response criarConta(Conta conta) {
        contaService.criarConta(conta);
        return Response.status(Response.Status.CREATED).entity(conta).build();
    }

    @PUT
    @Path("/{id}/inativar")
    public Response inativarConta(@PathParam("id") Long id) {
        try {
            contaService.inativarConta(id);
            return Response.noContent().build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/cartoes")
    public Response listarCartoesPorConta(@PathParam("id") Long id) {
        try {
            List<Cartao> cartoes = contaService.listarCartoesPorConta(id);
            return Response.ok(cartoes).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
