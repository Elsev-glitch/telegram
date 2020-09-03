package com.example.telegram.ui.screens.single_chat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.ui.mesage_recycle_view.view_holders.*
import com.example.telegram.ui.mesage_recycle_view.views.MesageView

class SingleChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mListMesagesCache = mutableListOf<MesageView>()
    private var mListHolders = mutableListOf<MesageHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return mListMesagesCache[position].getTypeView()

    }

    override fun getItemCount(): Int = mListMesagesCache.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MesageHolder).drawMesage(mListMesagesCache[position])
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as MesageHolder).onAttach(mListMesagesCache[holder.adapterPosition])
        mListHolders.add((holder as MesageHolder))
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as MesageHolder).onDetach()
        mListHolders.remove((holder as MesageHolder))
        super.onViewDetachedFromWindow(holder)
    }

    fun addItemToBotom(item: MesageView, onSuccess: () -> Unit) {
        if (!mListMesagesCache.contains(item)) {
            mListMesagesCache.add(item)
            notifyItemInserted(mListMesagesCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item: MesageView, onSuccess: () -> Unit) {
        if (!mListMesagesCache.contains(item)) {
            mListMesagesCache.add(item)
            mListMesagesCache.sortBy { it.timeStamp }
            notifyItemInserted(0)
        }
        onSuccess()
    }

    fun destroy() {
        mListHolders.forEach {
            it.onDetach()
        }
    }
}
