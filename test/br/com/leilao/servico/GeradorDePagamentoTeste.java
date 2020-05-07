package br.com.leilao.servico;

import br.com.leilao.builder.CriadorDeLeilao;
import br.com.leilao.dominio.Leilao;
import br.com.leilao.dominio.Pagamento;
import br.com.leilao.dominio.Usuario;
import br.com.leilao.infra.dao.Relogio;
import br.com.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.leilao.infra.dao.RepositorioDePagamento;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GeradorDePagamentoTeste {

    @Test
    public void deveGerarPagamentoParaUmLeilaoEncerrado(){

        //Criacao de cenario
        RepositorioDeLeiloes leiloes = mock(RepositorioDeLeiloes.class);
        RepositorioDePagamento pagamentos = mock(RepositorioDePagamento.class);
        Avaliador avaliador = mock(Avaliador.class);

        Leilao leilao = new CriadorDeLeilao().para("Playstation")
                .lance(new Usuario("José da Silva"), 2000.0)
                .lance(new Usuario("Maria Pereira"), 2500.0).constroi();

        when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));

        when(avaliador.getMaiorLance()).thenReturn(2500.0);

        //acao
        GeradorDePagamento gerador = new GeradorDePagamento(leiloes, pagamentos, avaliador);
        gerador.gera();

        //validacao
        //Capturando argumentos da minha classe pagamento por meio de pagamento
        ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
        verify(pagamentos).salva(argumento.capture());

        Pagamento pagamentoGerado = argumento.getValue();

        assertEquals(2500.0, pagamentoGerado.getValor(), 0.00001);
    }

    @Test
    public void deveEmpurrarParaOProximoDiaUtil(){
        RepositorioDeLeiloes leiloes = mock(RepositorioDeLeiloes.class);
        RepositorioDePagamento pagamentos = mock(RepositorioDePagamento.class);
        Relogio relogio = mock(Relogio.class);

        Leilao leilao = new CriadorDeLeilao()
                .para("Playstantion")
                .lance(new Usuario("João da Silva"), 2000.0)
                .lance(new Usuario("Maiara Silva"), 2500.0)
                .constroi();

        when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));

        Calendar sabado = Calendar.getInstance();
        sabado.set(2012, Calendar.APRIL, 7);

        //Vai simular o retorno da data como se fosse sábado
        when(relogio.hoje()).thenReturn(sabado);

        GeradorDePagamento gerador = new GeradorDePagamento(leiloes, pagamentos, new Avaliador(), relogio);
        gerador.gera();

        ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);

        verify(pagamentos).salva(argumento.capture());

        Pagamento pagamentoGerado = argumento.getValue();

        assertEquals(Calendar.MONDAY, pagamentoGerado.getData().get(Calendar.DAY_OF_WEEK));
        assertEquals(9, pagamentoGerado.getData().get(Calendar.DAY_OF_MONTH));
    }
}
