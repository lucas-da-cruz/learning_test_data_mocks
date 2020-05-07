package br.com.leilao.servico;

import br.com.leilao.builder.CriadorDeLeilao;
import br.com.leilao.dominio.Leilao;
import br.com.leilao.infra.dao.EnviadorDeEmail;
import br.com.leilao.infra.dao.RepositorioDeLeiloes;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class EncerradorDeLeilaoTest {

    @Test
    public void deveEncerrarLeiloesQueComecaramUmaSemanaAtras() {

        Calendar antiga = Calendar.getInstance();
        antiga.set(1999, 1, 20);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
                .naData(antiga).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira")
                .naData(antiga).constroi();

        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

        EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
        encerrador.encerra();

        assertEquals(2, encerrador.getTotalEncerrados());
        assertTrue(leilao1.isEncerrado());
        assertTrue(leilao2.isEncerrado());
    }

    @Test
    public void deveEncerrarLeiloesQueComecaramUmaSemanaAtrasByMe() {
        //Cria data
        Calendar antiga = Calendar.getInstance();
        antiga.set(1999, 1, 20);

        //Cria leilão via test data builder
        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(antiga).constroi();

        //Adiciono os objetos em uma lista do tipo Leilao
        List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);

        //Crio um objeto do tipo LeilaoDao o método mock
        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);

        //Quando o método correntes for acionado, vai me retornar a lista leiloesAntigos
        when(daoFalso.correntes()).thenReturn(leiloesAntigos);

        //Encerro Leilão
        EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
        encerrador.encerra();

        // vamos conferir tambem o tamanho da lista!
        assertEquals(2, encerrador.getTotalEncerrados());
        //Usamos o assertTrue para validação do tipo boolean
        assertTrue(leilao1.isEncerrado());
        assertTrue(leilao2.isEncerrado());
    }

    @Test
    public void naoDeveEncerrarLeiloesQueComecaramMenosDeUmaSemanaAtras() {
        //Cria data de ontem
        Calendar ontem = Calendar.getInstance();
        ontem.add(Calendar.DAY_OF_MONTH, -1);

        //Cria leilão via test data builder
        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(ontem).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(ontem).constroi();
        List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);

        //Falo ao mockito criar um mock da classe LeilaoDao
        //O método devolverá uma LeilaoDao
        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);

        //Quando alguém invocar o método correntes,
        //Nosso mock fará o método correntes() retornar a lista leiloesAntigos
        when(daoFalso.correntes()).thenReturn(leiloesAntigos);

        EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
        EncerradorDeLeilao encerrador =
                new EncerradorDeLeilao(daoFalso, carteiroFalso);
        encerrador.encerra();

        assertEquals(0, encerrador.getTotalEncerrados());
        assertFalse(leilao1.isEncerrado());
        assertFalse(leilao2.isEncerrado());

        verify(daoFalso, never()).atualiza(leilao1);
        verify(daoFalso, never()).atualiza(leilao2);
    }

    @Test
    public void naoDeveEncerrarLeiloesCasoNaoHajaNenhum() {

        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
        when(daoFalso.correntes()).thenReturn(new ArrayList<Leilao>());

        EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);

        encerrador.encerra();

        assertEquals(0, encerrador.getTotalEncerrados());
    }

    @Test
    public void deveAtualizarLeiloesEncerrados(){
        Calendar antiga = Calendar.getInstance();
        antiga.set(1999, 1, 20);

        Leilao leilao1 = new CriadorDeLeilao().para("Tv de Plasma").naData(antiga).constroi();

        //Criando um mock de RepositorioDeLeiloes
        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);

        //O mock criado responderá leilao1 quando o método correntes for invocado
        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1));

        EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
        EncerradorDeLeilao encerrador =
                new EncerradorDeLeilao(daoFalso, carteiroFalso);

        //Aqui é chamado o metodo dao.atualiza()
        encerrador.encerra();

        //Esse metodo mockito garante que se o metodo passado foi invocado
        //Aqui estamos falando ao mockito falar o teste, caso o metodo não seja invocado
        //O metodo foi invocado no encerrador.encerra()
        verify(daoFalso).atualiza(leilao1);

        //abaixo indico que ele só deveria ser acionado,  uma única vez
        //verify(daoFalso, times(1)).atualiza(leilao1);

        //abaixo indico que o metodo atualiza, nunca deveria ser invocado p/ retornar success
        //verify(daoFalso, never()).atualiza(leilao1);

    }

    @Test
    public void deveEnviarEmailAposPersistirLeilaoEncerrado() {
        Calendar antiga = Calendar.getInstance();
        antiga.set(1999, 1, 20);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
                .naData(antiga).constroi();

        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1));

        EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
        EncerradorDeLeilao encerrador =
                new EncerradorDeLeilao(daoFalso, carteiroFalso);

        encerrador.encerra();

        //Teste para ver se realmente é enviado ao email
        // passamos os mocks que serao verificados
        InOrder inOrder = inOrder(daoFalso, carteiroFalso);
        // a primeira invocação
        inOrder.verify(daoFalso, times(1)).atualiza(leilao1);
        // a segunda invocação
        inOrder.verify(carteiroFalso, times(1)).envia(leilao1);
    }

    @Test
    public void deveContinuarAExecucaoMesmoQuandoDaoFalha(){
        Calendar antiga = Calendar.getInstance();
        antiga.set(1999, 1, 20);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(antiga).constroi();

        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
        EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);

        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

        //Esse método faz com que seja lançada uma exception, quando
        //o método atualiza(leilao1) for invocado
        doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao1);

        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
        encerrador.encerra();

        //O verify garante que o métodos atualiza e envia
        //foram invocados passando leilao2
        verify(daoFalso).atualiza(leilao2);
        verify(carteiroFalso).envia(leilao2);

        verify(carteiroFalso, times(0)).envia(leilao1);
    }

    @Test
    public void deveDesistirSeDaoFalhaPraSempre(){
        Calendar antiga = Calendar.getInstance();
        antiga.set(1999, 1, 20);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(antiga).constroi();

        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
        EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);

        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

        //Esse método faz com que seja lançada uma exception, quando
        //o método atualiza(leilao1) for invocado
        /*doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao1);
        doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao2);*/
        //Maneira que podemos generalizar o acionamento dde leilao1 ou leilao2
        doThrow(new RuntimeException()).when(daoFalso).atualiza(any(Leilao.class));

        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
        encerrador.encerra();

        /*verify(carteiroFalso, never()).envia(leilao1);
        verify(carteiroFalso, never()).envia(leilao2);*/
        verify(carteiroFalso, never()).envia(any(Leilao.class));
    }
}
