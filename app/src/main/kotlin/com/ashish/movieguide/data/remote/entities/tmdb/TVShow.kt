package com.ashish.movieguide.data.remote.entities.tmdb

import android.os.Parcelable
import com.ashish.movieguide.ui.base.adapter.RecyclerViewItem
import com.ashish.movieguide.ui.base.adapter.RecyclerViewItem.Companion.CONTENT_VIEW
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class TVShow(
        val id: Long? = null,
        val name: String? = null,
        val overview: String? = null,
        val rating: Double? = null,
        @Json(name = "vote_count") val voteCount: Int? = null,
        @Json(name = "poster_path") val posterPath: String? = null,
        @Json(name = "vote_average") val voteAverage: Double? = null,
        @Json(name = "backdrop_path") val backdropPath: String? = null,
        @Json(name = "first_air_date") val firstAirDate: String? = null
) : RecyclerViewItem, Parcelable {

    override fun getViewType() = CONTENT_VIEW
}