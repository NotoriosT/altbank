package com.altbank.service;

import com.altbank.dto.AtivarCartaoDTO;
import com.altbank.dto.CriarCartaoDTO;
import com.altbank.dto.CvvWebhookDTO;
import com.altbank.dto.DeliveryWebhookDTO;
import com.altbank.dto.ReemissaoCartaoDTO;
import com.altbank.dto.enums.MotivoReemissao;
import com.altbank.model.Cartao;
import com.altbank.model.Conta;
import com.altbank.model.enums.StatusCartao;
import com.altbank.repository.CartaoRepository;
import com.altbank.repository.ContaRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@QuarkusTest
public class CartaoServiceTest {

    @Inject
    CartaoService cartaoService;

    @InjectMock
    ContaRepository contaRepository;

    @InjectMock
    CartaoRepository cartaoRepository;


    @Test
    public void testeCriarCartaoContaNaoEncontrada() {
        CriarCartaoDTO dto = new CriarCartaoDTO();
        dto.setIdConta(1L);
        dto.setRenda(new BigDecimal("1000"));

        when(contaRepository.findById(anyLong())).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartaoService.criarCartao(dto);
        });
        assertEquals("Conta não encontrada.", exception.getMessage());
    }

    @Test
    public void testeCriarCartaoRendaInvalida() {
        CriarCartaoDTO dto = new CriarCartaoDTO();
        dto.setIdConta(1L);
        dto.setRenda(BigDecimal.ZERO);

        Conta conta = new Conta();
        conta.setNome("Cliente Teste");
        when(contaRepository.findById(1L)).thenReturn(conta);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartaoService.criarCartao(dto);
        });
        assertEquals("Renda inválida.", exception.getMessage());
    }

    @Test
    public void testeCriarCartaoSucesso() {
        CriarCartaoDTO dto = new CriarCartaoDTO();
        dto.setIdConta(1L);
        dto.setRenda(new BigDecimal("2000"));

        Conta conta = new Conta();
        conta.setNome("Cliente Teste");
        when(contaRepository.findById(1L)).thenReturn(conta);

        Cartao cartao = cartaoService.criarCartao(dto);

        assertNotNull(cartao);
        assertEquals("Cliente Teste", cartao.getNomeTitular());
        assertEquals(StatusCartao.PENDENTE, cartao.getStatus());
        assertEquals(new BigDecimal("3000.0"), cartao.getLimite());
        assertTrue(cartao.getNumero().startsWith("400000"));
        assertEquals(LocalDate.now().plusYears(5), cartao.getDataValidade());
    }


    @Test
    public void testeAtivarCartaoNaoEncontrado() {
        AtivarCartaoDTO dto = new AtivarCartaoDTO();
        dto.setIdCartao(1L);
        dto.setUltimosQuatroDigitos("1234");

        when(cartaoRepository.findById(1L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartaoService.ativarCartao(dto);
        });
        assertEquals("Cartão não encontrado.", exception.getMessage());
    }

    @Test
    public void testeAtivarCartaoDigitosInvalidos() {
        Cartao cartao = new Cartao();
        cartao.setNumero("4000001234567890");
        cartao.setStatus(StatusCartao.PENDENTE);
        when(cartaoRepository.findById(1L)).thenReturn(cartao);

        AtivarCartaoDTO dto = new AtivarCartaoDTO();
        dto.setIdCartao(1L);
        dto.setUltimosQuatroDigitos("0000");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartaoService.ativarCartao(dto);
        });
        assertEquals("Os últimos 4 dígitos não correspondem ao cartão cadastrado.", exception.getMessage());
    }

    @Test
    public void testeAtivarCartaoSucesso() {
        Cartao cartao = new Cartao();
        cartao.setNumero("4000001234567890");
        cartao.setStatus(StatusCartao.PENDENTE);
        when(cartaoRepository.findById(1L)).thenReturn(cartao);

        AtivarCartaoDTO dto = new AtivarCartaoDTO();
        dto.setIdCartao(1L);
        dto.setUltimosQuatroDigitos("7890");

        cartaoService.ativarCartao(dto);
        assertEquals(StatusCartao.ATIVO, cartao.getStatus());
    }

    @Test
    public void testeBloquearCartaoNaoEncontrado() {
        when(cartaoRepository.findById(1L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartaoService.bloquearCartao(1L);
        });
        assertEquals("Cartão não encontrado.", exception.getMessage());
    }

    @Test
    public void testeBloquearCartaoSucesso() {
        Cartao cartao = new Cartao();
        cartao.setStatus(StatusCartao.PENDENTE);
        when(cartaoRepository.findById(1L)).thenReturn(cartao);

        cartaoService.bloquearCartao(1L);
        assertEquals(StatusCartao.BLOQUEADO, cartao.getStatus());
    }

    @Test
    public void testeSolicitarReemissaoCartaoNaoEncontrado() {
        ReemissaoCartaoDTO dto = new ReemissaoCartaoDTO();
        dto.setIdCartao(1L);
        dto.setMotivo(MotivoReemissao.PERDA);

        when(cartaoRepository.findById(1L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartaoService.solicitarReemissaoCartao(dto);
        });
        assertEquals("Cartão não encontrado.", exception.getMessage());
    }

    @Test
    public void testeSolicitarReemissaoCartaoBloqueado() {
        Cartao cartao = new Cartao();
        cartao.setStatus(StatusCartao.BLOQUEADO);
        when(cartaoRepository.findById(1L)).thenReturn(cartao);

        ReemissaoCartaoDTO dto = new ReemissaoCartaoDTO();
        dto.setIdCartao(1L);
        dto.setMotivo(MotivoReemissao.PERDA);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartaoService.solicitarReemissaoCartao(dto);
        });
        assertEquals("Cartão já bloqueado. Não é possível reemitir.", exception.getMessage());
    }

    @Test
    public void testeSolicitarReemissaoCartaoMotivoNulo() {
        Cartao cartao = new Cartao();
        cartao.setStatus(StatusCartao.ATIVO);
        when(cartaoRepository.findById(1L)).thenReturn(cartao);

        ReemissaoCartaoDTO dto = new ReemissaoCartaoDTO();
        dto.setIdCartao(1L);
        dto.setMotivo(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartaoService.solicitarReemissaoCartao(dto);
        });
        assertEquals("Motivo de reemissão deve ser informado.", exception.getMessage());
    }

    @Test
    public void testeSolicitarReemissaoCartaoSucesso() {
        // Cartão antigo ativo
        Cartao cartaoAntigo = new Cartao();
        cartaoAntigo.setStatus(StatusCartao.ATIVO);
        cartaoAntigo.setNomeTitular("Cliente Teste");
        cartaoAntigo.setLimite(new BigDecimal("3000"));
        Conta conta = new Conta();
        conta.setNome("Cliente Teste");
        cartaoAntigo.setConta(conta);

        when(cartaoRepository.findById(1L)).thenReturn(cartaoAntigo);

        ReemissaoCartaoDTO dto = new ReemissaoCartaoDTO();
        dto.setIdCartao(1L);
        dto.setMotivo(MotivoReemissao.DANO);

        Cartao novoCartao = cartaoService.solicitarReemissaoCartao(dto);

        // Verifica se o cartão antigo foi bloqueado
        assertEquals(StatusCartao.BLOQUEADO, cartaoAntigo.getStatus());
        // Verifica os dados do novo cartão
        assertNotNull(novoCartao);
        assertEquals("Cliente Teste", novoCartao.getNomeTitular());
        assertEquals(StatusCartao.PENDENTE, novoCartao.getStatus());
        assertEquals(new BigDecimal("3000"), novoCartao.getLimite());
    }

    @Test
    public void testeProcessarEntregaCartaoNaoEncontrado() {
        String trackingId = "trackingInvalido";
        DeliveryWebhookDTO dto = new DeliveryWebhookDTO();
        dto.setTrackingId(trackingId);
        dto.setDeliveryStatus("ENTREGUE");

        PanacheQuery<Cartao> query = Mockito.mock(PanacheQuery.class);
        when(cartaoRepository.find("numero", trackingId)).thenReturn(query);
        when(query.firstResultOptional()).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartaoService.processarEntregaCartao(dto);
        });
        assertTrue(exception.getMessage().contains("Cartão não encontrado com o tracking ID"));
    }

    @Test
    public void testeProcessarEntregaCartaoEntregue() {
        String trackingId = "4000001234567890";
        DeliveryWebhookDTO dto = new DeliveryWebhookDTO();
        dto.setTrackingId(trackingId);
        dto.setDeliveryStatus("ENTREGUE");

        Cartao cartao = new Cartao();
        cartao.setStatus(StatusCartao.PENDENTE);

        PanacheQuery<Cartao> query = Mockito.mock(PanacheQuery.class);
        when(cartaoRepository.find("numero", trackingId)).thenReturn(query);
        when(query.firstResultOptional()).thenReturn(Optional.of(cartao));

        cartaoService.processarEntregaCartao(dto);
        assertEquals(StatusCartao.ATIVO, cartao.getStatus());
    }

    @Test
    public void testeProcessarEntregaCartaoNaoEntregue() {
        String trackingId = "4000001234567890";
        DeliveryWebhookDTO dto = new DeliveryWebhookDTO();
        dto.setTrackingId(trackingId);
        dto.setDeliveryStatus("NAO_ENTREGUE");

        Cartao cartao = new Cartao();
        cartao.setStatus(StatusCartao.PENDENTE);

        PanacheQuery<Cartao> query = Mockito.mock(PanacheQuery.class);
        when(cartaoRepository.find("numero", trackingId)).thenReturn(query);
        when(query.firstResultOptional()).thenReturn(Optional.of(cartao));

        cartaoService.processarEntregaCartao(dto);
        // O status permanece inalterado
        assertEquals(StatusCartao.PENDENTE, cartao.getStatus());
    }

    @Test
    public void testeAtualizarCvvCartaoNaoEncontrado() {
        CvvWebhookDTO dto = new CvvWebhookDTO();
        dto.setCardId(1L);
        dto.setNextCvv("999");

        when(cartaoRepository.findById(1L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartaoService.atualizarCvvCartao(dto);
        });
        assertEquals("Cartão não encontrado.", exception.getMessage());
    }

    @Test
    public void testeAtualizarCvvCartaoCartaoNaoAtivo() {
        Cartao cartao = new Cartao();
        cartao.setStatus(StatusCartao.PENDENTE);
        when(cartaoRepository.findById(1L)).thenReturn(cartao);

        CvvWebhookDTO dto = new CvvWebhookDTO();
        dto.setCardId(1L);
        dto.setNextCvv("999");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartaoService.atualizarCvvCartao(dto);
        });
        assertEquals("O cartão não está ativo, não é possível atualizar o CVV.", exception.getMessage());
    }

    @Test
    public void testeAtualizarCvvCartaoSucesso() {
        Cartao cartao = new Cartao();
        cartao.setStatus(StatusCartao.ATIVO);
        cartao.setCvv("123");
        when(cartaoRepository.findById(1L)).thenReturn(cartao);

        CvvWebhookDTO dto = new CvvWebhookDTO();
        dto.setCardId(1L);
        dto.setNextCvv("999");

        cartaoService.atualizarCvvCartao(dto);
        assertEquals("999", cartao.getCvv());
    }
}
