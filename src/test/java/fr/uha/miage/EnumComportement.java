package fr.uha.miage;

public enum EnumComportement {
    CLASSIQUE,
    SPECIAL {
        @Override
        public String getMessage() {
            return "Je suis spécial !";
        }
    };

    public String getMessage() {
        return "Je suis classique";
    }
}

