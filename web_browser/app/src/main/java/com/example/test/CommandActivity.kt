package com.example.test

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_command.*

class CommandActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_command)

        init()

    }

    fun init() {
        // get extra
        var intent = getIntent()
        var funStr = intent.getStringExtra("fun").toString()


        // get function list in this page
        var funList = ArrayList<String>()
        for (i in funStr.split(',')) {
            if (i !in DefaultFunVO().getDefaultFun()) {
                funList.add(i)
            }
        }


        // set spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, funList)
        funSpinner.adapter = adapter
        funSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.e("asdf", funSpinner.getItemAtPosition(position).toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }


        // set listView


        var commandList = ArrayList<Command>()
    )
        customCommandListView.adapter = MyCustomAdapter(this, commandList)
    }
}

//    Adapter class
private class MyCustomAdapter(context: Context, data: ArrayList<Command>) : BaseAdapter() {
    private val mContext: Context
    private val mData: ArrayList<Command>

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
        val rowMain = layoutInflater.inflate(R.layout.row_command, viewGroup, false)

        val lineTextView = rowMain.findViewById<TextView>(R.id.line)
        val commandTextView = rowMain.findViewById<TextView>(R.id.function)

        lineTextView.text = mData.get(position).line
        commandTextView.text = mData.get(position).function

        return rowMain
    }
}