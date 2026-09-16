# Hugo HAMMER - M2 MIAGE FI

# complements-java-2026-2027

Projet 1 — Développer une bibliothèque d’analyse des énumérations Java

Ce dépôt contient la bibliothèque `EnumAnalyzer`, conçue pour analyser dynamiquement les énumérations Java via l'introspection.

## Structure du projet

*   `src/main/java/fr/uha/miage/` : Code source de la bibliothèque (`EnumAnalyzer`, `@ExperimentalAnalysis`).
*   `src/test/java/fr/uha/miage/` : Tests unitaires JUnit (`EnumAnalyzerTest`, `Jour`).
*   `demo/` : Un projet Maven de démonstration illustrant la consommation de la bibliothèque.

## 1. Compiler et tester la bibliothèque

Sous Windows :
```bash
.\mvnw.cmd clean test
```

Sous Linux/macOS :
```bash
./mvnw clean test
```

## 2. Installer la bibliothèque localement

Pour pouvoir utiliser `EnumAnalyzer` dans d'autres projets locaux (comme le module de démo), vous devez installer le JAR dans votre dépôt Maven local (`~/.m2/repository`).

Sous Windows :
```bash
.\mvnw.cmd install
```

Sous Linux/macOS :
```bash
./mvnw install
```

## 3. Utiliser le projet de démonstration

Le dossier `demo` contient un projet Maven indépendant qui inclut `complements-java` comme dépendance. Il contient une énumération `Saison` et un programme `Main` qui génère un rapport Markdown.

Pour exécuter la démonstration :

1.  Déplacez-vous dans le dossier `demo` :
    ```bash
    cd demo
    ```
2.  Exécutez la classe Main via Maven :
    Sous Windows :
    ```bash
    ..\mvnw.cmd clean compile exec:java -D"exec.mainClass"="fr.uha.miage.demo.Main"
    ```
    Sous Linux/macOS :
    ```bash
    ../mvnw clean compile exec:java -Dexec.mainClass="fr.uha.miage.demo.Main"
    ```

Le rapport sera affiché dans la console et sauvegardé dans le fichier `demo/rapport_saison.md`.
