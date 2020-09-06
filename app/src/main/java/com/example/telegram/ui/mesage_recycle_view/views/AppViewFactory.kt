package com.example.telegram.ui.mesage_recycle_view.views

import com.example.telegram.models.CommonModel
import com.example.telegram.utils.TYPE_MESAGE_FILE
import com.example.telegram.utils.TYPE_MESAGE_IMAGE
import com.example.telegram.utils.TYPE_MESAGE_VOICE

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
                TYPE_MESAGE_VOICE -> ViewVoiceMesage(
                    mesage.id,
                    mesage.from,
                    mesage.timeStamp.toString(),
                    mesage.fileUrl
                )
                TYPE_MESAGE_FILE -> ViewFileMesage(
                    mesage.id,
                    mesage.from,
                    mesage.timeStamp.toString(),
                    mesage.fileUrl,
                    mesage.text
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