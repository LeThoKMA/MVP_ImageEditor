package com.example.imageEditor.ui.home

import com.example.imageEditor.repository.HomeRepository

class HomePresenter(private val homeRepository: HomeRepository) : HomeContract.Presenter {
    private var mView: HomeContract.View? = null

    override fun getCollections(page: Int) {
        homeRepository.getCollections(page) { data ->
            data?.let { mView?.setupCollections(it) }
        }
    }

    override fun likeImage(id: String) {
        homeRepository.likeImage(id) {
            mView?.gotoAuthorize()
        }
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun setView(view: HomeContract.View?) {
        this.mView = view
    }
}
