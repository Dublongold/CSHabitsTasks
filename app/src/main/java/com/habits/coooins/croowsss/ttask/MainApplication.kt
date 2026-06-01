package com.habits.coooins.croowsss.ttask

import android.app.Application
import com.habits.coooins.croowsss.ttask.data.database.AppDatabase
import com.habits.coooins.croowsss.ttask.data.fake.SampleDataGenerator
import com.habits.coooins.croowsss.ttask.data.settings.SettingsManager
import com.habits.coooins.croowsss.ttask.data.focussession.FocusSessionRepository
import com.habits.coooins.croowsss.ttask.data.goal.GoalRepository
import com.habits.coooins.croowsss.ttask.data.habit.HabitRepository
import com.habits.coooins.croowsss.ttask.data.task.TaskRepository
import com.habits.coooins.croowsss.ttask.ui.session.FocusTimerViewModel
import com.habits.coooins.croowsss.ttask.ui.session.history.SessionHistoryViewModel
import com.habits.coooins.croowsss.ttask.ui.goals.delete.DeleteGoalsViewModel
import com.habits.coooins.croowsss.ttask.ui.goals.detail.GoalDetailViewModel
import com.habits.coooins.croowsss.ttask.ui.goals.GoalsViewModel
import com.habits.coooins.croowsss.ttask.ui.habits.delete.DeleteHabitsViewModel
import com.habits.coooins.croowsss.ttask.ui.habits.detail.HabitDetailViewModel
import com.habits.coooins.croowsss.ttask.ui.habits.HabitsViewModel
import com.habits.coooins.croowsss.ttask.ui.home.HomeViewModel
import com.habits.coooins.croowsss.ttask.ui.insights.InsightsViewModel
import com.habits.coooins.croowsss.ttask.ui.settings.SettingsViewModel
import com.habits.coooins.croowsss.ttask.ui.tasks.delete.DeleteTasksViewModel
import com.habits.coooins.croowsss.ttask.ui.tasks.detail.TaskDetailViewModel
import com.habits.coooins.croowsss.ttask.ui.tasks.TasksViewModel
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bind
import org.kodein.di.bindProviderOf
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.singleton

class MainApplication : Application(), DIAware {
    override val di: DI = DI {
        bind<AppDatabase>() with singleton {
            AppDatabase.getInstance(applicationContext)
        }

        bind<SampleDataGenerator>() with singleton {
            SampleDataGenerator(instance<AppDatabase>(), applicationContext)
        }

        bind<TaskRepository>() with singleton {
            TaskRepository(
                instance<AppDatabase>().taskDao()
            )
        }
        bind<HabitRepository>() with singleton {
            HabitRepository(
                instance<AppDatabase>().habitDao()
            )
        }
        bind<GoalRepository>() with singleton {
            GoalRepository(
                instance<AppDatabase>().goalDao()
            )
        }
        bind<FocusSessionRepository>() with singleton {
            FocusSessionRepository(
                instance<AppDatabase>().focusSessionDao()
            )
        }
        bind<SettingsManager>() with singleton {
            SettingsManager(
                applicationContext
            )
        }

        bindProviderOf(::HomeViewModel)
        bindProviderOf(::TasksViewModel)
        bind<TaskDetailViewModel>() with factory { taskId: Long? ->
            TaskDetailViewModel(
                instance(),
                taskId
            )
        }
        bindProviderOf(::HabitsViewModel)
        bind<HabitDetailViewModel>() with factory { habitId: Long? ->
            HabitDetailViewModel(
                instance(),
                habitId
            )
        }
        bindProviderOf(::GoalsViewModel)
        bind<GoalDetailViewModel>() with factory { goalId: Long? ->
            GoalDetailViewModel(
                instance(),
                goalId
            )
        }
        bindProviderOf(::FocusTimerViewModel)
        bindProviderOf(::SessionHistoryViewModel)
        bindProviderOf(::InsightsViewModel)
        bindProviderOf(::SettingsViewModel)
        bindProviderOf(::DeleteTasksViewModel)
        bindProviderOf(::DeleteHabitsViewModel)
        bindProviderOf(::DeleteGoalsViewModel)
    }
}