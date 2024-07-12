package com.example.imageEditor.ui.favourite

import com.example.imageEditor.base.BasePresenter
import com.example.imageEditor.model.PhotoModel

interface FavouriteContract {
    interface View {
        fun setFavoriteList(data: List<PhotoModel>)

        fun onFailure()
    }

    interface Presenter : BasePresenter<View> {
        fun getFavoriteList(
            name: String,
            page: Int = 1,
        )

        fun likeImage(id: String)

        fun dislikeImage(id: String)
    }
}
