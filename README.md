# Mon Civique

Application Android moderne pour préparer le test civique français lié à la naturalisation et à la carte de résident.

## Fonctionnalités

- apprentissage par thème ;
- QCM thématique de 20 questions ;
- examen blanc chronométré de 40 questions, avec un seuil de réussite de 32/40 ;
- questions tirées aléatoirement et réponses mélangées ;
- historique local des tentatives, réponses et corrections ;
- suppression d’une tentative ou de tout l’historique ;
- estimation de la préparation globale et de la maîtrise de chaque thème ;
- thème clair/sombre et interface Material 3.

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
```

Les documents sources utilisés pour préparer la banque sont conservés dans le dossier `ressources/`.
