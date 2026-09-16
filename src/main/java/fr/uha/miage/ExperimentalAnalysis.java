package fr.uha.miage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indiquant que la méthode doit être testée de façon expérimentale
 * par l'analyseur sur toutes les constantes de l'énumération.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExperimentalAnalysis {
    String description() default "Analyse expérimentale";
}

