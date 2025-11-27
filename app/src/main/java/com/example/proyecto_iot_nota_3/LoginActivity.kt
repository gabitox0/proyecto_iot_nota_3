package com.example.proyecto_iot_nota_3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_iot_nota_3.databinding.ActivityLoginBinding
// Importaciones necesarias para Firebase Auth y Google Sign-In
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.example.proyecto_iot_nota_3.HomeActivity // <--- ¡IMPORTACIÓN FALTANTE AÑADIDA!

/**
 * Activity responsable de manejar la interfaz de inicio de sesión de la aplicación.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    // Variables para Google Sign-In
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001 // Código de solicitud arbitrario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // 1. Configuración de Google Sign-In
        // Esto asume que R.string.default_web_client_id existe y es correcto.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setupListeners()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun setupListeners() {

        // 1. Botón de Ingresar (con Email y Contraseña)
        binding.btnLogin.setOnClickListener {
            loginWithEmail()
        }

        // 2. Botón/Texto para Crear Cuenta -> ¡NAVEGA A RegisterActivity!
        binding.btnCrearCuenta.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 3. Botón/Texto para Recuperar Contraseña -> ¡NAVEGACIÓN A ForgotPasswordActivity!
        binding.btnRecuperarClave.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // 4. Botón para Iniciar Sesión con Google -> ¡EJECUTA LA FUNCIÓN REAL!
        binding.tvGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }
    }

    // =========================================================================
    // Métodos de Google Sign-In
    // =========================================================================

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("LoginActivity", "Google Sign-In falló", e)
                Toast.makeText(this, "Fallo de Google Sign-In. Código: ${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    updateUI(auth.currentUser)
                } else {
                    Toast.makeText(this, "Fallo de autenticación con Firebase (Google).", Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
            }
    }

    // =========================================================================
    // Métodos de Email/Password (se mantienen)
    // =========================================================================

    private fun loginWithEmail() {
        val email = binding.etLoginEmail.text.toString().trim()
        val password = binding.etLoginPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa ambos campos (Email y Contraseña).", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    updateUI(auth.currentUser)
                } else {
                    Toast.makeText(this, "Fallo de autenticación: Verifica tu correo y contraseña.",
                        Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
            }
    }

    // =========================================================================
    // Método de Navegación
    // =========================================================================

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Navegación al Home
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}