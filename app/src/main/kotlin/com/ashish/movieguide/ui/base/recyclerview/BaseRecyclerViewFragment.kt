package com.ashish.movieguide.ui.base.recyclerview

import android.content.Intent
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import com.ashish.movieguide.R
import com.ashish.movieguide.ui.animation.SlideInUpAnimator
import com.ashish.movieguide.ui.base.mvp.MvpFragment
import com.ashish.movieguide.ui.common.adapter.InfiniteScrollListener
import com.ashish.movieguide.ui.common.adapter.OnItemClickListener
import com.ashish.movieguide.ui.common.adapter.RecyclerViewAdapter
import com.ashish.movieguide.ui.common.adapter.ViewType
import com.ashish.movieguide.ui.common.adapter.ViewType.Companion.ERROR_VIEW
import com.ashish.movieguide.ui.widget.ItemOffsetDecoration
import com.ashish.movieguide.utils.extensions.getPosterImagePair
import com.ashish.movieguide.utils.extensions.startActivityWithTransition
import icepick.State
import kotlinx.android.synthetic.main.fragment_recycler_view.*
import kotlinx.android.synthetic.main.layout_empty_view.*

/**
 * Created by Ashish on Dec 30.
 */
abstract class BaseRecyclerViewFragment<I : ViewType, V : BaseRecyclerViewMvpView<I>,
        P : BaseRecyclerViewPresenter<I, V>> : MvpFragment<V, P>(), BaseRecyclerViewMvpView<I>,
        SwipeRefreshLayout.OnRefreshListener, OnItemClickListener {

    @JvmField
    @State
    var type: Int? = null

    protected lateinit var recyclerViewAdapter: RecyclerViewAdapter<I>

    private val scrollListener: InfiniteScrollListener = InfiniteScrollListener { currentPage ->
        if (currentPage > 1) recyclerView.post { presenter?.loadMoreData(type, currentPage) }
    }

    override fun getLayoutId() = R.layout.fragment_recycler_view

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        emptyText.setText(getEmptyTextId())
        emptyImage.setImageResource(getEmptyImageId())

        recyclerViewAdapter = RecyclerViewAdapter(R.layout.list_item_content, getAdapterType(), this)

        recyclerView.run {
            setHasFixedSize(true)
            emptyView = emptyContentView
            itemAnimator = SlideInUpAnimator()
            addItemDecoration(ItemOffsetDecoration())
            val columnCount = resources.getInteger(R.integer.content_column_count)
            layoutManager = StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL)
            addOnScrollListener(scrollListener)
            adapter = recyclerViewAdapter
        }

        swipeRefresh.run {
            setSwipeableViews(emptyContentView, recyclerView)
            setOnRefreshListener(this@BaseRecyclerViewFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    protected open fun loadData() = presenter?.loadData(type)

    abstract fun getAdapterType(): Int

    @StringRes
    abstract fun getEmptyTextId(): Int

    @DrawableRes
    abstract fun getEmptyImageId(): Int

    override fun onRefresh() {
        scrollListener.resetPageCount()
        presenter?.loadFreshData(type, recyclerViewAdapter.itemCount == 0)
    }

    override fun showProgress() {
        swipeRefresh.isRefreshing = true
    }

    override fun hideProgress() {
        swipeRefresh.isRefreshing = false
    }

    override fun setCurrentPage(currentPage: Int) {
        scrollListener.currentPage = currentPage
    }

    override fun showItemList(itemList: List<I>?) = recyclerViewAdapter.showItemList(itemList)

    override fun showLoadingItem() = recyclerViewAdapter.addLoadingItem()

    override fun addNewItemList(itemList: List<I>?) = recyclerViewAdapter.addNewItemList(itemList)

    override fun removeLoadingItem() {
        recyclerViewAdapter.removeLoadingItem()
    }

    override fun showErrorView() = recyclerViewAdapter.showErrorItem()

    override fun resetLoading() = scrollListener.stopLoading()

    override fun onItemClick(position: Int, view: View) {
        val viewType = recyclerViewAdapter.getViewType(position)
        if (viewType == ERROR_VIEW) {
            recyclerViewAdapter.removeErrorItem()
            scrollListener.shouldLoadMore = true
            presenter?.loadMoreData(type, scrollListener.currentPage)

        } else {
            val intent = getDetailIntent(position)
            if (intent != null) {
                val posterImagePair = view.getPosterImagePair(getTransitionNameId(position))
                activity?.startActivityWithTransition(posterImagePair, intent)
            }
        }
    }

    abstract fun getTransitionNameId(position: Int): Int

    abstract fun getDetailIntent(position: Int): Intent?

    override fun onDestroyView() {
        recyclerViewAdapter.removeListener()
        super.onDestroyView()
    }
}