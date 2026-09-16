package fr.uha.miage.demo;

import fr.uha.miage.ExperimentalAnalysis;

public enum Saison {
    PRINTEMPS("Il fait doux"),
    ETE("Il fait chaud"),
    AUTOMNE("Les feuilles tombent"),
    HIVER("Il neige");

    private final String description;

    private Saison(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @ExperimentalAnalysis(description = "Vérifie si la saison est estivale")
    public boolean estEstival() {
        return this == ETE;
    }
}

