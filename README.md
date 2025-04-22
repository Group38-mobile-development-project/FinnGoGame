# 🎮 FinnGoGame - Game Review App

## 🔍 Overview
FinnGoGame is a mobile app that allows users to discover, rate, review, and save their favorite games. It includes user authentication via Firebase and features a modern UI built with Jetpack Compose. Users can also find top-rated games, filter game displays by genre or platform, and join a forum.

---

## 🔖 Features

### ✅ Must-Have Features
- User Login and Sign Up (Firebase Authentication)
- Stay logged in across app restarts
- Explore top-rated games
- Search games by title
- View detailed game information
- Rate games (1 to 5 stars)
- Update existing ratings
- Add games to favorites
- View saved favorite games
- Navigate via bottom and drawer menus

### ⭐ Nice-to-Have Features
- Sign in with Gmail (Google authentication)
- Filter by genre and platform
- View games by genre/platform
- Forum-like interaction screen
- Dark/Light theme toggle

---

## 🌐 Screen Structure

### 📅 Screen Archetypes
- **Login/Signup Screen**: Authenticates the user
- **Main Screen (Home)**: Shows top-rated and all games with a search bar
- **Game Detail Screen**: Displays description, image, and ratings
- **Favorites Screen**: Displays user-saved favorite games
- **Search Screen**: Enables search and filtered results
- **Genre/Platform Screens**: Allows exploration by category
- **Forum Page Screen**: Placeholder for community discussions
- **Settings/Profile Screen**: User info management

### ⚖️ Navigation
- **Tab & Drawer Navigation**
  - Home
  - Favorites
  - Settings
  - Profile (with login/logout)
  - Genre/Platform pages
- **Flow Navigation**
  - Home → Game Detail
  - Search → Game Detail
  - Genre/Platform List → Game List → Game Detail

---

## 🧳 Architecture & Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **State Management**: ViewModel + StateFlow
- **Backend**: Firebase (Firestore, Auth)
- **Paging**: Paging 3 for efficient game list
- **Image Loading**: Coil

---

## 📂 Data Layer Structure

The `data/` package is organized into the following submodules:

- `model/` — Defines domain models such as `Game`, `Genre`, `Platform`, etc.
- `dto/` — Data transfer objects used for parsing remote API responses.
- `mapper/` — Functions to convert DTOs into domain models.
- `repository/` — Repository classes that abstract data operations.
- `network/` — API service interfaces and related networking logic.
- `paging/` — PagingSource implementations for lazy loading game data.
- `users/` — Firebase-related operations (e.g., ratings, favorites).

This modular structure keeps data concerns separated and scalable.

---

## 👤 User Data Flow
- **Favorites** stored in Firestore under `users/{userId}/favorites/{gameId}`
- **Ratings** are updated transactionally and stored globally:
  - `gameRatings/{gameId}` for aggregate data
  - `users/{userId}/ratings/{gameId}` for user-specific rating

---

## 🎯 Key Components

### UI Components
- `TopAppBar.kt`: Includes menu, theme toggle, and drawer control
- `LeftAppBar.kt`: Drawer with navigation and auth-aware actions
- `MenuBar.kt`: Genre/Platform/Forum menu popup

### Screens
- `MainScreen.kt`: Home screen with top-rated games and list
- `GameDetailScreen.kt`: Details, preview, and rating interaction
- `FavoritesScreen.kt`: User's favorite games
- `GameListByGenreScreen.kt`, `GameListByPlatformScreen.kt`: Category filters
- `ForumPageScreen.kt`: Placeholder for community feature
- `FavoritesScreen.kt`: Shows the user's saved favorite games
- `LoginScreen.kt`: Email/password and Google login flow
- `SettingsScreen.kt`: Manage user account info (email, password, delete)

### Navigation
- Centralized in `AppNavHost.kt` using `NavHost` and `composable()`

---

## 🌐 Future Improvements
- Edit user profile with image and bio
- Game trailers and YouTube integration
- Push notifications for new top-rated games

---

## ✨ Setup & Run
```bash
# Clone repo
$ git clone https://github.com/Group38-mobile-development-project/GameStore.git

# Open in Android Studio
# Run on Android emulator or physical device
```

## ✨ Figma
- https://www.figma.com/design/qpacu3kVFz7cmwDu3HxSh1/MobileDevProjectFigmaGroup38?node-id=0-1&p=f&t=bpsysR6q2GyEcBc1-0
---

## ✨ Project Board
- https://github.com/orgs/Group38-mobile-development-project/projects/4
---

## 🚀 Author
- Developed by Ahn Sungmin, Ha Nguyen, Bibimaryam Jakipbaeva

