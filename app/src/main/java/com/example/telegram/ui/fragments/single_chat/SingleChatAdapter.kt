package com.example.telegram.ui.fragments.single_chat

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.ui.fragments.mesage_recycle_view.view_holders.AppHolderFactory
import com.example.telegram.ui.fragments.mesage_recycle_view.view_holders.HolderImageMesage
import com.example.telegram.ui.fragments.mesage_recycle_view.view_holders.HolderTextMesage
import com.example.telegram.ui.fragments.mesage_recycle_view.views.MesageView
import com.example.telegram.utils.asTime
import com.example.telegram.utils.downloadAndSetImage

class SingleChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mListMesagesCache = mutableListOf<MesageView>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return mListMesagesCache[position].getTypeView()

    }

    override fun getItemCount(): Int = mListMesagesCache.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       when(holder){
           is HolderImageMesage -> drawMesageImage(holder, position)
           is HolderTextMesage -> drawMesageText(holder, position)
           else -> {}
       }
    }

    private fun drawMesageImage(holder: HolderImageMesage, position: Int) {
        if (mListMesagesCache[position].from == CURRENT_UID) {
            holder.blocReceiveImageMesage.visibility = View.GONE
            holder.blocUserImageMesage.visibility = View.VISIBLE
            holder.chatUserImage.downloadAndSetImage(mListMesagesCache[position].fileUrl)
            holder.chatUserImageTime.text = mListMesagesCache[position].timeStamp.asTime()
        } else {
            holder.blocReceiveImageMesage.visibility = View.VISIBLE
            holder.blocUserImageMesage.visibility = View.GONE
            holder.chatReceiveImage.downloadAndSetImage(mListMesagesCache[position].fileUrl)
            holder.chatReceiveImageTime.text = mListMesagesCache[position].timeStamp.asTime()
        }
    }

    private fun drawMesageText(holder: HolderTextMesage, position: Int) {
        if (mListMesagesCache[position].from == CURRENT_UID) {
            holder.blocUserMesage.visibility = View.VISIBLE
            holder.blocReceiveMesage.visibility = View.GONE
            holder.chatUserMesage.text = mListMesagesCache[position].text
            holder.chatUserTime.text = mListMesagesCache[position].timeStamp.asTime()
        } else {
            holder.blocUserMesage.visibility = View.GONE
            holder.blocReceiveMesage.visibility = View.VISIBLE
            holder.chatReceiveMesage.text = mListMesagesCache[position].text
            holder.chatReceiveTime.text = mListMesagesCache[position].timeStamp.asTime()
        }
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
            mListMesagesCache.sortBy { it.timeStamp.toString() }
            notifyItemInserted(0)
        }
        onSuccess()
    }
}
