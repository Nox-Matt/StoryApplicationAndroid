package com.dicoding.picodiploma.loginwithanimation.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.response.ErrorResponse
import com.dicoding.picodiploma.loginwithanimation.response.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException


class SignupViewModel(private val repository: UserRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<RegisterResponse>()
    val registerResult: LiveData<RegisterResponse> = _registerResult

    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.registerUser(name, email, password)
                _registerResult.value = response
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.message
                _registerResult.value = RegisterResponse(error = true, message = errorMessage)
            } catch (e: Exception) {
                _registerResult.value = RegisterResponse(error = true, message = e.message)
            }
        }
    }
}