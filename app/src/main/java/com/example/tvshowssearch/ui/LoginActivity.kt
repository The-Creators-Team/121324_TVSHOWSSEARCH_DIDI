package com.example.tvshowssearch.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tvshowssearch.R
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.facebook.login.widget.LoginButton

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var forgotPasswordText: TextView
    private val GOOGLE_SIGN_IN_REQUEST_CODE = 9001
    private lateinit var googleSignInOptions: GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        callbackManager = CallbackManager.Factory.create()

        auth = FirebaseAuth.getInstance()

        emailField = findViewById(R.id.email)
        passwordField = findViewById(R.id.password)
        forgotPasswordText = findViewById(R.id.forgot_password_text)

        findViewById<Button>(R.id.signInButton).setOnClickListener {
            signInWithEmail()
        }

        findViewById<Button>(R.id.signUpButton).setOnClickListener {
            signUpWithEmail()
        }

        forgotPasswordText.setOnClickListener { resetPassword() }

        findViewById<Button>(R.id.googleSignInButton).setOnClickListener {
            googleSignIn()
        }

        findViewById<Button>(R.id.facebookSignInButton).setOnClickListener {
            facebookSignIn()
        }


    }

    private fun signInWithEmail() {
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    navigateToMain()
                } else {
                    Toast.makeText(baseContext, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signUpWithEmail() {
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    navigateToMain()
                } else {
                    Toast.makeText(baseContext, "Sign Up Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun resetPassword() {
        val email = emailField.text.toString()

        if (email.isNotEmpty()) {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                    } else {

                        Toast.makeText(this, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
        }
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    private fun facebookSignIn() {
        val loginButton = findViewById<LoginButton>(R.id.facebookSignInButton)
        loginButton.setPermissions("email", "public_profile")
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val token = AccessToken.getCurrentAccessToken()
                val credential = token?.let { FacebookAuthProvider.getCredential(it.token) }
                if (credential != null) {
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                navigateToMain()
                            } else {
                                Toast.makeText(baseContext, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }

            override fun onCancel() {}

            override fun onError(error: FacebookException) {
                Toast.makeText(baseContext, "Authentication Failed.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToMain() {
        val user = auth.currentUser
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(baseContext, "Google Sign In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    navigateToMain()
                } else {
                    Toast.makeText(baseContext, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}