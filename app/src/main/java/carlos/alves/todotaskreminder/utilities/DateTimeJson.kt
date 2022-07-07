package carlos.alves.todotaskreminder.utilities

import carlos.alves.todotaskreminder.database.DateTimeEntity
import java.time.LocalDate
import java.time.LocalTime

class DateTimeJson(val taskId: Int, val date: String, val time: String) {

    companion object{
        fun generateDateTimeJson(dateTimeEntity: DateTimeEntity): DateTimeJson {
            return DateTimeJson(dateTimeEntity.taskId, dateTimeEntity.date.toString(), dateTimeEntity.time.toString())
        }
    }

    fun convertToDateTimeEntity(): DateTimeEntity {
        val dateList = date.split('-')
        val timeList = time.split(':')

        return DateTimeEntity(
            taskId,
            LocalDate.of(dateList[0].toInt(), dateList[1].toInt(), dateList[2].toInt()),
            LocalTime.of(timeList[0].toInt(), timeList[1].toInt())
        )
    }
}