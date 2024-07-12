package com.example.imageEditor.ui.splash

import com.example.imageEditor.base.BasePresenter
import com.example.imageEditor.model.response.AuthorizeResponse

interface AuthorizeContract {
    interface View {
        fun authorized(data: AuthorizeResponse)
    }

    interface Presenter : BasePresenter<View> {
        fun authorize(authorizationCode: String)
    }
}
