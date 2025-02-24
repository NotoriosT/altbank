package com.altbank.controller;

import com.altbank.dto.CvvWebhookDTO;
import com.altbank.dto.DeliveryWebhookDTO;
import com.altbank.service.CartaoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/webhooks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WebhookResource {

    @Inject
    CartaoService cartaoService;

    @POST
    @Path("/delivery")
    public Response receberWebhookEntrega(DeliveryWebhookDTO dto) {
        cartaoService.processarEntregaCartao(dto);
        return Response.ok().build();
    }

    @POST
    @Path("/cvv")
    public Response receberWebhookCvv(CvvWebhookDTO dto) {
        cartaoService.atualizarCvvCartao(dto);
        return Response.ok().build();
    }
}
