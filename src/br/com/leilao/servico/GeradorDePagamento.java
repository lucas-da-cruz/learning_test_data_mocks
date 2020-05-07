package br.com.leilao.servico;

import br.com.leilao.dominio.Leilao;
import br.com.leilao.dominio.Pagamento;
import br.com.leilao.infra.dao.Relogio;
import br.com.leilao.infra.dao.RelogioDoSistema;
import br.com.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.leilao.infra.dao.RepositorioDePagamento;

import java.util.Calendar;
import java.util.List;

public class GeradorDePagamento {

    private final RepositorioDeLeiloes leiloes;
    private final Avaliador avaliador;
    private final RepositorioDePagamento pagamentos;
    private final Relogio relogio;

    public GeradorDePagamento(RepositorioDeLeiloes leiloes, RepositorioDePagamento pagamentos, Avaliador avaliador, Relogio relogio){
        this.leiloes = leiloes;
        this.avaliador = avaliador;
        this.pagamentos = pagamentos;
        this.relogio = relogio;
    }

    public GeradorDePagamento(RepositorioDeLeiloes leiloes, RepositorioDePagamento pagamentos, Avaliador avaliador) {
        this(leiloes, pagamentos, avaliador, new RelogioDoSistema());
    }

        public void gera(){
        List<Leilao> leiloesEncerrados = this.leiloes.encerrados();

        for(Leilao leilao : leiloesEncerrados){
            this.avaliador.avalia(leilao);

            Pagamento novoPagamento = new Pagamento(avaliador.getMaiorLance(), primeiroDiaUtil());
            this.pagamentos.salva(novoPagamento);
        }
    }

    private Calendar primeiroDiaUtil() {
        Calendar data = relogio.hoje();
        int diaDaSemana = data.get(Calendar.DAY_OF_WEEK);

        if(diaDaSemana == Calendar.SATURDAY) data.add(Calendar.DAY_OF_MONTH, 2);
        if(diaDaSemana == Calendar.SUNDAY) data.add(Calendar.DAY_OF_MONTH, 1);

        return data;
    }
}
