package com.example.test

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_command.*
import org.json.JSONArray
import org.json.JSONObject

class CommandActivity : AppCompatActivity() {

    var url = ""
    var selectedFun = ""

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_command)

        init()

    }

    fun init() {
        // get extra
        var intent = getIntent()
        url = intent.getStringExtra("url").toString()
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
//                Log.e("asdf", funSpinner.getItemAtPosition(position).toString())
                selectedFun = funSpinner.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }


        // set listView
        url = getHostPartInUrl(url)
        val jArr = getCommandOfUrl(url)
        var commandList = ArrayList<Command>()
        for (i in 0..jArr.length() - 1) {
            commandList.add(
                Command(
                    jArr.getJSONObject(i).getString("line"),
                    jArr.getJSONObject(i).getString("function")
                )
            )
        }
        customCommandListView.adapter = MyCustomAdapter(this, commandList)

        // set create Btn
        createBtn.setOnClickListener { v ->
            addCommand(url, line.text.toString(), selectedFun)
            line.setText("")
            Toast.makeText(this, "생성 완료", Toast.LENGTH_SHORT).show()
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

    fun addCommand(url: String, line: String, function: String) {
        var sharedPref = getSharedPreferences("command", Context.MODE_PRIVATE)

        var json = getCommand()
        for (i in 0..json.length() - 1) {
            var tmp_url = json.getJSONObject(i).getString("url")
            if (tmp_url.equals(url)) {
                var tmp_command = json.getJSONObject(i).getJSONArray("command")

                var tmp_command_item = JSONObject()
                tmp_command_item.put("line", line)
                tmp_command_item.put("function", function)
                tmp_command.put(tmp_command_item)
                break
            }
            if (i == json.length() - 1) {
                // 기존에 있던 url이 아닌 경우
                var tmp_item = JSONObject()
                tmp_item.put("url", url)
                var tmp_command = JSONArray()
                var tmp_command_item = JSONObject()
                tmp_command_item.put("line", line)
                tmp_command_item.put("function", function)

                tmp_command.put(tmp_command_item)
                tmp_item.put("command", tmp_command)
                json.put(tmp_item)

            }
        }

        with(sharedPref.edit()) {
            putString("command", json.toString())
            commit()
        }
    }

    fun getHostPartInUrl(url: String): String {
        var res = url.substring(url.indexOf("://") + 3)
//        url = url.substring(url.indexOf(".")+1,url.indexOf("/"))
        res = res.substring(0, res.indexOf("/"))
        Log.e("asdf", res)
        return res
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
