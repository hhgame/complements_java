package fr.uha.miage;

public enum EnumAttributs {
    ROUGE("#FF0000"),
    VERT("#00FF00"),
    BLEU("#0000FF");

    private final String codeHexa;

    private EnumAttributs(String codeHexa) {
        this.codeHexa = codeHexa;
    }

    public String getCodeHexa() {
        return codeHexa;
    }
}

