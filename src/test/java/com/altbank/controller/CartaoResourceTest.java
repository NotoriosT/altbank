package com.altbank.controller;

import com.altbank.dto.AtivarCartaoDTO;
import com.altbank.dto.CriarCartaoDTO;
import com.altbank.dto.ReemissaoCartaoDTO;
import com.altbank.model.Cartao;
import com.altbank.model.enums.StatusCartao;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class CartaoResourceTest {

    @Test
    public void testCriarCartao() {
        CriarCartaoDTO dto = new CriarCartaoDTO();
        dto.setIdConta(1L);
        dto.setRenda(new BigDecimal("5000"));

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/cartoes")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("status", equalTo(StatusCartao.PENDENTE.name()));
    }

    @Test
    public void testAtivarCartao() {
        AtivarCartaoDTO dto = new AtivarCartaoDTO();
        dto.setIdCartao(1L);
        dto.setUltimosQuatroDigitos("1234");

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .put("/cartoes/ativar")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void testBloquearCartao() {
        given()
                .when()
                .put("/cartoes/1/bloquear")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void testReemitirCartao() {
        ReemissaoCartaoDTO dto = new ReemissaoCartaoDTO();
        dto.setIdCartao(1L);
        dto.setMotivo(com.altbank.dto.enums.MotivoReemissao.PERDA);

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .put("/cartoes/reemitir")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("status", equalTo(StatusCartao.PENDENTE.name()));
    }
}
