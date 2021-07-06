package com.example.test

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.Button

class BookmarkDialog(context : Context) {
    private val dlg = Dialog(context)   //부모 액티비티의 context 가 들어감

    fun start() {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dlg.setContentView(R.layout.dialog_bookmark)     //다이얼로그에 사용할 xml 파일을 불러옴
        dlg.setCancelable(false)    //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함

        val cancelBtn = dlg.findViewById<Button>(R.id.cancelBtn)

        cancelBtn.setOnClickListener { v->
            dlg.dismiss()
        }


        dlg.show()
    }
}