package com.apexbytestudio.entrix.core.database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import org.json.JSONObject
import com.apexbytestudio.entrix.core.database.EntrixDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        val context = androidContext()
        Room.databaseBuilder(
            context,
            EntrixDatabase::class.java,
            EntrixDatabase.NAME,
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                // Private installation presets are injected into the unsigned APK before signing.
                // The public source and CI artifact contain no personal location.
                val preset = runCatching {
                    context.assets.open("office-default.json").bufferedReader().use { JSONObject(it.readText()) }
                }.getOrNull() ?: return
                val name = preset.getString("name")
                val latitude = preset.getDouble("latitude")
                val longitude = preset.getDouble("longitude")
                val radius = preset.getDouble("radiusMeters")
                require(name.isNotBlank() && latitude in -90.0..90.0 && longitude in -180.0..180.0 && radius in 100.0..2000.0)
                db.execSQL(
                    "INSERT INTO office (id, name, latitude, longitude, radiusMeters) VALUES (?, ?, ?, ?, ?)",
                    arrayOf<Any>(1L, name, latitude, longitude, radius),
                )
            }
        }).build()
    }
    single { get<EntrixDatabase>().officeDao() }
    single { get<EntrixDatabase>().attendanceDao() }
}
