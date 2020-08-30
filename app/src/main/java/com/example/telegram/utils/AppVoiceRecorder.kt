package com.example.telegram.utils

import android.media.MediaRecorder
import java.io.File
import java.lang.Exception

class AppVoiceRecorder {
    private var mMediaRecorder = MediaRecorder()
    private lateinit var mFile: File
    lateinit var mMesageKey: String

    fun startRecord(mesageKey: String){
        try {
            mMesageKey = mesageKey
            createFileForRecord()
            prepareMediaRecorder()
            mMediaRecorder.start()
        }catch (e:Exception){
            showToast(e.message.toString())
        }
    }

    private fun prepareMediaRecorder() {
        mMediaRecorder.reset()
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
        mMediaRecorder.setOutputFile(mFile.absolutePath)
        mMediaRecorder.prepare()
    }

    private fun createFileForRecord() {
        mFile = File(APP_ACTIVITY.filesDir, mMesageKey)
        mFile.createNewFile()
    }

    fun stopRecord(onSuccess:(file: File, mesageKey: String) -> Unit){
        try {
            mMediaRecorder.stop()
            onSuccess(mFile, mMesageKey)
        }catch (e:Exception){
            showToast(e.message.toString())
            mFile.delete()
        }
    }

    fun releaseRecorder(){
        try {
            mMediaRecorder.release()
        }catch (e:Exception){
            showToast(e.message.toString())
        }
    }
}