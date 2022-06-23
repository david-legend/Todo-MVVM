package com.codinginflow.mvvmtodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codinginflow.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class CallBack @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDao()

            val launch = applicationScope.launch {
                dao.insert(Task("Wash the dishes", completed = true))
                dao.insert(Task("Brush teeth and bath", completed = true))
                dao.insert(Task("Read AtMamun", important = true))
                dao.insert(Task("Prepare and eat breakfast"))
                dao.insert(Task("Call Mom"))
                dao.insert(Task("Send Invoice", important = true))
            }
        }
    }
}