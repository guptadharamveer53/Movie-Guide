package com.ashish.movies.utils.extensions

import com.ashish.movies.data.api.OMDbApi
import com.ashish.movies.data.models.FullDetailContent
import com.ashish.movies.data.models.OMDbDetail
import io.reactivex.Observable

/**
 * Created by Ashish on Jan 09.
 */
fun <I> OMDbApi.convertToFullDetailContent(imdbId: String?, detailContent: I): Observable<FullDetailContent<I>> {
    if (imdbId.isNotNullOrEmpty()) {
        return getDetailFromIMDbId(imdbId!!)
                .flatMap({ getFullDetailContent(detailContent, it) })
                .onErrorReturnItem(FullDetailContent(detailContent, null))
    } else {
        return getFullDetailContent(detailContent, null)
    }
}

private fun <I> getFullDetailContent(detailContent: I, omdbDetail: OMDbDetail?): Observable<FullDetailContent<I>> {
    return Observable.just(FullDetailContent(detailContent, omdbDetail))
}