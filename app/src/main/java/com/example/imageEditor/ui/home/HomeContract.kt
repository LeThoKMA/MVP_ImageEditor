package com.example.imageEditor.ui.home

import com.example.imageEditor.base.BasePresenter
import com.example.imageEditor.model.CollectionModel

interface HomeContract {
    interface View {
        fun setupCollections(list: List<CollectionModel>)

        fun gotoAuthorize()
    }

    interface Presenter : BasePresenter<View> {
        fun getCollections(page: Int)

        fun likeImage(id: String)
    }
}
