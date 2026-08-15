package com.example.testcivique.data

/**
 * Associe les concepts aux trois mentions officielles de l'examen civique.
 *
 * Les concepts non listés constituent le socle commun du référentiel. Les concepts
 * listés proviennent des listes de connaissances publiées séparément par le ministère
 * pour la CSP, la carte de résident et la naturalisation.
 */
internal object QuestionTargetCatalog {
    data class OfficialSource(val title: String, val url: String)
    private val csp = setOf(ExamTarget.CARTE_PLURIANNUELLE)
    private val resident = setOf(ExamTarget.CARTE_RESIDENT)
    private val naturalisation = setOf(ExamTarget.NATURALISATION)

    private val assignments: Map<String, Set<ExamTarget>> = mapOf(
        "p08" to csp,
        "p09" to resident,
        "p21" to csp,
        "p22" to resident,
        "p23" to naturalisation,
        "p24" to naturalisation,
        "i3" to csp,
        "i13" to resident,
        "i21" to csp,
        "i22" to resident,
        "i23" to naturalisation,
        "i24" to naturalisation,
        "d2" to csp,
        "d12" to resident,
        "d21" to csp,
        "d22" to resident,
        "d23" to naturalisation,
        "d24" to naturalisation,
        "h16" to csp,
        "h09" to resident,
        "h21" to csp,
        "h22" to resident,
        "h23" to naturalisation,
        "h24" to naturalisation,
        "s3" to csp,
        "s09" to resident,
        "s21" to csp,
        "s22" to resident,
        "s23" to naturalisation,
        "s24" to naturalisation,
    )

    fun targetsFor(conceptId: String): Set<ExamTarget> =
        assignments[conceptId] ?: ExamTarget.entries.toSet()

    fun sourceFor(conceptId: String): OfficialSource = when (targetsFor(conceptId).singleOrNull()) {
        ExamTarget.CARTE_PLURIANNUELLE -> OfficialSource(
            "Liste officielle des questions — Carte de séjour pluriannuelle",
            "https://formation-civique.interieur.gouv.fr/examen-civique/liste-officielle-des-questions-de-connaissance-csp/",
        )
        ExamTarget.CARTE_RESIDENT -> OfficialSource(
            "Liste officielle des questions — Carte de résident",
            "https://formation-civique.interieur.gouv.fr/examen-civique/liste-officielle-des-questions-de-connaissance-cr/",
        )
        ExamTarget.NATURALISATION -> OfficialSource(
            "Liste officielle des questions — Naturalisation",
            "https://www.immigration.interieur.gouv.fr/documentation/guides-textes-et-brochures/questions-de-connaissance-pour-lexamen-civique-nationalite-francaise.html",
        )
        null -> OfficialSource(
            "Référentiel officiel de l'examen civique — Arrêté du 10 octobre 2025",
            "https://www.legifrance.gouv.fr/loda/id/JORFTEXT000052381620",
        )
    }
}
