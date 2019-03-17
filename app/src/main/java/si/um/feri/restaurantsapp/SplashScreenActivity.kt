package si.um.feri.restaurantsapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashScreenActivity : AppCompatActivity() {

    private var delayHandler: Handler? = null
    @Suppress("PrivatePropertyName")
    private val SPLASH_DELAY: Long = 2500 // 2.5 seconds

    private val runnable: Runnable = Runnable {
        if (!isFinishing) {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        delayHandler = Handler()
        delayHandler!!.postDelayed(runnable, SPLASH_DELAY)
    }

    public override fun onDestroy() {
        if (delayHandler != null) {
            delayHandler!!.removeCallbacks(runnable)
        }

        super.onDestroy()
    }
}
