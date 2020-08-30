package com.example.telegram.ui.fragments.single_chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.telegram.R
import com.example.telegram.database.*
import com.example.telegram.models.CommonModel
import com.example.telegram.models.UserModel
import com.example.telegram.ui.fragments.BaseFragment
import com.example.telegram.utils.*
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.toolbar_info.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {
    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mReceivingUser: UserModel
    private lateinit var mToolbarInfo: View
    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefMesages: DatabaseReference
    private lateinit var mAdapter: SingleChatAdapter
    private lateinit var mRecycleView: RecyclerView
    private lateinit var mMesagesListener: AppChildEventListener
    private var mCountMesages = 10
    private var mIsScrolling = false
    private var mSmoothScrollToPosition = true
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAppVoiceRecorder: AppVoiceRecorder

    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initRecyclerView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFields() {
        mSwipeRefreshLayout = chat_swipe_refresh
        mLayoutManager = LinearLayoutManager(this.context)
        mAppVoiceRecorder = AppVoiceRecorder()

        // Включение выключение кнопки отправить, скрепка, звук в зависимости от содержимого в EditText
        chat_input_message.addTextChangedListener(AppTextWatcher {
            val string = chat_input_message.text.toString()
            if (string.isEmpty() || string == "запись") {
                chat_btn_attach.visibility = View.VISIBLE
                chat_btn_voice.visibility = View.VISIBLE
                chat_btn_send_message.visibility = View.GONE
            } else {
                chat_btn_send_message.visibility = View.VISIBLE
                chat_btn_attach.visibility = View.GONE
                chat_btn_voice.visibility = View.GONE
            }
        })

        // Нажатие на кнопку скрепка
        chat_btn_attach.setOnClickListener { attachFile() }

        // Запись звука в чате
        CoroutineScope(Dispatchers.IO).launch {
            chat_btn_voice.setOnTouchListener { view, motionEvent ->
                if (checkPermission(RECORD_AUDIO)){
                    if (motionEvent.action == MotionEvent.ACTION_DOWN){ // Кнопка запись звука нажата
                        // record
                        chat_input_message.setText("запись")
                        chat_btn_voice.setColorFilter(ContextCompat.getColor(APP_ACTIVITY, R.color.primary))
                        val mesageKey = getMesageKey(contact.id)
                        mAppVoiceRecorder.startRecord(mesageKey)
                    } else if (motionEvent.action == MotionEvent.ACTION_UP){ // Кнопка запись звука отжат
                        // stop record
                        chat_input_message.setText("")
                        chat_btn_voice.colorFilter = null
                        mAppVoiceRecorder.stopRecord{ file, mesageKey ->
                            uploadFileToStorage(Uri.fromFile(file), mesageKey, contact.id, TYPE_MESAGE_VOICE)
                            mSmoothScrollToPosition = true
                        }
                    }
                }
                true
            }
        }
    }

    // Функции после нажатия кнопки скрепка
    private fun attachFile() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(400, 400)
            .start(APP_ACTIVITY, this)
    }

    private fun initRecyclerView() {
        mRecycleView = chat_recycle_view
        mAdapter = SingleChatAdapter()
        mRecycleView.adapter = mAdapter
        mRecycleView.layoutManager = mLayoutManager
        mRefMesages = REF_DATABASE_ROOT.child(NODE_MESAGES).child(CURRENT_UID).child(contact.id)
        mRecycleView.setHasFixedSize(true)
        mRecycleView.isNestedScrollingEnabled = false

        mMesagesListener = AppChildEventListener {

            val mesage = it.getCommonModel()

            // Добавление элементов в зависимости от состояния положения списка
            if (mSmoothScrollToPosition) { // Проверка на необходимость опуститься вниз списка
                mAdapter.addItemToBotom(mesage) {
                    mRecycleView.smoothScrollToPosition(mAdapter.itemCount) // Опускается в нижний элемент списка
                }
            } else {
                mAdapter.addItemToTop(mesage) {
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }
        }

        mRefMesages.limitToLast(mCountMesages).addChildEventListener(mMesagesListener)

        mRecycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mIsScrolling && dy < 0 && mLayoutManager.findFirstCompletelyVisibleItemPosition() <= 3) {
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true
                }
            }
        })

        mSwipeRefreshLayout.setOnRefreshListener { updateData() } // Обновление данных при тяге резинки
    }

    private fun updateData() {
        mIsScrolling = false
        mSmoothScrollToPosition = false
        mCountMesages += 10
        mRefMesages.removeEventListener(mMesagesListener)
        mRefMesages.limitToLast(mCountMesages).addChildEventListener(mMesagesListener)
    }

    private fun initToolbar() {
        mToolbarInfo = APP_ACTIVITY.mToolbar.toolbar_info
        mToolbarInfo.visibility = View.VISIBLE
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUser()
            initToolbarInfo()
        }
        mRefUser = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
        mRefUser.addValueEventListener(mListenerInfoToolbar)
        chat_btn_send_message.setOnClickListener {
            val message = chat_input_message.text.toString()
            mSmoothScrollToPosition = true
            if (message.isEmpty()) {
                showToast("Нет сообщения")
            } else sendMessage(
                message,
                contact.id,
                TYPE_TEXT
            ) {
                chat_input_message.setText("")
            }
        }
    }

    private fun initToolbarInfo() {
        if (mReceivingUser.fullname.isEmpty()) {
            mToolbarInfo.toolbar_chat_fullname.text = contact.fullname
        } else mToolbarInfo.toolbar_chat_fullname.text = mReceivingUser.fullname
        mToolbarInfo.toolbar_chat_image.downloadAndSetImage(mReceivingUser.photoUrl)
        mToolbarInfo.toolbar_chat_status.text = mReceivingUser.state
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val uri = CropImage.getActivityResult(data).uri
            val mesageKey = getMesageKey(contact.id)
            uploadFileToStorage(uri, mesageKey, contact.id, TYPE_MESAGE_IMAGE)
            mSmoothScrollToPosition = true
        }
    }

    override fun onPause() {
        super.onPause()
        mToolbarInfo.visibility = View.GONE
        mRefUser.removeEventListener(mListenerInfoToolbar)
        mRefMesages.removeEventListener(mMesagesListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAppVoiceRecorder.releaseRecorder()
    }
}
