package id.fs.dia_padcv.ui.utils

import id.fs.dia_padcv.data.remote.api.UserData

sealed class LoginResult {
    data class Success(val user: UserData) : LoginResult()
    object InvalidCredentials : LoginResult()
    object NetworkError : LoginResult()
}
