package com.ashish.movieguide.ui.people.list

import com.ashish.movieguide.data.interactors.PeopleInteractor
import com.ashish.movieguide.data.models.tmdb.Person
import com.ashish.movieguide.data.models.tmdb.Results
import com.ashish.movieguide.ui.base.recyclerview.BaseRecyclerViewMvpView
import com.ashish.movieguide.ui.base.recyclerview.BaseRecyclerViewPresenter
import com.ashish.movieguide.utils.schedulers.BaseSchedulerProvider
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Ashish on Dec 31.
 */
class PeoplePresenter @Inject constructor(
        private val peopleInteractor: PeopleInteractor,
        schedulerProvider: BaseSchedulerProvider
) : BaseRecyclerViewPresenter<Person, BaseRecyclerViewMvpView<Person>>(schedulerProvider) {

    override fun getResults(type: String?, page: Int): Single<Results<Person>>
            = peopleInteractor.getPopularPeople(page)
}