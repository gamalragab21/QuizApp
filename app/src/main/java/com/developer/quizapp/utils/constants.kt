package com.developers.healtywise.common.helpers.utils


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


    /*
     * for data store
     */
    val USER_TOKEN = stringPreferencesKey("USER_TOKEN")

    val USER_ID = stringPreferencesKey("USER_ID")
    val USER_NAME = stringPreferencesKey("USER_FIRST_NAME")
    val USER_EMAIL1 = stringPreferencesKey("USER_EMAIL1")
    val USER_IMAGE = stringPreferencesKey("USER_IMAGE")
    val USER_ADMIN = booleanPreferencesKey("USER_ADMIN")


}