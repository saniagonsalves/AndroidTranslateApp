package com.firstapp.androidtranslate

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Exception
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val text: EditText = findViewById(R.id.text)
        val fromLangCode: EditText = findViewById(R.id.from_lang)
        val toLangCode: EditText = findViewById(R.id.to_lang)
        val btnTranslate: Button = findViewById(R.id.btnTranslate)
        val res: TextView = findViewById(R.id.translated_text)

        btnTranslate.setOnClickListener {
            val translate = Translate()
            val x = translate.execute(
                text.text.toString(),
                fromLangCode.text.toString(),
                toLangCode.text.toString()
            )
            res.text = x.get()
        }
    }

    class Translate : AsyncTask<String?, String?, String>() {

        override fun doInBackground(vararg params: String?): String {
            val strArr = params as Array<String>
            val str = ""
            try {
                val encode = URLEncoder.encode(strArr[0], "utf-8")
                val sb = StringBuilder()
                .append("https://translate.googleapis.com/translate_a/single?client=gtx&sl=")
                .append(strArr[1])
                .append("&tl=")
                .append(strArr[2])
                .append("&dt=t&q=")
                .append(encode)

                Log.i("doInBackground", sb.toString())
                val execute = DefaultHttpClient().execute(HttpGet(sb.toString()))
                val statusLine = execute.statusLine
                if (statusLine.statusCode == 200) {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    execute.entity.writeTo(byteArrayOutputStream)
                    val byteArrayOutputStream2 = byteArrayOutputStream.toString()
                    byteArrayOutputStream.close()
                    val jSONArray = JSONArray(byteArrayOutputStream2).getJSONArray(0)
                    var str2 = str
                    for (i in 0 until jSONArray.length()) {
                        val jSONArray2 = jSONArray.getJSONArray(i)
                        val sb2 = java.lang.StringBuilder()
                        sb2.append(str2)
                        sb2.append(jSONArray2[0].toString())
                        str2 = sb2.toString()
                    }
                    return str2
                }
                execute.entity.content.close()
                throw IOException(statusLine.reasonPhrase)
            } catch (e: Exception) {
                Log.e("doInBackground","ERROR" , e)
                return "ERROR";
            }
        }

        override fun onPostExecute(result: String) {
            Log.i("onPostExecute", result)
        }
    }
}