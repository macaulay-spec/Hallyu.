package app.hallyu.ui.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.hallyu.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.login(email, pass)
            _isLoading.value = false
            handleResult(result, "Login failed")
        }
    }

    fun signup(email: String, pass: String, username: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.signup(email, pass, username)
            _isLoading.value = false
            handleResult(result, "Signup failed")
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.signInWithGoogle(idToken)
            _isLoading.value = false
            handleResult(result, "Google Sign-In failed")
        }
    }

    fun signInWithApple(activity: Activity) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.signInWithApple(activity)
            _isLoading.value = false
            handleResult(result, "Apple Sign-In failed")
        }
    }

    fun setError(message: String) {
        _error.value = message
    }

    private fun handleResult(result: Result<Unit>, defaultErrorMessage: String) {
        if (result.isSuccess) {
            _isSuccess.value = true
        } else {
            // Check for configuration error
            val msg = result.exceptionOrNull()?.message ?: defaultErrorMessage
            _error.value = if (msg.contains("CONFIGURATION_NOT_FOUND") || msg.contains("12500")) {
                "Firebase Auth is not enabled in your Firebase Console. Please enable Email/Password, Google, and Apple in the Firebase Console."
            } else {
                msg
            }
        }
    }
}
