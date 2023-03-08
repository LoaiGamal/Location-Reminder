package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var database: RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @get:Rule
    var instantExecutor = InstantTaskExecutorRule()

    @Before
    fun initRepository(){
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java).allowMainThreadQueries().build()
        remindersLocalRepository = RemindersLocalRepository(database.reminderDao())
    }

    @After
    fun closeDatabase() {
        if(::database.isInitialized){
            database.close()
        }
    }

    private val reminder1 = ReminderDTO(
        "Title1",
        "Description1",
        "Location1",
        (-360 .. 360).random().toDouble(),
        (-360 .. 360).random().toDouble()
    )
    private val reminder2 = ReminderDTO(
        "Title2",
        "Description2",
        "Location2",
        (-360 .. 360).random().toDouble(),
        (-360 .. 360).random().toDouble()
    )
    private val reminder3 = ReminderDTO(
        "Title3",
        "Description3",
        "Location3",
        (-360 .. 360).random().toDouble(),
        (-360 .. 360).random().toDouble()
    )

    @Test
    fun saveReminder_retrieveReminderById() = runBlocking {
        remindersLocalRepository.saveReminder(reminder1)

        val result = remindersLocalRepository.getReminder(reminder1.id) as com.udacity.project4.locationreminders.data.dto.Result.Success

        assertThat(result.data.id, `is`(reminder1.id))
        assertThat(result.data.title, `is`(reminder1.title))
        assertThat(result.data.description, `is`(reminder1.description))
        assertThat(result.data.latitude, `is`(reminder1.latitude))
        assertThat(result.data.longitude, `is`(reminder1.longitude))
        assertThat(result.data.location, `is`(reminder1.location))
    }

    @Test
    fun saveReminders_retrieveAllReminders() = runBlocking {
        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.saveReminder(reminder2)
        remindersLocalRepository.saveReminder(reminder3)

        val result = remindersLocalRepository.getReminders() as com.udacity.project4.locationreminders.data.dto.Result.Success

        assertThat(result.data.size, `is`(3))
    }

    @Test
    fun saveReminders_deleteAllReminders() = runBlocking {
        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.saveReminder(reminder2)
        remindersLocalRepository.saveReminder(reminder3)

        val result = remindersLocalRepository.getReminders() as com.udacity.project4.locationreminders.data.dto.Result.Success

        assertThat(result.data.size, `is`(3))

        remindersLocalRepository.deleteAllReminders()

        val result1 = remindersLocalRepository.getReminders() as com.udacity.project4.locationreminders.data.dto.Result.Success

        assertThat(result1.data.size, `is`(0))
    }

    @Test
    fun getReminder_returnError() = runBlocking {
        remindersLocalRepository.deleteAllReminders()

        val result = remindersLocalRepository.getReminder(reminder1.id) as com.udacity.project4.locationreminders.data.dto.Result.Error

        assertThat(result.message, `is`("Reminder not found!"))
    }
}