# Mon Civique

Application Android moderne pour préparer les trois mentions du test civique français : naturalisation, carte de résident et carte de séjour pluriannuelle.

## Fonctionnalités

- apprentissage par thème ;
- fiches d'apprentissage détaillées avec progression persistante par chapitre ;
- QCM thématique de 20 questions ;
- examen blanc chronométré de 40 questions, avec un seuil de réussite de 32/40 ;
- reprise d'une série après une rotation ou une recréation de l'activité ;
- navigation libre entre les questions, réponses facultatives et confirmation avant de terminer ;
- questions tirées aléatoirement et réponses mélangées ;
- banques de questions adaptées à la mention sélectionnée, avec 100 formulations par thème et par parcours ;
- historique local filtrable des tentatives, réponses et corrections ;
- suppression d’une tentative ou de l’historique du parcours sélectionné ;
- estimation de la préparation avec pondération des examens récents, couverture des notions et niveau de confiance ;
- sources officielles accessibles depuis les corrections ;
- mode sombre permanent, code couleur par parcours et interface Material 3.

Les cinq thèmes sont : principes et valeurs de la République, système institutionnel et politique, droits et devoirs, histoire-géographie-culture, et vivre dans la société française.

## Ouvrir le projet

1. Ouvrir ce dossier dans Android Studio.
2. Attendre la synchronisation Gradle.
3. Sélectionner la configuration `app` et un appareil ou émulateur Android.
4. Cliquer sur **Run**.

Le projet utilise actuellement le JDK 21 pour Gradle. Sur cette machine, le chemin utilisé est `C:\Users\hamza\.jdks\jbr-21.0.11`.

## Vérifications locales

```powershell
./gradlew.bat testDebugUnitTest
./gradlew.bat assembleDebug
./gradlew.bat lintDebug
./gradlew.bat assembleRelease
```

La banque s'appuie sur le référentiel de l'arrêté du 10 octobre 2025 et sur les listes de questions de connaissance publiées par le ministère de l'Intérieur pour les mentions CSP, carte de résident et naturalisation.

Mon Civique est une application indépendante et n'est ni éditée ni approuvée par l'administration française. Les références officielles restent prioritaires.

## APK de test

La dernière APK debug installable est disponible dans [`artifacts/mon-civique-debug.apk`](artifacts/mon-civique-debug.apk). Elle est destinée aux tests Android et n’est pas signée pour une publication Play Store.
