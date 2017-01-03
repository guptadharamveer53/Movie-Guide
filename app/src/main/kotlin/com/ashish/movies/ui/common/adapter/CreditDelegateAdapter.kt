package com.ashish.movies.ui.common.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.ashish.movies.R
import com.ashish.movies.data.models.Credit
import com.ashish.movies.ui.base.recyclerview.BaseContentHolder
import com.ashish.movies.utils.Constants.PROFILE_ORIGINAL_URL_PREFIX

/**
 * Created by Ashish on Jan 03.
 */
class CreditDelegateAdapter(val layoutId: Int = R.layout.list_item_content_alt,
                            var onItemClickListener: OnItemClickListener?)
    : ViewTypeDelegateAdapter, RemoveListener {

    override fun onCreateViewHolder(parent: ViewGroup) = CreditHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        (holder as CreditHolder).bindData(item as Credit)
    }

    override fun removeListener() {
        onItemClickListener = null
    }

    inner class CreditHolder(parent: ViewGroup) : BaseContentHolder<Credit>(parent, layoutId) {

        override fun bindData(item: Credit) = with(item) {
            contentTitle.text = name
            itemView.setOnClickListener { onItemClickListener?.onItemClick(adapterPosition, it) }
            super.bindData(item)
        }

        override fun getImageUrl(item: Credit) = PROFILE_ORIGINAL_URL_PREFIX + item.profilePath
    }
}