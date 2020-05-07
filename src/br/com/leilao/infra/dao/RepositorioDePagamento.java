package br.com.leilao.infra.dao;

import br.com.leilao.dominio.Pagamento;

public interface RepositorioDePagamento {

    void salva(Pagamento pagamento);
}
