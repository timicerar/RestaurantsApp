package si.um.feri.restaurantsapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.Runnable

private const val SPLASH_DELAY: Long = 1500 // 1.5 seconds
private const val ACTIVITY = "activity"
private const val LOGIN_ACTIVITY = -1
private const val MAIN_ACTIVITY = 1

class LoadingSpinnerActivity : AppCompatActivity() {

    private var delayHandler: Handler? = null

    private val runnableMainActivity: Runnable = Runnable {
        if (!isFinishing) {
            val whichActivity = intent.getIntExtra(ACTIVITY, 0)

            if (whichActivity == MAIN_ACTIVITY) {
                intent = Intent(applicationContext, MainActivity::class.java)
            } else if (whichActivity == LOGIN_ACTIVITY)
                intent = Intent(applicationContext, LoginActivity::class.java)

            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_spinner)

        delayHandler = Handler()
        delayHandler!!.postDelayed(runnableMainActivity, SPLASH_DELAY)
    }

    public override fun onDestroy() {
        if (delayHandler != null) {
            delayHandler!!.removeCallbacks(runnableMainActivity)
        }

        super.onDestroy()
    }
}
