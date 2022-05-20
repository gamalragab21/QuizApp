package com.developer.quizapp.data.local.dataStore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.developers.healtywise.common.helpers.utils.Constants.HOLDER_ICON
import com.developers.healtywise.common.helpers.utils.Constants.USERS_INFO_FILE
import com.developers.healtywise.common.helpers.utils.Constants.USER_ADMIN

import com.developers.healtywise.common.helpers.utils.Constants.USER_EMAIL1
import com.developers.healtywise.common.helpers.utils.Constants.USER_ID
import com.developers.healtywise.common.helpers.utils.Constants.USER_IMAGE

import com.developers.healtywise.common.helpers.utils.Constants.USER_NAME
import com.developers.healtywise.common.helpers.utils.Constants.USER_TOKEN
import com.developer.quizapp.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


private val Context.dataStore by preferencesDataStore(USERS_INFO_FILE)


class DataStoreManager constructor(appContext: Context) {

    private val dataStoreManger = appContext.dataStore

//       private val scope = CoroutineScope(Job() + Dispatchers.Main)
//    private val _userInfoFlow = MutableLiveData<String>()
//    private val _currentMethod = MutableLiveData<String>()

    // For public variables, prefer use LiveData just to read values.
//    val userInfoFlow: LiveData<String> get() = _userInfoFlow
//    val currentMethod: LiveData<String> get() = _currentMethod


    // generic values

    suspend fun<T> putValue(key:Preferences.Key<T>,value:T){
        dataStoreManger.edit { preferences ->
            preferences[key] = value
        }
    }

    fun<T> getValue(key:Preferences.Key<T>): Flow<T?> = dataStoreManger.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences[key]
    }

    suspend fun setToken(token: String) {
        dataStoreManger.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }


    suspend fun saveUserProfile(user: User) = dataStoreManger.edit { preferences ->
        preferences[USER_ID] = user.userId
        preferences[USER_NAME] = user.username
        preferences[USER_EMAIL1] = user.email
        preferences[USER_ADMIN] = user.admin
        preferences[USER_IMAGE] = user.imageProfile

    }

    suspend fun logOut()=dataStoreManger.edit {
            it.clear()
        }



    fun getUserTokenORId(): Flow<String> = dataStoreManger.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences[USER_TOKEN] ?: ""
    }


    fun getUserProfile(): Flow<User> = dataStoreManger.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        User(
            userId = preferences[USER_ID]?:"",
            username = preferences[USER_NAME] ?: "",
            email = preferences[USER_EMAIL1] ?: "",
            imageProfile = preferences[USER_IMAGE] ?: HOLDER_ICON,
            admin  =  preferences[USER_ADMIN] ?:true
        )

    }


}