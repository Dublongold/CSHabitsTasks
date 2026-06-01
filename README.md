# Focus Ledger

Focus Ledger is a small Android productivity app for managing daily tasks, habits, goals, and focus sessions.

## Features

- Today dashboard with a quick overview of current progress
- Task management with details, priorities, statuses, and deletion flow
- Habit tracking with habit details and logs
- Goal tracking with progress screens
- Focus timer and session history
- Insights screen for productivity statistics
- Settings screen for app preferences

## Screenshots

<p align="center">
  <img width="360" height="800" alt="image" src="https://github.com/user-attachments/assets/541807b1-dc5f-48dc-914e-6396ae4696b8" />
  <img width="360" height="800" alt="image" src="https://github.com/user-attachments/assets/aa4648b9-cf2f-4885-a48d-f977c64dcc67" />
  <br><br>
  <img width="360" height="800" alt="image" src="https://github.com/user-attachments/assets/4bc0c1af-4c99-49c9-86c3-01407092ffb8" />
  <img width="360" height="800" alt="image" src="https://github.com/user-attachments/assets/b6fa58fd-d06a-4ad1-9365-09cf05af5795" />
  <br><br>
  <img width="360" height="800" alt="image" src="https://github.com/user-attachments/assets/eb3cce0a-d255-4e63-8b6f-ad1372cd3252" />
  <img width="360" height="800" alt="image" src="https://github.com/user-attachments/assets/47cf2d94-1728-4805-ad50-b41a071a81d5" />
  <br><br>
  <img width="360" height="800" alt="image" src="https://github.com/user-attachments/assets/7774bb46-32b7-4654-bda0-893f55cb8000" />
  <img width="360" height="800" alt="image" src="https://github.com/user-attachments/assets/b6e536ad-cda5-42b9-aa91-e7f577031f79" />
</p>


## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Room
- DataStore Preferences
- Kodein DI

## Requirements

- Android Studio
- Android SDK 36
- Minimum Android version: Android 8.0 / API 26

## Run Locally

```bash
git clone https://github.com/Dublongold/CSHabitsTasks.git
cd CSHabitsTasks
./gradlew assembleDebug
```

Or open the project in Android Studio, sync Gradle, and run the `app` configuration.

## Project Structure

```text
app/src/main/java/com/habits/coooins/croowsss/ttask/
├── data/          # Room database, entities, DAOs, repositories, settings
├── navigation/    # App routes and navigation graph
└── ui/            # Compose screens and theme
```

## License

No license specified.
