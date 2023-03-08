package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource (var reminders: MutableList<ReminderDTO> = mutableListOf()) : ReminderDataSource {

    var returnError = false

    fun setError(value: Boolean){
        returnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (!returnError) {
            return com.udacity.project4.locationreminders.data.dto.Result.Success(ArrayList(reminders))
        }
        return com.udacity.project4.locationreminders.data.dto.Result.Error("Error in getting reminders")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (!returnError) {
            val reminder = reminders.find {
                it.id == id
            }
            if(reminder != null)
                return com.udacity.project4.locationreminders.data.dto.Result.Success(reminder)
            else
                return com.udacity.project4.locationreminders.data.dto.Result.Error("Reminder not found!")
        }
        return com.udacity.project4.locationreminders.data.dto.Result.Error("Error in getting reminder")
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }
}