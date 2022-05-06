package com.example.quizapp.fragments.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.remote.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountService,
) : ViewModel() {

    private val _loginState = Channel<LoginUiState>()
    val loginState: Flow<LoginUiState> = _loginState.receiveAsFlow()

    fun login(
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            try {
                _loginState.send(LoginUiState(isLoading = true))
                val result = accountService.login(email, password)
                _loginState.send(LoginUiState(data = result))
            } catch (e: Exception) {
                _loginState.send(LoginUiState(error = e.localizedMessage))

            }
        }
    }

}