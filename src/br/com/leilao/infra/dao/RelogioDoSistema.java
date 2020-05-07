package br.com.leilao.infra.dao;

import java.util.Calendar;

public class RelogioDoSistema implements Relogio {

    public Calendar hoje() {
        return Calendar.getInstance();
    }
}
