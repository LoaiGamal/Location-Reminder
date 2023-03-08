package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.pauseDispatcher
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get: Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Before
    fun setup() {
        fakeDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }
    @After
    fun stopDown(){
        GlobalContext.stopKoin()
    }

    @Test
    fun saveReminderTest() = runBlocking {
        val reminder =
            ReminderDataItem(
                "Title",
                "Description",
                "Location",
                (-360 .. 360). random().toDouble(),
                (-360 .. 360). random().toDouble()
            )

        saveReminderViewModel.saveReminder(reminder)

        val temp = fakeDataSource.getReminder(reminder.id) as com.udacity.project4.locationreminders.data.dto.Result.Success

        assertEquals("Reminder Saved !", saveReminderViewModel.showToast.getOrAwaitValue())
        assertThat(reminder.id, `is`(temp.data.id))
        assertThat(reminder.title, `is`(temp.data.title))
        assertThat(reminder.description, `is`(temp.data.description))
        assertThat(reminder.location, `is`(temp.data.location))
        assertThat(reminder.latitude, `is`(temp.data.latitude))
        assertThat(reminder.longitude, `is`(temp.data.longitude))
    }

    @Test
    fun reminderData_validateAndSaveReminder() = runBlocking {
        val reminder =
            ReminderDataItem(
                "Title",
                "Description",
                "Location",
                (-360 .. 360). random().toDouble(),
                (-360 .. 360). random().toDouble()
            )

        val resultOfValidation = saveReminderViewModel.validateEnteredData(reminder)

        assertThat(resultOfValidation, `is`(true))
    }

    @Test
    fun validateAndSaveReminderTest_emptyTitle_returnFalse() = runBlocking {
        val reminder =
            ReminderDataItem(
                "",
                "Description",
                "Location",
                (-360 .. 360). random().toDouble(),
                (-360 .. 360). random().toDouble()
            )

        val resultOfValidation = saveReminderViewModel.validateEnteredData(reminder)

        assertFalse(resultOfValidation)
    }

    @Test
    fun validateAndSaveReminderTest_emptyLocation_returnFalse() = runBlocking {
        val reminder =
            ReminderDataItem(
                "Title",
                "Description",
                "",
                (-360 .. 360). random().toDouble(),
                (-360 .. 360). random().toDouble()
            )

        val resultOfValidation = saveReminderViewModel.validateEnteredData(reminder)

        assertFalse(resultOfValidation)
    }

    @Test
    fun validateAndSaveReminderTest_emptyTitleAndLocation_returnFalse() = runBlocking {
        val reminder =
            ReminderDataItem(
                "",
                "Description",
                "",
                (-360 .. 360). random().toDouble(),
                (-360 .. 360). random().toDouble()
            )

        val resultOfValidation = saveReminderViewModel.validateEnteredData(reminder)

        assertFalse(resultOfValidation)
    }

    @Test
    fun checkLoading() {
        mainCoroutineRule.pauseDispatcher()

        val reminder =
            ReminderDataItem(
                "Title",
                "Description",
                "Location",
                (-360 .. 360). random().toDouble(),
                (-360 .. 360). random().toDouble()
            )

        saveReminderViewModel.saveReminder(reminder)

        assertTrue(saveReminderViewModel.showLoading.getOrAwaitValue())
    }
}