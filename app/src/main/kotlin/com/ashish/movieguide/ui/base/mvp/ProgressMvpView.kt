package com.ashish.movieguide.ui.base.mvp

/**
 * Base MVP interface to handle showing or hiding progress bar
 * depending upon whether data is loading or loaded.
 */
interface ProgressMvpView : MvpView {

    fun setLoadingIndicator(showIndicator: Boolean)
}