package com.example.tvshowssearch.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.tvshowssearch.data.api.RetrofitInstance
import com.example.tvshowssearch.ui.viewmodel.ShowViewModelFactory
import com.example.tvshowssearch.ui.viewmodel.TVShowViewModel
import com.example.tvshowssearch.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var binding: ActivityMainBinding
    private val tvShowViewModel: TVShowViewModel by viewModels{
        ShowViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        analytics = Firebase.analytics


        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                tvShowViewModel.searchTVShowByName(binding.searchEditText.text.toString())
            } else {
                Toast.makeText(this, "Please enter a show name", Toast.LENGTH_SHORT).show()
            }
        }

        searchTVShow()

        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        if (user == null) {
            navigateToLogin()
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun searchTVShow() {
        tvShowViewModel.tvShow.observe(this, Observer { tvShow ->
            if (tvShow != null) {

                binding.showNameTextView.text = tvShow.name
                binding.premiereDateTextView.text = "Days since premiere: ${tvShow.daysSincePremiere}"


                Glide.with(this)
                    .load(tvShow.url)
                    .into(binding.showImageView)
            } else {

                Toast.makeText(this, "No TV show found", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun logout() {
        auth.signOut()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {

        if (auth.currentUser == null) {
            super.onBackPressed()
        }
    }

}