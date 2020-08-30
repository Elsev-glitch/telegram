package com.example.telegram.ui.fragments.single_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.models.CommonModel
import com.example.telegram.utils.TYPE_MESAGE_IMAGE
import com.example.telegram.utils.TYPE_TEXT
import com.example.telegram.utils.asTime
import com.example.telegram.utils.downloadAndSetImage
import kotlinx.android.synthetic.main.message_item.view.*

class SingleChatAdapter : RecyclerView.Adapter<SingleChatAdapter.SingleChatHolder>() {
    private var mListMesagesCache = mutableListOf<CommonModel>()

    class SingleChatHolder(view: View) : RecyclerView.ViewHolder(view) {
        //Text
        val blocUserMesage: ConstraintLayout = view.bloc_user_message
        val chatUserMesage: TextView = view.chat_user_message
        val chatUserTime: TextView = view.chat_user_time

        val blocReceiveMesage: ConstraintLayout = view.bloc_receive_message
        val chatReceiveMesage: TextView = view.chat_receive_message
        val chatReceiveTime: TextView = view.chat_receive_time

        //Image
        val blocUserImageMesage: ConstraintLayout = view.bloc_user_image_message
        val chatUserImage: ImageView = view.chat_user_image
        val chatUserImageTime: TextView = view.chat_user_image_time

        val blocReceiveImageMesage: ConstraintLayout = view.bloc_receive_image_message
        val chatReceiveImage: ImageView = view.chat_receive_image
        val chatReceiveImageTime: TextView = view.chat_receive_image_time
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return SingleChatHolder(view)
    }

    override fun getItemCount(): Int = mListMesagesCache.size

    override fun onBindViewHolder(holder: SingleChatHolder, position: Int) {
        when (mListMesagesCache[position].type) {
            TYPE_TEXT -> drawMesageText(holder, position)
            TYPE_MESAGE_IMAGE -> drawMesageImage(holder, position)
        }
    }

    private fun drawMesageImage(holder: SingleChatHolder, position: Int) {
        holder.blocUserMesage.visibility = View.GONE
        holder.blocReceiveMesage.visibility = View.GONE
        if (mListMesagesCache[position].from == CURRENT_UID) {
            holder.blocReceiveImageMesage.visibility = View.GONE
            holder.blocUserImageMesage.visibility = View.VISIBLE
            holder.chatUserImage.downloadAndSetImage(mListMesagesCache[position].fileUrl)
            holder.chatUserImageTime.text = mListMesagesCache[position].timeStamp.toString().asTime()
        } else {
            holder.blocReceiveImageMesage.visibility = View.VISIBLE
            holder.blocUserImageMesage.visibility = View.GONE
            holder.chatReceiveImage.downloadAndSetImage(mListMesagesCache[position].fileUrl)
            holder.chatReceiveImageTime.text = mListMesagesCache[position].timeStamp.toString().asTime()
        }
    }

    private fun drawMesageText(holder: SingleChatHolder, position: Int) {
        holder.blocUserImageMesage.visibility = View.GONE
        holder.blocReceiveImageMesage.visibility = View.GONE
        if (mListMesagesCache[position].from == CURRENT_UID) {
            holder.blocUserMesage.visibility = View.VISIBLE
            holder.blocReceiveMesage.visibility = View.GONE
            holder.chatUserMesage.text = mListMesagesCache[position].text
            holder.chatUserTime.text = mListMesagesCache[position].timeStamp.toString().asTime()
        } else {
            holder.blocUserMesage.visibility = View.GONE
            holder.blocReceiveMesage.visibility = View.VISIBLE
            holder.chatReceiveMesage.text = mListMesagesCache[position].text
            holder.chatReceiveTime.text = mListMesagesCache[position].timeStamp.toString().asTime()
        }
    }

    fun addItemToBotom(item: CommonModel, onSuccess: () -> Unit) {
        if (!mListMesagesCache.contains(item)) {
            mListMesagesCache.add(item)
            notifyItemInserted(mListMesagesCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item: CommonModel, onSuccess: () -> Unit) {
        if (!mListMesagesCache.contains(item)) {
            mListMesagesCache.add(item)
            mListMesagesCache.sortBy { it.timeStamp.toString() }
            notifyItemInserted(0)
        }
        onSuccess()
    }
}
