package com.example.testcivique.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.testcivique.data.AttemptMode
import com.example.testcivique.data.AttemptRepository
import com.example.testcivique.data.CivicThemeId
import com.example.testcivique.data.ExamTarget
import com.example.testcivique.data.MasteryStatus
import com.example.testcivique.data.ProgressSnapshot
import com.example.testcivique.data.ReadinessStatus
import com.example.testcivique.data.ThemeMastery
import com.example.testcivique.data.local.AttemptAnswerEntity
import com.example.testcivique.data.local.AttemptEntity
import com.example.testcivique.ui.theme.CivicBlue
import com.example.testcivique.ui.theme.CivicGold
import com.example.testcivique.ui.theme.CivicGreen
import com.example.testcivique.ui.theme.CivicRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
internal fun HistoryScreen(
    repository: AttemptRepository,
    target: ExamTarget,
    onOpenAttempt: (String) -> Unit,
) {
    val allAttempts by repository.attempts.collectAsStateWithLifecycle(initialValue = emptyList())
    val targetAttempts = allAttempts.filter { it.target == target.name }
    var filter by rememberSaveable { mutableStateOf(HistoryFilter.ALL) }
    val attempts = targetAttempts.filter { attempt ->
        when (filter) {
            HistoryFilter.ALL -> true
            HistoryFilter.MOCK -> attempt.mode == AttemptMode.MOCK.name
            HistoryFilter.THEMATIC -> attempt.mode == AttemptMode.THEMATIC.name
        }
    }
    val scope = rememberCoroutineScope()
    var attemptToDelete by remember { mutableStateOf<AttemptEntity?>(null) }
    var confirmDeleteAll by remember { mutableStateOf(false) }

    if (attemptToDelete != null) {
        AlertDialog(
            onDismissRequest = { attemptToDelete = null },
            title = { Text("Supprimer ce résultat ?") },
            text = { Text("Le test et toutes ses réponses seront supprimés définitivement.") },
            confirmButton = {
                TextButton(onClick = {
                    val id = attemptToDelete?.id ?: return@TextButton
                    attemptToDelete = null
                    scope.launch { repository.deleteAttempt(id) }
                }) { Text("Supprimer", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { attemptToDelete = null }) { Text("Annuler") } },
        )
    }
    if (confirmDeleteAll) {
        AlertDialog(
            onDismissRequest = { confirmDeleteAll = false },
            title = { Text("Vider l’historique ?") },
            text = { Text("Tous les tests enregistrés pour le parcours « ${target.label} » seront supprimés.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDeleteAll = false
                    scope.launch { repository.deleteForTarget(target) }
                }) { Text("Tout supprimer", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { confirmDeleteAll = false }) { Text("Annuler") } },
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Historique", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "Vos tests et leurs réponses détaillées.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (targetAttempts.isNotEmpty()) {
                    TextButton(onClick = { confirmDeleteAll = true }) { Text("Tout effacer") }
                }
            }
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(HistoryFilter.entries.size) { index ->
                    val option = HistoryFilter.entries[index]
                    FilterChip(
                        selected = filter == option,
                        onClick = { filter = option },
                        label = { Text(option.label) },
                    )
                }
            }
        }
        if (attempts.isEmpty()) {
            item { EmptyHistoryCard() }
        } else {
            items(attempts, key = { it.id }) { attempt ->
                AttemptCard(
                    attempt = attempt,
                    onOpen = { onOpenAttempt(attempt.id) },
                    onDelete = { attemptToDelete = attempt },
                )
            }
        }
        item { Spacer(Modifier.height(4.dp)) }
    }
}

private enum class HistoryFilter(val label: String) {
    ALL("Tous"),
    MOCK("Examens blancs"),
    THEMATIC("QCM"),
}

@Composable
private fun EmptyHistoryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.size(70.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Text("Aucun test enregistré", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp))
            Text(
                "Terminez un QCM ou un examen blanc pour retrouver ici votre score et chaque correction.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 7.dp),
            )
        }
    }
}

@Composable
private fun AttemptCard(attempt: AttemptEntity, onOpen: () -> Unit, onDelete: () -> Unit) {
    val accent = if (attempt.passed) CivicGreen else CivicGold
    val title = if (attempt.mode == AttemptMode.MOCK.name) {
        "Examen blanc"
    } else {
        attempt.theme?.let { runCatching { CivicThemeId.valueOf(it).shortTitle }.getOrNull() } ?: "QCM thématique"
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onOpen),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(15.dp), color = accent.copy(alpha = 0.14f), modifier = Modifier.size(50.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(if (attempt.passed) Icons.Default.CheckCircle else Icons.Default.Quiz, contentDescription = null, tint = accent)
                }
            }
            Column(Modifier.weight(1f).padding(horizontal = 13.dp)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(
                    runCatching { ExamTarget.valueOf(attempt.target).shortLabel }.getOrDefault(attempt.target),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(formatDate(attempt.completedAt), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(if (attempt.passed) "Objectif atteint" else "À retravailler", style = MaterialTheme.typography.labelLarge, color = accent)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${attempt.score}/${attempt.total}", style = MaterialTheme.typography.titleLarge, color = accent)
                IconButton(onClick = onDelete, modifier = Modifier.size(38.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HistoryDetailScreen(attemptId: String, repository: AttemptRepository, onBack: () -> Unit) {
    val detail by repository.observeAttempt(attemptId).collectAsStateWithLifecycle(initialValue = null)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détail du test") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        val value = detail
        if (value == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Ce test n’existe plus.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { AttemptSummary(value.attempt) }
                item {
                    Text("Correction détaillée", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 8.dp))
                }
                items(value.answers.sortedBy { it.id }, key = { it.id }) { answer ->
                    AnswerReviewCard(answer)
                }
            }
        }
    }
}

@Composable
private fun AttemptSummary(attempt: AttemptEntity) {
    val accent = if (attempt.passed) CivicGreen else CivicGold
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.12f)),
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(if (attempt.passed) Icons.Default.CheckCircle else Icons.Default.Error, contentDescription = null, tint = accent, modifier = Modifier.size(42.dp))
            Column(Modifier.weight(1f).padding(start = 14.dp)) {
                Text(if (attempt.passed) "Objectif atteint" else "À consolider", style = MaterialTheme.typography.titleLarge)
                Text(formatDate(attempt.completedAt), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Durée : ${formatDuration(attempt.durationSeconds)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "Parcours : ${runCatching { ExamTarget.valueOf(attempt.target).label }.getOrDefault(attempt.target)}",
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text("${attempt.score}/${attempt.total}", style = MaterialTheme.typography.headlineMedium, color = accent)
        }
    }
}

@Composable
private fun AnswerReviewCard(answer: AttemptAnswerEntity) {
    val uriHandler = LocalUriHandler.current
    val options = listOf(answer.optionA, answer.optionB, answer.optionC, answer.optionD)
    val selectedText = options.getOrNull(answer.selectedIndex) ?: "Aucune réponse"
    val correctText = options.getOrElse(answer.correctIndex) { "" }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    if (answer.isCorrect) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = if (answer.isCorrect) CivicGreen else CivicRed,
                )
                Text(answer.questionText, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f).padding(start = 10.dp))
            }
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            Text("Votre réponse : $selectedText", color = if (answer.isCorrect) CivicGreen else CivicRed)
            if (!answer.isCorrect) {
                Text("Bonne réponse : $correctText", color = CivicGreen, modifier = Modifier.padding(top = 5.dp))
            }
            Text(answer.explanation, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 10.dp))
            if (answer.sourceTitle.isNotBlank()) {
                TextButton(
                    onClick = { runCatching { uriHandler.openUri(answer.sourceUrl) } },
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Text("Source officielle : ${answer.sourceTitle}", style = MaterialTheme.typography.labelMedium, color = CivicBlue)
                }
            }
        }
    }
}

internal fun emptyProgressSnapshot() = ProgressSnapshot(
    attemptsCount = 0,
    mockAttemptsCount = 0,
    bestMockScore = null,
    readinessScore = 0f,
    readinessConfidence = 0f,
    readinessStatus = ReadinessStatus.INSUFFICIENT,
    themes = CivicThemeId.entries.map { ThemeMastery(it, 0, 0, 0f, MasteryStatus.INSUFFICIENT) },
)

internal fun readinessLabel(status: ReadinessStatus) = when (status) {
    ReadinessStatus.INSUFFICIENT -> "Données insuffisantes"
    ReadinessStatus.NOT_READY -> "Pas encore prêt"
    ReadinessStatus.ALMOST_READY -> "Presque prêt"
    ReadinessStatus.READY -> "Prêt pour l’examen"
}

internal fun readinessDescription(status: ReadinessStatus) = when (status) {
    ReadinessStatus.INSUFFICIENT -> "Faites au moins 3 examens blancs et couvrez 10 notions distinctes par thème pour obtenir une estimation exploitable."
    ReadinessStatus.NOT_READY -> "Certains acquis restent fragiles. Concentrez-vous sur les thèmes signalés à retravailler."
    ReadinessStatus.ALMOST_READY -> "Votre niveau se rapproche de l’objectif. Quelques entraînements réguliers peuvent faire la différence."
    ReadinessStatus.READY -> "Vos résultats récents et votre maîtrise des thèmes indiquent une préparation solide. Cette estimation ne garantit pas la réussite officielle."
}

internal fun readinessColor(status: ReadinessStatus): Color = when (status) {
    ReadinessStatus.INSUFFICIENT -> CivicBlue
    ReadinessStatus.NOT_READY -> CivicRed
    ReadinessStatus.ALMOST_READY -> CivicGold
    ReadinessStatus.READY -> CivicGreen
}

internal fun masteryLabel(status: MasteryStatus) = when (status) {
    MasteryStatus.INSUFFICIENT -> "À évaluer"
    MasteryStatus.TO_REVIEW -> "À revoir"
    MasteryStatus.CONSOLIDATING -> "En progrès"
    MasteryStatus.MASTERED -> "Maîtrisé"
}

internal fun masteryColor(status: MasteryStatus): Color = when (status) {
    MasteryStatus.INSUFFICIENT -> CivicBlue
    MasteryStatus.TO_REVIEW -> CivicRed
    MasteryStatus.CONSOLIDATING -> CivicGold
    MasteryStatus.MASTERED -> CivicGreen
}

private fun formatDate(timestamp: Long): String =
    SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.FRANCE).format(Date(timestamp))

private fun formatDuration(seconds: Int): String =
    if (seconds >= 60) "${seconds / 60} min ${seconds % 60} s" else "$seconds s"
