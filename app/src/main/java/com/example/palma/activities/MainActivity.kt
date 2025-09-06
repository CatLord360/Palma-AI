package com.example.palma.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.palma.databinding.ActivityMainBinding

@Suppress("DEPRECATION")
//START of CLASS: MainActivity
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    //START of FUNCTION: onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@MainActivity, LogActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000)
    }//END of FUNCTION: onCreate
}//END of CLASS: MainActivity