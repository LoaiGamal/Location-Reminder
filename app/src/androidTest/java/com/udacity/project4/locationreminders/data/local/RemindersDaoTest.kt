package com.udacity.project4.locationreminders.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

    @Before
    fun initDatabase(){
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java).build()
    }

    @After
    fun closeDatabase() {
        if (::database.isInitialized){
            database.close()
        }
    }

    @Test
    fun testDatabase() = runBlockingTest {
        val reminder =
            ReminderDTO(
                "Title",
                "Description",
                "Location",
                (-360 .. 360). random().toDouble(),
                (-360 .. 360). random().toDouble()
            )

        runBlocking {
            database.reminderDao().saveReminder(reminder)

            val result = database.reminderDao().getReminderById(reminder.id)

            assertThat(reminder.id, `is`(result!!.id))
            assertThat(reminder.title, `is`(result.title))
            assertThat(reminder.description, `is`(result.description))
            assertThat(reminder.latitude, `is`(result.latitude))
            assertThat(reminder.longitude, `is`(result.longitude))
            assertThat(reminder.location, `is`(result.location))
        }
    }
}
