package fr.uha.miage;

import java.lang.reflect.Modifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EnumAnalyzer {

    private final Class<?> enumClass;
    private StringBuilder reportBuilder;
    private ObjectMapper mapper;
    private ObjectNode jsonRoot;

    public EnumAnalyzer(Class<?> enumClass) {
        if (enumClass == null) {
            throw new IllegalArgumentException("La classe à analyser ne peut pas être null.");
        }
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException("La classe fournie n'est pas une énumération : " + enumClass.getName());
        }
        this.enumClass = enumClass;
        this.reportBuilder = new StringBuilder();
        this.mapper = new ObjectMapper();
        this.jsonRoot = mapper.createObjectNode();
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

        // Alimentation du JSON
        ObjectNode typeNode = jsonRoot.putObject("type");
        typeNode.put("enumeration", true);
        typeNode.put("nom", enumClass.getSimpleName());
        typeNode.put("nomComplet", enumClass.getName());
        typeNode.put("paquetage", enumClass.getPackageName());
        typeNode.put("modificateurs", modifiers.isEmpty() ? "aucun" : modifiers);
        typeNode.put("superclasse", superClass != null ? superClass.getName() : "aucune");
    }

    /**
     * Analyse 2. Analyse des constantes (avec approfondissement : valeurs des attributs)
     */
    public void analyzeConstants() {
        reportBuilder.append("## Constantes\n\n");
        Object[] constants = enumClass.getEnumConstants();
        ArrayNode constArray = jsonRoot.putArray("constantes");
        
        if (constants == null || constants.length == 0) {
            reportBuilder.append("Aucune constante déclarée.\n\n");
            return;
        }

        // Identifier les attributs "métier" (non synthétiques, non constantes d'enum)
        java.util.List<java.lang.reflect.Field> metierFields = new java.util.ArrayList<>();
        for (java.lang.reflect.Field f : enumClass.getDeclaredFields()) {
            if (!f.isEnumConstant() && !f.isSynthetic()) {
                metierFields.add(f);
            }
        }

        // Construction de l'en-tête du tableau
        reportBuilder.append("| Constante | ordinal() | toString() | Sous-classe propre |");
        for (java.lang.reflect.Field f : metierFields) {
            reportBuilder.append(" Val: ").append(f.getName()).append(" |");
        }
        reportBuilder.append("\n|");
        reportBuilder.append("---|---|---|---|");
        for (int i = 0; i < metierFields.size(); i++) {
            reportBuilder.append("---|");
        }
        reportBuilder.append("\n");

        for (Object constant : constants) {
            Enum<?> enumConstant = (Enum<?>) constant;
            
            String name = enumConstant.name();
            int ordinal = enumConstant.ordinal();
            String toStringVal = enumConstant.toString();
            boolean hasOwnClassBody = (enumConstant.getClass() != enumClass);

            reportBuilder.append(String.format("| %s | %d | %s | %s |", 
                name, ordinal, toStringVal, hasOwnClassBody ? "Oui" : "Non"));
                
            ObjectNode constNode = constArray.addObject();
            constNode.put("nom", name);
            constNode.put("ordinal", ordinal);
            constNode.put("toString", toStringVal);
            constNode.put("sousClassePropre", hasOwnClassBody);
            ObjectNode valNode = constNode.putObject("valeursAttributs");

            // Extraction des valeurs des attributs métier pour cette constante
            for (java.lang.reflect.Field f : metierFields) {
                try {
                    f.setAccessible(true);
                    Object value = f.get(constant);
                    reportBuilder.append(" ").append(value != null ? value.toString() : "null").append(" |");
                    valNode.put(f.getName(), value != null ? value.toString() : "null");
                } catch (Exception e) {
                    reportBuilder.append(" erreur |");
                    valNode.put(f.getName(), "erreur");
                }
            }
            reportBuilder.append("\n");
        }
        reportBuilder.append("\n");
    }

    /**
     * Analyse 3. Analyse des attributs
     */
    public void analyzeFields() {
        reportBuilder.append("## Attributs\n\n");
        java.lang.reflect.Field[] fields = enumClass.getDeclaredFields();
        ArrayNode fieldsArray = jsonRoot.putArray("attributs");

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
                
            ObjectNode fNode = fieldsArray.addObject();
            fNode.put("nom", name);
            fNode.put("type", type);
            fNode.put("modificateurs", modifiers.isEmpty() ? "aucun" : modifiers);
            fNode.put("isEnumConstant", isEnumConstant);
            fNode.put("isSynthetic", isSynthetic);
        }
        reportBuilder.append("\n");
    }

    /**
     * Analyse 4. Analyse des constructeurs
     */
    public void analyzeConstructors() {
        reportBuilder.append("## Constructeurs\n\n");
        java.lang.reflect.Constructor<?>[] constructors = enumClass.getDeclaredConstructors();
        ArrayNode ctorsArray = jsonRoot.putArray("constructeurs");
        
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

            Class<?>[] paramTypes = constructor.getParameterTypes();
            StringBuilder paramsBuilder = new StringBuilder();
            ArrayNode pArray = mapper.createArrayNode();
            
            if (paramTypes.length == 0) {
                paramsBuilder.append("aucun");
            } else {
                for (int i = 0; i < paramTypes.length; i++) {
                    paramsBuilder.append(paramTypes[i].getSimpleName());
                    pArray.add(paramTypes[i].getSimpleName());
                    if (i < paramTypes.length - 1) {
                        paramsBuilder.append(", ");
                    }
                }
            }

            reportBuilder.append(String.format("| %s | %s | %s | %s |\n",
                name, modifiers.isEmpty() ? "aucun" : modifiers,
                paramsBuilder.toString(),
                isSynthetic ? "Oui" : "Non"));
                
            ObjectNode cNode = ctorsArray.addObject();
            cNode.put("nom", name);
            cNode.put("modificateurs", modifiers.isEmpty() ? "aucun" : modifiers);
            cNode.set("parametres", pArray);
            cNode.put("isSynthetic", isSynthetic);
        }
        reportBuilder.append("\n");
    }

    /**
     * Analyse 5 & 6. Analyse des méthodes
     */
    public void analyzeMethods() {
        reportBuilder.append("## Méthodes\n\n");
        java.lang.reflect.Method[] methods = enumClass.getDeclaredMethods();
        ArrayNode methodsArray = jsonRoot.putArray("methodes");
        
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

            Class<?>[] paramTypes = method.getParameterTypes();
            StringBuilder paramsBuilder = new StringBuilder();
            ArrayNode pArray = mapper.createArrayNode();
            if (paramTypes.length == 0) {
                paramsBuilder.append("aucun");
            } else {
                for (int i = 0; i < paramTypes.length; i++) {
                    paramsBuilder.append(paramTypes[i].getSimpleName());
                    pArray.add(paramTypes[i].getSimpleName());
                    if (i < paramTypes.length - 1) paramsBuilder.append(", ");
                }
            }

            reportBuilder.append(String.format("| %s | %s | %s | %s | %s |\n",
                name, returnType, paramsBuilder.toString(),
                modifiers.isEmpty() ? "aucun" : modifiers,
                isSynthetic ? "Oui" : "Non"));
                
            ObjectNode mNode = methodsArray.addObject();
            mNode.put("nom", name);
            mNode.put("retour", returnType);
            mNode.put("modificateurs", modifiers.isEmpty() ? "aucun" : modifiers);
            mNode.set("parametres", pArray);
            mNode.put("isSynthetic", isSynthetic);
        }
        reportBuilder.append("\n");
    }

    /**
     * Analyse des interfaces implémentées
     */
    public void analyzeInterfaces() {
        reportBuilder.append("## Interfaces\n\n");
        Class<?>[] interfaces = enumClass.getInterfaces();
        ObjectNode intfNode = jsonRoot.putObject("interfaces");
        ArrayNode directArray = intfNode.putArray("directes");
        
        reportBuilder.append("- **Interfaces directement déclarées** : ");
        if (interfaces.length == 0) {
            reportBuilder.append("aucune\n");
        } else {
            for (int i = 0; i < interfaces.length; i++) {
                reportBuilder.append(interfaces[i].getName());
                directArray.add(interfaces[i].getName());
                if (i < interfaces.length - 1) {
                    reportBuilder.append(", ");
                }
            }
            reportBuilder.append("\n");
        }

        java.util.Set<String> inheritedInterfaces = new java.util.HashSet<>();
        Class<?> superClass = enumClass.getSuperclass();
        while (superClass != null) {
            for (Class<?> intf : superClass.getInterfaces()) {
                inheritedInterfaces.add(intf.getName());
            }
            superClass = superClass.getSuperclass();
        }
        
        ArrayNode inheritedArray = intfNode.putArray("heritees");
        reportBuilder.append("- **Interfaces héritées (via superclasse)** : ");
        if (inheritedInterfaces.isEmpty()) {
            reportBuilder.append("aucune\n");
        } else {
            reportBuilder.append(String.join(", ", inheritedInterfaces)).append("\n");
            for (String ii : inheritedInterfaces) {
                inheritedArray.add(ii);
            }
        }
        reportBuilder.append("\n");
    }

    /**
     * Analyse de toutes les annotations et exécution expérimentale
     */
    public void analyzeAnnotations() {
        reportBuilder.append("## Annotations\n\n");
        boolean foundAny = false;
        ObjectNode annosNode = jsonRoot.putObject("annotations");

        java.lang.annotation.Annotation[] typeAnnotations = enumClass.getAnnotations();
        ArrayNode typeAnnosArray = annosNode.putArray("surType");
        if (typeAnnotations.length > 0) {
            foundAny = true;
            reportBuilder.append("### Sur le type (classe)\n");
            for (java.lang.annotation.Annotation a : typeAnnotations) {
                reportBuilder.append("- `@").append(a.annotationType().getSimpleName()).append("`\n");
                typeAnnosArray.add(a.annotationType().getSimpleName());
            }
            reportBuilder.append("\n");
        }

        boolean hasFieldAnnos = false;
        ArrayNode fieldAnnosArray = annosNode.putArray("surAttributs");
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
                    ObjectNode faNode = fieldAnnosArray.addObject();
                    faNode.put("cible", field.getName());
                    faNode.put("annotation", a.annotationType().getSimpleName());
                }
            }
        }
        if (hasFieldAnnos) reportBuilder.append("\n");

        boolean hasCtorAnnos = false;
        ArrayNode ctorAnnosArray = annosNode.putArray("surConstructeurs");
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
                    ObjectNode caNode = ctorAnnosArray.addObject();
                    caNode.put("cible", ctor.getName());
                    caNode.put("annotation", a.annotationType().getSimpleName());
                }
            }
        }
        if (hasCtorAnnos) reportBuilder.append("\n");

        boolean hasMethodAnnos = false;
        ArrayNode methodAnnosArray = annosNode.putArray("surMethodes");
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
                    ObjectNode maNode = methodAnnosArray.addObject();
                    maNode.put("cible", method.getName());
                    maNode.put("annotation", a.annotationType().getSimpleName());
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

        ArrayNode expArray = jsonRoot.putArray("analyseExperimentale");
        if (!experimentalMethods.isEmpty()) {
            reportBuilder.append("### Analyse Expérimentale déclenchée par annotation\n\n");
            Object[] constants = enumClass.getEnumConstants();
            for (java.lang.reflect.Method method : experimentalMethods) {
                ExperimentalAnalysis anno = method.getAnnotation(ExperimentalAnalysis.class);
                reportBuilder.append("#### Méthode `").append(method.getName()).append("()` - ").append(anno.description()).append("\n\n");
                
                ObjectNode exNode = expArray.addObject();
                exNode.put("methode", method.getName());
                exNode.put("description", anno.description());
                ArrayNode resultsArray = exNode.putArray("resultats");
                
                if (method.getParameterCount() > 0) {
                    reportBuilder.append("*La méthode nécessite des paramètres, l'invocation automatique est ignorée.*\n\n");
                    exNode.put("erreur", "La méthode nécessite des paramètres");
                    continue;
                }

                reportBuilder.append("| Constante | Résultat (ou Exception) |\n");
                reportBuilder.append("|---|---|\n");
                for (Object constant : constants) {
                    ObjectNode resNode = resultsArray.addObject();
                    resNode.put("constante", ((Enum<?>) constant).name());
                    try {
                        method.setAccessible(true);
                        Object result = method.invoke(constant);
                        reportBuilder.append(String.format("| %s | %s |\n", ((Enum<?>) constant).name(), result));
                        resNode.put("valeur", result != null ? result.toString() : "null");
                    } catch (Exception e) {
                        reportBuilder.append(String.format("| %s | Erreur : %s |\n", ((Enum<?>) constant).name(), e.getCause() != null ? e.getCause().toString() : e.toString()));
                        resNode.put("erreur", e.getCause() != null ? e.getCause().toString() : e.toString());
                    }
                }
                reportBuilder.append("\n");
            }
        }
    }

    public String generateReport() {
        return reportBuilder.toString();
    }

    /**
     * Génère un rapport au format JSON complet avec Jackson.
     */
    public String generateJsonReport() {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonRoot);
        } catch (JsonProcessingException e) {
            return "{ \"error\": \"Impossible de générer le JSON\" }";
        }
    }
}
