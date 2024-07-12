package com.example.imageEditor.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.core.view.forEachIndexed
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imageEditor.R
import com.example.imageEditor.base.BaseFragment
import com.example.imageEditor.databinding.FragmentSearchBinding
import com.example.imageEditor.model.PhotoSearchModel
import com.example.imageEditor.model.QueryModel
import com.example.imageEditor.repository.SearchRepository
import com.example.imageEditor.ui.detail.ImageDetailActivity
import com.example.imageEditor.ui.search.adapter.SearchImageAdapter
import com.example.imageEditor.ui.search.adapter.SearchItemCallback
import com.example.imageEditor.ui.search.adapter.SearchTextAdapter
import com.example.imageEditor.utils.URL
import com.example.imageEditor.utils.setSpanForString

class SearchFragment :
    BaseFragment<FragmentSearchBinding>(),
    SearchContract.View,
    SearchItemCallback {
    private val mPresenter by lazy { SearchPresenter(SearchRepository.getInstance(this)) }
    private val mAdapter by lazy {
        SearchImageAdapter(onClickImage = {
            val intent = Intent(requireContext(), ImageDetailActivity::class.java)
            intent.putExtra(URL, it)
            startActivity(intent)
        })
    }

    private var mQueryList = mutableListOf<QueryModel>()
    private val mSearchTextAdapter by lazy {
        SearchTextAdapter(this, mQueryList)
    }
    private var mPageQuery = 0

    override fun getViewBinding(inflater: LayoutInflater): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
        mPresenter.setView(this)
        binding?.rbShop?.text =
            setSpanForString(
                getString(R.string.shop),
                resources.getDrawable(R.drawable.ic_shop, null),
            )

        binding?.recycleView?.adapter = mAdapter

        binding?.recycleViewSearch?.adapter = mSearchTextAdapter
    }

    override fun initData() {
        mPresenter.searchPhotos()
        mPresenter.getAllQueryFromLocal()
    }

    override fun initListener() {
        binding?.recycleView?.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int,
                ) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount &&
                        firstVisibleItemPosition >= 0 && totalItemCount >= mAdapter.currentList.size
                    ) {
                        mPageQuery++
                        mPresenter.searchPhotos(mPageQuery)
                    }
                }
            },
        )
        binding?.radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            setBackgroundRadioButton(checkedId)
            mAdapter.submitList(null)
            when (checkedId) {
                R.id.rbIgtv -> {
                    mPresenter.getAllQueryFromLocal()
                    mPresenter.searchPhotos(query = getString(R.string.igtv))
                }

                R.id.rbShop -> {
                    mPresenter.searchPhotos(query = getString(R.string.shop))
                }

                R.id.rbStyle -> {
                    mPresenter.searchPhotos(query = getString(R.string.style))
                }

                R.id.rbSports -> {
                    mPresenter.searchPhotos(query = getString(R.string.sports))
                }

                R.id.rbAuto -> {
                    mPresenter.searchPhotos(query = getString(R.string.auto))
                }
            }
        }

        binding?.searchView?.setOnQueryTextListener(
            object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!query.isNullOrBlank()) {
                        mAdapter.submitList(null)
                        mPresenter.searchPhotos(query = query)
                        mPresenter.saveQueryToLocal(query)
                        binding?.searchView?.isIconified = true
                        val inputMethodManager =
                            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(
                            binding?.searchView?.windowToken,
                            0,
                        )
                        binding?.searchView?.clearFocus()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            },
        )

        binding?.searchView?.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                binding?.recycleViewSearch?.visibility = View.GONE
            } else {
                mSearchTextAdapter.updateDataList(mPresenter.listQuery)
                binding?.recycleViewSearch?.visibility = View.VISIBLE
            }
        }
    }

    private fun setBackgroundRadioButton(id: Int) {
        binding?.radioGroup?.forEachIndexed { index, view ->
            view.background =
                if (view.id == id) {
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_radio_picking,
                    )
                } else {
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_radio_unpick)
                }
        }
    }

    override fun setupData(data: PhotoSearchModel?) {
        data?.photoModels?.let {
            val newList = mAdapter.currentList.toMutableList()
            newList.addAll(it)
            mAdapter.submitList(newList)
        }
    }

    override fun deleteItemQuery(
        id: Long,
        position: Int,
    ) {
        mQueryList.removeAt(position)
        mSearchTextAdapter.notifyDataSetChanged()
        mPresenter.deleteQuery(id)
    }

    override fun selectQuery(query: String) {
        mAdapter.submitList(null)
        mPresenter.searchPhotos(query = query)
    }
}
