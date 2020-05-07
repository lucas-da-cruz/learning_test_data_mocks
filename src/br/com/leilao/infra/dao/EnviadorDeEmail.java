package br.com.leilao.infra.dao;

import br.com.leilao.dominio.Leilao;

public interface EnviadorDeEmail {    void envia(Leilao leilao);
}
