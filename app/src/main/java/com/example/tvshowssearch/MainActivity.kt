package com.example.tvshowssearch

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tvshowssearch.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private val tvShowViewModel: TVShowViewModel by viewModels{
        ShowViewModelFactory(RetrofitInstance.retrofitService)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchTVShow(query)
            } else {
                Toast.makeText(this, "Please enter a show name", Toast.LENGTH_SHORT).show()
            }
        }

        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        if (user == null) {
            navigateToLogin()
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun searchTVShow(query: String) {
        tvShowViewModel.searchShowByName(query).observe(this, Observer { tvShow ->
            if (tvShow != null) {

                binding.showNameTextView.text = tvShow.name
                binding.premiereDateTextView.text = "Days since premiere: ${tvShow.daysSincePremiere}"


                Glide.with(this)
                    .load(tvShow.image?.medium)
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