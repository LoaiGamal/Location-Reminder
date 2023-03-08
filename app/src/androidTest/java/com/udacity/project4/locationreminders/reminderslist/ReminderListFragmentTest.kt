package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.koin.test.inject
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest: AutoCloseKoinTest() {

    private lateinit var localRepository: RemindersLocalRepository
    private lateinit var repository: ReminderDataSource
    private val dataSource: ReminderDataSource by inject()
    private lateinit var applicationContext: Application

    @get: Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutor = InstantTaskExecutorRule()

    @Before
    fun setup() {
        stopKoin()

        applicationContext = getApplicationContext()

        val testModules = module {
            viewModel {
                RemindersListViewModel(applicationContext, get() as ReminderDataSource)
            }

            single {
                SaveReminderViewModel(applicationContext, get() as ReminderDataSource)
            }

            single {
                RemindersLocalRepository(get()) as ReminderDataSource
            }

            single {
                LocalDB.createRemindersDao(applicationContext)
            }
        }

        startKoin { modules(testModules) }

        repository = get()

        runBlocking { dataSource.deleteAllReminders() }
    }

    @After
    fun tearDown(){
        runBlocking {
            dataSource.deleteAllReminders()
        }
        stopKoin()
    }

    private val reminder1 =
        ReminderDTO(
            "Title1",
            "Description1",
            "Location1",
            (-360 .. 360).random().toDouble(),
            (-360 .. 360).random().toDouble()
        )

    private val reminder2 =
        ReminderDTO(
            "Title2",
            "Description2",
            "Location2",
            (-360 .. 360).random().toDouble(),
            (-360 .. 360).random().toDouble()
        )

    @Test
    fun clickAddReminderFAB_navigateToSaveReminderFragment(){
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)

        scenario.onFragment{
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }

    @Test
    fun remindersList_displayRemindersDataInUI() = runTest{
        dataSource.saveReminder(reminder1)
        dataSource.saveReminder(reminder2)

        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        val navController = mock(NavController::class.java)

        scenario.onFragment{
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withText(reminder1.title)).check(matches(isDisplayed()))
        onView(withText(reminder1.description)).check(matches(isDisplayed()))
        onView(withId(R.id.noDataTextView)).check(matches(not(isDisplayed())))

        onView(withId(R.id.reminderssRecyclerView)).perform(RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
            hasDescendant(withText(reminder1.title))
        ))
    }

    @Test
    fun reminderList_noDataDisplayed() = runTest {
        dataSource.saveReminder(reminder1)
        dataSource.saveReminder(reminder2)
        dataSource.deleteAllReminders()

        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)

        scenario.onFragment{
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
        onView(withText(reminder1.title)).check(doesNotExist())
    }
}
