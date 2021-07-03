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
package com.aisc.plap.ui.fragments.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.rxkprefs.Pref
import com.google.android.material.snackbar.Snackbar
import com.aisc.plap.PREF_SONG_SORT_ORDER
import com.aisc.plap.R
import com.aisc.plap.constants.SongSortOrder
import com.aisc.plap.constants.SongSortOrder.SONG_A_Z
import com.aisc.plap.constants.SongSortOrder.SONG_DURATION
import com.aisc.plap.constants.SongSortOrder.SONG_YEAR
import com.aisc.plap.constants.SongSortOrder.SONG_Z_A
import com.aisc.plap.databinding.LayoutRecyclerviewBinding
import com.aisc.plap.extensions.*
import com.aisc.plap.models.Song
import com.aisc.plap.ui.adapters.SongsAdapter
import com.aisc.plap.ui.fragments.base.MediaItemFragment
import com.aisc.plap.ui.listeners.SortMenuListener
import com.aisc.plap.util.AutoClearedValue
import org.koin.android.ext.android.inject

class SongsFragment : MediaItemFragment() {
    private lateinit var songsAdapter: SongsAdapter
    private val sortOrderPref by inject<Pref<SongSortOrder>>(name = PREF_SONG_SORT_ORDER)

    var binding by AutoClearedValue<LayoutRecyclerviewBinding>(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.layout_recyclerview, container)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        songsAdapter = SongsAdapter(this).apply {
            showHeader = true
            popupMenuListener = mainViewModel.popupMenuListener
            sortMenuListener = sortListener
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(safeActivity)
            adapter = songsAdapter
            addOnItemClick { position: Int, _: View ->
                songsAdapter.getSongForPosition(position)?.let { song ->
                    val extras = getExtraBundle(songsAdapter.songs.toSongIds(), getString(R.string.all_songs))
                    mainViewModel.mediaItemClicked(song, extras)
                }
            }
        }

        mediaItemFragmentViewModel.mediaItems
                .filter { it.isNotEmpty() }
                .observe(this) { list ->
                    @Suppress("UNCHECKED_CAST")
                    songsAdapter.updateData(list as List<Song>)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Auto trigger a reload when the sort order pref changes
        sortOrderPref.observe()
                .ioToMain()
                .subscribe { mediaItemFragmentViewModel.reloadMediaItems() }
                .disposeOnDetach(view)
    }

    private val sortListener = object : SortMenuListener {
        override fun shuffleAll() {
            songsAdapter.songs.shuffled().apply {
                val extras = getExtraBundle(toSongIds(), getString(R.string.all_songs))

                if (this.isEmpty()) {
                    Snackbar.make(binding.recyclerView, R.string.shuffle_no_songs_error, Snackbar.LENGTH_SHORT)
                            .show()
                } else {
                    mainViewModel.mediaItemClicked(this[0], extras)
                }
            }
        }

        override fun sortAZ() = sortOrderPref.set(SONG_A_Z)

        override fun sortDuration() = sortOrderPref.set(SONG_DURATION)

        override fun sortYear() = sortOrderPref.set(SONG_YEAR)

        override fun numOfSongs() {}

        override fun sortZA() = sortOrderPref.set(SONG_Z_A)
    }
}
