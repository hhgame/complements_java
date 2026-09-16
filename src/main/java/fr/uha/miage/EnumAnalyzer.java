package fr.uha.miage;

import java.lang.reflect.Modifier;

public class EnumAnalyzer {

    private final Class<?> enumClass;
    private StringBuilder reportBuilder;

    public EnumAnalyzer(Class<?> enumClass) {
        if (enumClass == null) {
            throw new IllegalArgumentException("La classe à analyser ne peut pas être null.");
        }
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException("La classe fournie n'est pas une énumération : " + enumClass.getName());
        }
        this.enumClass = enumClass;
        this.reportBuilder = new StringBuilder();
    }

    /**
     * Analyse 1. Vérification et identification du type
     */
    public void analyzeType() {
        reportBuilder.append("## Type\n\n");
        reportBuilder.append("- **Énumération** : oui\n");
        reportBuilder.append("- **Nom** : ").append(enumClass.getSimpleName()).append("\n");
        reportBuilder.append("- **Nom pleinement qualifié** : ").append(enumClass.getName()).append("\n");
        reportBuilder.append("- **Paquetage** : ").append(enumClass.getPackageName()).append("\n");
        
        String modifiers = Modifier.toString(enumClass.getModifiers());
        reportBuilder.append("- **Modificateurs** : ").append(modifiers.isEmpty() ? "aucun" : modifiers).append("\n");
        
        Class<?> superClass = enumClass.getSuperclass();
        reportBuilder.append("- **Superclasse** : ").append(superClass != null ? superClass.getName() : "aucune").append("\n");
        reportBuilder.append("\n");
    }

    /**
     * Analyse 2. Analyse des constantes
     */
    public void analyzeConstants() {
        reportBuilder.append("## Constantes\n\n");
        Object[] constants = enumClass.getEnumConstants();
        if (constants == null || constants.length == 0) {
            reportBuilder.append("Aucune constante déclarée.\n\n");
            return;
        }

        reportBuilder.append("| Constante | ordinal() | toString() | Sous-classe propre |\n");
        reportBuilder.append("|---|---|---|---|\n");

        for (Object constant : constants) {
            Enum<?> enumConstant = (Enum<?>) constant;
            
            String name = enumConstant.name();
            int ordinal = enumConstant.ordinal();
            String toStringVal = enumConstant.toString();
            
            // Si la classe de l'objet est différente de la classe de l'énumération,
            // alors cette constante a son propre corps de classe (sous-classe anonyme).
            boolean hasOwnClassBody = (enumConstant.getClass() != enumClass);

            reportBuilder.append(String.format("| %s | %d | %s | %s |\n", 
                name, ordinal, toStringVal, hasOwnClassBody ? "Oui" : "Non"));
        }
        reportBuilder.append("\n");
    }

    /**
     * Analyse 3. Analyse des attributs
     */
    public void analyzeFields() {
        reportBuilder.append("## Attributs\n\n");
        java.lang.reflect.Field[] fields = enumClass.getDeclaredFields();
        if (fields.length == 0) {
            reportBuilder.append("Aucun attribut déclaré.\n\n");
            return;
        }

        reportBuilder.append("| Nom | Type | Modificateurs | EnumConstant | Synthétique |\n");
        reportBuilder.append("|---|---|---|---|---|\n");

        for (java.lang.reflect.Field field : fields) {
            String name = field.getName();
            String type = field.getType().getSimpleName();
            String modifiers = java.lang.reflect.Modifier.toString(field.getModifiers());
            boolean isEnumConstant = field.isEnumConstant();
            boolean isSynthetic = field.isSynthetic();

            reportBuilder.append(String.format("| %s | %s | %s | %s | %s |\n",
                name, type, modifiers.isEmpty() ? "aucun" : modifiers,
                isEnumConstant ? "Oui" : "Non",
                isSynthetic ? "Oui" : "Non"));
        }
        reportBuilder.append("\n");
    }

    /**
     * Analyse 4. Analyse des constructeurs
     */
    public void analyzeConstructors() {
        reportBuilder.append("## Constructeurs\n\n");
        // Les constructeurs d'énumérations ne sont pas publics, il faut utiliser getDeclaredConstructors()
        java.lang.reflect.Constructor<?>[] constructors = enumClass.getDeclaredConstructors();
        
        if (constructors.length == 0) {
            reportBuilder.append("Aucun constructeur déclaré.\n\n");
            return;
        }

        reportBuilder.append("| Constructeur | Modificateurs | Paramètres | Synthétique |\n");
        reportBuilder.append("|---|---|---|---|\n");

        for (java.lang.reflect.Constructor<?> constructor : constructors) {
            String name = constructor.getName();
            String modifiers = java.lang.reflect.Modifier.toString(constructor.getModifiers());
            boolean isSynthetic = constructor.isSynthetic();

            // Construction de la chaîne des paramètres
            Class<?>[] paramTypes = constructor.getParameterTypes();
            StringBuilder paramsBuilder = new StringBuilder();
            if (paramTypes.length == 0) {
                paramsBuilder.append("aucun");
            } else {
                for (int i = 0; i < paramTypes.length; i++) {
                    paramsBuilder.append(paramTypes[i].getSimpleName());
                    if (i < paramTypes.length - 1) {
                        paramsBuilder.append(", ");
                    }
                }
            }

            reportBuilder.append(String.format("| %s | %s | %s | %s |\n",
                name, modifiers.isEmpty() ? "aucun" : modifiers,
                paramsBuilder.toString(),
                isSynthetic ? "Oui" : "Non"));
        }
        reportBuilder.append("\n");
    }

    /**
     * Analyse 5 & 6. Analyse des méthodes et des annotations
     */
    public void analyzeMethods() {
        reportBuilder.append("## Méthodes\n\n");
        java.lang.reflect.Method[] methods = enumClass.getDeclaredMethods();
        
        if (methods.length == 0) {
            reportBuilder.append("Aucune méthode déclarée.\n\n");
            return;
        }

        reportBuilder.append("| Méthode | Retour | Paramètres | Modificateurs | Synthétique |\n");
        reportBuilder.append("|---|---|---|---|---|\n");

        for (java.lang.reflect.Method method : methods) {
            String name = method.getName();
            String returnType = method.getReturnType().getSimpleName();
            String modifiers = java.lang.reflect.Modifier.toString(method.getModifiers());
            boolean isSynthetic = method.isSynthetic();

            // Paramètres
            Class<?>[] paramTypes = method.getParameterTypes();
            StringBuilder paramsBuilder = new StringBuilder();
            if (paramTypes.length == 0) {
                paramsBuilder.append("aucun");
            } else {
                for (int i = 0; i < paramTypes.length; i++) {
                    paramsBuilder.append(paramTypes[i].getSimpleName());
                    if (i < paramTypes.length - 1) paramsBuilder.append(", ");
                }
            }

            reportBuilder.append(String.format("| %s | %s | %s | %s | %s |\n",
                name, returnType, paramsBuilder.toString(),
                modifiers.isEmpty() ? "aucun" : modifiers,
                isSynthetic ? "Oui" : "Non"));
        }
        reportBuilder.append("\n");
    }

    /**
     * Analyse des interfaces implémentées par l'énumération.
     */
    public void analyzeInterfaces() {
        reportBuilder.append("## Interfaces\n\n");
        Class<?>[] interfaces = enumClass.getInterfaces();
        reportBuilder.append("- **Interfaces directement déclarées** : ");
        if (interfaces.length == 0) {
            reportBuilder.append("aucune\n");
        } else {
            for (int i = 0; i < interfaces.length; i++) {
                reportBuilder.append(interfaces[i].getName());
                if (i < interfaces.length - 1) {
                    reportBuilder.append(", ");
                }
            }
            reportBuilder.append("\n");
        }

        // Recherche des interfaces héritées (ex: via java.lang.Enum)
        java.util.Set<String> inheritedInterfaces = new java.util.HashSet<>();
        Class<?> superClass = enumClass.getSuperclass();
        while (superClass != null) {
            for (Class<?> intf : superClass.getInterfaces()) {
                inheritedInterfaces.add(intf.getName());
            }
            superClass = superClass.getSuperclass();
        }
        
        reportBuilder.append("- **Interfaces héritées (via superclasse)** : ");
        if (inheritedInterfaces.isEmpty()) {
            reportBuilder.append("aucune\n");
        } else {
            reportBuilder.append(String.join(", ", inheritedInterfaces)).append("\n");
        }
        reportBuilder.append("\n");
    }

    /**
     * Analyse de toutes les annotations (classe, attributs, constructeurs, méthodes).
     */
    public void analyzeAnnotations() {
        reportBuilder.append("## Annotations\n\n");
        boolean foundAny = false;

        // Annotations sur le type
        java.lang.annotation.Annotation[] typeAnnotations = enumClass.getAnnotations();
        if (typeAnnotations.length > 0) {
            foundAny = true;
            reportBuilder.append("### Sur le type (classe)\n");
            for (java.lang.annotation.Annotation a : typeAnnotations) {
                reportBuilder.append("- `@").append(a.annotationType().getSimpleName()).append("`\n");
            }
            reportBuilder.append("\n");
        }

        // Annotations sur les attributs
        boolean hasFieldAnnos = false;
        for (java.lang.reflect.Field field : enumClass.getDeclaredFields()) {
            java.lang.annotation.Annotation[] fieldAnnotations = field.getAnnotations();
            if (fieldAnnotations.length > 0) {
                if (!hasFieldAnnos) {
                    reportBuilder.append("### Sur les attributs\n");
                    hasFieldAnnos = true;
                    foundAny = true;
                }
                for (java.lang.annotation.Annotation a : fieldAnnotations) {
                    reportBuilder.append("- `").append(field.getName()).append("` : `@").append(a.annotationType().getSimpleName()).append("`\n");
                }
            }
        }
        if (hasFieldAnnos) reportBuilder.append("\n");

        // Annotations sur les constructeurs
        boolean hasCtorAnnos = false;
        for (java.lang.reflect.Constructor<?> ctor : enumClass.getDeclaredConstructors()) {
            java.lang.annotation.Annotation[] ctorAnnotations = ctor.getAnnotations();
            if (ctorAnnotations.length > 0) {
                if (!hasCtorAnnos) {
                    reportBuilder.append("### Sur les constructeurs\n");
                    hasCtorAnnos = true;
                    foundAny = true;
                }
                for (java.lang.annotation.Annotation a : ctorAnnotations) {
                    reportBuilder.append("- `").append(ctor.getName()).append("` : `@").append(a.annotationType().getSimpleName()).append("`\n");
                }
            }
        }
        if (hasCtorAnnos) reportBuilder.append("\n");

        // Annotations sur les méthodes
        boolean hasMethodAnnos = false;
        java.util.List<java.lang.reflect.Method> experimentalMethods = new java.util.ArrayList<>();
        for (java.lang.reflect.Method method : enumClass.getDeclaredMethods()) {
            java.lang.annotation.Annotation[] methodAnnotations = method.getAnnotations();
            if (methodAnnotations.length > 0) {
                if (!hasMethodAnnos) {
                    reportBuilder.append("### Sur les méthodes\n");
                    hasMethodAnnos = true;
                    foundAny = true;
                }
                for (java.lang.annotation.Annotation a : methodAnnotations) {
                    reportBuilder.append("- `").append(method.getName()).append("` : `@").append(a.annotationType().getSimpleName()).append("`\n");
                    if (a instanceof ExperimentalAnalysis) {
                        experimentalMethods.add(method);
                    }
                }
            }
        }
        if (hasMethodAnnos) reportBuilder.append("\n");

        if (!foundAny) {
            reportBuilder.append("Aucune annotation détectée dans l'énumération.\n\n");
        }

        // Expérimentation basée sur l'annotation personnalisée
        if (!experimentalMethods.isEmpty()) {
            reportBuilder.append("### Analyse Expérimentale déclenchée par annotation\n\n");
            Object[] constants = enumClass.getEnumConstants();
            for (java.lang.reflect.Method method : experimentalMethods) {
                ExperimentalAnalysis anno = method.getAnnotation(ExperimentalAnalysis.class);
                reportBuilder.append("#### Méthode `").append(method.getName()).append("()` - ").append(anno.description()).append("\n\n");
                
                if (method.getParameterCount() > 0) {
                    reportBuilder.append("*La méthode nécessite des paramètres, l'invocation automatique est ignorée.*\n\n");
                    continue;
                }

                reportBuilder.append("| Constante | Résultat (ou Exception) |\n");
                reportBuilder.append("|---|---|\n");
                for (Object constant : constants) {
                    try {
                        method.setAccessible(true);
                        Object result = method.invoke(constant);
                        reportBuilder.append(String.format("| %s | %s |\n", ((Enum<?>) constant).name(), result));
                    } catch (Exception e) {
                        reportBuilder.append(String.format("| %s | Erreur : %s |\n", ((Enum<?>) constant).name(), e.getCause() != null ? e.getCause().toString() : e.toString()));
                    }
                }
                reportBuilder.append("\n");
            }
        }
    }

    public String generateReport() {
        return reportBuilder.toString();
    }
}
