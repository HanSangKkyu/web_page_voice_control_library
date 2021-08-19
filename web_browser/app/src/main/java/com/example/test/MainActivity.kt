package com.example.test

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.AudioManager.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TableLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_bookmark.*
import kotlinx.android.synthetic.main.fragment_blank.*
import org.json.JSONArray
import java.util.*


class MainActivity : AppCompatActivity() {

    private var speechRecognizer: SpeechRecognizer? = null
    private var frList: ArrayList<TabInfo> = ArrayList()
    private var selectedBtnTag: String = ""
    private var bookmarkDialog: BookmarkDialog? = null
    private var dlg: Dialog? = null

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestMic()
        initWidget()
        initDialog()

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun initDialog() {

        bookmarkDialog = BookmarkDialog(this)
        bookmarkDialog!!.init()
        dlg = bookmarkDialog!!.getDlg()

        // Item click listener
        dlg?.bookmarkListView?.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectItem = parent.getItemAtPosition(position) as String
                if (frList.isEmpty()) {
                    makeNewTab()
                }
                Handler().postDelayed({
                    frList.get(tagToIndex(selectedBtnTag)).blankFragment.changeUrl(selectItem.toString())
                }, 1000L)

                dlg?.dismiss()
            }

    }

    private fun requestMic() {
        // 마이크를 허용해달라고 요청한다.
        val REQUEST_CODE = 1

        if (Build.VERSION.SDK_INT >= 23)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO),
                REQUEST_CODE
            )
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun initWidget() {
        // 마이크
        micBtn.setOnClickListener(View.OnClickListener() {
            startSTT()
        })

        // 주소입력창
        urlEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                menuBtn.setVisibility(View.GONE)
                micBtn.setVisibility(View.GONE)
                enterBtn.visibility = View.VISIBLE
            } else {
                menuBtn.setVisibility(View.VISIBLE)
                micBtn.setVisibility(View.VISIBLE)
                enterBtn.visibility = View.GONE
            }
        }

        urlEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                v.clearFocus()
                if (frList.isEmpty()) {
                    makeNewTab()
                }
                Handler().postDelayed({
                    frList.get(tagToIndex(selectedBtnTag)).blankFragment.changeUrl(urlEditText.text.toString())
                }, 1000L)


                return@OnKeyListener true
            }
            false
        })

        enterBtn.setOnClickListener { v ->
            v.clearFocus()
            if (frList.isEmpty()) {
                makeNewTab()
            }
            Handler().postDelayed({
                frList.get(tagToIndex(selectedBtnTag)).blankFragment.changeUrl(urlEditText.text.toString())
            }, 1000L)


        }

        // 새로고침 버튼
        refreshBtn.setOnClickListener { v ->
            // 앱에서 자바스크립트 코드 실행시키기
//            webview.loadUrl("javascript:location.reload()");
//
//            webview.evaluateJavascript("(function(){return('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();"){
//                Log.e("it",it)
//            }

//            webview.evaluateJavascript("(function(){return('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();"){
//                Log.e("it",it)
//            }
//            showNextTab()
//            addBookmark()
            matchCustomCommand("efefef")

//            var script = ""
//            webview.evaluateJavascript("(function(){return("+script+"); })();"){
//                Log.e("it",it)
//            }
        }

        // 새탭 추가
        tabBtn.setOnClickListener { v ->
            makeNewTab()
        }

        // 북마크 버튼
        bookmarkBtn.setOnClickListener { v ->
            loadBookmarkDialog()
        }

        // 사용자 명령어 관리 페이지
        commandBtn.setOnClickListener { v ->

            var intent = Intent(this, CommandActivity::class.java)
            intent.putExtra("url", getNowUrl())
            val script =
                "Object.getOwnPropertyNames(window).filter(item => typeof window[item] === 'function')"
            webview.evaluateJavascript("(function(){return(" + script + "); })();") {

                // get this page functions
                var res = it.substring(1, it.length - 1)
                res = res.replace("\"", "")

                intent.putExtra("fun", res)
                startActivity(intent)
            }


        }
    }

    private fun makeNewTab() {
        val newTabBtn = Button(this)

        val randomString = makeRanStr()

        newTabBtn.setLayoutParams(
            TableLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )
        newTabBtn.tag = randomString
        newTabBtn.text = randomString
        selectedBtnTag = randomString


        // 탭 화면 띄우기
        newTabBtn.setOnClickListener { v ->
            supportFragmentManager.beginTransaction()
                .hide(frList.get(tagToIndex(selectedBtnTag)).blankFragment)
                .commit()
            supportFragmentManager.beginTransaction()
                .show(frList.get(tagToIndex(newTabBtn.tag.toString())).blankFragment).commit()
            selectedBtnTag = newTabBtn.tag.toString()
        }

        // 탭 닫기
        newTabBtn.setOnLongClickListener { v ->
            supportFragmentManager.beginTransaction()
                .remove(frList.get(tagToIndex(selectedBtnTag)).blankFragment)
                .commit()
            frList.removeAt(tagToIndex(selectedBtnTag))
            newTabBtn.visibility = View.GONE
            return@setOnLongClickListener true
        }

        tabList.addView(newTabBtn)
        frList.add(TabInfo(newTabBtn.tag.toString(), BlankFragment(), newTabBtn))
        supportFragmentManager.beginTransaction()
            .add(R.id.frame, frList.get(tagToIndex(newTabBtn.tag.toString())).blankFragment)
            .commit()


    }

    private fun showNextTab() {
        var flag = false;

        for (item in frList) {
            if (flag) {
                supportFragmentManager.beginTransaction()
                    .hide(frList.get(tagToIndex(selectedBtnTag)).blankFragment)
                    .commit()
                supportFragmentManager.beginTransaction()
                    .show(frList.get(tagToIndex(item.tag)).blankFragment).commit()
                selectedBtnTag = item.tag
                break
            }
            if (item.tag == selectedBtnTag) {
                flag = true
            }
        }
    }

    private fun showPreviousTab() {
        var flag = false;

        for (item in frList.reversed()) {
            if (flag) {
                supportFragmentManager.beginTransaction()
                    .hide(frList.get(tagToIndex(selectedBtnTag)).blankFragment)
                    .commit()
                supportFragmentManager.beginTransaction()
                    .show(frList.get(tagToIndex(item.tag)).blankFragment).commit()
                selectedBtnTag = item.tag
                break
            }
            if (item.tag == selectedBtnTag) {
                flag = true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun loadBookmarkDialog() {
//        BookmarkDialog(this).getDlg().show()
        dlg?.show()
    }

    private fun makeRanStr(): String {
        val charPool = arrayOf(
            'a',
            'b',
            'c',
            'd',
            'e',
            'f',
            'g',
            'h',
            'i',
            'j',
            'k',
            'l',
            'm',
            'n',
            'o',
            'p',
            'q',
            'r',
            's',
            't',
            'u',
            'v',
            'w',
            'x',
            'z'
        )

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
        override fun onReadyForSpeech(params: Bundle?) =
            Toast.makeText(this@MainActivity, "음성인식 시작", Toast.LENGTH_SHORT).show()

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}

        override fun onBeginningOfSpeech() {}

        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            when (error) {
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> Toast.makeText(
                    this@MainActivity,
                    "퍼미션 없음",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        @RequiresApi(Build.VERSION_CODES.KITKAT)
        override fun onResults(results: Bundle) {
            var speechText = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0]
            Log.e("음성 인식 결과:", speechText)
            Toast.makeText(this@MainActivity, "음성 인식 결과: " + speechText, Toast.LENGTH_SHORT).show()

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
    fun matchCommand(speechText: String) {
        val scollDown = arrayOf("내려", "아래로")
        val scollUp = arrayOf("올려", "위로")
        val zoomIn = arrayOf("크게", "확대")
        val zoomOut = arrayOf("작게", "축소")
        val goBack = arrayOf("뒤로", "백", "이전 페이지")
        val goForward = arrayOf("앞으로", "다음 페이지")
        val click = arrayOf("클릭", "누르기", "터치")
        val newTab = arrayOf("새탭", "새탭 열기", "새탬 만들기")
        val nexTab = arrayOf("다음 탭", "앞 탭")
        val previousTab = arrayOf("이전 탭", "전 탭")
        val closeTab = arrayOf("탭 닫기", "닫기")
        val refresh = arrayOf("새로고침", "리프래시")
        val addBookmark = arrayOf("즐겨찾기 추가", "북마크 추가")
        val removeBookmark = arrayOf("즐겨찾기 제거", "북마크 제거")
        val showBookmark = arrayOf("북마크", "즐겨찾기", "북마크 보여줘", "증겨찾기 보여줘")
        val findKeyword = arrayOf("~찾기")
        val readScreen = arrayOf("", "")
        val nextFocus = arrayOf("", "")
        val showFocus = arrayOf("", "")
        val volUp = arrayOf("볼륨 업", "소리 키워 줘")
        val volDown = arrayOf("볼륨 다운", "소리 줄여 줘")



        if (speechText in scollDown) {
            webview.evaluateJavascript("scrollTo(document.documentElement.scrollTop, document.documentElement.scrollTop+50);") {}
        } else if (speechText in scollUp) {
            webview.evaluateJavascript("scrollTo(document.documentElement.scrollTop, document.documentElement.scrollTop-50);") {}
        } else if (speechText in zoomIn) {
            webview.evaluateJavascript(
                "    if(document.body.style.zoom==\"\"){\n" +
                        "        document.body.style.zoom = 110+\"%\";\n" +
                        "    }else{\n" +
                        "        var zoom = document.body.style.zoom.toString().substring(0, document.body.style.zoom.toString().indexOf(\"%\"));\n" +
                        "        console.log(zoom);\n" +
                        "        document.body.style.zoom = (parseInt(zoom)+10)+\"%\";\n" +
                        "    }"
            ) {}
        } else if (speechText in zoomOut) {
            webview.evaluateJavascript(
                "    if(document.body.style.zoom==\"\"){\n" +
                        "        document.body.style.zoom = 90+\"%\";\n" +
                        "    }else{\n" +
                        "        var zoom = document.body.style.zoom.toString().substring(0, document.body.style.zoom.toString().indexOf(\"%\"));\n" +
                        "        console.log(zoom);\n" +
                        "        document.body.style.zoom = (parseInt(zoom)-10)+\"%\";\n" +
                        "    }"
            ) {}
        } else if (speechText in goBack) {
            webview.evaluateJavascript(
                "history.back();"
            ) {}
        } else if (speechText in goForward) {
            webview.evaluateJavascript(
                "history.forward();"
            ) {}
        } else if (speechText in click) {
            webview.evaluateJavascript(
                "document.activeElement.click();"
            ) {}
        } else if (speechText in newTab) {
            makeNewTab()
        } else if (speechText in nexTab) {
            showNextTab()
        } else if (speechText in previousTab) {
            showPreviousTab()
        } else if (speechText in closeTab) {
            closeTab()
        } else if (speechText in refresh) {
            webview.evaluateJavascript(
                "location.reload();"
            ) {}
        } else if (speechText in addBookmark) {
            addBookmark()
        } else if (speechText in zoomOut) {

        } else if (speechText in zoomOut) {

        } else if (speechText in zoomOut) {

        } else if (speechText in zoomOut) {

        } else if (speechText in volUp) {
            volUp()
        } else if (speechText in volDown) {
            volDown()
        }


        matchCustomCommand(speechText)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun matchCustomCommand(speechText: String) {
        val commandArr = getCommandOfUrl(getHostPartInUrl(getNowUrl()))
        for (i in 0..commandArr.length() - 1) {
            if (commandArr.getJSONObject(i).getString("line") == speechText) {
                var function = commandArr.getJSONObject(i).getString("function")
                var script = ""
                if (function.contains("#")) {
                    script = function.replace("#", "")
                } else {
                    script = "window['" + function + "']()"
                }

                webview.evaluateJavascript("(function(){return(" + script + "); })();") {
                    Log.e("asdf", it)
                }
            }
        }
    }

    fun getCommand(): JSONArray {

        val djson = "[\n" +
                "    {\n" +
                "        \"url\" : \"naver.com\",\n" +
                "        \"command\" : [\n" +
                "            {\n" +
                "                \"line\" : \"hi\",\n" +
                "                \"function\" : \"foo\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]"
        var sharedPref = getSharedPreferences("command", Context.MODE_PRIVATE)
        val command = sharedPref.getString("command", djson)

        return JSONArray(command)
    }

    fun getCommandOfUrl(url: String): JSONArray {
        val json = getCommand()
        for (i in 0..json.length() - 1) {
            var tmp_url = json.getJSONObject(i).getString("url")
            if (tmp_url.equals(url)) {
                return json.getJSONObject(i).getJSONArray("command")
            }
        }

        return JSONArray("[]")
    }

    fun getHostPartInUrl(url: String): String {
        var res = url.substring(url.indexOf("://") + 3)
        res = res.substring(0, res.indexOf("/"))
        return res
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun getHTML() {
        webview.evaluateJavascript("Object.getOwnPropertyNames(window).filter(item => typeof window[item] === 'function');") {
//                Log.e("it",it)
            val functionList = it.toString().split(",")
            val resFunctionList = ArrayList<String>()
            for (i in functionList.reversed()) {
                if (i == "\"FragmentDirective\"") {
                    break
                }
                resFunctionList.add(i.trim('\"'))
//                    Log.i("jerrat",i)
            }

            for (i in resFunctionList) {
                Log.i("resFunctionList", i)
            }
        }
    }

    fun tagToIndex(_tag: String): Int {
        for (i in 0..frList.size) {
            try {
                if (frList.get(i).tag == _tag) {
                    return i
                }
            } catch (e: Exception) {
                Log.d("dd", e.toString())
                if (frList.size > 0) {
                    return 0
                } else {
                    return -1
                }
            }
        }
        return -1
    }

    fun closeTab() {
        supportFragmentManager.beginTransaction()
            .remove(frList.get(tagToIndex(selectedBtnTag)).blankFragment).commit()
        frList.get(tagToIndex(selectedBtnTag)).button.visibility = View.GONE
        frList.removeAt(tagToIndex(selectedBtnTag))
    }

    private fun volUp() {
        val mAudioManager: AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        mAudioManager.adjustStreamVolume(STREAM_MUSIC, ADJUST_RAISE, FLAG_PLAY_SOUND + FLAG_SHOW_UI)
    }

    private fun volDown() {
        val mAudioManager: AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        mAudioManager.adjustStreamVolume(STREAM_MUSIC, ADJUST_LOWER, FLAG_PLAY_SOUND + FLAG_SHOW_UI)
    }

    private fun addBookmark() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            bookmarkDialog?.addBookmark(getNowUrl())
        }

    }

    private fun getNowUrl(): String {
        return frList.get(tagToIndex(selectedBtnTag)).blankFragment.webview.url.toString()
    }
}
