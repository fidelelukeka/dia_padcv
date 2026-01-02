# 📱 DIA_PADCV

Application Android développée en **Kotlin** avec **Jetpack Compose** pour la gestion des bénéficiaires et des distributions.  
Elle intègre un design moderne basé sur **Material 3**, une logique robuste **offline-first**, et une expérience utilisateur cohérente en **light/dark mode**.

Développée par l’équipe **Lukeka Digital Services**.

---

## ✨ Fonctionnalités principales

### 🔐 PermissionScreen
- Vérification cumulative des permissions obligatoires :
  - Localisation (suivi terrain)
  - Caméra (prise de photos)
  - Galerie (sauvegarde et récupération d’images)
- Présentation étape par étape avec explications claires.
- Barre de progression animée (0 → 3 étapes).
- Utilisation de **Material 3 Cards** et **icônes explicites** (LocationOn, CameraAlt, Photo).
- Blocage de la navigation tant que toutes les permissions ne sont pas accordées.

### 👤 LoginScreen
- Champs contrôlés avec validation et gestion des erreurs.
- Bouton de connexion désactivé si champs vides ou requête en cours.
- Loader circulaire pendant la connexion.
- Navigation automatique après succès via `LaunchedEffect`.
- Affichage des erreurs en rouge (`colorScheme.error`).

### 🧑‍🎨 AvatarViewScreen
- Génération sécurisée des initiales (évite crash `NoSuchElementException`).
- Fallback automatique (`"-"` ou `"?"`) si username vide.
- Texte responsive : taille proportionnelle au cercle (~65%).
- Texte en gras et contrasté (`onPrimary`) pour une meilleure lisibilité.
- Animation d’apparition (scale + fade-in).

### 🎨 Thème principal
- Gestion centralisée des couleurs système :
  - Par défaut → `colorScheme.background`
  - Avec Scaffold → `colorScheme.primary`
- Cohérence totale avec **Material 3** (dark/light mode, dynamic colors).
- Typographies modernisées (`headlineMedium`, `bodyMedium`).

### 🖼️ Icône & SplashScreen
- **Adaptive Icon** :
  - Foreground dynamique (`logo.png` / `logo-night.png`).
  - Background basé sur `@color/ic_launcher_background` (blanc en light, noir en dark).
- **SplashScreen Android 12+** :
  - Logo et fond synchronisés avec le thème du système.
  - Correction du problème de logo tronqué (scaling ~66%).
  - Expérience fluide et cohérente dès le lancement.

---

## 🛠️ Stack technique

- **Langage** : Kotlin
- **UI** : Jetpack Compose + Material 3
- **Architecture** : Offline-first, modularité, navigation via `NavHost`
- **Gestion des préférences** : `SharedPreferences` / `DataStore`
- **Permissions** : Accompanist Permissions API
- **Design** : Adaptive Icons, SplashScreen API (Android 12+)

---

## 🚀 Installation

1. Cloner le repository :
   ```bash
   git clone https://github.com/<username>/DIA_PADCV.git
