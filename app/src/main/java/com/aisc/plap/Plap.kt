/*
 * Copyright (c) 2019 Naman Dwivedi.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */
@file:Suppress("unused")

package com.aisc.plap

import android.app.Application
import com.aisc.plap.BuildConfig.DEBUG
import com.aisc.plap.db.roomModule
import com.aisc.plap.logging.FabricTree
import com.aisc.plap.network.lastFmModule
import com.aisc.plap.network.lyricsModule
import com.aisc.plap.network.networkModule
import com.aisc.plap.notifications.notificationModule
import com.aisc.plap.permissions.permissionsModule
import com.aisc.plap.playback.mediaModule
import com.aisc.plap.repository.repositoriesModule
import com.aisc.plap.ui.viewmodels.viewModelsModule
import org.koin.android.ext.android.startKoin
import timber.log.Timber

class Plap : Application() {

    override fun onCreate() {
        super.onCreate()

        if (DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(FabricTree())
        }

        val modules = listOf(
                mainModule,
                permissionsModule,
                mediaModule,
                prefsModule,
                networkModule,
                roomModule,
                notificationModule,
                repositoriesModule,
                viewModelsModule,
                lyricsModule,
                lastFmModule
        )
        startKoin(
                androidContext = this,
                modules = modules
        )
    }
}
