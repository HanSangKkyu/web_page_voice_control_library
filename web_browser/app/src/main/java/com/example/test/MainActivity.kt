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
    private var frList: HashMap<String, BlankFragment> = HashMap()
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
            showPreviousTab()
        }

        // 새탭 추가
        tabBtn.setOnClickListener{ v ->
            makeNewTab()
        }

        // 북마크 버튼
        bookmarkBtn.setOnClickListener { v->
            loadBookmarkDialog()
        }
    }

    private fun makeNewTab(){
        val newTabBtn = Button(this)

        val randomString = makeRanStr()

        newTabBtn.setLayoutParams(TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        newTabBtn.tag = randomString
        newTabBtn.text = randomString
        selectedBtnTag = randomString

        frList.put(newTabBtn.tag.toString(),BlankFragment())
        supportFragmentManager.beginTransaction().add(R.id.frame, frList.get(newTabBtn.tag.toString())!!).commit()

        // 탭 화면 띄우기
        newTabBtn.setOnClickListener{v ->
            supportFragmentManager.beginTransaction().hide(frList.get(selectedBtnTag)!!).commit()
            supportFragmentManager.beginTransaction().show(frList.get(newTabBtn.tag.toString())!!).commit()
            selectedBtnTag = newTabBtn.tag.toString()
        }

        // 탭 닫기
        newTabBtn.setOnLongClickListener { v ->
            supportFragmentManager.beginTransaction().remove(frList.get(selectedBtnTag)!!).commit()
            frList.remove(selectedBtnTag)
            newTabBtn.visibility = View.GONE
            return@setOnLongClickListener true
        }

        tabList.addView(newTabBtn)
    }

    private fun showNextTab(){
        var flag = false;
        for (item in frList.keys){
            Log.e("dd",item.toString())
            if(flag){
                Log.e("dd",item.toString())
                supportFragmentManager.beginTransaction().hide(frList.get(selectedBtnTag)!!).commit()
                supportFragmentManager.beginTransaction().show(frList.get(item.toString())!!).commit()
                selectedBtnTag = item.toString()
                break
            }
            Log.i("dd",item.toString()+" "+selectedBtnTag.toString())
            if(item == selectedBtnTag){
                flag  = true
            }
        }
    }

    private fun showPreviousTab(){
        var flag = false;
        for (item in frList.keys.reversed()){
            Log.e("dd",item.toString())
            if(flag){
                Log.e("dd",item.toString())
                supportFragmentManager.beginTransaction().hide(frList.get(selectedBtnTag)!!).commit()
                supportFragmentManager.beginTransaction().show(frList.get(item.toString())!!).commit()
                selectedBtnTag = item.toString()
                break
            }
            Log.i("dd",item.toString()+" "+selectedBtnTag.toString())
            if(item == selectedBtnTag){
                flag  = true
            }
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
        val scollDown = arrayOf("내려", "아래로")
        val scollUp = arrayOf("올려", "위로")
        val zoomIn = arrayOf("크게", "확대")
        val zoomOut = arrayOf("작게", "축소")
        val goBack = arrayOf("뒤로","백","이전 페이지")
        val goForward = arrayOf("앞으로","다음 페이지")
        val click = arrayOf("클릭","누르기", "터치")
        val newTab = arrayOf("새탭","새탭 열기","새탬 만들기")
        val nexTab = arrayOf("다음 탭","앞 탭")
        val previousTab = arrayOf("이전 탭","전 탭")
        val closeTab = arrayOf("탭 닫기","닫기")
        val refresh = arrayOf("새로고침","리프래시")
        val addBookmark = arrayOf("즐겨찾기 추가","북마크 추가")
        val removeBookmark = arrayOf("즐겨찾기 제거","북마크 제거")
        val showBookmark = arrayOf("북마크","즐겨찾기", "북마크 보여줘","증겨찾기 보여줘")
        val findKeyword = arrayOf("~찾기")
        val readScreen = arrayOf("","")
        val nextFocus = arrayOf("","")
        val showFocus = arrayOf("","")



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
        }else if(speechText in zoomOut) {
            webview.evaluateJavascript(
                "    if(document.body.style.zoom==\"\"){\n" +
                        "        document.body.style.zoom = 90+\"%\";\n" +
                        "    }else{\n" +
                        "        var zoom = document.body.style.zoom.toString().substring(0, document.body.style.zoom.toString().indexOf(\"%\"));\n" +
                        "        console.log(zoom);\n" +
                        "        document.body.style.zoom = (parseInt(zoom)-10)+\"%\";\n" +
                        "    }"
            ) {}
        }else if(speechText in goBack){
            webview.evaluateJavascript(
                "history.back();"
            ) {}
        }else if(speechText in goForward){
            webview.evaluateJavascript(
                "history.forward();"
            ) {}
        }else if(speechText in click){
            webview.evaluateJavascript(
                "document.activeElement.click();"
            ) {}
        }else if(speechText in newTab){
            makeNewTab()
        }else if(speechText in nexTab){

        }else if(speechText in previousTab){

        }else if(speechText in closeTab){

        }else if(speechText in refresh){
            webview.evaluateJavascript(
                "location.reload();"
            ) {}
        }else if(speechText in zoomOut){

        }else if(speechText in zoomOut){

        }else if(speechText in zoomOut){

        }else if(speechText in zoomOut){

        }else if(speechText in zoomOut){

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
