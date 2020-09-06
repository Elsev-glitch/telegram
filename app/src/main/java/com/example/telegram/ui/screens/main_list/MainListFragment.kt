package com.example.telegram.ui.screens.main_list

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.database.*
import com.example.telegram.models.CommonModel
import com.example.telegram.utils.APP_ACTIVITY
import com.example.telegram.utils.AppValueEventListener
import com.example.telegram.utils.hideKeyboard
import kotlinx.android.synthetic.main.fragment_main_list.*

class MainListFragment : Fragment(R.layout.fragment_main_list) {
    private lateinit var mRecycleView: RecyclerView
    private lateinit var mAdapter: MainListAdapter
    private val mRefMainList = REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID)
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val mRefMesage = REF_DATABASE_ROOT.child(NODE_MESAGES).child(CURRENT_UID)
    private var mListItem = listOf<CommonModel>()

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "telegram"
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
        initRecycleView()
    }

    private fun initRecycleView() {
        mRecycleView = main_list_recycle_view
        mAdapter = MainListAdapter()
        // 1 запрос
        mRefMainList.addListenerForSingleValueEvent(AppValueEventListener { dataSnapShot ->
            mListItem = dataSnapShot.children.map { it.getCommonModel() }
            mListItem.forEach { model ->
                // 2 запрос
                mRefUsers.child(model.id)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapShot1 ->
                        val newModel = dataSnapShot1.getCommonModel()
                        // 3 запрос
                        mRefMesage.child(model.id).limitToLast(1)
                            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapShot2 ->
                                val tempList = dataSnapShot2.children.map { it.getCommonModel() }
                                if (tempList.isEmpty()){
                                    newModel.lastMesage = "Чат очищен"
                                } else {
                                    newModel.lastMesage = tempList[0].text
                                }
                                if (newModel.fullname.isEmpty()){
                                    newModel.fullname = newModel.phone
                                }
                                mAdapter.updateListItem(newModel)
                            })
                    })
            }
        })
        mRecycleView.adapter = mAdapter
    }
}