package com.dicoding.picodiploma.loginwithanimation.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val response = repository.loginUser(email, password)
            if (response.error != true) {
                val token = response.loginResult?.token ?: ""
                if (token.isNotBlank()) {
                    saveSession(UserModel(email, token))
                    true
                } else {
                    _errorMessage.postValue("Token is empty")
                    false
                }
            } else {
                _errorMessage.postValue(response.message ?: "Unknown error")
                false
            }
        } catch (e: HttpException) {
            _errorMessage.postValue("HTTP Error: ${e.message}")
            false
        } catch (e: Exception) {
            _errorMessage.postValue("An unexpected error occurred: ${e.message}")
            false
        }
    }


    fun getSession(): Flow<UserModel> {
        return repository.getSession()
    }
}