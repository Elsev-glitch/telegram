package com.example.telegram.ui.mesage_recycle_view.view_holders

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.database.getFileFromStorage
import com.example.telegram.ui.mesage_recycle_view.views.MesageView
import com.example.telegram.utils.WRITE_FILE
import com.example.telegram.utils.asTime
import com.example.telegram.utils.checkPermission
import com.example.telegram.utils.showToast
import kotlinx.android.synthetic.main.message_item_file.view.*
import java.io.File
import java.lang.Exception

class HolderFileMesage(view: View) : RecyclerView.ViewHolder(view), MesageHolder {

    private val blocUserFileMesage: ConstraintLayout = view.bloc_user_file_message
    private val chatUserFileTime: TextView = view.chat_user_file_time
    private val chatUserFileName: TextView = view.chat_user_filename
    private val chatUserBtnImage: ImageView = view.chat_btn_user_file
    private val chatUserProgresBar: ProgressBar = view.chat_user_progress_bar

    private val blocReceiveFileMesage: ConstraintLayout = view.bloc_receive_file_message
    private val chatReceiveFileTime: TextView = view.chat_receive_file_time
    private val chatReceiveFileName: TextView = view.chat_receive_filename
    private val chatReceiveBtnImage: ImageView = view.chat_btn_receive_file
    private val chatReceiveProgresBar: ProgressBar = view.chat_receive_progress_bar


    override fun drawMesage(view: MesageView) {
        if (view.from == CURRENT_UID) {
            blocReceiveFileMesage.visibility = View.GONE
            blocUserFileMesage.visibility = View.VISIBLE
            chatUserFileTime.text = view.timeStamp.asTime()
            chatUserFileName.text = view.text
        } else {
            blocReceiveFileMesage.visibility = View.VISIBLE
            blocUserFileMesage.visibility = View.GONE
            chatReceiveFileTime.text = view.timeStamp.asTime()
            chatReceiveFileName.text = view.text
        }
    }

    override fun onAttach(view: MesageView) {
        if (view.from == CURRENT_UID) chatUserBtnImage.setOnClickListener { clickToBtnFile(view) }
        else chatReceiveBtnImage.setOnClickListener { clickToBtnFile(view) }
    }

    private fun clickToBtnFile(view: MesageView) {
        if (view.from == CURRENT_UID){
            chatUserBtnImage.visibility = View.INVISIBLE
            chatUserProgresBar.visibility = View.VISIBLE
        } else {
            chatReceiveBtnImage.visibility = View.INVISIBLE
            chatReceiveProgresBar.visibility = View.VISIBLE
        }
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            view.text
        )
        try {
            if (checkPermission(WRITE_FILE)){
                file.createNewFile()
                getFileFromStorage(file, view.fileUrl){
                    if (view.from == CURRENT_UID){
                        chatUserBtnImage.visibility = View.VISIBLE
                        chatUserProgresBar.visibility = View.INVISIBLE
                    } else {
                        chatReceiveBtnImage.visibility = View.VISIBLE
                        chatReceiveProgresBar.visibility = View.INVISIBLE
                    }
                }
            }
        } catch (e:Exception){
            showToast(e.message.toString())
        }
    }

    override fun onDetach() {
        chatUserBtnImage.setOnClickListener(null)
        chatReceiveBtnImage.setOnClickListener(null)
    }
}