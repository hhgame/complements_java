package fr.uha.miage.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import fr.uha.miage.EnumAnalyzer;

public class Main {
    public static void main(String[] args) {
        System.out.println("Démarrage de l'analyse de l'énumération Saison...");
        
        EnumAnalyzer analyzer = new EnumAnalyzer(Saison.class);
        analyzer.analyzeType();
        analyzer.analyzeConstants();
        analyzer.analyzeFields();
        analyzer.analyzeConstructors();
        analyzer.analyzeMethods();
        analyzer.analyzeInterfaces();
        analyzer.analyzeAnnotations();
        
        String report = analyzer.generateReport();
        
        System.out.println("Rapport généré avec succès !");
        System.out.println("==================================================");
        System.out.println(report);
        System.out.println("==================================================");
        
        try {
            Path outputPath = Paths.get("rapport_saison.md");
            Files.writeString(outputPath, report);
            System.out.println("Rapport sauvegardé dans le fichier : " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde du rapport : " + e.getMessage());
        }
    }
}

