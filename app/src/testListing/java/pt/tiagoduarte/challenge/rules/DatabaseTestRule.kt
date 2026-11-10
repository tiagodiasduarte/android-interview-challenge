package pt.tiagoduarte.challenge.rules

import androidx.annotation.VisibleForTesting
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.reflect.KClass

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
class DatabaseTestRule<T : RoomDatabase>(private val databaseClass: KClass<T>) : TestWatcher() {

    lateinit var database: T
        private set

    override fun starting(description: Description) {
        super.starting(description)

        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), databaseClass.java)
            .allowMainThreadQueries()
            .build()
    }

    override fun finished(description: Description) {
        super.finished(description)

        database.close()
    }
}
