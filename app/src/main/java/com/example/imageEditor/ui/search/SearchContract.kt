package com.example.imageEditor.ui.search

import com.example.imageEditor.base.BasePresenter
import com.example.imageEditor.model.PhotoSearchModel

interface SearchContract {
    interface View {
        fun setupData(data: PhotoSearchModel?)
    }

    interface Presenter : BasePresenter<View> {
        fun searchPhotos(
            page: Int = 0,
            query: String? = null,
        )

        fun saveQueryToLocal(query: String)

        fun deleteQuery(id: Long)

        fun getAllQueryFromLocal()
    }
}
