package com.example.testcivique.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.testcivique.data.AnswerSnapshot
import com.example.testcivique.data.AttemptMode
import com.example.testcivique.data.AttemptRepository
import com.example.testcivique.data.CivicThemeId
import com.example.testcivique.data.DemoContent
import com.example.testcivique.data.ExamTarget
import com.example.testcivique.data.Question
import com.example.testcivique.data.QuestionGenerator
import com.example.testcivique.data.ProgressSnapshot
import com.example.testcivique.data.ReadinessStatus
import com.example.testcivique.data.MasteryStatus
import com.example.testcivique.data.ThemeMastery
import com.example.testcivique.data.local.AttemptEntity
import com.example.testcivique.data.local.AttemptWithAnswers
import com.example.testcivique.data.local.CivicDatabase
import com.example.testcivique.ui.theme.CivicBlue
import com.example.testcivique.ui.theme.CivicGold
import com.example.testcivique.ui.theme.CivicGreen
import com.example.testcivique.ui.theme.CivicNavy
import com.example.testcivique.ui.theme.CivicRed
import com.example.testcivique.ui.theme.CivicSky
import kotlin.math.roundToInt
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class MainTab(val label: String, val icon: ImageVector) {
    HOME("Accueil", Icons.Default.Home),
    LEARN("Apprendre", Icons.Default.School),
    PRACTICE("Tests", Icons.Default.Quiz),
    HISTORY("Historique", Icons.Default.History),
    PROGRESS("Progrès", Icons.Default.Insights),
}

@Composable
fun CivicTestApp(darkTheme: Boolean, onToggleTheme: () -> Unit) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val repository = remember { AttemptRepository(CivicDatabase.getInstance(context)) }
    var target by rememberSaveable { mutableStateOf(ExamTarget.NATURALISATION) }

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainShell(
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme,
                repository = repository,
                target = target,
                onTargetChange = { target = it },
                onOpenLearning = { navController.navigate("learn/${it.name}") },
                onStartQuiz = { navController.navigate("quiz/${it.name}") },
                onOpenExam = { navController.navigate("exam-intro") },
                onOpenAttempt = { navController.navigate("history/$it") },
            )
        }
        composable(
            route = "learn/{theme}",
            arguments = listOf(navArgument("theme") { type = NavType.StringType }),
        ) { entry ->
            val theme = CivicThemeId.valueOf(entry.arguments?.getString("theme") ?: CivicThemeId.PRINCIPLES.name)
            LearningDetailScreen(
                theme = theme,
                onBack = { navController.popBackStack() },
                onStartQuiz = { navController.navigate("quiz/${theme.name}") },
            )
        }
        composable(
            route = "quiz/{theme}",
            arguments = listOf(navArgument("theme") { type = NavType.StringType }),
        ) { entry ->
            QuizScreen(
                themeKey = entry.arguments?.getString("theme") ?: CivicThemeId.PRINCIPLES.name,
                target = target,
                repository = repository,
                onBack = { navController.popBackStack() },
            )
        }
        composable("exam-intro") {
            ExamIntroScreen(
                onBack = { navController.popBackStack() },
                onStart = { navController.navigate("quiz/ALL") },
            )
        }
        composable(
            route = "history/{attemptId}",
            arguments = listOf(navArgument("attemptId") { type = NavType.StringType }),
        ) { entry ->
            HistoryDetailScreen(
                attemptId = entry.arguments?.getString("attemptId").orEmpty(),
                repository = repository,
                onBack = { navController.popBackStack() },
            )
        }
    }
}

@Composable
private fun MainShell(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    repository: AttemptRepository,
    target: ExamTarget,
    onTargetChange: (ExamTarget) -> Unit,
    onOpenLearning: (CivicThemeId) -> Unit,
    onStartQuiz: (CivicThemeId) -> Unit,
    onOpenExam: () -> Unit,
    onOpenAttempt: (String) -> Unit,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val progress by repository.observeProgress(target).collectAsStateWithLifecycle(
        initialValue = emptyProgressSnapshot(),
    )
    Scaffold(
        topBar = {
            CivicTopBar(
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme,
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
            ) {
                MainTab.entries.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                (fadeIn(tween(220)) + slideInHorizontally { it / 10 })
                    .togetherWith(fadeOut(tween(120)))
            },
            label = "main-tab",
            modifier = Modifier.fillMaxSize().padding(padding),
        ) { tab ->
            when (tab) {
                0 -> HomeScreen(
                    target = target,
                    onTargetChange = onTargetChange,
                    onGoToLearn = { selectedTab = 1 },
                    onGoToPractice = { selectedTab = 2 },
                    onStartQuiz = onStartQuiz,
                    onOpenExam = onOpenExam,
                    progress = progress,
                )
                1 -> LearningScreen(onOpenTheme = onOpenLearning)
                2 -> PracticeScreen(onStartQuiz = onStartQuiz, onOpenExam = onOpenExam)
                3 -> HistoryScreen(repository = repository, target = target, onOpenAttempt = onOpenAttempt)
                else -> ProgressScreen(progress = progress, onGoToPractice = { selectedTab = 2 })
            }
        }
    }
}

@Composable
private fun CivicTopBar(darkTheme: Boolean, onToggleTheme: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(CivicBlue, CivicNavy))),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("RF", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                }
                Spacer(Modifier.width(11.dp))
                Column {
                    Text("Mon Civique", style = MaterialTheme.typography.titleLarge)
                    Text("Objectif République", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(44.dp),
            ) {
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (darkTheme) "Mode clair" else "Mode sombre",
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(
    target: ExamTarget,
    onTargetChange: (ExamTarget) -> Unit,
    onGoToLearn: () -> Unit,
    onGoToPractice: () -> Unit,
    onStartQuiz: (CivicThemeId) -> Unit,
    onOpenExam: () -> Unit,
    progress: ProgressSnapshot,
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Text("Bonjour 👋", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Prêt à progresser ?", style = MaterialTheme.typography.headlineMedium)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ExamTarget.entries.forEach { option ->
                    FilterChip(
                        selected = target == option,
                        onClick = { onTargetChange(option) },
                        label = { Text(option.shortLabel) },
                        leadingIcon = if (target == option) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        } else null,
                    )
                }
            }
        }
        item { HeroPreparationCard(progress = progress, onOpenExam = onOpenExam) }
        item {
            SectionHeader("Raccourcis", "Tout votre parcours en un geste")
            Spacer(Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    QuickActionCard("Apprendre", "Fiches essentielles", Icons.Default.AutoStories, CivicBlue, onGoToLearn)
                }
                item {
                    QuickActionCard("QCM rapide", "Par thème", Icons.Default.Quiz, CivicRed, onGoToPractice)
                }
                item {
                    QuickActionCard("Examen blanc", "Mode 45 min", Icons.Default.Timer, CivicGreen, onOpenExam)
                }
            }
        }
        item {
            SectionHeader("Commencer un thème", "Quelques minutes suffisent aujourd'hui")
        }
        items(CivicThemeId.entries.take(3)) { theme ->
            CompactThemeCard(theme = theme, onClick = { onStartQuiz(theme) })
        }
    }
}

@Composable
private fun HeroPreparationCard(progress: ProgressSnapshot, onOpenExam: () -> Unit) {
    Card(
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(Brush.linearGradient(listOf(CivicNavy, CivicBlue, Color(0xFF4D7EEA))))
                .padding(22.dp),
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(Color.White.copy(alpha = 0.07f), radius = 110.dp.toPx(), center = Offset(size.width, 0f))
                drawCircle(Color.White.copy(alpha = 0.05f), radius = 70.dp.toPx(), center = Offset(0f, size.height))
            }
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Surface(color = Color.White.copy(alpha = 0.14f), shape = RoundedCornerShape(50)) {
                            Text(readinessLabel(progress.readinessStatus).uppercase(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                        Spacer(Modifier.height(14.dp))
                        Text(if (progress.attemptsCount == 0) "Votre préparation\ncommence ici" else "Votre préparation\nen un coup d'œil", color = Color.White, style = MaterialTheme.typography.headlineMedium)
                        Text(if (progress.attemptsCount == 0) "Faites un premier test pour obtenir une estimation personnalisée." else "${progress.attemptsCount} test(s) analysé(s) pour votre objectif.", color = Color.White.copy(alpha = 0.78f), modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                    ProgressRing(progress = progress.readinessScore, label = if (progress.readinessStatus == ReadinessStatus.INSUFFICIENT) "—" else "${(progress.readinessScore * 100).roundToInt()}%", trackColor = Color.White.copy(alpha = 0.2f), progressColor = CivicGold)
                }
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = onOpenExam,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = CivicNavy),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Découvrir l'examen blanc")
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(title: String, subtitle: String, icon: ImageVector, accent: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(154.dp).height(132.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Surface(shape = RoundedCornerShape(13.dp), color = accent.copy(alpha = 0.13f), modifier = Modifier.size(42.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null, tint = accent) }
            }
            Spacer(Modifier.height(11.dp))
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun LearningScreen(onOpenTheme: (CivicThemeId) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text("Apprendre", style = MaterialTheme.typography.headlineMedium)
            Text("Des fiches courtes, structurées et faciles à reprendre.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(22.dp),
            ) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrackChanges, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(34.dp))
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Votre parcours", fontWeight = FontWeight.Bold)
                        Text("1 chapitre exploré sur 15", style = MaterialTheme.typography.bodyMedium)
                        LinearProgressIndicator(progress = { 1f / 15f }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), strokeCap = StrokeCap.Round)
                    }
                }
            }
        }
        items(CivicThemeId.entries) { theme ->
            LearningThemeCard(theme = theme, onClick = { onOpenTheme(theme) })
        }
    }
}

@Composable
private fun LearningThemeCard(theme: CivicThemeId, onClick: () -> Unit) {
    val accent = themeAccent(theme)
    val completed = if (theme == CivicThemeId.PRINCIPLES) 1 else 0
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            ThemeSymbol(theme, accent, 54)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(theme.shortTitle, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(theme.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 9.dp)) {
                    LinearProgressIndicator(
                        progress = { completed / 3f },
                        color = accent,
                        trackColor = accent.copy(alpha = 0.12f),
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier.weight(1f),
                    )
                    Text("$completed/3", modifier = Modifier.padding(start = 10.dp), style = MaterialTheme.typography.labelLarge, color = accent)
                }
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Ouvrir", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun PracticeScreen(onStartQuiz: (CivicThemeId) -> Unit, onOpenExam: () -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text("S'entraîner", style = MaterialTheme.typography.headlineMedium)
            Text("Choisissez un format adapté au temps dont vous disposez.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item { ExamFeatureCard(onClick = onOpenExam) }
        item { SectionHeader("QCM par thème", "20 questions tirées aléatoirement") }
        items(CivicThemeId.entries) { theme ->
            PracticeThemeCard(theme = theme, onClick = { onStartQuiz(theme) })
        }
    }
}

@Composable
private fun ExamFeatureCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(26.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
    ) {
        Box(Modifier.background(Brush.linearGradient(listOf(Color(0xFF173C80), CivicBlue))).padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Surface(color = CivicGold, shape = RoundedCornerShape(50)) {
                        Text("FORMAT OFFICIEL", color = CivicNavy, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                    }
                    Text("Examen blanc", color = Color.White, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 12.dp))
                    Text("40 questions • 45 minutes • 32/40", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 14.dp)) {
                        Text("Voir le détail", color = Color.White, fontWeight = FontWeight.Bold)
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.padding(start = 5.dp))
                    }
                }
                Surface(color = Color.White.copy(alpha = 0.14f), shape = CircleShape, modifier = Modifier.size(76.dp)) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = CivicGold, modifier = Modifier.size(40.dp)) }
                }
            }
        }
    }
}

@Composable
private fun PracticeThemeCard(theme: CivicThemeId, onClick: () -> Unit) {
    val accent = themeAccent(theme)
    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            ThemeSymbol(theme, accent, 46)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(theme.shortTitle, fontWeight = FontWeight.Bold)
                Text("20 questions • tirage aléatoire", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(shape = CircleShape, color = accent.copy(alpha = 0.12f), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.PlayArrow, contentDescription = "Commencer", tint = accent) }
            }
        }
    }
}

@Composable
private fun ProgressScreen(progress: ProgressSnapshot, onGoToPractice: () -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text("Vos progrès", style = MaterialTheme.typography.headlineMedium)
            Text("Une lecture claire de votre préparation, thème par thème.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(3.dp)) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    ProgressRing(
                        progress.readinessScore,
                        if (progress.readinessStatus == ReadinessStatus.INSUFFICIENT) "—" else "${(progress.readinessScore * 100).roundToInt()}%",
                        MaterialTheme.colorScheme.surfaceVariant,
                        readinessColor(progress.readinessStatus),
                    )
                    Spacer(Modifier.width(18.dp))
                    Column(Modifier.weight(1f)) {
                        Text(readinessLabel(progress.readinessStatus), style = MaterialTheme.typography.titleLarge)
                        Text(readinessDescription(progress.readinessStatus), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 5.dp))
                    }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(progress.attemptsCount.toString(), "Tests passés", Icons.Default.Quiz, CivicBlue, Modifier.weight(1f))
                StatCard(progress.bestMockScore?.let { "$it/40" } ?: "—", "Meilleur examen", Icons.Default.EmojiEvents, CivicGold, Modifier.weight(1f))
            }
        }
        item { SectionHeader("Maîtrise des thèmes", "Calculée à partir des réponses enregistrées") }
        items(progress.themes) { mastery ->
            val color = masteryColor(mastery.status)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text(mastery.theme.shortTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text(if (mastery.total == 0) "Aucune réponse" else "${mastery.correct}/${mastery.total} réponses correctes", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = 0.13f)) {
                    Text(masteryLabel(mastery.status), style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), color = color)
                }
            }
        }
        item {
            Button(onClick = onGoToPractice, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (progress.attemptsCount == 0) "Faire mon premier test" else "Continuer à m'entraîner")
            }
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, icon: ImageVector, accent: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = accent)
            Text(value, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 9.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LearningDetailScreen(theme: CivicThemeId, onBack: () -> Unit, onStartQuiz: () -> Unit) {
    val content = DemoContent.learningFor(theme)
    val accent = themeAccent(theme)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fiche d'apprentissage") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        bottomBar = {
            Surface(shadowElevation = 12.dp) {
                Button(onClick = onStartQuiz, modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp).height(52.dp), shape = RoundedCornerShape(16.dp)) {
                    Icon(Icons.Default.Quiz, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Tester mes connaissances")
                }
            }
        },
    ) { padding ->
        LazyColumn(contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 100.dp), modifier = Modifier.padding(padding), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.13f))) {
                    Column(Modifier.padding(20.dp)) {
                        ThemeSymbol(theme, accent, 62)
                        Text(theme.title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp))
                        Text(content.overview, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
            item { SectionHeader("L'essentiel à retenir", "Les repères prioritaires de ce thème") }
            items(content.keyFacts) { fact ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(18.dp)) {
                    Row(Modifier.padding(15.dp), verticalAlignment = Alignment.Top) {
                        Surface(shape = CircleShape, color = accent.copy(alpha = 0.14f), modifier = Modifier.size(30.dp)) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Check, contentDescription = null, tint = accent, modifier = Modifier.size(17.dp)) }
                        }
                        Text(fact, modifier = Modifier.padding(start = 12.dp), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            item { SectionHeader("Chapitres", "Avancez à votre rythme") }
            items(content.chapters) { chapter ->
                Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(18.dp), tonalElevation = 1.dp) {
                    Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (chapter.completed) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = if (chapter.completed) CivicGreen else accent)
                        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                            Text(chapter.title, fontWeight = FontWeight.SemiBold)
                            Text("${chapter.durationMinutes} min", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamIntroScreen(onBack: () -> Unit, onStart: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Examen blanc") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.size(112.dp).clip(CircleShape).background(Brush.linearGradient(listOf(CivicBlue, CivicNavy))),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = CivicGold, modifier = Modifier.size(58.dp)) }
            Text("Dans les conditions de l'examen", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 20.dp))
            Text("Un entraînement chronométré pour mesurer vos acquis sur les cinq thèmes.", color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ExamInfoBadge(Icons.Default.Quiz, "40", "questions", Modifier.weight(1f))
                ExamInfoBadge(Icons.Default.Timer, "45", "minutes", Modifier.weight(1f))
                ExamInfoBadge(Icons.Default.Verified, "32", "pour réussir", Modifier.weight(1f))
            }
            Spacer(Modifier.height(22.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(22.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text("Avant de commencer", style = MaterialTheme.typography.titleLarge)
                    ExamRule(Icons.Default.LockClock, "Vous disposez de 45 minutes ; le test se termine automatiquement à zéro.")
                    ExamRule(Icons.Default.CheckCircle, "Une seule bonne réponse parmi quatre propositions.")
                    ExamRule(Icons.Default.Public, "Toutes les thématiques sont représentées.")
                }
            }
            Button(onClick = onStart, modifier = Modifier.fillMaxWidth().padding(top = 22.dp).height(54.dp), shape = RoundedCornerShape(17.dp)) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Lancer l'examen blanc")
            }
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(50.dp), shape = RoundedCornerShape(17.dp)) {
                Text("Continuer à réviser")
            }
        }
    }
}

@Composable
private fun ExamInfoBadge(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.aspectRatio(0.9f), color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(18.dp), tonalElevation = 2.dp) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(value, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 6.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ExamRule(icon: ImageVector, text: String) {
    Row(Modifier.padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        Text(text, modifier = Modifier.padding(start = 11.dp), style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizScreen(
    themeKey: String,
    target: ExamTarget,
    repository: AttemptRepository,
    onBack: () -> Unit,
) {
    val isMock = themeKey == "ALL"
    val theme = if (isMock) null else CivicThemeId.valueOf(themeKey)
    var runId by rememberSaveable { mutableIntStateOf(0) }
    val questions by produceState<List<Question>>(initialValue = emptyList(), themeKey, target, runId) {
        val recentConcepts = repository.recentConceptIds()
        value = if (isMock) {
            QuestionGenerator.mockExam(target = target, recentConceptIds = recentConcepts)
        } else {
            QuestionGenerator.thematicQuiz(theme = theme!!, target = target, recentConceptIds = recentConcepts)
        }
    }
    var currentIndex by rememberSaveable(runId) { mutableIntStateOf(0) }
    var selectedIndex by rememberSaveable(runId) { mutableIntStateOf(-1) }
    var validated by rememberSaveable(runId) { mutableStateOf(false) }
    var finished by rememberSaveable(runId) { mutableStateOf(false) }
    val answers = remember(runId) { mutableStateMapOf<Int, Int>() }
    var remainingSeconds by rememberSaveable(runId) { mutableIntStateOf(if (isMock) 45 * 60 else 0) }
    val startedAt = remember(runId) { System.currentTimeMillis() }
    var savedAttemptId by remember(runId) { mutableStateOf<String?>(null) }
    val haptic = LocalHapticFeedback.current
    val score = answers.count { (index, answer) -> questions.getOrNull(index)?.correctIndex == answer }

    LaunchedEffect(isMock, runId, finished, questions.isNotEmpty()) {
        if (isMock && !finished && questions.isNotEmpty()) {
            while (remainingSeconds > 0 && !finished) {
                delay(1_000)
                remainingSeconds--
            }
            if (remainingSeconds <= 0 && !finished) {
                if (selectedIndex >= 0) answers[currentIndex] = selectedIndex
                finished = true
            }
        }
    }

    LaunchedEffect(finished, runId) {
        if (finished && questions.isNotEmpty() && savedAttemptId == null) {
            savedAttemptId = repository.saveAttempt(
                mode = if (isMock) AttemptMode.MOCK else AttemptMode.THEMATIC,
                target = target,
                theme = theme,
                startedAt = startedAt,
                durationSeconds = ((System.currentTimeMillis() - startedAt) / 1_000).toInt(),
                answers = questions.mapIndexed { index, question ->
                    AnswerSnapshot(question, answers[index] ?: -1)
                },
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isMock) "Examen blanc" else theme!!.shortTitle, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quitter") } },
                actions = {
                    if (isMock && !finished) {
                        Surface(shape = RoundedCornerShape(50), color = CivicGold.copy(alpha = 0.16f)) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = CivicGold, modifier = Modifier.size(17.dp))
                                Text(
                                    "%02d:%02d".format(remainingSeconds / 60, remainingSeconds % 60),
                                    color = CivicGold,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 5.dp),
                                )
                            }
                        }
                        Spacer(Modifier.width(7.dp))
                    }
                    Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.padding(end = 12.dp)) {
                        Text(
                            if (questions.isEmpty()) "…" else "${currentIndex + 1}/${questions.size}",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        if (finished) {
            ResultScreen(
                score = score,
                total = questions.size,
                isMock = isMock,
                saved = savedAttemptId != null,
                onBack = onBack,
                onRetry = {
                    runId++
                    currentIndex = 0
                    selectedIndex = -1
                    validated = false
                    finished = false
                },
                modifier = Modifier.padding(padding),
            )
        } else if (questions.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Préparation de votre série…", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Column(Modifier.fillMaxSize().padding(padding)) {
                LinearProgressIndicator(
                    progress = { (currentIndex + 1f) / questions.size },
                    modifier = Modifier.fillMaxWidth().height(5.dp),
                    strokeCap = StrokeCap.Round,
                )
                AnimatedContent(
                    targetState = currentIndex,
                    transitionSpec = {
                        (slideInHorizontally { it / 2 } + fadeIn()).togetherWith(slideOutHorizontally { -it / 3 } + fadeOut())
                    },
                    label = "question",
                    modifier = Modifier.weight(1f),
                ) { index ->
                    QuestionContent(
                        question = questions[index],
                        selectedIndex = selectedIndex,
                        validated = validated,
                        onSelect = { if (!validated) selectedIndex = it },
                    )
                }
                Surface(shadowElevation = 14.dp, color = MaterialTheme.colorScheme.surface) {
                    Button(
                        onClick = {
                            val question = questions[currentIndex]
                            if (isMock) {
                                answers[currentIndex] = selectedIndex
                                if (selectedIndex == question.correctIndex) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                                if (currentIndex == questions.lastIndex) {
                                    finished = true
                                } else {
                                    currentIndex++
                                    selectedIndex = -1
                                }
                            } else if (!validated) {
                                answers[currentIndex] = selectedIndex
                                validated = true
                                if (selectedIndex == question.correctIndex) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                } else {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            } else if (currentIndex == questions.lastIndex) {
                                finished = true
                            } else {
                                currentIndex++
                                selectedIndex = -1
                                validated = false
                            }
                        },
                        enabled = selectedIndex >= 0,
                        modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp).height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(
                            when {
                                isMock && currentIndex == questions.lastIndex -> "Terminer l’examen"
                                isMock -> "Question suivante"
                                !validated -> "Valider ma réponse"
                                currentIndex == questions.lastIndex -> "Voir mon résultat"
                                else -> "Question suivante"
                            },
                        )
                        if (validated || isMock) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionContent(question: Question, selectedIndex: Int, validated: Boolean, onSelect: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        AssistChip(
            onClick = {},
            label = { Text(question.theme.shortTitle) },
            leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(17.dp)) },
            colors = AssistChipDefaults.assistChipColors(containerColor = themeAccent(question.theme).copy(alpha = 0.12f)),
            border = null,
        )
        Text(question.text, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 14.dp, bottom = 18.dp))
        question.options.forEachIndexed { index, option ->
            AnswerOption(
                index = index,
                text = option,
                selected = selectedIndex == index,
                correct = question.correctIndex == index,
                validated = validated,
                onClick = { onSelect(index) },
            )
            Spacer(Modifier.height(10.dp))
        }
        AnimatedVisibility(
            visible = validated,
            enter = fadeIn() + slideInVertically { it / 3 },
        ) {
            val success = selectedIndex == question.correctIndex
            Card(
                colors = CardDefaults.cardColors(containerColor = (if (success) CivicGreen else CivicRed).copy(alpha = 0.12f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(if (success) Icons.Default.CheckCircle else Icons.Default.Error, contentDescription = null, tint = if (success) CivicGreen else CivicRed)
                    Column(Modifier.padding(start = 12.dp)) {
                        Text(if (success) "Bonne réponse !" else "À retenir", fontWeight = FontWeight.Bold, color = if (success) CivicGreen else CivicRed)
                        Text(question.explanation, modifier = Modifier.padding(top = 4.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun AnswerOption(index: Int, text: String, selected: Boolean, correct: Boolean, validated: Boolean, onClick: () -> Unit) {
    val stateColor = when {
        validated && correct -> CivicGreen
        validated && selected && !correct -> CivicRed
        selected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }
    val container = when {
        validated && correct -> CivicGreen.copy(alpha = 0.12f)
        validated && selected && !correct -> CivicRed.copy(alpha = 0.12f)
        selected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).clickable(enabled = !validated, onClick = onClick)
            .border(if (selected || (validated && correct)) 2.dp else 1.dp, stateColor, RoundedCornerShape(18.dp)),
        color = container,
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = stateColor.copy(alpha = if (selected || (validated && correct)) 1f else 0.12f), modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    if (validated && correct) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text(('A'.code + index).toChar().toString(), color = if (selected) Color.White else stateColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Text(text, modifier = Modifier.weight(1f).padding(start = 13.dp), style = MaterialTheme.typography.bodyLarge, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
        }
    }
}

@Composable
private fun ResultScreen(
    score: Int,
    total: Int,
    isMock: Boolean,
    saved: Boolean,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ratio = if (total == 0) 0f else score.toFloat() / total
    val passed = ratio >= 0.8f
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(28.dp))
        ProgressRing(
            progress = ratio,
            label = "${(ratio * 100).roundToInt()}%",
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            progressColor = if (passed) CivicGreen else CivicGold,
            size = 132,
        )
        Text(if (passed) "Très beau résultat !" else "Continuez, vous progressez", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 22.dp))
        Text("$score bonne${if (score > 1) "s" else ""} réponse${if (score > 1) "s" else ""} sur $total", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
            Column(Modifier.padding(18.dp)) {
                ResultLine("Score obtenu", "$score/$total")
                HorizontalDivider(Modifier.padding(vertical = 12.dp))
                ResultLine("Seuil de réussite", if (isMock) "32/40" else "16/20")
                HorizontalDivider(Modifier.padding(vertical = 12.dp))
                ResultLine("Mode", if (isMock) "Examen blanc" else "QCM thématique")
            }
        }
        Button(onClick = onRetry, modifier = Modifier.fillMaxWidth().padding(top = 22.dp).height(52.dp), shape = RoundedCornerShape(16.dp)) {
            Icon(Icons.Default.Quiz, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Refaire un test")
        }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(50.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Retour à l'accueil")
        }
        Text(
            if (saved) "Résultat et réponses enregistrés dans l’historique." else "Enregistrement du résultat…",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = if (saved) CivicGreen else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 18.dp),
        )
    }
}

@Composable
private fun ResultLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ProgressRing(
    progress: Float,
    label: String,
    trackColor: Color,
    progressColor: Color,
    size: Int = 92,
) {
    var target by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(progress) { target = progress }
    val animated by animateFloatAsState(targetValue = target, animationSpec = tween(900), label = "ring")
    Box(modifier = Modifier.size(size.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 9.dp.toPx()
            drawArc(trackColor, -90f, 360f, false, style = Stroke(stroke, cap = StrokeCap.Round), size = Size(this.size.width - stroke, this.size.height - stroke), topLeft = Offset(stroke / 2, stroke / 2))
            if (animated > 0f) {
                drawArc(progressColor, -90f, animated * 360f, false, style = Stroke(stroke, cap = StrokeCap.Round), size = Size(this.size.width - stroke, this.size.height - stroke), topLeft = Offset(stroke / 2, stroke / 2))
            }
        }
        Text(label, fontWeight = FontWeight.ExtraBold, fontSize = if (size > 100) 25.sp else 19.sp, color = if (progress > 0f) progressColor else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun CompactThemeCard(theme: CivicThemeId, onClick: () -> Unit) {
    val accent = themeAccent(theme)
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            ThemeSymbol(theme, accent, 48)
            Column(Modifier.weight(1f).padding(horizontal = 13.dp)) {
                Text(theme.shortTitle, fontWeight = FontWeight.Bold)
                Text(theme.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = accent)
        }
    }
}

@Composable
private fun ThemeSymbol(theme: CivicThemeId, accent: Color, size: Int) {
    Surface(shape = RoundedCornerShape((size / 3).dp), color = accent.copy(alpha = 0.13f), modifier = Modifier.size(size.dp)) {
        Box(contentAlignment = Alignment.Center) {
            Text(theme.symbol, fontWeight = FontWeight.ExtraBold, color = accent, fontSize = if (theme.symbol.length > 3) 11.sp else 14.sp)
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun themeAccent(theme: CivicThemeId): Color = when (theme) {
    CivicThemeId.PRINCIPLES -> CivicBlue
    CivicThemeId.INSTITUTIONS -> Color(0xFF7956D8)
    CivicThemeId.RIGHTS -> CivicGreen
    CivicThemeId.HISTORY -> Color(0xFFD28524)
    CivicThemeId.SOCIETY -> CivicRed
}
