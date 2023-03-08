package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeDataSource (var reminders: MutableList<ReminderDTO> = mutableListOf()): ReminderDataSource {

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (reminders.isEmpty())
            com.udacity.project4.locationreminders.data.dto.Result.Success(reminders)
        else
            com.udacity.project4.locationreminders.data.dto.Result.Success(reminders)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val result = reminders.firstOrNull{it.id == id}
        result?.let { return com.udacity.project4.locationreminders.data.dto.Result.Success(it) }
        return com.udacity.project4.locationreminders.data.dto.Result.Error("Reminder is not found")
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }
}