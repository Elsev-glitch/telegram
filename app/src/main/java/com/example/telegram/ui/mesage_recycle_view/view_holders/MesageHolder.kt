package com.example.telegram.ui.mesage_recycle_view.view_holders

import com.example.telegram.ui.mesage_recycle_view.views.MesageView

interface MesageHolder {
    fun drawMesage(view: MesageView)
    fun onAttach(view: MesageView)
    fun onDetach()
}