package com.example.flashscores_nightlight

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.flashscores_nightlight.DataHolder.appDatasetMatch
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.jsoup.Jsoup


class MainActivity : AppCompatActivity() {
    private val LOG_TAG: String = "MainActivityLogs";
    var orientation: Int? = null
    lateinit var webView: WebView
    lateinit var status: TextView
    lateinit var table: TableLayout
    val handler_land = Handler(Looper.getMainLooper())
    //lateinit var constraintLayout: ConstraintLayout

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orientation = this.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // remove title
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            setContentView(R.layout.activity_main)
            table = findViewById<TableLayout>(R.id.table)

            status = findViewById(R.id.status)

            webView = findViewById(R.id.webView)
            val webSettings = webView.settings
            webSettings.javaScriptEnabled = true
            webView.setInitialScale(350)
            webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.66 Mobile Safari/537.36")
            webView.settings.domStorageEnabled = true
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
                    handler_land.postDelayed(object : Runnable {
                        override fun run() {
                            webView.run {
                                loadUrl(
                                    "javascript:window.HtmlHandler.handleHtml" +
                                            "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"
                                )
                            }
                            handler_land.postDelayed(this, 1000)
                        }
                    }, 1000)
                }
            }
        }

        else{
            setContentView(R.layout.activity_main)
            val navView: BottomNavigationView = findViewById(R.id.nav_view)
            val navController = findNavController(R.id.nav_host_fragment)
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home,
                    R.id.navigation_dashboard,
                    R.id.navigation_notifications,
                    R.id.navigation_settings
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }
        supportActionBar?.hide()
    }

    override fun onBackPressed(){
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        handler_land.removeCallbacksAndMessages(null)
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

            this@MainActivity.runOnUiThread {
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Log.d(LOG_TAG, "orientation = " + "ORIENTATION_LANDSCAPE")
                    setDataToUI()
                } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    Log.d(LOG_TAG, "orientation = " + "ORIENTATION_PORTRAIT")
                }
            }
        }
    }

    fun setDataToUI(){
        // fix later
        status.text = getString(R.string.Updated) + DataHolder.timeNowhms
        table.removeAllViewsInLayout()
        val inflater = LayoutInflater.from(this@MainActivity)
        val numberOfLinesHorizontalTable: Int
        Log.d(LOG_TAG, appDatasetMatch.size.toString())

        numberOfLinesHorizontalTable = Math.min(DataHolder.appDatasetMatch.size, 8)
        for (i in 0 until numberOfLinesHorizontalTable) {
            val tr: TableRow = inflater.inflate(R.layout.tr, container, false) as TableRow
            val event_stage: TextView = tr.findViewById(R.id.event_stage)
            val participant_home: TextView = tr.findViewById(R.id.participant_home)
            val score: TextView = tr.findViewById(R.id.score)
            val participant_away: TextView = tr.findViewById(R.id.participant_away)
            val ball_home: ImageView = tr.findViewById(R.id.ball_home)
            val ball_away: ImageView = tr.findViewById(R.id.ball_away)
            event_stage.setText(DataHolder.appDatasetMatch.get(i).get(6))
            participant_home.setText(DataHolder.appDatasetMatch.get(i).get(2))
            if (DataHolder.appDatasetMatch.get(i).get(3).equals("_fontBold")) {
                participant_home.setTypeface(participant_home.getTypeface(), Typeface.BOLD)
            }
            else if (DataHolder.appDatasetMatch.get(i).get(3).equals("_highlighted")) {
                participant_home.setTextColor(Color.YELLOW)
            }
            participant_away.setText(DataHolder.appDatasetMatch.get(i).get(4))
            if (DataHolder.appDatasetMatch.get(i).get(5).equals("_fontBold")) {
                participant_away.setTypeface(participant_home.getTypeface(), Typeface.BOLD)
            }
            else if (DataHolder.appDatasetMatch.get(i).get(5).equals("_highlighted")) {
                participant_away.setTextColor(Color.YELLOW)
            }

            if (DataHolder.appDatasetMatch.get(i).get(9).equals("serveUndefined")) {
                ball_home.setVisibility(View.INVISIBLE)
                ball_away.setVisibility(View.INVISIBLE)
            }
            else if (DataHolder.appDatasetMatch.get(i).get(9).equals("home_red_card")) {
                ball_home.setImageResource(R.drawable.ic_red_card_vector_very_small)
            }
            else if (DataHolder.appDatasetMatch.get(i).get(9).equals("away_red_card")) {
                ball_away.setImageResource(R.drawable.ic_red_card_vector_very_small)
            }
            else if (DataHolder.appDatasetMatch.get(i).get(9).equals("both_red_card")) {
                ball_home.setImageResource(R.drawable.ic_red_card_vector_very_small)
                ball_away.setImageResource(R.drawable.ic_red_card_vector_very_small)
            }
            else if (DataHolder.appDatasetMatch.get(i).get(9).equals("serveHome")) {
                ball_home.setImageResource(R.drawable.ic_football_icon)
            }
            else if (DataHolder.appDatasetMatch.get(i).get(9).equals("serveAway")) {
                ball_away.setImageResource(R.drawable.ic_football_icon)
            }
            score.setText(DataHolder.appDatasetMatch.get(i).get(7) + ":" + DataHolder.appDatasetMatch.get(i).get(8))

            table.addView(tr)
        }
    }
}
