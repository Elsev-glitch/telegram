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
import java.io.File
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

fun sendMesageAsFile(receiveUserId: String, fileUrl: String, mesageKey: String, typeMesage: String, fileName: String) {
    val refDialogUsers = "$NODE_MESAGES/$CURRENT_UID/$receiveUserId"
    val refDialogReceiveUser = "$NODE_MESAGES/$receiveUserId/$CURRENT_UID"

    val mapMesage = hashMapOf<String, Any>()
    mapMesage[CHILD_TYPE] = typeMesage
    mapMesage[CHILD_FROM] = CURRENT_UID
    mapMesage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMesage[CHILD_ID] = mesageKey
    mapMesage[CHILD_FILE_URL] = fileUrl
    mapMesage[CHILD_TEXT] = fileName

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUsers/$mesageKey"] = mapMesage
    mapDialog["$refDialogReceiveUser/$mesageKey"] = mapMesage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun getMesageKey(id: String) = REF_DATABASE_ROOT.child(NODE_MESAGES)
    .child(CURRENT_UID).child(id)
    .push().key.toString()

fun uploadFileToStorage(uri: Uri, mesageKey: String, receivedID: String, typeMesage: String, fileName: String=""){
    val path = REF_STORAGE_ROOT.child(FOLDER_FILES).child(mesageKey)
    putFileToStorage(uri, path) {
        getUrlFromStorage(path) {
            sendMesageAsFile(receivedID, it, mesageKey, typeMesage, fileName)
        }
    }
}

fun getFileFromStorage(mFile: File, fileUrl: String, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.getFile(mFile).addOnSuccessListener { function() }
        .addOnFailureListener{ showToast(it.message.toString())}
}

fun saveToMainList(id: String, type: String) {
    val refUser = "$NODE_MAIN_LIST/$CURRENT_UID/$id"
    val refReceive = "$NODE_MAIN_LIST/$id/$CURRENT_UID"

    val mapUser = hashMapOf<String,Any>()
    val mapReceive = hashMapOf<String,Any>()

    mapUser[CHILD_ID] = id
    mapUser[CHILD_TYPE] = type
    mapReceive[CHILD_ID] = CURRENT_UID
    mapReceive[CHILD_TYPE] = type

    val commonMap = hashMapOf<String,Any>()
    commonMap[refUser] = mapUser
    commonMap[refReceive] = mapReceive

    REF_DATABASE_ROOT.updateChildren(commonMap)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun deleteChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID).child(id)
        .removeValue()
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun clearChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_MESAGES).child(CURRENT_UID).child(id)
        .removeValue()
        .addOnSuccessListener {
            REF_DATABASE_ROOT.child(NODE_MESAGES).child(id).child(CURRENT_UID)
                .removeValue()
                .addOnSuccessListener { function() }
                .addOnFailureListener { showToast(it.message.toString()) }
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}
fun createGroupToDatabase(nameGroup: String, uri: Uri, listContacts: List<CommonModel>, function: () -> Unit) {
    val keyGroup = REF_DATABASE_ROOT.child(NODE_GROUPS).push().key.toString()
    val path = REF_DATABASE_ROOT.child(NODE_GROUPS).child(keyGroup)
    val mapData = hashMapOf<String, Any>()
    val pathStorage = REF_STORAGE_ROOT.child(FOLDER_GROUPS_IMAGE).child(keyGroup)

    mapData[CHILD_ID] = keyGroup
    mapData[CHILD_FULLNAME] = nameGroup

    val mapMembers = hashMapOf<String, Any>()
    listContacts.forEach {
        mapMembers[it.id] = USER_MEMBER
    }
    mapMembers[CURRENT_UID] = USER_CREATOR

    mapData[NODE_MEMBERS] = mapMembers
    path.updateChildren(mapData)
        .addOnSuccessListener {
            if (uri != Uri.EMPTY){
                putFileToStorage(uri, pathStorage) {
                    getUrlFromStorage(pathStorage) {
                        path.child(CHILD_FILE_URL).setValue(it)
                    }
                }
            }
            addGroupsToMainList(mapData, listContacts){
                function()
            }
        }
        .addOnFailureListener { showToast(it.message.toString())}
}

fun addGroupsToMainList(mapData: HashMap<String, Any>, listContacts: List<CommonModel>, function: () -> Unit) {
    val path = REF_DATABASE_ROOT.child(NODE_MAIN_LIST)
    val map = hashMapOf<String, Any>()

    map[CHILD_ID] = mapData[CHILD_ID].toString()
    map[CHILD_TYPE] = TYPE_GROUP
    listContacts.forEach {
        path.child(it.id).child(map[CHILD_ID].toString()).updateChildren(map)
    }
    path.child(CURRENT_UID).child(map[CHILD_ID].toString()).updateChildren(map)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}
