package fr.uha.miage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class EnumAnalyzerTest {

    @Test
    public void testEnumValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new EnumAnalyzer(String.class); // Not an enum
        });

        assertDoesNotThrow(() -> {
            new EnumAnalyzer(Jour.class); // Is an enum
        });
    }

    @Test
    public void testAnalyzeType() {
        EnumAnalyzer analyzer = new EnumAnalyzer(Jour.class);
        analyzer.analyzeType();
        String report = analyzer.generateReport();
        
        assertTrue(report.contains("## Type"));
        assertTrue(report.contains("Énumération** : oui"));
        assertTrue(report.contains("Nom** : Jour"));
        assertTrue(report.contains("Paquetage** : fr.uha.miage"));
        assertTrue(report.contains("Superclasse** : java.lang.Enum"));
    }

    @Test
    public void testAnalyzeConstants() {
        EnumAnalyzer analyzer = new EnumAnalyzer(Jour.class);
        analyzer.analyzeConstants();
        String report = analyzer.generateReport();
        
        assertTrue(report.contains("## Constantes"));
        assertTrue(report.contains("| LUNDI | 0 | Lundi | Non |"));
        assertTrue(report.contains("| DIMANCHE | 6 | Dimanche | Non |"));
    }

    @Test
    public void testAnalyzeFields() {
        EnumAnalyzer analyzer = new EnumAnalyzer(Jour.class);
        analyzer.analyzeFields();
        String report = analyzer.generateReport();
        
        assertTrue(report.contains("## Attributs"));
        assertTrue(report.contains("| LUNDI | Jour | public static final | Oui | Non |"));
        // L'énumération génère aussi un champ synthétique privé $VALUES
        assertTrue(report.contains("$VALUES"));
    }

    @Test
    public void testAnalyzeConstructors() {
        EnumAnalyzer analyzer = new EnumAnalyzer(Jour.class);
        analyzer.analyzeConstructors();
        String report = analyzer.generateReport();
        
        assertTrue(report.contains("## Constructeurs"));
        // Le compilateur génère le constructeur `private Jour(String, int)` 
        assertTrue(report.contains("private"));
        assertTrue(report.contains("String, int"));
    }

    @Test
    public void testAnalyzeMethods() {
        EnumAnalyzer analyzer = new EnumAnalyzer(Jour.class);
        analyzer.analyzeMethods();
        String report = analyzer.generateReport();
        
        assertTrue(report.contains("## Méthodes"));
        // Vérifier la présence des méthodes ajoutées/redéfinies
        assertTrue(report.contains("toString"));
        assertTrue(report.contains("estWeekend"));
        // La méthode values() synthétique ajoutée par le compilateur
        assertTrue(report.contains("values"));
    }

    @Test
    public void testAnalyzeAnnotations() {
        EnumAnalyzer analyzer = new EnumAnalyzer(Jour.class);
        analyzer.analyzeAnnotations();
        String report = analyzer.generateReport();
        
        assertTrue(report.contains("## Annotations"));
        // L'annotation personnalisée
        assertTrue(report.contains("@ExperimentalAnalysis"));
        // Vérifier l'analyse expérimentale déclenchée par l'annotation
        assertTrue(report.contains("### Analyse Expérimentale"));
        assertTrue(report.contains("#### Méthode `estWeekend()`"));
        assertTrue(report.contains("| SAMEDI | true |"));
    }

    @Test
    public void testEnumSimple() {
        EnumAnalyzer analyzer = new EnumAnalyzer(EnumSimple.class);
        analyzer.analyzeConstants();
        String report = analyzer.generateReport();
        assertTrue(report.contains("| UN | 0 | UN | Non |"));
    }

    @Test
    public void testEnumComportement() {
        EnumAnalyzer analyzer = new EnumAnalyzer(EnumComportement.class);
        analyzer.analyzeConstants();
        String report = analyzer.generateReport();
        assertTrue(report.contains("| CLASSIQUE | 0 | CLASSIQUE | Non |"));
        assertTrue(report.contains("| SPECIAL | 1 | SPECIAL | Oui |"));
    }

    @Test
    public void testEnumAttributs() {
        EnumAnalyzer analyzer = new EnumAnalyzer(EnumAttributs.class);
        analyzer.analyzeFields();
        analyzer.analyzeConstructors();
        String report = analyzer.generateReport();
        
        // Vérification attribut métier
        assertTrue(report.contains("| codeHexa | String | private final | Non | Non |"), "L'attribut codeHexa doit être listé");
        
        // Vérification constructeur métier
        assertTrue(report.contains("| fr.uha.miage.EnumAttributs | private | String, int, String | Non |"), "Le constructeur avec ses paramètres techniques (String, int) et métier (String) doit être détecté");
    }

    @Test
    public void testEnumInterface() {
        EnumAnalyzer analyzer = new EnumAnalyzer(EnumInterface.class);
        analyzer.analyzeInterfaces();
        String report = analyzer.generateReport();
        assertTrue(report.contains("java.lang.Runnable"), "L'interface Runnable doit être détectée");
    }

    @Test
    public void testProductionRapportComplet() {
        // La production du rapport complet doit également être testée (exigence PDF)
        EnumAnalyzer analyzer = new EnumAnalyzer(Jour.class);
        analyzer.analyzeType();
        analyzer.analyzeConstants();
        analyzer.analyzeFields();
        analyzer.analyzeConstructors();
        analyzer.analyzeMethods();
        analyzer.analyzeInterfaces();
        analyzer.analyzeAnnotations();
        
        String report = analyzer.generateReport();
        
        // On vérifie que toutes les sections majeures sont présentes dans l'ordre
        assertTrue(report.indexOf("## Type") < report.indexOf("## Constantes"));
        assertTrue(report.indexOf("## Constantes") < report.indexOf("## Attributs"));
        assertTrue(report.indexOf("## Attributs") < report.indexOf("## Constructeurs"));
        assertTrue(report.indexOf("## Constructeurs") < report.indexOf("## Méthodes"));
        assertTrue(report.indexOf("## Méthodes") < report.indexOf("## Interfaces"));
        assertTrue(report.indexOf("## Interfaces") < report.indexOf("## Annotations"));
    }
}
