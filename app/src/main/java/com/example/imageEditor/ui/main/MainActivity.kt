package com.example.imageEditor.ui.main

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import com.example.imageEditor.R
import com.example.imageEditor.base.BaseActivity
import com.example.imageEditor.databinding.ActivityMainBinding
import com.example.imageEditor.ui.create.CreateImageFragment
import com.example.imageEditor.ui.favourite.FavouriteFragment
import com.example.imageEditor.ui.home.HomeFragment
import com.example.imageEditor.ui.search.SearchFragment
import com.example.imageEditor.utils.AUTHORIZE_DATA
import com.example.imageEditor.utils.CREATE_INDEX
import com.example.imageEditor.utils.FAVORITE_INDEX
import com.example.imageEditor.utils.HOME_INDEX
import com.example.imageEditor.utils.SEARCH_INDEX

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val mMainViewPagerAdapter by lazy {
        MainViewPagerAdapter(
            supportFragmentManager,
            lifecycle,
        )
    }
    private val mAuthorizeData by lazy {
        intent.getStringExtra(AUTHORIZE_DATA)
    }
    private val mHomeFragment by lazy { HomeFragment() }
    private val mSearchFragment by lazy { SearchFragment() }
    private val mCreateImageFragment by lazy { CreateImageFragment() }
    private val mFavouriteFragment by lazy {
        FavouriteFragment.newInstance(mAuthorizeData ?: "")
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val launcher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!permissions.getOrDefault(Manifest.permission.POST_NOTIFICATIONS, false)) {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        startActivity(intent)
                    }
                }
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.CAMERA,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                ),
            )
        } else {
            launcher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ),
            )
        }
        setupPager()
    }

    override fun initListener() {
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    binding.pager2.currentItem = HOME_INDEX
                    true
                }

                R.id.search -> {
                    binding.pager2.currentItem = SEARCH_INDEX
                    true
                }

                R.id.add -> {
                    binding.pager2.currentItem = CREATE_INDEX
                    true
                }

                R.id.favourite -> {
                    binding.pager2.currentItem = FAVORITE_INDEX
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun setupPager() {
        mMainViewPagerAdapter.addFragment(mHomeFragment)
        mMainViewPagerAdapter.addFragment(mSearchFragment)
        mMainViewPagerAdapter.addFragment(mCreateImageFragment)
        mMainViewPagerAdapter.addFragment(mFavouriteFragment)
        binding.pager2.offscreenPageLimit = mMainViewPagerAdapter.itemCount
        binding.pager2.adapter = mMainViewPagerAdapter
        binding.pager2.isUserInputEnabled = false // disable swiping
    }
}
