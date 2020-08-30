package com.example.telegram.ui.fragments.mesage_recycle_view.view_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.ui.fragments.mesage_recycle_view.views.MesageView

class AppHolderFactory {
    companion object{
        fun getHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
            return when(viewType){
                MesageView.MESAGE_IMAGE -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item_image, parent, false)
                    HolderImageMesage(view)
                } else ->{
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item_text, parent, false)
                    HolderTextMesage(view)
                }
            }
        }
    }
}