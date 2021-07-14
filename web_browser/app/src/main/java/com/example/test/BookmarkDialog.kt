package com.example.test

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.dialog_bookmark.*

class BookmarkDialog(context : Context) {
    private val context = context   //부모 액티비티의 context 가 들어감
    private val dlg = Dialog(context)   //부모 액티비티의 context 가 들어감
    private val sharedPref = context?.getSharedPreferences("bookmark", Context.MODE_PRIVATE)


    //    Adapter class
    private class MyCustomAdapter(context: Context, data: ArrayList<String>) : BaseAdapter() {
        private val mContext: Context
        private val mData: ArrayList<String>

        init {
            mContext = context
            mData = data
        }

        override fun getCount(): Int {
            return mData.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            val selectItem = mData.get(position)
            return selectItem
        }

        override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.row_bookmark, viewGroup, false)

            val nameTextView = rowMain.findViewById<TextView>(R.id.bookmarkName)
            nameTextView.text = mData.get(position)

            return rowMain
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun start() {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dlg.setContentView(R.layout.dialog_bookmark)     //다이얼로그에 사용할 xml 파일을 불러옴
        dlg.setCancelable(false)    //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함

        val cancelBtn = dlg.findViewById<Button>(R.id.cancelBtn)

        cancelBtn.setOnClickListener { v->
            dlg.dismiss()
        }

        // 어답터 설정
        dlg.bookmarkListView.adapter = MyCustomAdapter(context,getBookmarks())

        // Item click listener
        dlg.bookmarkListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectItem = parent.getItemAtPosition(position) as String
            Log.e("listview",selectItem.toString())
        }

        dlg.show()


//        with (sharedPref.edit()) {
//            putString("bookmark", "")
//            commit()
//        }
//
//        Log.e("bookmark",getBookmarks().toString()+" "+getBookmarks().size.toString())
//        addBookmark("naver.com")
//        addBookmark("nate.com")
//        Log.e("bookmark",getBookmarks().toString()+" "+getBookmarks().size.toString())
//        removeBookmark("naver.com")
//        Log.e("bookmark",getBookmarks().toString()+" "+getBookmarks().size.toString())



    }



    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public fun getBookmarks(): ArrayList<String> {
        val bookmark = sharedPref.getString("bookmark", "")
        var res = ArrayList<String>()
        val arr = bookmark?.split(',')
        for(i in arr!!){
            if(i != ""){
                res.add(i.toString())
            }
        }
        return res
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun addBookmark(newItem:String){
        var al = getBookmarks()
        al.add(newItem)

        var res = ""
        for(i in al){
            res += i+","
        }

        with (sharedPref.edit()) {
            putString("bookmark", res.substring(0,res.length-1))
            commit()
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun removeBookmark(item:String){
        var al = getBookmarks()
        al.remove(item)

        var res = ""
        for(i in al){
            res += i+","
        }

        with (sharedPref.edit()) {
            putString("bookmark", res.substring(0,res.length-1))
            commit()
        }
    }
}