package com.example.telegram.database

import android.net.Uri
import com.example.telegram.R
import com.example.telegram.models.CommonModel
import com.example.telegram.models.UserModel
import com.example.telegram.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import java.util.ArrayList

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = UserModel()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
}

fun updatePhonesToDatabase(arrayContacts: ArrayList<CommonModel>) {
    if (AUTH.currentUser != null) {
        REF_DATABASE_ROOT.child(NODE_PHONES).addListenerForSingleValueEvent(
            AppValueEventListener {
                it.children.forEach { snapshot ->
                    arrayContacts.forEach { contact ->
                        if (snapshot.key == contact.phone) {
                            REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS)
                                .child(CURRENT_UID)
                                .child(snapshot.value.toString())
                                .child(CHILD_ID)
                                .setValue(snapshot.value.toString())
                                .addOnFailureListener { showToast(it.message.toString()) }
                            REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS)
                                .child(CURRENT_UID)
                                .child(snapshot.value.toString())
                                .child(CHILD_FULLNAME)
                                .setValue(contact.fullname)
                                .addOnFailureListener { showToast(it.message.toString()) }
                        }
                    }
                }
            })
    }
}

fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()

fun DataSnapshot.getUser(): UserModel = this.getValue(
    UserModel::class.java) ?: UserModel()
fun sendMessage(message: String, receiveUserId: String, typeText: String, function: () -> Unit) {
    val refDialogUsers = "$NODE_MESAGES/$CURRENT_UID/$receiveUserId"
    val refDialogReceiveUser = "$NODE_MESAGES/$receiveUserId/$CURRENT_UID"
    val mesageKey = REF_DATABASE_ROOT.child(refDialogUsers).push().key

    val mapMesage = hashMapOf<String, Any>()
    mapMesage[CHILD_TYPE] = typeText
    mapMesage[CHILD_FROM] = CURRENT_UID
    mapMesage[CHILD_TEXT] = message
    mapMesage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMesage[CHILD_ID] = mesageKey.toString()

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUsers/$mesageKey"] = mapMesage
    mapDialog["$refDialogReceiveUser/$mesageKey"] = mapMesage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun updateCurrentUsername(newUserName: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(
        CURRENT_UID
    ).child(CHILD_USERNAME)
        .setValue(newUserName)
        .addOnSuccessListener {
            deleteOldUsername(newUserName)
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

private fun deleteOldUsername(newUserName: String) {
    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(
        USER.username)
        .removeValue()
        .addOnSuccessListener {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
            USER.username = newUserName
            showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun setBioToDatabase(newBio: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(
        CURRENT_UID
    ).child(CHILD_BIO)
        .setValue(newBio).addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
            USER.bio = newBio
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun setNameToDatabase(fullname: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FULLNAME)
        .setValue(fullname).addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
            USER.fullname = fullname
            APP_ACTIVITY.mAppDrawer.updateHeader()
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun sendMesageAsFile(receiveUserId: String, fileUrl: String, mesageKey: String, typeMesage: String) {
    val refDialogUsers = "$NODE_MESAGES/$CURRENT_UID/$receiveUserId"
    val refDialogReceiveUser = "$NODE_MESAGES/$receiveUserId/$CURRENT_UID"

    val mapMesage = hashMapOf<String, Any>()
    mapMesage[CHILD_TYPE] = typeMesage
    mapMesage[CHILD_FROM] = CURRENT_UID
    mapMesage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMesage[CHILD_ID] = mesageKey
    mapMesage[CHILD_FILE_URL] = fileUrl

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUsers/$mesageKey"] = mapMesage
    mapDialog["$refDialogReceiveUser/$mesageKey"] = mapMesage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun getMesageKey(id: String) = REF_DATABASE_ROOT.child(NODE_MESAGES)
    .child(CURRENT_UID).child(id)
    .push().key.toString()

fun uploadFileToStorage(uri: Uri, mesageKey: String, receivedID: String, typeMesage: String){
    val path = REF_STORAGE_ROOT.child(FOLDER_FILES).child(mesageKey)
    putFileToStorage(uri, path) {
        getUrlFromStorage(path) {
            sendMesageAsFile(receivedID, it, mesageKey, typeMesage)
        }
    }
}