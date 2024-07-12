package com.example.imageEditor.ui.search

import com.example.imageEditor.model.QueryModel
import com.example.imageEditor.repository.SearchRepository
import com.example.imageEditor.utils.ALL

class SearchPresenter(private val searchRepository: SearchRepository) : SearchContract.Presenter {
    private var mQuery = ALL
    private var mView: SearchContract.View? = null
    private var mListQuery = mutableListOf<QueryModel>()
    val listQuery get() = mListQuery.toList()

    override fun searchPhotos(
        page: Int,
        query: String?,
    ) {
        query?.let { mQuery = it }
        searchRepository.searchPhotos(page, mQuery) {
            mView?.setupData(it)
        }
    }

    override fun saveQueryToLocal(query: String) {
        searchRepository.saveQueryToLocal(query) {
            mListQuery.clear()
            mListQuery.addAll(it)
        }
    }

    override fun deleteQuery(id: Long) {
        searchRepository.deleteQuery(id) {
            mListQuery.clear()
            mListQuery.addAll(it)
        }
    }

    override fun getAllQueryFromLocal() {
        searchRepository.getAllQuery {
            mListQuery.clear()
            mListQuery.addAll(it)
        }
    }

    override fun onStart() {
        TODO("Not yet implemented")
    }

    override fun onStop() {
        TODO("Not yet implemented")
    }

    override fun setView(view: SearchContract.View?) {
        this.mView = view
    }
}
