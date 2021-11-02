package com.example.flashscores_nightlight.ui.home

import android.R.attr.orientation
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import com.example.flashscores_nightlight.DataHolder
import com.example.flashscores_nightlight.R
import com.example.flashscores_nightlight.R.layout
import org.jsoup.Jsoup


class HomeFragment : Fragment() {
    private val LOG_TAG: String = "HomeFragmentLogs";
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var webView: WebView
    private lateinit var status: TextView
    lateinit var mContext: Context
    var orientation: Int? = null
    val handler = Handler(Looper.getMainLooper())


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?


    ): View? {
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(layout.fragment_home, container, false)
       // val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            ///textView.text = it
        })

        orientation = this.resources.configuration.orientation

        status = root.findViewById(R.id.status)

        webView = root.findViewById(R.id.webView)
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = false
        webView.setInitialScale(350)
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.66 Mobile Safari/537.36")
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.allowFileAccess = true
        webView.settings.domStorageEnabled = true
        webView.canGoBack()
        webView.addJavascriptInterface(MyJavaScriptInterface(), "HtmlHandler")
        webView.loadUrl("https://www.flashscore.com/favorites/")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(
                view: WebView,
                url: String,
                favicon: Bitmap?
            ) {
                status.setText(getString(R.string.loading))

            }

            override fun onPageFinished(view: WebView, url: String) {
                status.setText(getString(R.string.select_matches_rotate))
//                webView.run {
//                    loadUrl("javascript:window.HtmlHandler.handleHtml" +
//                                    "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
//                }
                if(webView.url == "https://www.flashscore.com/favorites/") {
                    Log.d(LOG_TAG, "webView.url == \"https://www.flashscore.com/favorites/\"")
                    val handler2 = Handler(Looper.getMainLooper())
                    handler2.postDelayed(object : Runnable {
                        override fun run() {
                            webView.run {
                                loadUrl("javascript:window.HtmlHandler.handleHtml" +
                                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
                            }
                            //handler.postDelayed(this, 1000)
                        }
                    },1000)
                }
                else{
                    Log.d(LOG_TAG, "huynja")
                }
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    periodicTableUpdate()
                }

            }
        }






        return root
    }


    private inner class MyJavaScriptInterface
    {
        @JavascriptInterface
        fun showToast(text: String?)
        {
            Log.d("WEBVIEW", "text");
        }
        @JavascriptInterface
        fun handleHtml(html: String?) {
            Log.d("MyJavaScriptInterface", "fun handleHtml started")
            // Use jsoup on this String here to search for your content.
            DataHolder.doc = Jsoup.parse(html)
            DataHolder.parcePage()

            (activity as AppCompatActivity?)!!.runOnUiThread {
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                Log.d(LOG_TAG, "orientation = " + "ORIENTATION_LANDSCAPE")
        //                        table = findViewById(R.id.table)
        //                        setDataToUI()
                    status.setText("ORIENTATION_LANDSCAPE")
                } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                               Log.d(LOG_TAG, "orientation = " + "ORIENTATION_PORTRAIT")
//                    status.setText("ORIENTATION_PORTRAIT")
                }
            }
        }
    }


    fun periodicTableUpdate() {
        //if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(LOG_TAG, "orientation = " + "ORIENTATION_LANDSCAPE")
            // Define the code block to be executed
            //val handler = Handler(Looper.getMainLooper())
            handler.postDelayed(object : Runnable {
                override fun run() {
                    webView.run {
                   loadUrl("javascript:window.HtmlHandler.handleHtml" +
                                    "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
                }


                    handler.postDelayed(this, 1000)
                }
            },2000)

        //}
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

}
