package com.ashish.movies.data.models

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import paperparcel.PaperParcel

@PaperParcel
data class TVShowSeason(
        val id: Long? = null,
        @Json(name = "air_date") val airDate: String? = null,
        @Json(name = "poster_path") val posterPath: String? = null,
        @Json(name = "episode_count") val episodeCount: Int? = null,
        @Json(name = "season_number") val seasonNumber: Int? = null
) : Parcelable {

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = PaperParcelTVShowSeason.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelTVShowSeason.writeToParcel(this, dest, flags)
    }
}