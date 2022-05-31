package com.developer.quizapp.utils


import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey


object Constants {
    const val TAG = "GAMALRAGAB"
    const val ACTION_LOGIN_FRAGMENT_AFTER_LOGOUT: String = "ACTION_LOGIN_FRAGMENT_AFTER_LOGOUT"

    const val HOLDER_ICON = "https://cdn-icons-png.flaticon.com/512/149/149071.png"

    const val USERS_INFO_FILE: String = "USER_INFO"
    const val REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSIONS: Int = 123
    const val USERS = "users"


    /************ Complex Preference ***********/
    const val PREF_FILE = "VODO_APP_PREF"
    const val MODE_PRIVATE = 0


    /************ User Flag ***********/
    const val IS_LOGIN = "IS_LOGIN"
    const val USER_OBJECT_LOCAL="USER_OBJECT_LOCAL"

}