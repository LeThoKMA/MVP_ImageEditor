package com.example.imageEditor.ui.favourite

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imageEditor.R
import com.example.imageEditor.base.BaseFragment
import com.example.imageEditor.databinding.FragmentFavouriteBinding
import com.example.imageEditor.model.PhotoModel
import com.example.imageEditor.model.response.AuthorizeResponse
import com.example.imageEditor.repository.FavoriteRepository
import com.example.imageEditor.ui.detail.ImageDetailActivity
import com.example.imageEditor.ui.favourite.adapter.FavoriteAdapter
import com.example.imageEditor.ui.favourite.adapter.OnClickImage
import com.example.imageEditor.ui.splash.AuthorizeActivity
import com.example.imageEditor.utils.URL
import com.google.gson.Gson

class FavouriteFragment :
    BaseFragment<FragmentFavouriteBinding>(),
    FavouriteContract.View,
    OnClickImage {
    private val mPresenter by lazy { FavouritePresenter(FavoriteRepository.getInstance(this)) }
    private val mAdapter by lazy { FavoriteAdapter(this) }
    private var mPageQuery = 1
    private val mNameUser by lazy {
        Gson().fromJson(
            this.arguments?.getString(DATA),
            AuthorizeResponse::class.java,
        ).username
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentFavouriteBinding {
        return FragmentFavouriteBinding.inflate(inflater)
    }

    override fun initView() {
        mPresenter.setView(this)
        binding?.recycleViewFavorite?.adapter = mAdapter
    }

    override fun initData() {
    }

    override fun initListener() {
        binding?.recycleViewFavorite?.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int,
                ) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager: LinearLayoutManager =
                        recyclerView.layoutManager as LinearLayoutManager
                    if (dy > 0 && linearLayoutManager.findLastCompletelyVisibleItemPosition() == mAdapter.currentList.size - 1) {
                        mPageQuery++
                        mPresenter.getFavoriteList(mNameUser, mPageQuery)
                    }
                }
            },
        )
    }

    override fun setFavoriteList(data: List<PhotoModel>) {
        if (!mAdapter.currentList.containsAll(data)) {
            val newList = mAdapter.currentList.toMutableList()
            newList.addAll(data)
            if (newList.isEmpty()) {
                binding?.tvEmpty?.visibility = View.VISIBLE
            } else {
                mAdapter.submitList(newList)
            }
        }
    }

    override fun onFailure() {
        requireActivity().runOnUiThread {
            Toast.makeText(requireActivity(), getString(R.string.un_authorize), Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(requireActivity(), AuthorizeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun likeImage(id: String) {
        mPresenter.likeImage(id)
    }

    override fun dislikeImage(id: String) {
        mPresenter.dislikeImage(id)
    }

    override fun clickDetailImage(url: String) {
        val intent = Intent(requireContext(), ImageDetailActivity::class.java)
        intent.putExtra(URL, url)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        mPresenter.getFavoriteList(mNameUser)
    }

    override fun onPause() {
        super.onPause()
        mPageQuery = 1
        mAdapter.submitList(null)
        mAdapter.resetStateList()
    }

    companion object {
        private const val DATA = "AuthorizeData"

        @JvmStatic
        fun newInstance(authorizeResponse: String): FavouriteFragment =
            FavouriteFragment().apply {
                arguments = bundleOf(Pair(DATA, authorizeResponse))
            }
    }
}
