package com.example.testcivique.data

enum class ExamTarget(val label: String, val shortLabel: String) {
    NATURALISATION("Naturalisation", "Naturalisation"),
    CARTE_RESIDENT("Carte de résident", "Carte résident"),
    CARTE_PLURIANNUELLE("Carte pluriannuelle", "Carte pluriannuelle"),
}

enum class CivicThemeId(
    val title: String,
    val shortTitle: String,
    val description: String,
    val symbol: String,
) {
    PRINCIPLES(
        "Principes et valeurs de la République",
        "Principes et valeurs",
        "Devise, symboles, libertés et laïcité",
        "RF",
    ),
    INSTITUTIONS(
        "Système institutionnel et politique",
        "Institutions",
        "Démocratie, élections, pouvoirs et Europe",
        "III",
    ),
    RIGHTS(
        "Droits et devoirs",
        "Droits et devoirs",
        "Droits fondamentaux et responsabilités",
        "§",
    ),
    HISTORY(
        "Histoire, géographie et culture",
        "Histoire et culture",
        "Repères historiques, territoires et patrimoine",
        "1789",
    ),
    SOCIETY(
        "Vivre dans la société française",
        "Vie en France",
        "Santé, travail, famille et éducation",
        "FR",
    ),
}

data class Question(
    val id: String,
    val theme: CivicThemeId,
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val conceptId: String = id,
    val type: QuestionType = QuestionType.KNOWLEDGE,
    val subtopic: String = "",
    val targets: Set<ExamTarget> = ExamTarget.entries.toSet(),
    val sourceTitle: String = "Livret du citoyen 2026 — Ministère de l'Intérieur",
    val sourceUrl: String = "https://www.immigration.interieur.gouv.fr/documentation/guides-textes-et-brochures/livret-du-citoyen.html",
) : java.io.Serializable

enum class QuestionType {
    KNOWLEDGE,
    SITUATION,
}

data class LearningChapter(
    val title: String,
    val durationMinutes: Int,
    val keyPoints: List<String>,
    val completed: Boolean = false,
)

data class ThemeLearningContent(
    val theme: CivicThemeId,
    val overview: String,
    val keyFacts: List<String>,
    val chapters: List<LearningChapter>,
)

object DemoContent {
    val learning: List<ThemeLearningContent> = listOf(
        ThemeLearningContent(
            CivicThemeId.PRINCIPLES,
            "Comprendre les fondements de la République française et la manière dont ils s'appliquent au quotidien.",
            listOf(
                "La devise est « Liberté, Égalité, Fraternité ».",
                "La laïcité garantit la liberté de conscience et la neutralité de l'État.",
                "La liberté d'expression existe dans les limites fixées par la loi.",
            ),
            listOf(
                LearningChapter("La devise et les symboles", 7, listOf("La devise officielle est « Liberté, Égalité, Fraternité ».", "Le drapeau bleu, blanc, rouge et La Marseillaise sont des symboles officiels.", "Marianne, le 14 juillet et le coq sont aussi des repères républicains.")),
                LearningChapter("Libertés et égalité", 9, listOf("Les libertés s'exercent dans le respect de la loi et des droits d'autrui.", "L'égalité interdit notamment les discriminations fondées sur l'origine, le sexe ou le handicap.", "La fraternité se traduit par la solidarité entre les personnes et les générations.")),
                LearningChapter("Comprendre la laïcité", 8, listOf("La laïcité garantit la liberté de croire, de ne pas croire et de changer de conviction.", "L'État et les agents publics sont neutres à l'égard des religions.", "Les usagers peuvent exprimer leurs convictions dans les limites de l'ordre public et du fonctionnement du service.")),
            ),
        ),
        ThemeLearningContent(
            CivicThemeId.INSTITUTIONS,
            "Identifier les institutions françaises, leurs rôles et les principaux mécanismes démocratiques.",
            listOf(
                "Le Parlement est composé de l'Assemblée nationale et du Sénat.",
                "Le président de la République est élu pour cinq ans.",
                "Le préfet représente l'État dans le département.",
            ),
            listOf(
                LearningChapter("Le droit de vote", 8, listOf("Le droit de vote s'exerce à partir de 18 ans pour les citoyens remplissant les conditions légales.", "Le président et les députés sont élus au suffrage universel direct.", "Les conseillers municipaux sont élus par les électeurs puis élisent le maire.")),
                LearningChapter("Les pouvoirs de la République", 11, listOf("Le pouvoir exécutif est exercé par le président de la République et le Gouvernement.", "Le Parlement, composé de l'Assemblée nationale et du Sénat, vote les lois.", "L'autorité judiciaire est indépendante et veille au respect du droit.")),
                LearningChapter("La France dans l'Union européenne", 9, listOf("L'Union européenne compte 27 États membres.", "Le Parlement européen siège officiellement à Strasbourg et la Commission européenne à Bruxelles.", "La citoyenneté européenne complète la citoyenneté nationale des ressortissants de l'Union.")),
            ),
        ),
        ThemeLearningContent(
            CivicThemeId.RIGHTS,
            "Connaître les droits protégés en France et les devoirs qui s'imposent à chaque personne.",
            listOf(
                "Toute personne présente en France doit respecter la loi.",
                "Les libertés fondamentales sont protégées mais ne sont pas absolues.",
                "Porter secours à une personne en danger est une obligation.",
            ),
            listOf(
                LearningChapter("Les droits fondamentaux", 10, listOf("La dignité, la sûreté et les libertés d'expression, de conscience et de circulation sont protégées.", "La Déclaration des droits de l'homme et du citoyen date de 1789.", "Les droits de la défense permettent notamment d'être assisté par un avocat.")),
                LearningChapter("Les devoirs du citoyen", 9, listOf("Toute personne présente en France doit respecter la loi.", "Déclarer ses revenus et contribuer aux charges publiques sont des obligations légales.", "Un citoyen convoqué comme juré d'assises doit en principe se présenter.")),
                LearningChapter("Agir de façon responsable", 7, listOf("Il faut alerter les secours lorsqu'une personne est en danger sans se mettre soi-même gravement en danger.", "Les violences, discriminations et incitations à la haine peuvent être sanctionnées.", "Le respect de l'environnement et des règles communes participe à la responsabilité civique.")),
            ),
        ),
        ThemeLearningContent(
            CivicThemeId.HISTORY,
            "Mémoriser les grandes dates, les personnages, les territoires et les éléments du patrimoine français.",
            listOf(
                "La Révolution française débute en 1789.",
                "La Ve République est fondée en 1958.",
                "La France possède des territoires en Europe et outre-mer.",
            ),
            listOf(
                LearningChapter("Les grandes périodes historiques", 12, listOf("1789 marque le début de la Révolution française et 1804 l'Empire de Napoléon Ier.", "Les deux guerres mondiales se déroulent de 1914 à 1918 et de 1939 à 1945.", "La Ve République est fondée en 1958 et la peine de mort est abolie en 1981.")),
                LearningChapter("Territoires et géographie", 10, listOf("La France métropolitaine compte 13 régions.", "La France possède des territoires ultramarins dans plusieurs océans.", "Le mont Blanc est le plus haut sommet de France et la Seine traverse Paris.")),
                LearningChapter("Culture et patrimoine", 9, listOf("La Joconde est exposée au musée du Louvre.", "Molière, Victor Hugo, Marie Curie et de nombreux artistes participent au patrimoine français.", "Le château de Versailles est particulièrement associé au règne de Louis XIV.")),
            ),
        ),
        ThemeLearningContent(
            CivicThemeId.SOCIETY,
            "Comprendre les démarches et les règles essentielles de la vie quotidienne en France.",
            listOf(
                "Le 15 permet de joindre le SAMU et le 17 la police ou la gendarmerie.",
                "L'instruction est obligatoire de 3 à 16 ans.",
                "Le SMIC correspond au salaire minimum légal.",
            ),
            listOf(
                LearningChapter("Se soigner et demander de l'aide", 8, listOf("Le 15 joint le SAMU, le 17 la police, le 18 les pompiers et le 112 les urgences européennes.", "La carte Vitale facilite la prise en charge par l'Assurance maladie.", "Une complémentaire santé peut rembourser une partie des frais restant à charge.")),
                LearningChapter("Travailler en France", 10, listOf("La durée légale du travail à temps complet est de 35 heures par semaine.", "Le SMIC fixe un salaire minimum légal.", "Le travail dissimulé est interdit et le conseil de prud'hommes traite les litiges individuels du travail.")),
                LearningChapter("Famille et système éducatif", 11, listOf("L'instruction est obligatoire de 3 à 16 ans.", "Après l'école élémentaire, les élèves vont généralement au collège.", "L'autorité parentale s'exerce dans l'intérêt de l'enfant et sans violences physiques ou psychologiques.")),
            ),
        ),
    )

    private val initialQuestions: List<Question> = listOf(
        Question("p1", CivicThemeId.PRINCIPLES, "Quelle est la devise de la République française ?", listOf("Unité, justice, paix", "Liberté, Égalité, Fraternité", "Travail, famille, patrie", "Honneur, courage, respect"), 1, "La devise républicaine est « Liberté, Égalité, Fraternité »."),
        Question("p2", CivicThemeId.PRINCIPLES, "Quel principe garantit la neutralité de l'État en matière de religion ?", listOf("Le fédéralisme", "La décentralisation", "La laïcité", "La monarchie"), 2, "La laïcité garantit la neutralité de l'État et la liberté de conscience."),
        Question("p3", CivicThemeId.PRINCIPLES, "Quel texte date de 1789 ?", listOf("Le traité de Maastricht", "La Constitution de 1958", "La Déclaration des droits de l'homme et du citoyen", "Le Code civil"), 2, "La Déclaration des droits de l'homme et du citoyen a été adoptée en 1789."),
        Question("p4", CivicThemeId.PRINCIPLES, "La liberté d'expression permet-elle d'insulter ou de menacer une personne ?", listOf("Oui, toujours", "Non, elle a des limites prévues par la loi", "Seulement sur internet", "Uniquement à l'étranger"), 1, "Les menaces, injures et provocations à la haine peuvent être sanctionnées."),

        Question("i1", CivicThemeId.INSTITUTIONS, "Quelle est la durée du mandat du président de la République ?", listOf("4 ans", "5 ans", "6 ans", "7 ans"), 1, "Le président de la République est élu pour cinq ans."),
        Question("i2", CivicThemeId.INSTITUTIONS, "Qui vote les lois ?", listOf("Le Parlement", "La police", "Le Conseil municipal", "Les tribunaux"), 0, "Le Parlement, composé de l'Assemblée nationale et du Sénat, vote les lois."),
        Question("i3", CivicThemeId.INSTITUTIONS, "À quel âge peut-on voter en France ?", listOf("16 ans", "18 ans", "20 ans", "21 ans"), 1, "Il faut avoir 18 ans et être inscrit sur les listes électorales."),
        Question("i4", CivicThemeId.INSTITUTIONS, "Qui représente l'État dans un département ?", listOf("Le maire", "Le préfet", "Le député européen", "Le président du conseil municipal"), 1, "Le préfet représente l'État dans le département."),

        Question("d1", CivicThemeId.RIGHTS, "Quel est l'âge de la majorité civile en France ?", listOf("16 ans", "18 ans", "20 ans", "21 ans"), 1, "La majorité civile est fixée à 18 ans."),
        Question("d2", CivicThemeId.RIGHTS, "Quelles personnes doivent respecter la loi française ?", listOf("Les citoyens français seulement", "Les personnes au travail uniquement", "Toute personne présente en France", "Personne si elle n'est pas d'accord"), 2, "Toute personne présente en France doit respecter la loi."),
        Question("d3", CivicThemeId.RIGHTS, "Que protège la liberté de la presse ?", listOf("La possibilité d'informer dans le respect de la loi", "L'interdiction de toute critique", "Le droit de publier des menaces", "Le secret de toutes les infractions"), 0, "Elle protège l'information et l'expression journalistique dans le cadre légal."),
        Question("d4", CivicThemeId.RIGHTS, "Le devoir de solidarité signifie notamment :", listOf("Ne jamais payer d'impôts", "Aider les personnes en difficulté selon ses possibilités", "Refuser toute règle commune", "Voter à la place des autres"), 1, "La solidarité fait partie des responsabilités de chacun dans la société."),

        Question("h1", CivicThemeId.HISTORY, "En quelle année a débuté la Révolution française ?", listOf("1789", "1804", "1848", "1914"), 0, "La Révolution française commence en 1789."),
        Question("h2", CivicThemeId.HISTORY, "Que commémore le 8 mai ?", listOf("La fête nationale", "La création de l'euro", "La victoire de 1945 en Europe", "La prise de la Bastille"), 2, "Le 8 mai commémore la victoire des Alliés sur l'Allemagne nazie en 1945."),
        Question("h3", CivicThemeId.HISTORY, "Quel fleuve traverse Paris ?", listOf("La Loire", "La Garonne", "La Seine", "Le Rhône"), 2, "La Seine traverse Paris et se jette dans la Manche."),
        Question("h4", CivicThemeId.HISTORY, "Dans quel musée se trouve la Joconde ?", listOf("Le musée d'Orsay", "Le musée du Louvre", "Le Centre Pompidou", "Le musée Rodin"), 1, "La Joconde est exposée au musée du Louvre à Paris."),

        Question("s1", CivicThemeId.SOCIETY, "Quel numéro permet d'appeler la police en urgence ?", listOf("15", "17", "18", "3114"), 1, "Le 17 permet de joindre la police ou la gendarmerie en urgence."),
        Question("s2", CivicThemeId.SOCIETY, "Quel numéro permet d'appeler le SAMU ?", listOf("15", "17", "18", "114"), 0, "Le 15 permet de joindre le SAMU pour une urgence médicale."),
        Question("s3", CivicThemeId.SOCIETY, "L'instruction des enfants est obligatoire jusqu'à :", listOf("14 ans", "16 ans", "18 ans", "21 ans"), 1, "L'instruction est obligatoire de 3 à 16 ans."),
        Question("s4", CivicThemeId.SOCIETY, "Le SMIC est :", listOf("Une aide au logement", "Une carte de santé", "Le salaire minimum légal", "Un impôt local"), 2, "Le SMIC est le salaire minimum interprofessionnel de croissance."),
    )

    val canonicalQuestions: List<Question> = initialQuestions + AdditionalQuestions.items

    val questions: List<Question> = canonicalQuestions.flatMap { question ->
        val source = QuestionTargetCatalog.sourceFor(question.id)
        (listOf(question.text) + NaturalQuestionVariants.forQuestion(question.id))
            .mapIndexed { index, text ->
                question.copy(
                    id = "${question.id}_v${index + 1}",
                    text = text,
                    targets = QuestionTargetCatalog.targetsFor(question.id),
                    sourceTitle = source.title,
                    sourceUrl = source.url,
                )
            }
    }

    fun questionsFor(theme: CivicThemeId): List<Question> = questions.filter { it.theme == theme }

    fun learningFor(theme: CivicThemeId): ThemeLearningContent = learning.first { it.theme == theme }
}
