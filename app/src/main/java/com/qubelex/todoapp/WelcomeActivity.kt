package com.qubelex.todoapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.qubelex.todoapp.common.Constant
import com.qubelex.todoapp.databinding.ActivityWelcomeBinding
import com.qubelex.todoapp.ui.MainActivity


class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //hide actionbar
        supportActionBar?.hide()

        //start splash screen logo animation
        val fadeInAnim = AnimationUtils.loadAnimation(this,R.anim.fade_in_anim)
        binding.splashLogo.startAnimation(fadeInAnim)

        //wait for 4 second
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        },Constant.SPLASH_SCREEN_TIME)


    }
}
