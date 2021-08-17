package com.example.test

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class CommandActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_command)

        var intent = getIntent()
        Log.e("asdf",intent.getStringExtra("url").toString())



//        val script = "Object.getOwnPropertyNames(window).filter(item => typeof window[item] === 'function')"
//        webview.evaluateJavascript("(function(){return("+script+"); })();"){
//
//            // get this page functions
//            var res = it.substring(1,it.length-1)
//            var resList = res.split(',')
//            for (i in 0..resList.size-1){
//                val tmp = resList.get(i).replace("\"","")
//                if(tmp !in DefaultFunVO().getDefaultFun()){
//                    Log.e("asdf",i.toString()+" "+tmp)
//                }
//            }
//        }
    }
}