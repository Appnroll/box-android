package com.appnroll.box.ui.components

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import com.appnroll.box.R
import com.appnroll.box.ui.components.biometric.BiometricFragment
import com.appnroll.box.ui.components.confirmationprompt.ConfirmationPromptFragment
import com.appnroll.box.ui.components.imagedecoder.ImageDecoderFragment
import com.appnroll.box.ui.custom.NavItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*


class MainActivity : AppCompatActivity() {

    private var currentNavItem: NavItem? = null

    private val navItems = listOf(
            NavItem(R.id.navItemBiometric, "BIOMETRIC_FRAGMENT_TAG", R.string.biometric_screen_title),
            NavItem(R.id.navItemImageDecoder, "IMAGE_DECODER_FRAGMENT_TAG", R.string.image_decoder_screen_title),
            NavItem(R.id.navItemConfirmationPrompt, "CONFIRMATION_PROMPT_FRAGMENT_TAG", R.string.confirmation_prompt_screen_title)
    )

    private fun NavItem.getNewFragment()
            = when(this.navItemId) {
        R.id.navItemBiometric -> BiometricFragment.getInstance()
        R.id.navItemImageDecoder -> ImageDecoderFragment.getInstance()
        R.id.navItemConfirmationPrompt -> ConfirmationPromptFragment.getInstance()
        else -> throw IllegalStateException("Missing create fragment function for tag ${this.fragmentTag}")
    }

    private val navItemSelectedListener = NavigationView.OnNavigationItemSelectedListener { item ->
        navItems.find { it.navItemId == item.itemId }?.let { navItem -> showFragment(navItem) }
        drawerLayout.closeDrawer(GravityCompat.START)
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.main_nav_drawer_open_desc, R.string.main_nav_drawer_close_desc)
                .apply {
                    drawerLayout.addDrawerListener(this)
                    syncState()
                }

        navigationView.setNavigationItemSelectedListener(navItemSelectedListener)
        showFragment(savedInstanceState?.getParcelable(NavItem.PARCEL_KEY) ?: navItems.first())
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable(NavItem.PARCEL_KEY, currentNavItem)
        super.onSaveInstanceState(outState)
    }

    private fun showFragment(navItem: NavItem) {
        currentNavItem = navItem
        setTitle(navItem.screenTitleResId)
        navigationView.setCheckedItem(navItem.navItemId)

        with(supportFragmentManager) {
            findFragmentByTag(navItem.fragmentTag).let { fragment ->
                beginTransaction()
                        .replace(R.id.container, fragment ?: navItem.getNewFragment(), navItem.fragmentTag)
                        .apply { if (fragment == null) addToBackStack(null) }
                        .commitAllowingStateLoss()
            }
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            finish()
        }
    }
}
