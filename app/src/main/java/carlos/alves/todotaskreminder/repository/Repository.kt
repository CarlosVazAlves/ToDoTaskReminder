package carlos.alves.todotaskreminder.repository

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

open class Repository {

    companion object {
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
    }
}