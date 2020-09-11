package com.example.telegram.ui.screens.groups

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
import com.example.telegram.utils.showToast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.add_contacts_item.view.*
import kotlinx.android.synthetic.main.main_list_item.view.*

class AddContactsAdapter : RecyclerView.Adapter<AddContactsAdapter.AddContactsHolder>() {

    private var listItem = mutableListOf<CommonModel>()

    class AddContactsHolder(view: View) : RecyclerView.ViewHolder(view){
        val itemPhoto: CircleImageView = view.add_contacts_item_photo
        val itemName: TextView = view.add_contacts_item_fullname
        val itemLastMesage: TextView = view.add_contacts_last_mesage
        val itemChoice: CircleImageView = view.add_contacts_item_choice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_contacts_item, parent, false)
        val holder = AddContactsHolder(view)
        holder.itemView.setOnClickListener{
            if (listItem[holder.adapterPosition].choice){
                holder.itemChoice.visibility = View.INVISIBLE
                listItem[holder.adapterPosition].choice = false
                AddContactsFragment.listContacts.remove(listItem[holder.adapterPosition])
            } else {
                holder.itemChoice.visibility = View.VISIBLE
                listItem[holder.adapterPosition].choice = true
                AddContactsFragment.listContacts.add(listItem[holder.adapterPosition])
            }
        }
        return holder
    }

    override fun getItemCount(): Int = listItem.size

    override fun onBindViewHolder(holder: AddContactsHolder, position: Int) {
        holder.itemName.text = listItem[position].fullname
        holder.itemLastMesage.text = listItem[position].lastMesage
        holder.itemPhoto.downloadAndSetImage(listItem[position].photoUrl)
    }

    fun updateListItem(item: CommonModel){
        listItem.add(item)
        notifyItemInserted(listItem.size)
    }
}