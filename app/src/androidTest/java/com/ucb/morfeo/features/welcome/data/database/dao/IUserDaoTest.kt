package com.ucb.morfeo.features.welcome.data.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ucb.morfeo.features.welcome.data.database.AppRoomDatabase
import com.ucb.morfeo.features.welcome.data.database.entity.UserEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class IUserDaoTest {

    private lateinit var userDao: IUserDao
    private lateinit var db: AppRoomDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Usamos una base de datos en memoria para que se destruya después de los tests
        db = Room.inMemoryDatabaseBuilder(
            context, AppRoomDatabase::class.java
        ).build()
        userDao = db.userDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetUser() = runBlocking {
        val user = UserEntity(
            email = "test@example.com",
            password = "password123",
            burnedDate = "2023-01-01",
            weight = 70.0,
            height = 1.75
        )
        
        userDao.insert(user)
        
        val allUsers = userDao.getAll()
        assertEquals(1, allUsers.size)
        assertEquals("test@example.com", allUsers[0].email)
    }

    @Test
    fun deleteAllUsers() = runBlocking {
        val user1 = UserEntity(email = "u1@test.com", password = "p", burnedDate = "d", weight = 60.0, height = 1.6)
        val user2 = UserEntity(email = "u2@test.com", password = "p", burnedDate = "d", weight = 60.0, height = 1.6)
        
        userDao.insert(user1)
        userDao.insert(user2)
        
        userDao.deleteAll()
        
        val allUsers = userDao.getAll()
        assertTrue(allUsers.isEmpty())
    }
}