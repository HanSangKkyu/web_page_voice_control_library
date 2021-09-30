package com.example.test

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.media.AudioManager.*
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Base64.NO_WRAP
import android.util.Base64.encodeToString
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.Button
import android.widget.TableLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.dialog_bookmark.*
import kotlinx.android.synthetic.main.fragment_blank.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.HashMap
import kotlin.experimental.and


class MainActivity : AppCompatActivity() {

    private var speechRecognizer: SpeechRecognizer? = null
    private var bookmarkDialog: BookmarkDialog? = null
    private var dlg: Dialog? = null
    private var searchSelector: String = ""
    private var searchIndex: Int = 0
    private var searchLength: Int = 0
    private var recorder: MediaRecorder? = null
    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.pcm"
    }
    private val wavFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.wav"
    }
    val audioRecord : OnlyAudioRecorder = OnlyAudioRecorder.instance
    private var STTresult:String = "" // STT의 결과를 담는다.
    private var SRresult:String = ""// SR의 결과를 담는다.

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    val handlerOutSide = Handler(){
        when (it.what) {
            0 ->{
                micBtn.setTextColor(Color.parseColor("#ffffff"))
                micBtn.setText("MIC")
            }
            1->{
                Toast.makeText(this, "화자인식을 활성화 했을 때는 4초 이상 말해야만 됩니다.", Toast.LENGTH_SHORT).show()
            }
            2->{
                Toast.makeText(this, "아무것도 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
            3 ->{
                micBtn.setTextColor(Color.parseColor("#ffffff"))
                micBtn.setText("MIC")
            }
            4->{
                Toast.makeText(this, "음성 프로필이 등록되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
            5->{
                println("매칭 시작 $STTresult")
                matchCommand(STTresult)
                startSTT()
            }
        }
        true
    }

    companion object {
        val handler = Handler(){
            when (it.what) {
                0 ->{
                    getNowBtn().text = nowBtnTitle
                }
            }
            true
        }

        var frList: ArrayList<TabInfo> = ArrayList()
        private var selectedBtnTag: String = ""
        var nowBtnTitle = ""

        fun changeBtnTitle(title:String){
            nowBtnTitle = title
            handler.sendEmptyMessage(0)
        }

        fun getNowBtn(): Button {
            return frList.get(tagToIndex(selectedBtnTag)).button
        }

        fun tagToIndex(_tag: String): Int {
            for (i in 0..frList.size) {
                try {
                    if (frList.get(i).tag == _tag) {
                        return i
                    }
                } catch (e: Exception) {
                    if (frList.size > 0) {
                        return 0
                    } else {
                        return -1
                    }
                }
            }
            return -1
        }
    }

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
                    makeNewTab().changeUrl(selectItem.toString())
                }else{
                    goToSite(urlEditText.text.toString())
                }

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
        // 마이크 버튼
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
                    makeNewTab().changeUrl(urlEditText.text.toString())
                }else{
                    goToSite(urlEditText.text.toString())
                }
                return@OnKeyListener true
            }
            false
        })

        // 엔터 버튼
        enterBtn.setOnClickListener { v ->
            v.clearFocus()
            if (frList.isEmpty()) {
                makeNewTab().changeUrl(urlEditText.text.toString())
            }else{
                goToSite(urlEditText.text.toString())
            }
        }

        // 새로고침 버튼
        refreshBtn.setOnClickListener { v ->
            getNowTab().webview.reload()
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
//            matchCustomCommand("efefef")

//            var script = ""
//            webview.evaluateJavascript("(function(){return("+script+"); })();"){
//                Log.e("it",it)
//            }
//            addBookmark()
//            matchCommand("대한민국 검색 해 줘")
//            addBookmark()
        }

        // 새탭 버튼
        tabBtn.setOnClickListener { v ->
            makeNewTab()
        }

        // 북마크 버튼
        bookmarkBtn.setOnClickListener { v ->
            loadBookmarkDialog()
        }

        // 사용자 명령어 관리 페이지
        commandBtn.setOnClickListener { v ->
            if (frList.isEmpty()) {
                return@setOnClickListener
            }

            var intent = Intent(this, CommandActivity::class.java)
            intent.putExtra("url", getNowUrl())
            val script =
                "Object.getOwnPropertyNames(window).filter(item => typeof window[item] === 'function' && !(/\\{\\s*\\[native code\\]\\s*\\}/).test('' + window[item]))"

            getNowTab().webview.evaluateJavascript("(function(){return(" + script + "); })();") {
                // get this page functions
                var res = it.substring(1, it.length - 1)
                res = res.replace("\"", "")

                intent.putExtra("fun", res)
                startActivity(intent)
            }
        }

        // 메뉴 버튼
        menuBtn.setOnClickListener{v->
            // 화자 식별 기능으로 이동
            val intent = Intent(this, SpeakerRecnitionActivity::class.java)
            startActivity(intent)
        }
    }

    fun getNowTab(): BlankFragment {
        return frList.get(tagToIndex(selectedBtnTag)).blankFragment
    }

    private fun makeNewTab(): BlankFragment {
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
        newTabBtn.text = "새 탭"
        selectedBtnTag = randomString

        // 탭 화면 띄우기
        newTabBtn.setOnClickListener { v ->

            supportFragmentManager.beginTransaction()
                .hide(frList.get(tagToIndex(selectedBtnTag)).blankFragment)
                .commit()
            supportFragmentManager.beginTransaction()
                .show(frList.get(tagToIndex(newTabBtn.tag.toString())).blankFragment).commit()
            selectedBtnTag = newTabBtn.tag.toString()

            changeBtnTextColor()



        }

        // 탭 닫기
        newTabBtn.setOnLongClickListener { v ->
            if(newTabBtn.tag == frList.get(tagToIndex(selectedBtnTag)).button.tag) {
                // 사용 중인 탭만 닫을 수 있다.
                closeTab()
            }
            return@setOnLongClickListener true
        }

        tabList.addView(newTabBtn)
        var bf = BlankFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.frame, bf)
            .commit()

        frList.add(TabInfo(newTabBtn.tag.toString(), bf, newTabBtn))

        changeBtnTextColor()

        return frList.get(tagToIndex(newTabBtn.tag.toString())).blankFragment
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
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun startSTT() {
        if(getOnSRChk() == "on"){
            startREC()
        }else{
            // off
            // 화자 인식 기술을 사용하지 않으면 speechRecognizer를 이용한다.
            val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            }

            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
                setRecognitionListener(recognitionListener())
                startListening(speechRecognizerIntent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun startREC(){
        // 결과 초기화
        STTresult = "" // STT의 결과를 담는다.
        SRresult = ""// SR의 결과를 담는다.

        // 파일 경로 설정
        audioRecord.PCMPath = recordingFilePath
        audioRecord.WAVPath = wavFilePath

        // REC 중이라고 버튼에 표기하기
        micBtn.setTextColor(Color.parseColor("#ff0000"))
        micBtn.setText("REC")

        audioRecord.startRecord() //Start recording

        Thread(Runnable {
            var startFlag = false
            var cnt = 10 // 1초 동안 말이 없으면 녹음을 멈춘다.
            var secCnt = 30 // 3초 동안 시작을 안하면 녹음을 멈춘다.
            while(true) {
                Thread.sleep(100L) // 0.1초 마다 발화를 하고 있는 상태인지 확인한다.
                var Amplitude =  audioRecord.Amplitude
                Log.e("Amplitude",Amplitude.toString())
                if(Amplitude.toString().toInt() > 100000){
                    if(!startFlag) {
                        startFlag = true
                    }
                    cnt = 10
                }

                if(Amplitude.toString().toInt() < 100000 && startFlag){
                    cnt --
                    if(cnt == 0){
                        stopREC()
                        break
                    }
                }

                if(!startFlag){
                    secCnt --
                    if(secCnt == 0){
                        // 마이크 제자리
                        println("3초 입력 없었음")
                        handlerOutSide.sendEmptyMessage(3)
                        audioRecord.stopRecord()
                        break
                    }
                }

            }
        }).start()

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun stopREC() {
        // 마이크 제자리
        handlerOutSide.sendEmptyMessage(3)

        audioRecord.stopRecord()

        Thread(Runnable {
            while(true) {
                Thread.sleep(100L) // 0.1초 마다 wav파일이 완성되었는지 확인한다.
                if(audioRecord.isWavComplete){
                    requestSTT() // STT를 요청한다.
                    requestSR()

                    Thread(Runnable {
                        while(true) {
                            Thread.sleep(100L) // 0.1초 마다 STT와 SR이 완료되었는지 확인한다.
                            if(STTresult.length > 0 && SRresult.length > 0){
                                println("sangkyu $STTresult $SRresult")

                                handlerOutSide.sendEmptyMessage(0) // REC으로 표시된 버튼은 MIC바꾸는 작업을 수행하라고 핸들러에게 알린다.

                                if(SRresult == "Reject"){
                                    println("사용자가 다릅니다. 안됩니다. SRresult: $SRresult 입니다.")
                                }else{
                                    handlerOutSide.sendEmptyMessage(5) // matchCommand()
                                    if(SRresult == "Accept"){
                                        println("등록된 사용자가 맞습니다.")
                                    }else if(SRresult == "Invalid audio length. Minimum allowed length is 4 second(s)."){
                                        handlerOutSide.sendEmptyMessage(1)
                                    }else if(SRresult == "No value for message"){
                                        handlerOutSide.sendEmptyMessage(2)
                                    }else if(SRresult == "Profile is not enrolled."){
                                        handlerOutSide.sendEmptyMessage(4)
                                    }
                                }

                                break
                            }
                        }
                    }).start()

                    break
                }
            }
        }).start()


//        음성인식 결과 듣고 싶을 때 사용
//        var mediaPlayer = MediaPlayer()
//        mediaPlayer.setDataSource(wavFilePath)
//        mediaPlayer.prepare()
//        mediaPlayer.start()
    }

    private fun requestSR() {
        // 화자 인식을 요청한다.
        val request = object : VolleyFileUploadRequest(
            Request.Method.POST,
            "https://westus.api.cognitive.microsoft.com/speaker/verification/v2.0/text-independent/profiles/"+getProfileID()+"/verify",
            Response.Listener {
                val response = it
                val json = String(
                    response?.data ?: ByteArray(0),
                    Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))

                val jObject = JSONObject(json)
                SRresult = jObject.getString("recognitionResult")

                println("error is: $it $json")
            },
            Response.ErrorListener {
                val response = it.networkResponse
                val json = String(
                    response?.data ?: ByteArray(0),
                    Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))

                val jObject = JSONObject(json)
                try{
                    SRresult = jObject.getString("message")
                }catch (e:Exception){
                    SRresult = "No value for message"
                    Log.e("asdf",e.toString())
                }

                println("error is: $it $json")
            }
        ) {
            // wav 파일 보내기
            override fun getBody(): ByteArray {
                return File(wavFilePath).readBytes()
            }

            // Providing Request Headers
            override fun getHeaders(): MutableMap<String, String> {
                // Create HashMap of your Headers as the example provided below

                val headers = HashMap<String, String>()
                headers["Ocp-Apim-Subscription-Key"] = "11dee688d18444d9837321f89ce98c38"
                headers["Content-Type"] = "audio/wav"
                return headers
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun requestSTT() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://speech.googleapis.com/v1/speech:recognize?key=AIzaSyAq7bI-K6mDlK1Hd706j5eSaQbUgiB6m2Q"

        val bytes = File(wavFilePath).readBytes()
        var base64 = encodeToString(bytes, 0)
        base64 = base64.replace("\n","")
        base64 = base64.replace("\"","")
//        println("base64 $base64")

        val stringReq : StringRequest =
            object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    // response
                    var json = response.toString()

                    val jObject = JSONObject(json)
                    try{
                        STTresult =  jObject.getJSONArray("results").getJSONObject(0).getJSONArray("alternatives").getJSONObject(0).getString("transcript")
                    }catch (e:Exception){
                        Log.e("asdf", e.toString())
                        STTresult = "No value for results"
                    }

                    Log.d("API", json)
                },
                Response.ErrorListener { error ->
                    val json = String(
                        error.networkResponse?.data ?: ByteArray(0),
                        Charset.forName(HttpHeaderParser.parseCharset(error.networkResponse?.headers)))
                    Log.d("API", "error => $json")
                }
            ){
                override fun getBody(): ByteArray {
                    val raw = "{\n" +
                            "  \"config\":{\n" +
                            "      \"languageCode\":\"ko-KR\"\n" +
                            "  },\n" +
                            "  \"audio\":{\n" +
                            "    \"content\":\""+base64+"\"\n" +
                            "  }\n" +
                            "}"
                    return raw.toByteArray()
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }
        queue.add(stringReq)


//        val request = object : VolleyFileUploadRequest(
//            Request.Method.POST,
//            "https://westus.stt.speech.microsoft.com/speech/recognition/conversation/cognitiveservices/v1?language=ko-KR",
//            Response.Listener {
//                val response = it
//                val json = String(
//                    response?.data ?: ByteArray(0),
//                    Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))
//
//                println("error is: $it $json")
//            },
//            Response.ErrorListener {
//                val response = it.networkResponse
//                val json = String(
//                    response?.data ?: ByteArray(0),
//                    Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))
//
//                println("error is: $it $json")
//            }
//        ) {
////            override fun getByteData(): MutableMap<String, FileDataPart> {
////                var params = HashMap<String, FileDataPart>()
//////                params["file\"; filename=\"recording.wav\""] = FileDataPart("recording", File(recordingFilePath).readBytes(), "wav")
//////                params["file"] = FileDataPart("recording", File(recordingFilePath).readBytes(), "wav")
////                return params
////            }
//
//            override fun getBody(): ByteArray {
//                return File(wavFilePath).readBytes()
//            }
//
//            // Providing Request Headers
//            override fun getHeaders(): MutableMap<String, String> {
//                // Create HashMap of your Headers as the example provided below
//
//                val headers = HashMap<String, String>()
//                headers["Ocp-Apim-Subscription-Key"] = "11dee688d18444d9837321f89ce98c38"
//                headers["Content-Type"] = "audio/wav"
//                return headers
//            }
//        }
//        Volley.newRequestQueue(this).add(request)

    }

    private fun recognitionListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
//            Toast.makeText(this@MainActivity, "음성인식 시작", Toast.LENGTH_SHORT).show()
            micBtn.setTextColor(Color.parseColor("#ff0000"))
            micBtn.setText("REC")
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}

        override fun onBeginningOfSpeech() {
        }

        override fun onEndOfSpeech() {
            micBtn.setTextColor(Color.parseColor("#ffffff"))
            micBtn.setText("MIC")
        }

        override fun onError(error: Int) {
            micBtn.setTextColor(Color.parseColor("#ffffff"))
            micBtn.setText("MIC")

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
//            Toast.makeText(this@MainActivity, "음성 인식 결과: " + speechText, Toast.LENGTH_SHORT).show()
            micBtn.setTextColor(Color.parseColor("#ffffff"))
            micBtn.setText("MIC")
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
        // 형태소 분석을 통한 의도 파악 기술이 들어가야 할 듯하다.
        val scrollDown = arrayOf("내려", "아래로")
        val scrollUp = arrayOf("올려", "위로")
        val scrollUpMax = arrayOf("맨 위로")
        val scrollLeft = arrayOf("왼쪽으로")
        val scrollRight = arrayOf("옆으로", "오른쪽으로")
        val zoomIn = arrayOf("크게", "확대")
        val zoomOut = arrayOf("작게", "축소")
        val goBack = arrayOf("뒤로", "백", "이전 페이지")
        val goForward = arrayOf("앞으로", "다음 페이지")
        val click = arrayOf("클릭", "누르기", "터치")
        val newTab = arrayOf("새 탭", "새 탭 열기", "새 탭 만들기")
        val nexTab = arrayOf("다음 탭", "앞 탭")
        val previousTab = arrayOf("이전 탭", "전 탭")
        val closeTab = arrayOf("탭 닫기", "닫기")
        val refresh = arrayOf("새로 고침", "리프레쉬")
        val addBookmark = arrayOf("즐겨찾기 추가", "북마크 추가")
        val removeBookmark = arrayOf("즐겨찾기 제거", "북마크 제거")
        val showBookmark = arrayOf("북마크", "즐겨찾기", "북마크 보여줘", "증겨찾기 보여줘")
        val findKeyword = arrayOf("~찾기")
        val readScreen = arrayOf("", "")
        val nextFocus = arrayOf("", "")
        val showFocus = arrayOf("", "")
        val volUp = arrayOf("볼륨 업", "소리 키워 줘")
        val volDown = arrayOf("볼륨 다운", "소리 줄여 줘")
        val listing = arrayOf("리스트", "리스트 시작")
        val prevElement = arrayOf("이전")
        val nextElement = arrayOf("다음")
        val endListing = arrayOf("리스트 종료")
        val play = arrayOf("재생")
        val pause = arrayOf("정지")

        if (speechText in scrollDown) {
            getNowTab().webview.pageDown(false)
        } else if (speechText in scrollUp) {
            getNowTab().webview.pageUp(false)
        } else if (speechText in scrollUpMax) {
            getNowTab().webview.pageUp(true)
        } else if (speechText in scrollLeft) {
            val view = getNowTab().webview
            val desX = if(0 > view.scrollX - view.width) { 0 } else { view.scrollX - view.width }
            smoothScrollAnime(view, desX, view.scrollY, 1000).start()
        } else if (speechText in scrollRight) {
            val view = getNowTab().webview
            val desX = if(view.horizontalScrollableRange < view.scrollX + view.width) { view.horizontalScrollableRange } else { view.scrollX + view.width }
            smoothScrollAnime(view, desX, view.scrollY, 1000).start()
        } else if (speechText in zoomIn) {
            getNowTab().webview.zoomIn()
        } else if (speechText in zoomOut) {
            getNowTab().webview.zoomOut()
        } else if (speechText in goBack) {
            getNowTab().webview.evaluateJavascript(
                "history.back();"
            ) {}
        } else if (speechText in goForward) {
            getNowTab().webview.evaluateJavascript(
                "history.forward();"
            ) {}
        } else if (speechText in click) {
            clickThis()
        } else if (speechText in newTab) {
            makeNewTab()
        } else if (speechText in nexTab) {
            showNextTab()
        } else if (speechText in previousTab) {
            showPreviousTab()
        } else if (speechText in closeTab) {
            closeTab()
        } else if (speechText in refresh) {
            getNowTab().webview.reload()
        } else if (speechText in addBookmark) {
            addBookmark()
        } else if (speechText in findKeyword) {

        } else if (speechText in readScreen) {

        } else if (speechText in nextFocus) {

        } else if (speechText in showFocus) {

        } else if (speechText in volUp) {
            volUp()
        } else if (speechText in volDown) {
            volDown()
        } else if (speechText in listing) {
            startlistingElement("a")
        } else if (speechText in nextElement) {
            moveElementList(1)
        } else if (speechText in prevElement) {
            moveElementList(-1)
        } else if (speechText in endListing) {
            endListingElement()
        } else if (speechText in play) {
            play()
        } else if (speechText in pause) {
            pause()
        } else {
            // 기본 명령어에 해당되지 않은 요청이 들어왔을 때 사용자 지정 명령어를 검색한다.
            matchCustomCommand(speechText)
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun clickThis() {
        getNowTab().webview.evaluateJavascript(
            "javascript:(function() {" +
                    "   let queries = document.querySelectorAll('$searchSelector');" +
                    "   for(let i = 0; i < queries.length; i++) {" +
                    "       if(queries[i].parentNode.className != 'highlightedByBrowser') {" +
                    "           let wrapper = document.createElement('div');" +
                    "           wrapper.className = 'highlightedByBrowser';" +
                    "           wrapper.style.backgroundColor = (i == $searchIndex ? 'orange' : 'yellow');" +
                    "           queries[i].parentNode.insertBefore(wrapper, queries[i]);" +
                    "           wrapper.append(queries[i]);" +
                    "       }" +
                    "   }" +
                    "   if (queries && queries.length > $searchIndex) {" +
                    "       queries[$searchIndex].scrollIntoView();" +
                    "       queries[$searchIndex].click();" +
                    "   }" +
                    "   return queries.length;" +
                    "})();"
        ) {

        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun matchCustomCommand(speechText: String) {
        // 사용자 지정 공통 명령어에서 일치하는 것이 있는지 검색한다.
        var commandArr = getCommandOfUrl("공통 명령어")
        for (i in 0..commandArr.length() - 1) {
            // * 가 line에 포함되어 있는지 판단하여 적용해야 한다.

            if (commandArr.getJSONObject(i).getString("line") == speechText) {

                var function = commandArr.getJSONObject(i).getString("function")
                var script = function
                if (function.contains("#")) {
                    // js 스크립트라면
                    script = function.replace("#", "")
                    getNowTab().webview.evaluateJavascript(script) {
                    }
                }else if(function.contains("@")){
                    // 안드로이드 함수라면
                    fireAndroidFun(function)
                }


            } else if (commandArr.getJSONObject(i).getString("line").contains("*")) {
                // 사용자 지정 명령어가 *(와일드카드)를 가지고 있다면
                // 사용자의 발화에서 어떤 부분이 와일드 카드이 인지 알아낸다.
                var commmand = commandArr.getJSONObject(i).getString("line").replace("*", "")
                var wc = speechText
                if (wc.contains(commmand)) {
                    wc = wc.replace(commmand, "")

                    var function = commandArr.getJSONObject(i).getString("function")
                    var script = function.replace("#", "")
                    script = script.replace("*", wc)
                    getNowTab().webview.evaluateJavascript(script) {
                        Log.e("asdf", it)
                    }
                }
            }
        }

        // 사용자 지정 명령어 중 이 페이지에 해당하는 것이 있는지 검색한다.
        try{
            commandArr = getCommandOfUrl(getHostPartInUrl(getNowUrl()))
            for (i in 0..commandArr.length() - 1) {
                // * 가 line에 포함되어 있는지 판단하여 적용해야 한다.
                if (commandArr.getJSONObject(i).getString("line") == speechText) {
                    var function = commandArr.getJSONObject(i).getString("function")
                    var script = ""
                    if (function.contains("#")) {
                        script = function.replace("#", "")
                    } else {
                        script = "window['" + function + "']()"
                    }

                    getNowTab().webview.evaluateJavascript("(function(){return(" + script + "); })();") {
                        Log.e("asdf", it)
                    }
                } else if (commandArr.getJSONObject(i).getString("line").contains("*")) {
                    // 사용자 지정 명령어가 *(와일드카드)를 가지고 있다면
                    // 사용자의 발화에서 어떤 부분이 와일드 카드이 인지 알아낸다.
                    var commmand = commandArr.getJSONObject(i).getString("line").replace("*", "")
                    var wc = speechText
                    if (wc.contains(commmand)) {
                        wc = wc.replace(commmand, "")

                        var function = commandArr.getJSONObject(i).getString("function")
                        var script = function.replace("#", "")
                        script = script.replace("*", wc)
                        getNowTab().webview.evaluateJavascript(script) {
                            Log.e("asdf", it)
                        }
                    }


                }
            }
        }catch(e:Exception){
            Log.e("ERROR","url 정보를 알 수 없음")
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
        getNowTab().webview.evaluateJavascript("Object.getOwnPropertyNames(window).filter(item => typeof window[item] === 'function');") {
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





    fun closeTab() {
        supportFragmentManager.beginTransaction()
            .remove(frList.get(tagToIndex(selectedBtnTag)).blankFragment).commit()
        frList.get(tagToIndex(selectedBtnTag)).button.visibility = View.GONE
        frList.removeAt(tagToIndex(selectedBtnTag))

        if(!frList.isEmpty()){
            selectedBtnTag = frList.get(0).button.tag.toString()
            changeBtnTextColor()

            supportFragmentManager.beginTransaction()
                .show(frList.get(tagToIndex(selectedBtnTag)).blankFragment).commit()
        }

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
        return getNowTab().webview.url.toString()
    }

    private fun smoothScrollAnime(view: WebView, xval: Int, yval: Int, dur: Long) : AnimatorSet {
        return AnimatorSet().apply {
            duration = dur
            play(ObjectAnimator.ofInt(view, "scrollX", view.scrollX, xval)).with(ObjectAnimator.ofInt(view, "scrollY", view.scrollY, yval))
        }
    }

//     오류가 많고 zoom 설정을 직접 제어할 수 있는 코드를 주지 않아 제어가 힘듦듦.
//   @RequiresApi(Build.VERSION_CODES.O)
//    private fun smoothZoomAnime(view: WebView, zoomScale: Float, dur: Long): ValueAnimator {
//        return ValueAnimator.ofFloat((view.webViewClient as MyWebViewClient).presentScale, zoomScale).apply {
//            duration = dur
//            addUpdateListener {
//                view.zoomBy(it.animatedValue as Float)
//            }
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun startlistingElement(selector: String) {
        searchSelector = selector
        searchIndex = 0
        getNowTab().webview.evaluateJavascript(
            "javascript:(function() {" +
                    "   let queries = document.querySelectorAll('$searchSelector');" +
                    "   let wrapper;" +
                    "   if(queries && queries.length != 0) {" +
                    "       queries = Array.from(queries).filter(query => query.style.visibility != 'hidden'" +
                    "       && query.style.display != 'none'" +
                    "       && query.style.opacity != 1" +
                    "       && query.offsetWidth != 0" +
                    "       && query.offsetHeight != 0);" +
                    "       if(queries && queries.length != 0) {" +
                    "           let bound;" +
                    "           for(let i = 0; i < queries.length; i++) {" +
                    "               if(queries[i].parentNode.className == 'highlightedByBrowser') {" +
                    "                   wrapper = queries[i].parentNode;" +
                    "               } else if (queries[i].parentNode.className == 'selectedByBrowser') {" +
                    "                   wrapper = queries[i].parentNode;" +
                    "                   wrapper.className = 'highlightedByBrowser';" +
                    "               } else {" +
                    "                   wrapper = document.createElement('div');" +
                    "                   wrapper.className = 'highlightedByBrowser';" +
                    "                   queries[i].parentNode.insertBefore(wrapper, queries[i]);" +
                    "                   wrapper.append(queries[i]);" +
                    "               }" +
                    "               wrapper.style.backgroundColor = 'yellow';" +
                    "           }" +
                    "           queries[0].parentNode.className = 'selectedByBrowser';" +
                    "           queries[0].parentNode.style.backgroundColor = 'orange';" +
                    "           bound = queries[0].getBoundingClientRect();" +
                    "           return JSON.stringify({ 'length': queries.length," +
                    "               'selectedLeft' : bound.left + window.pageXOffset," +
                    "               'selectedRight' : bound.right + window.pageXOffset," +
                    "               'selectedTop' : bound.top + window.pageYOffset," +
                    "               'selectedBottom' : bound.bottom + window.pageYOffset" +
                    "            });" +
                    "       }" +
                    "   }" +
                    "   return 'x';" +
                    "})();"
        ) {
            try {
                if(it != "x") {
                    var json : JSONObject = JSONObject(it.replace("\"", "").replace("\\", "\""))
                    Log.v("테스트", json.toString())
                }
            } catch (e: Exception) {
                Log.e("테스트", e.localizedMessage)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun moveElementList(direction: Int) {
        val oldSearchIndex: Int = searchIndex
        searchIndex = when {
            (searchIndex + direction) >= searchLength -> {
                searchIndex + direction - searchLength
            }
            searchIndex + direction < 0 -> {
                searchLength + (searchIndex + direction)
            }
            else -> {
                searchIndex + direction
            }
        }
        getNowTab().webview.evaluateJavascript(
            "javascript:(function() {" +
                    "   let queries = document.querySelectorAll('$searchSelector');" +
                    "   let wrapper;" +
                    "   if(queries && queries.length != 0) {" +
                    "       queries = Array.from(queries).filter(query => query.style.visibility != 'hidden'" +
                    "       && query.style.display != 'none'" +
                    "       && query.style.opacity != 1" +
                    "       && query.offsetWidth != 0" +
                    "       && query.offsetHeight != 0);" +
                    "       if(queries && queries.length != 0) {" +
                    "           let bound;" +
                    "           let index = -1;" +
                    "           for(let i = 0; i < queries.length; i++) {" +
                    "               if(queries[i].parentNode.className == 'highlightedByBrowser') {" +
                    "                   wrapper = queries[i].parentNode;" +
                    "               } else if (queries[i].parentNode.className == 'selectedByBrowser') {" +
                    "                   index = i;" +
                    "                   wrapper = queries[i].parentNode;" +
                    "                   wrapper.className = 'highlightedByBrowser';" +
                    "               } else {" +
                    "                   wrapper = document.createElement('div');" +
                    "                   wrapper.className = 'highlightedByBrowser';" +
                    "                   queries[i].parentNode.insertBefore(wrapper, queries[i]);" +
                    "                   wrapper.append(queries[i]);" +
                    "               }" +
                    "               wrapper.style.backgroundColor = 'yellow';" +
                    "           }" +
                    "           if(index != -1) {" +
                    "               index = (index + 1) % queries.length;" +
                    "           } else {" +
                    "               index = 0;" +
                    "           }" +
                    "           queries[index].parentNode.className = 'selectedByBrowser';" +
                    "           queries[index].parentNode.style.backgroundColor = 'orange';" +
                    "           bound = queries[index].getBoundingClientRect();" +
                    "           return JSON.stringify({ 'length': queries.length," +
                    "               'selectedLeft' : bound.left + window.pageXOffset," +
                    "               'selectedRight' : bound.right + window.pageXOffset," +
                    "               'selectedTop' : bound.top + window.pageYOffset," +
                    "               'selectedBottom' : bound.bottom + window.pageYOffset" +
                    "            });" +
                    "       }" +
                    "   }" +
                    "   return 'x';" +
                    "})();"
        ) {
            try {
                if(it != "x") {
                    var json : JSONObject = JSONObject(it.replace("\"", "").replace("\\", "\""))
                    Log.v("테스트", json.toString())
                }
            } catch (e: Exception) {
                Log.e("테스트", "why: " + e.localizedMessage)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun endListingElement() {
        getNowTab().webview.evaluateJavascript(
            "javascript:(function() {" +
                    "   let queries=document.querySelectorAll('$searchSelector');" +
                    "   let wrapper;" +
                    "   for(let i = 0; i < queries.length; i++) {" +
                    "       wrapper = queries[i].parentNode;" +
                    "       if(wrapper.className == 'highlightedByBrowser') {" +
                    "           wrapper.parentNode.insertBefore(queries[i], wrapper);" +
                    "           wrapper.remove();" +
                    "       };" +
                    "      " +
                    "   }" +
                    "})();"
        ) {}
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun play() {
        if (!searchSelector.contains("video") && !searchSelector.contains("audio")) {
            searchSelector = "video, audio"
            searchIndex = 0
        }
        getNowTab().webview.evaluateJavascript(
            "javascript:(function() {" +
                    "   let queries = document.querySelectorAll('$searchSelector');" +
                    "   for(let i = 0; i < queries.length; i++) {" +
                    "       if(queries[i].parentNode.className != 'highlightedByBrowser') {" +
                    "           let wrapper = document.createElement('div');" +
                    "           wrapper.className = 'highlightedByBrowser';" +
                    "           wrapper.style.backgroundColor = (i == $searchIndex ? 'orange' : 'yellow');" +
                    "           queries[i].parentNode.insertBefore(wrapper, queries[i]);" +
                    "           wrapper.append(queries[i]);" +
                    "       }" +
                    "   }" +
                    "   if (queries && queries.length > $searchIndex) {" +
                    "       queries[$searchIndex].scrollIntoView();" +
                    "       queries[$searchIndex].play();" +
                    "   }" +
                    "   return queries.length;" +
                    "})();"
        ) {
            Log.e("테스트", it);
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun pause() {
        if (!searchSelector.contains("video") && !searchSelector.contains("audio")) {
            searchSelector = "video, audio"
            searchIndex = 0
        }
        getNowTab().webview.evaluateJavascript(
            "javascript:(function() {" +
                    "   let queries = document.querySelectorAll('$searchSelector');" +
                    "   for(let i = 0; i < queries.length; i++) {" +
                    "       if(queries[i].parentNode.className != 'highlightedByBrowser') {" +
                    "           let wrapper = document.createElement('div');" +
                    "           wrapper.className = 'highlightedByBrowser';" +
                    "           wrapper.style.backgroundColor = (i == $searchIndex ? 'orange' : 'yellow');" +
                    "           wrapper.innerHTML = queries[i].outerHTML;" +
                    "           queries.parentNode.insertBefore(wrapper, queries[i]);" +
                    "           queries[i].remove();" +
                    "           queries[i] = wrapper.firstChild;" +
                    "       }" +
                    "   }" +
                    "   if (queries && queries.length > $searchIndex && !queries[$searchIndex].paused) {" +
                    "       queries[$searchIndex].pause();" +
                    "   }" +
                    "   else {" +
                    "       for(let i = 0; i < queries.length; i++) {" +
                    "           if (!queries[i].paused) {" +
                    "               queries[i].pause();" +
                    "               queries[$searchIndex].parentNode.style.backgrondColor = 'yellow';" +
                    "               queries[i].parentNode.style.backgroundColor = 'orange';" +
                    "               return i;" +
                    "           }" +
                    "       }" +
                    "   }" +
                    "   return $searchIndex;" +
                    "})();"
        ) {
            searchIndex = it.toInt()
        }
    }

    private fun goToSite(siteUrl:String){
        frList.get(tagToIndex(selectedBtnTag)).blankFragment.changeUrl(siteUrl)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun fireAndroidFun(funStr: String) {

        if (funStr == "@makeNewtab()") {
            makeNewTab()
        } else if (funStr == "@showNextTab()") {
            showNextTab()
        } else if (funStr == "@showPreviousTab()") {
            showPreviousTab()
        } else if (funStr == "@closeTab()") {
            closeTab()
        } else if (funStr == "@addBookmark()") {
            addBookmark()
        } else if (funStr == "@volUp()") {
            volUp()
        } else if (funStr == "@volDown()") {
            volDown()
        } else if (funStr == "@startlistingElement(*)") {
            startlistingElement("*") // 더 개발 필요
        } else if (funStr == "@moveElementList(1)") {
            moveElementList(1)
        } else if (funStr == "@moveElementList(-1)") {
            moveElementList(-1)
        } else if (funStr == "@endListingElement()") {
            endListingElement()
        } else if (funStr == "@play()") {
            play()
        } else if (funStr == "@pause()") {
            pause()
        } else if (funStr.contains("goToSite")) {
            val url = funStr.substring(funStr.indexOf('(')+1, funStr.indexOf(')'))
            goToSite(url)
        } else if (funStr == "") {

        } else if (funStr == "") {

        } else if (funStr == "") {

        }
    }

    fun changeBtnTextColor(){
        // 현재 사용중인 탭 버튼만 색을 빨간색으로 바꾼다.
        for(tmpTabInfo in frList){
            tmpTabInfo.button.setTextColor(Color.parseColor("#ffffff"))
            if(tmpTabInfo.button.tag.toString() == selectedBtnTag){
                tmpTabInfo.button.setTextColor(Color.parseColor("#ff0000"))
            }
        }
    }

    fun getProfileID(): String? {
        val sharedPref = getSharedPreferences("profileId", Context.MODE_PRIVATE)
        return sharedPref.getString("profileId", "not Created")
    }

    fun getOnSRChk(): String? {
        val sharedPref = getSharedPreferences("onSRChk", Context.MODE_PRIVATE)
        return sharedPref.getString("onSRChk", "off")
    }

}
