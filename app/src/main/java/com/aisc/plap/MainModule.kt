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
package com.aisc.plap

import android.app.Application
import android.content.ComponentName
import android.content.ContentResolver
import com.aisc.plap.playback.MediaSessionConnection
import com.aisc.plap.playback.PlapMusicService
import com.aisc.plap.playback.RealMediaSessionConnection
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.dsl.module.module

const val MAIN = "main"

val mainModule = module {

    factory<ContentResolver> {
        get<Application>().contentResolver
    }

    single {
        val component = ComponentName(get(), PlapMusicService::class.java)
        RealMediaSessionConnection(get(), component)
    } bind MediaSessionConnection::class

    factory(name = MAIN) {
        AndroidSchedulers.mainThread()
    }
}
