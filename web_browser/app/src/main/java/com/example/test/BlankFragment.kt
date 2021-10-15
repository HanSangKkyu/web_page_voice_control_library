package com.example.test

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import kotlinx.android.synthetic.main.fragment_blank.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var isWebViewReady = false
    private var tempStr :String = ""

    override fun onStart() {
        super.onStart()
        // 인터넷 연결 되어 있을 때 (셀룰러/와이파이)
        webview.settings.javaScriptEnabled = true // 자바 스크립트 허용
        webview.settings.builtInZoomControls = true
        webview.settings.setSupportZoom(true)
        webview.settings.displayZoomControls = false

        // 웹뷰안에 새 창이 뜨지 않도록 방지
        webview.webViewClient = MyWebViewClient()
        webview.webChromeClient = WebChromeClient()

        // 웹뷰의 준비가 다 됐으면 상태를 업데이트하고 tempStr에 보관된 주소로 접속
        isWebViewReady = true
        if (tempStr != "") {
            webview.loadUrl("http://" + tempStr)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        fun newInstance(): BlankFragment {
            return newInstance()
        }
    }

    fun changeUrl(url:String){
        var turl = ""

        if (isWebViewReady) {
//            turl = url.replace("http://","")
            turl = url.replace("https://","")
            turl = turl.replace("http://","")
            Log.e("here",turl+" name")

            try {
                webview.loadUrl("http://" + turl)
            }catch (e:Exception){}


            Log.e("here",MainActivity.frList.size.toString()+" size")
        } else {
            Log.e("here",this.toString()+" not ready")
            tempStr = url
        }

    }

    fun getUrl(): String? {
        if (isWebViewReady) {
            return webview.url
        }
        return "not rendered yet"
    }
}