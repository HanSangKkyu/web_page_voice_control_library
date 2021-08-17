package com.example.test

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_command.*
import java.util.*
import kotlin.collections.ArrayList

class CommandActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_command)

        var intent = getIntent()
        Log.e("asdf",intent.getStringExtra("url").toString())
        var funStr = intent.getStringExtra("fun").toString()
        var funList = ArrayList<String>()
        for (i in funStr.split(',')){
            if(i !in DefaultFunVO().getDefaultFun()){
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
                Log.e("asdf",funSpinner.getItemAtPosition(position).toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

    }
}