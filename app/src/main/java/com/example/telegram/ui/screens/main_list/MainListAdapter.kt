package com.example.telegram.ui.screens.main_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.models.CommonModel
import com.example.telegram.ui.screens.single_chat.SingleChatFragment
import com.example.telegram.utils.downloadAndSetImage
import com.example.telegram.utils.replaceFragment
import kotlinx.android.synthetic.main.main_list_item.view.*

class MainListAdapter : RecyclerView.Adapter<MainListAdapter.MainListHolder>() {

    private var listItem = mutableListOf<CommonModel>()

    class MainListHolder(view: View) : RecyclerView.ViewHolder(view){
        val itemPhoto: ImageView = view.main_list_item_photo
        val itemName: TextView = view.main_list_item_fullname
        val itemLastMesage: TextView = view.main_list_last_mesage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_list_item, parent, false)
        val holder = MainListHolder(view)
        holder.itemView.setOnClickListener{
            replaceFragment(SingleChatFragment(listItem[holder.adapterPosition]))
        }
        return holder
    }

    override fun getItemCount(): Int = listItem.size

    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        holder.itemName.text = listItem[position].fullname
        holder.itemLastMesage.text = listItem[position].lastMesage
        holder.itemPhoto.downloadAndSetImage(listItem[position].photoUrl)
    }

    fun updateListItem(item: CommonModel){
        listItem.add(item)
        notifyItemInserted(listItem.size)
    }
}