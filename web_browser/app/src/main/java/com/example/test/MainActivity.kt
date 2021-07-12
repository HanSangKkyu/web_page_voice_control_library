package com.example.test

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_blank.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private var speechRecognizer: SpeechRecognizer? = null
    private var frList: HashMap<String,BlankFragment> = HashMap()
    private var selectedBtnTag: String = ""

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestMic()
        initWidget()

    }

    private fun requestMic(){
        // 마이크를 허용해달라고 요청한다.
        val REQUEST_CODE = 1

        if (Build.VERSION.SDK_INT >= 23)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO), REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun initWidget(){
        // 마이크
        micBtn.setOnClickListener(View.OnClickListener() {
            startSTT()
        })

        // 주소입력창
        urlEditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                menuBtn.setVisibility(View.GONE)
                micBtn.setVisibility(View.GONE)
            }else{
                menuBtn.setVisibility(View.VISIBLE)
                micBtn.setVisibility(View.VISIBLE)
            }
        }

        urlEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                v.clearFocus()
                frList.get(selectedBtnTag)!!.changeUrl(urlEditText.text.toString())
                return@OnKeyListener true
            }
            false
        })


        // 새로고침 버튼
        refreshBtn.setOnClickListener{ v ->
            // 앱에서 자바스크립트 코드 실행시키기
//            webview.loadUrl("javascript:location.reload()");
//
//            webview.evaluateJavascript("(function(){return('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();"){
//                Log.e("it",it)
//            }

//            webview.evaluateJavascript("(function(){return('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();"){
//                Log.e("it",it)
//            }

        }

        // 새탭 추가
        tabBtn.setOnClickListener{ v ->
            val newTabBtn = Button(this)

            val randomString = makeRanStr()

            newTabBtn.setLayoutParams(TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            newTabBtn.tag = randomString
            newTabBtn.text = randomString
            selectedBtnTag = randomString

            frList.put(newTabBtn.tag.toString(),BlankFragment())
            supportFragmentManager.beginTransaction().add(R.id.frame, frList.get(newTabBtn.tag.toString())!!).commit()


            newTabBtn.setOnClickListener{v ->
                supportFragmentManager.beginTransaction().hide(frList.get(selectedBtnTag)!!).commit()
                supportFragmentManager.beginTransaction().show(frList.get(newTabBtn.tag.toString())!!).commit()
                selectedBtnTag = newTabBtn.tag.toString()


            }

            // 탭 닫기
            newTabBtn.setOnLongClickListener { v ->
                supportFragmentManager.beginTransaction().remove(frList.get(selectedBtnTag)!!).commit()
                newTabBtn.visibility = View.GONE
                return@setOnLongClickListener true
            }

            tabList.addView(newTabBtn)
        }

        // 북마크 버튼
        bookmarkBtn.setOnClickListener { v->
            loadBookmarkDialog()
        }
    }

    private fun loadBookmarkDialog() {
        BookmarkDialog(this).start()
    }

    private fun makeRanStr(): String {
        val charPool = arrayOf('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','z')

        val randomString = (1..10)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");

        return randomString
    }

    // RecognitionListener 사용한 예제
    private fun startSTT() {
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(recognitionListener())
            startListening(speechRecognizerIntent)
        }

    }

    private fun recognitionListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) = Toast.makeText(this@MainActivity, "음성인식 시작", Toast.LENGTH_SHORT).show()

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}

        override fun onBeginningOfSpeech() {}

        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            when(error) {
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> Toast.makeText(this@MainActivity, "퍼미션 없음", Toast.LENGTH_SHORT).show()
            }
        }

        @RequiresApi(Build.VERSION_CODES.KITKAT)
        override fun onResults(results: Bundle) {
            var speechText = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0]
            Log.e("음성 인식 결과:", speechText)
            Toast.makeText(this@MainActivity, "음성 인식 결과: "+speechText, Toast.LENGTH_SHORT).show()

            matchCommand(speechText)

            startSTT()
        }
    }

    override fun onDestroy() {

        if (speechRecognizer != null) {
            speechRecognizer!!.stopListening()
        }

        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun matchCommand(speechText:String){
        var scollDown = arrayOf("내려", "아래로")
        var scollUp = arrayOf("올려", "위로")
        var zoomIn = arrayOf("크게", "확대")
        var zoomOut = arrayOf("작게", "축소")


        if(speechText in scollDown){
            webview.evaluateJavascript("scrollTo(document.documentElement.scrollTop, document.documentElement.scrollTop+50);"){}
        }else if(speechText in scollUp){
            webview.evaluateJavascript("scrollTo(document.documentElement.scrollTop, document.documentElement.scrollTop-50);"){}
        }else if(speechText in zoomIn){
            webview.evaluateJavascript("    if(document.body.style.zoom==\"\"){\n" +
                    "        document.body.style.zoom = 110+\"%\";\n" +
                    "    }else{\n" +
                    "        var zoom = document.body.style.zoom.toString().substring(0, document.body.style.zoom.toString().indexOf(\"%\"));\n" +
                    "        console.log(zoom);\n" +
                    "        document.body.style.zoom = (parseInt(zoom)+10)+\"%\";\n" +
                    "    }"){}
        }else if(speechText in zoomOut){
            webview.evaluateJavascript("    if(document.body.style.zoom==\"\"){\n" +
                    "        document.body.style.zoom = 90+\"%\";\n" +
                    "    }else{\n" +
                    "        var zoom = document.body.style.zoom.toString().substring(0, document.body.style.zoom.toString().indexOf(\"%\"));\n" +
                    "        console.log(zoom);\n" +
                    "        document.body.style.zoom = (parseInt(zoom)-10)+\"%\";\n" +
                    "    }"){}
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun getHTML(){
        webview.evaluateJavascript("Object.getOwnPropertyNames(window).filter(item => typeof window[item] === 'function');"){
//                Log.e("it",it)
            val functionList = it.toString().split(",")
            val resFunctionList = ArrayList<String>()
            for(i in functionList.reversed()){
                if(i == "\"FragmentDirective\""){
                    break
                }
                resFunctionList.add(i.trim('\"'))
//                    Log.i("jerrat",i)
            }

            for(i in resFunctionList){
                Log.i("resFunctionList",i)
            }
        }
    }
}
