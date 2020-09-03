package com.example.telegram.ui.mesage_recycle_view.views

class ViewTextMesage(
    override val id: String,
    override val from: String,
    override val timeStamp: String,
    override val fileUrl: String = "",
    override val text: String
) : MesageView{
    override fun getTypeView(): Int {
        return MesageView.MESAGE_TEXT
    }
    override fun equals(other: Any?): Boolean {
        return (other as MesageView).id == id
    }
}