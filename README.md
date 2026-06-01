# Focus Ledger - Offline Productivity App

A fully offline Android productivity application built with Jetpack Compose, following all specified constraints.

## Features

### Navigation Structure

**Bottom Navigation (5 Main Tabs)**
1. **Today** - Dashboard overview with day at a glance, quick actions, and summaries
2. **Tasks** - Full task management with filters and search
3. **Habits** - Habit tracking with streak visualization
4. **Goals** - Goal management with progress tracking
5. **More** - Menu for additional features (Focus Timer, Session History, Insights, Settings)

### 12 Functional Screens

1. **Today/Dashboard** - Overview with stats, today's tasks preview, habits & goals summary
2. **Tasks List** - Filter by status, taskPriority, tag, with search functionality
3. **Task Details** - View and edit task information with full CRUD operations
4. **Habits List** - List all habits with streak visualization and quick logging
5. **Habit Details** - Calendar view of logs, edit habit settings, reset data
6. **Goals List** - List goals with progress bars and increment/decrement controls
7. **Goal Details** - View/edit goals with progress adjustment
8. **More Menu** - Central hub for additional features
9. **Focus Timer** - In-app timer (no background/notifications) with pause/resume
10. **Session History** - View all focus sessions with date filtering
11. **Insights** - Analytics dashboard with completion rates, habit consistency, focus time
12. **Settings** - Theme, default taskPriority, first day of week, data reset

## Technical Stack

- **UI**: Jetpack Compose + Material 3
- **Navigation**: Navigation Compose with bottom navigation
- **Architecture**: MVVM (ViewModel + UI State)
- **Database**: Room for offline storage
- **Settings**: DataStore Preferences
- **State Management**: StateFlow + Kotlin Coroutines

## Compliance with Constraints

✅ **No Network Access** - No internet permission, no API calls
✅ **No Camera** - No camera features
✅ **No File System** - No file picker or external storage
✅ **No Notifications** - No local/push notifications or alarms
✅ **Fully Offline** - All data stored locally in Room database
✅ **Zero Runtime Permissions** - App runs without requesting any permissions

## Data Model

- **Tasks** - Title, notes, due date, taskPriority (Low/Med/High), status (Open/Done/Archived), tags
- **Habits** - Name, schedule (Daily/Weekly), target count, active status, logs
- **Goals** - Title, description, target/current values, unit, status
- **Focus Sessions** - Start/end time, label, optional task link
- **Tags** - Name, color for task categorization

## Key Functionality

### Tasks
- Create, edit, delete tasks
- Filter by status, taskPriority, tag
- Search functionality
- Mark as done/archive

### Habits
- Track daily/weekly habits
- Log counts with calendar view
- View streaks and consistency
- Toggle active/inactive

### Goals
- Set numeric goals with units
- Track progress with visual indicators
- Increment/decrement progress
- Archive completed goals

### Focus Timer
- Start/pause/stop focus sessions
- Works only while app is open (no background service)
- Save sessions with optional labels
- View session history and statistics

### Insights
- Task completion rate
- Habit consistency score
- Total focus time
- Goal progress averages
- Filter by time window (Week/Month/All)

### Settings
- Theme selection (System/Light/Dark)
- Default task taskPriority
- First day of week
- Data reset (danger zone)

## Building & Running

1. Open project in Android Studio
2. Sync Gradle dependencies
3. Run on device or emulator (minSdk 26+)

## Architecture Highlights

- Clean separation: Data → Repository → ViewModel → UI
- Single Activity with Compose Navigation
- Type-safe navigation with sealed classes
- Reactive UI with StateFlow
- Room foreign keys and cascade deletes
- DataStore for app preferences

## Notes

- Timer stops when app is backgrounded (no notifications/alarms allowed)
- All data persists across app restarts
- No external dependencies beyond AndroidX
- Material 3 design throughout
