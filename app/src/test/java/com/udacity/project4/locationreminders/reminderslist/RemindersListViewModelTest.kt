package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get: Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var reminderListViewModel: RemindersListViewModel

    @Before
    fun setup() {
        fakeDataSource = FakeDataSource()
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    val list = listOf<ReminderDTO>(
        ReminderDTO(
            "Title1",
            "Description1",
            "Location1",
            0.0,
            0.0
        ),
        ReminderDTO(
            "Title2",
            "Description2",
            "Location2",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        ),
        ReminderDTO(
            "Title3",
            "Description3",
            "Location3",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        ),
        ReminderDTO(
            "Title4",
            "Description4",
            "Location4",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        )
    )
    private val reminder1 = list[0]
    private val reminder2 = list[1]
    private val reminder3 = list[2]

    @Test
    fun loadRemindersTest_withEmptyList_returnTrue() {
        reminderListViewModel.loadReminders()

        assertEquals(emptyList<ReminderDataItem>(), reminderListViewModel.remindersList.getOrAwaitValue())
    }

    @Test
    fun loadRemindersTest_withThreeReminders_returnFalse() {
        val remindersList = mutableListOf(reminder1, reminder2, reminder3)


        fakeDataSource = FakeDataSource(remindersList)
        reminderListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)

        reminderListViewModel.loadReminders()


        assertFalse(emptyList<ReminderDataItem>() == reminderListViewModel.remindersList.getOrAwaitValue())
    }

    @Test
    fun checkLoading() {
        mainCoroutineRule.pauseDispatcher()

        reminderListViewModel.loadReminders()

        assertTrue(reminderListViewModel.showLoading.getOrAwaitValue())

        mainCoroutineRule.resumeDispatcher()

        assertFalse(reminderListViewModel.showLoading.getOrAwaitValue())
    }

    @Test
    fun checkError() {
        fakeDataSource.setError(true)

        reminderListViewModel.loadReminders()

        assertEquals("Error in getting reminders", reminderListViewModel.showSnackBar.getOrAwaitValue())
    }
}