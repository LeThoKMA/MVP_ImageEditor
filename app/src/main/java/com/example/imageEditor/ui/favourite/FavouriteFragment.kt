package com.example.imageEditor.ui.favourite

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
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
    private val favouriteViewModel: FavouriteViewModel by viewModels()
    private val mAdapter by lazy {
        FavoriteAdapter(onLock = {
            //Khóa ảnh
            favouriteViewModel.lockFile(it)
        }, onUnlock = {
            // Mở khóa ảnh
            favouriteViewModel.unLockFile(it)
        })
    }


    override fun getViewBinding(inflater: LayoutInflater): FragmentFavouriteBinding {
        return FragmentFavouriteBinding.inflate(inflater)
    }

    override fun initView() {
        binding?.recycleViewFavorite?.adapter = mAdapter

    }

    override fun initData() {
        favouriteViewModel.data.observe(viewLifecycleOwner) {
            mAdapter.submitList(it)
        }
        favouriteViewModel.message.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun initListener() {
        binding?.swipeRefresh?.setOnRefreshListener {
            binding?.swipeRefresh?.isRefreshing = false
        }
    }

    override fun setFavoriteList(data: List<PhotoModel>) {
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
    }

    override fun dislikeImage(id: String) {
    }

    override fun clickDetailImage(url: String) {
        val intent = Intent(requireContext(), ImageDetailActivity::class.java)
        intent.putExtra(URL, url)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        favouriteViewModel.getData()
    }

    override fun onPause() {
        super.onPause()
    }

    companion object {
        private const val DATA = "AuthorizeData"

        @JvmStatic
        fun newInstance(): FavouriteFragment =
            FavouriteFragment()
    }
}
