package pt.tiagoduarte.challenge.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import pt.tiagoduarte.challenge.data.local.db.AppDatabase.Companion.DATABASE_VERSION

@Database(entities = [ProductEntity::class], version = DATABASE_VERSION, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {
        const val DATABASE_NAME = "android-interview-challenge.db"
        const val DATABASE_VERSION = 1
    }

}
