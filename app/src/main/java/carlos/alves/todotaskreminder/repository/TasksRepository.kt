package carlos.alves.todotaskreminder.repository

import java.util.concurrent.Callable
import java.util.concurrent.Executors

class TasksRepository(database: ToDoTaskReminderDatabase) {

    private val executor = Executors.newSingleThreadExecutor()

    private val databaseDao = database.tasksDatabaseDao()

    fun getWordCount(): List<String> = executor.submit(Callable { databaseDao.getAllTaskNames() }).get()

    fun getWordItem(): String? = executor.submit(Callable { databaseDao.getWordItem()}).get()

    fun insertWordItem(word: String) {
        executor.submit { databaseDao.insertWordItem(WordsList(word)) }
    }

    fun deleteWordItem(item: String) {
        executor.submit { databaseDao.deleteWordItem(item) }
    }

    fun getRound(round: Int): RoundItem = executor.submit(Callable { databaseDao.getRoundItem(round) }).get()

    fun insertRoundItem(word: String = "", draw: List<Point>, round: Int) {
        executor.submit { databaseDao.insertRoundItem(RoundItem(word, draw, round)) }
    }
}

data class Point (val x: Float, val y: Float, val isLast: Boolean)