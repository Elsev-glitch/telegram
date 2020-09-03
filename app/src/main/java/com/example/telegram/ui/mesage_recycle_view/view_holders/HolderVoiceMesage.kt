package com.example.telegram.ui.mesage_recycle_view.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.ui.mesage_recycle_view.views.MesageView
import com.example.telegram.utils.AppVoicePlayer
import com.example.telegram.utils.asTime
import kotlinx.android.synthetic.main.message_item_voice.view.*

class HolderVoiceMesage(view: View) : RecyclerView.ViewHolder(view), MesageHolder {
    private val mAppVoicePlayer = AppVoicePlayer()

    private val blocUserVoiceMesage: ConstraintLayout = view.bloc_user_voice_message
    private val chatUserVoiceTime: TextView = view.chat_user_voice_time
    private val chatUserBtnPlay: ImageView = view.chat_btn_user_play
    private val chatUserBtnStop: ImageView = view.chat_btn_user_stop

    private val blocReceiveVoiceMesage: ConstraintLayout = view.bloc_receive_voice_message
    private val chatReceiveVoiceTime: TextView = view.chat_receive_voice_time
    private val chatReceiveBtnPlay: ImageView = view.chat_btn_receive_play
    private val chatReceiveBtnStop: ImageView = view.chat_btn_receive_stop

    override fun drawMesage(view: MesageView) {
        if (view.from == CURRENT_UID) {
            blocReceiveVoiceMesage.visibility = View.GONE
            blocUserVoiceMesage.visibility = View.VISIBLE
            chatUserVoiceTime.text = view.timeStamp.asTime()
        } else {
            blocReceiveVoiceMesage.visibility = View.VISIBLE
            blocUserVoiceMesage.visibility = View.GONE
            chatReceiveVoiceTime.text = view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MesageView) {
        mAppVoicePlayer.init()
        if (view.from == CURRENT_UID){
            chatUserBtnPlay.setOnClickListener {
                chatUserBtnPlay.visibility = View.GONE
                chatUserBtnStop.visibility = View.VISIBLE
                chatUserBtnStop.setOnClickListener {
                    stop{
                        chatUserBtnStop.setOnClickListener(null)
                        chatUserBtnPlay.visibility = View.VISIBLE
                        chatUserBtnStop.visibility = View.GONE
                    }
                }
                play(view){
                    chatUserBtnPlay.visibility = View.VISIBLE
                    chatUserBtnStop.visibility = View.GONE
                }
            }
        } else {
            chatReceiveBtnPlay.setOnClickListener {
                chatReceiveBtnPlay.visibility = View.GONE
                chatReceiveBtnStop.visibility = View.VISIBLE
                chatReceiveBtnStop.setOnClickListener {
                    stop {
                        chatReceiveBtnStop.setOnClickListener(null)
                        chatReceiveBtnPlay.visibility = View.VISIBLE
                        chatReceiveBtnStop.visibility = View.GONE
                    }
                }
                play(view){
                    chatReceiveBtnPlay.visibility = View.VISIBLE
                    chatReceiveBtnStop.visibility = View.GONE
                }
            }
        }
    }

    private fun stop( function: () -> Unit) {
        mAppVoicePlayer.stop { function() }
    }

    private fun play(view: MesageView, function: () -> Unit) {
        mAppVoicePlayer.play(view.id, view.fileUrl){
            function()
        }
    }

    override fun onDetach() {
        chatUserBtnPlay.setOnClickListener(null)
        chatReceiveBtnPlay.setOnClickListener(null)
        mAppVoicePlayer.release()
    }
}