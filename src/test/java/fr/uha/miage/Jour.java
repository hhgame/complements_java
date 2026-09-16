package fr.uha.miage;

public enum Jour {
    LUNDI,
    MARDI,
    MERCREDI,
    JEUDI,
    VENDREDI,
    SAMEDI,
    DIMANCHE;

    @Override
    public String toString() {
        return super.toString().charAt(0) + super.toString().substring(1).toLowerCase();
    }

    @ExperimentalAnalysis(description = "Vérifie si le jour est un weekend pour chaque constante")
    public boolean estWeekend() {
        return this == SAMEDI || this == DIMANCHE;
    }
}

