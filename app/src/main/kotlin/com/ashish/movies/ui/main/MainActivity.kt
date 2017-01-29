package com.ashish.movies.ui.main

import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat.START
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import butterknife.bindView
import com.ashish.movies.R
import com.ashish.movies.data.preferences.PreferenceHelper
import com.ashish.movies.di.components.AppComponent
import com.ashish.movies.ui.base.mvp.MvpActivity
import com.ashish.movies.ui.main.TabPagerAdapter.Companion.CONTENT_TYPE_DISCOVER
import com.ashish.movies.ui.main.TabPagerAdapter.Companion.CONTENT_TYPE_MOVIE
import com.ashish.movies.ui.main.TabPagerAdapter.Companion.CONTENT_TYPE_PEOPLE
import com.ashish.movies.ui.main.TabPagerAdapter.Companion.CONTENT_TYPE_TV_SHOW
import com.ashish.movies.ui.multisearch.MultiSearchActivity
import com.ashish.movies.ui.widget.CircleImageView
import com.ashish.movies.ui.widget.FontTextView
import com.ashish.movies.utils.CustomTypefaceSpan
import com.ashish.movies.utils.FontUtils
import com.ashish.movies.utils.extensions.applyText
import com.ashish.movies.utils.extensions.changeMenuFont
import com.ashish.movies.utils.extensions.changeViewGroupTextFont
import com.ashish.movies.utils.extensions.getColorCompat
import com.ashish.movies.utils.extensions.getStringArray
import com.ashish.movies.utils.extensions.isNotNullOrEmpty
import com.ashish.movies.utils.extensions.loadImageUrl
import com.ashish.movies.utils.extensions.setVisibility
import com.ashish.movies.utils.extensions.showToast
import javax.inject.Inject

class MainActivity : MvpActivity<MainView, MainPresenter>(), MainView {

    @Inject lateinit var preferenceHelper: PreferenceHelper

    private val viewPager: ViewPager by bindView(R.id.view_pager)
    private val tabLayout: TabLayout by bindView(R.id.tab_layout)
    private val drawerLayout: DrawerLayout by bindView(R.id.drawer_layout)
    private val navigationView: NavigationView by bindView(R.id.navigation_view)

    private var nameText: FontTextView? = null
    private var userImage: CircleImageView? = null
    private var userNameText: FontTextView? = null

    private lateinit var toolbarTitles: Array<String>
    private lateinit var movieTabTitles: Array<String>
    private lateinit var tvShowTabTitles: Array<String>
    private lateinit var peopleTabTitles: Array<String>
    private lateinit var discoverTabTitles: Array<String>

    private lateinit var tabPagerAdapter: TabPagerAdapter

    private var tmdbLoginDialog: AlertDialog? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_TransparentStatusBar)
        super.onCreate(savedInstanceState)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        }

        peopleTabTitles = arrayOf(getString(R.string.popular_txt))
        toolbarTitles = getStringArray(R.array.toolbar_title_array)
        movieTabTitles = getStringArray(R.array.movie_list_type_array)
        tvShowTabTitles = getStringArray(R.array.tv_show_list_type_array)
        discoverTabTitles = getStringArray(R.array.discover_type_array)

        initDrawerHeader()
        showViewPagerFragment(CONTENT_TYPE_MOVIE, movieTabTitles)
        tabLayout.setupWithViewPager(viewPager)

        changeTabFont()
        toolbar?.changeViewGroupTextFont()
        setupNavigationView()
    }

    override fun injectDependencies(appComponent: AppComponent) {
        appComponent.plus(MainModule(this)).inject(this)
    }

    override fun getLayoutId() = R.layout.activity_main

    private fun initDrawerHeader() {
        val headerView = navigationView.getHeaderView(0)
        nameText = headerView.findViewById(R.id.name_text) as FontTextView
        userImage = headerView.findViewById(R.id.user_image) as CircleImageView
        userNameText = headerView.findViewById(R.id.user_name_text) as FontTextView

        showUserProfile()
        userImage?.setOnClickListener {
            drawerLayout.closeDrawers()
            Handler().postDelayed({ showTmdbLoginDialog() }, 200L)
        }
    }

    private fun showUserProfile() {
        preferenceHelper.apply {
            val name = getName()
            if (name.isNotNullOrEmpty()) {
                nameText?.applyText(name)
            } else {
                nameText?.setText(R.string.login_with_tmdb)
            }

            val userName = getUserName()
            userNameText?.applyText(userName)
            userImage?.isClickable = userName.isNullOrEmpty()

            val gravatarHash = getGravatarHash()
            if (gravatarHash.isNotNullOrEmpty()) {
                userImage?.loadImageUrl("https://www.gravatar.com/avatar/$gravatarHash.jpg?s=90")
            }
        }
    }

    private fun showTmdbLoginDialog() {
        if (tmdbLoginDialog == null) {
            tmdbLoginDialog = AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(R.string.title_tmdb_login)
                    .setMessage(R.string.content_tmdb_login)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(R.string.login_btn, { dialogInterface, which ->
                        presenter?.createRequestToken()
                    }).create()
        }

        tmdbLoginDialog?.show()
    }

    private fun showViewPagerFragment(contentType: Int, titleArray: Array<String>) {
        supportActionBar?.title = toolbarTitles[contentType]
        tabLayout.setVisibility(contentType != CONTENT_TYPE_PEOPLE)

        tabPagerAdapter = TabPagerAdapter(contentType, supportFragmentManager, titleArray)
        viewPager.apply {
            adapter = tabPagerAdapter
            offscreenPageLimit = tabPagerAdapter.count - 1
        }

        if (contentType == CONTENT_TYPE_DISCOVER) {
            tabLayout.tabMode = TabLayout.MODE_FIXED
        } else {
            tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        }
    }

    private fun changeTabFont() {
        val vg = tabLayout.getChildAt(0) as ViewGroup
        (0..vg.childCount - 1)
                .map { vg.getChildAt(it) as ViewGroup }
                .forEach { it.changeViewGroupTextFont() }
    }

    private fun setupNavigationView() {
        val typeface = FontUtils.getTypeface(this, FontUtils.MONTSERRAT_REGULAR)
        val customTypefaceSpan = CustomTypefaceSpan(typeface)
        navigationView.menu.changeMenuFont(customTypefaceSpan)

        navigationView.setNavigationItemSelectedListener { item ->
            item.isChecked = true
            drawerLayout.closeDrawers()

            when (item.itemId) {
                R.id.action_movies -> showViewPagerFragment(CONTENT_TYPE_MOVIE, movieTabTitles)
                R.id.action_tv_shows -> showViewPagerFragment(CONTENT_TYPE_TV_SHOW, tvShowTabTitles)
                R.id.action_people -> showViewPagerFragment(CONTENT_TYPE_PEOPLE, peopleTabTitles)
                R.id.action_discover -> showViewPagerFragment(CONTENT_TYPE_DISCOVER, discoverTabTitles)
            }

            changeTabFont()
            true
        }
    }

    override fun onResume() {
        super.onResume()
        presenter?.createSession()
    }

    override fun showProgressDialog(messageId: Int) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this)
            progressDialog!!.setCancelable(false)
            progressDialog!!.isIndeterminate = true
        }

        progressDialog!!.setMessage(getString(messageId))
        progressDialog!!.show()
    }

    override fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun validateRequestToken(tokenValidationUrl: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
                .setToolbarColor(getColorCompat(R.color.colorPrimary))
                .setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left)
                .setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right)
                .enableUrlBarHiding()
                .build()

        customTabsIntent.launchUrl(this, Uri.parse(tokenValidationUrl))
    }

    override fun onLoginSuccess() {
        showUserProfile()
        showToast(String.format(getString(R.string.success_tmdb_login), preferenceHelper.getName()))
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        hideProgressDialog()
        tmdbLoginDialog?.dismiss()
        super.onConfigurationChanged(newConfig)
    }

    override fun onStop() {
        hideProgressDialog()
        tmdbLoginDialog?.dismiss()
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(START)
            return true
        } else if (item.itemId == R.id.action_search) {
            startActivity(Intent(this, MultiSearchActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(START)) {
            drawerLayout.closeDrawer(START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        viewPager.adapter = null
        navigationView.setNavigationItemSelectedListener(null)
        super.onDestroy()
    }
}