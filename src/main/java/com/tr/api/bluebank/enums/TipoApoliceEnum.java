package com.tr.api.bluebank.enums;

public enum TipoApoliceEnum {
    VIAGEM("Proteção global para suas aventuras. Viaje com tranquilidade, sabendo que você está coberto em caso de emergências médicas, perda de bagagem e cancelamentos inesperados."),
    FRAUDE("Segurança em cada transação. Proteja-se contra atividades fraudulentas, desde roubo de identidade até transações não autorizadas, garantindo que suas finanças e informações pessoais estejam sempre seguras.");

    private final String descricao;

    TipoApoliceEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
