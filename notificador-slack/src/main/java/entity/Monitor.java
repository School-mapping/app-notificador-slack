package entity;

import jdbc.BancoRepositorio;

public class Monitor {
    private BancoRepositorio banco;

    public Boolean verificarCondicao(Configuracao config){

        if ("NOVAS_VERBAS".equalsIgnoreCase(config.getTipoAlerta())){
            return banco.existemNovasVerbas(config.getUltimoDisparo());
        } else if ("NOVAS_ESCOLAS".equalsIgnoreCase(config.getTipoAlerta())) {
            return banco.existemNovasEscolas(config.getUltimoDisparo());
        } else if ("NOVAS_NOTAS".equalsIgnoreCase(config.getTipoAlerta())) {
            return banco.existemNovasNotas(config.getUltimoDisparo());
        }

        return false;
    }

    public String gerarMensagem(Configuracao config){
        if ("NOVAS_VERBAS".equalsIgnoreCase(config.getTipoAlerta())) {
            return "🚨 Alerta School Mapping: Novas verbas detectadas!";
        } else if ("NOVAS_ESCOLAS".equalsIgnoreCase(config.getTipoAlerta())) {
            return "🚨 Alerta School Mapping: Novas escolas detectadas!";
        } else if ("NOVAS_NOTAS".equalsIgnoreCase(config.getTipoAlerta())) {
            return "🚨 Alerta School Mapping: Novas notas detectadas!";
        }
        return null;
    }
}
