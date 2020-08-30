package com.example.telegram.ui.fragments.mesage_recycle_view.views

import com.example.telegram.models.CommonModel
import com.example.telegram.utils.TYPE_MESAGE_IMAGE

class AppViewFactory {
    companion object{
        fun getView(mesage: CommonModel): MesageView{
            return when(mesage.type){
                TYPE_MESAGE_IMAGE -> ViewImageMesage(
                    mesage.id,
                    mesage.from,
                    mesage.timeStamp.toString(),
                    mesage.fileUrl
                )
                else -> ViewTextMesage(
                    mesage.id,
                    mesage.from,
                    mesage.timeStamp.toString(),
                    mesage.fileUrl,
                    mesage.text
                )
            }
        }
    }
}