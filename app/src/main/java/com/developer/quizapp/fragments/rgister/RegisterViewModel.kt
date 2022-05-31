package com.developer.quizapp.fragments.rgister

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.developer.quizapp.data.remote.AccountService
import com.developer.quizapp.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val accountService: AccountService,
) : ViewModel() {

    private val _registerState = Channel<RegisterUiState>()
    val registerStateRegister: Flow<RegisterUiState> = _registerState.receiveAsFlow()


    fun register(
        user: User,
        password: String,
        imageUserProfile: String?,
        ) {

        viewModelScope.launch {
            try {
                _registerState.send(RegisterUiState(isLoading = true))
                val result = accountService.register(user.username,user.email,password,user.role,imageUserProfile)
                _registerState.send(RegisterUiState(data = result))
            } catch (e: Exception) {
                _registerState.send(RegisterUiState(error = e.localizedMessage))
            }
        }
    }


}