package br.com.rodolfo.ferramenta.segmentacao.models.enums;

public enum TecidoTipo {

    ESCARA(1, "Escara"),
    ESFACELO(2, "Esfacelo"),
    GRANULACAO(3, "Granulação"),
    APAGAR(0, "Apagar");

    private Integer codigo;
    private String descricao;

    private TecidoTipo(Integer codigo, String descricao) {

        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {

        return this.codigo;
    }

    public String getDescricao() {

        return this.descricao;
    }

    public static TecidoTipo toEnum(String descricao) {
        
        if(descricao == null || descricao.equals("")) {

            return null;
        }

        for(TecidoTipo tipo : TecidoTipo.values()) {

            if(descricao.equals(tipo.descricao))
                return tipo;
        }

        throw new IllegalArgumentException("Descrição inválida : " + descricao);
    }
}