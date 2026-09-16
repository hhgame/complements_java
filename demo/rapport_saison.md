## Type

- **Énumération** : oui
- **Nom** : Saison
- **Nom pleinement qualifié** : fr.uha.miage.demo.Saison
- **Paquetage** : fr.uha.miage.demo
- **Modificateurs** : public final
- **Superclasse** : java.lang.Enum

## Constantes

| Constante | ordinal() | toString() | Sous-classe propre |
|---|---|---|---|
| PRINTEMPS | 0 | PRINTEMPS | Non |
| ETE | 1 | ETE | Non |
| AUTOMNE | 2 | AUTOMNE | Non |
| HIVER | 3 | HIVER | Non |

## Attributs

| Nom | Type | Modificateurs | EnumConstant | Synthétique |
|---|---|---|---|---|
| PRINTEMPS | Saison | public static final | Oui | Non |
| ETE | Saison | public static final | Oui | Non |
| AUTOMNE | Saison | public static final | Oui | Non |
| HIVER | Saison | public static final | Oui | Non |
| description | String | private final | Non | Non |
| $VALUES | Saison[] | private static final | Non | Oui |

## Constructeurs

| Constructeur | Modificateurs | Paramètres | Synthétique |
|---|---|---|---|
| fr.uha.miage.demo.Saison | private | String, int, String | Non |

## Méthodes

| Méthode | Retour | Paramètres | Modificateurs | Synthétique |
|---|---|---|---|---|
| values | Saison[] | aucun | public static | Non |
| valueOf | Saison | String | public static | Non |
| $values | Saison[] | aucun | private static | Oui |
| getDescription | String | aucun | public | Non |
| estEstival | boolean | aucun | public | Non |

## Interfaces

- **Interfaces directement déclarées** : aucune
- **Interfaces héritées (via superclasse)** : java.lang.constant.Constable, java.lang.Comparable, java.io.Serializable

## Annotations

### Sur les méthodes
- `estEstival` : `@ExperimentalAnalysis`

### Analyse Expérimentale déclenchée par annotation

#### Méthode `estEstival()` - Vérifie si la saison est estivale

| Constante | Résultat (ou Exception) |
|---|---|
| PRINTEMPS | false |
| ETE | true |
| AUTOMNE | false |
| HIVER | false |

