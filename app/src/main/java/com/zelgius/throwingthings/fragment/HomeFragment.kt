/*
Copyright 2016 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.zelgius.throwingthings.fragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.drawer.WearableDrawerLayout
import androidx.wear.widget.drawer.WearableDrawerView
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import com.zelgius.throwingthings.R
import com.zelgius.throwingthings.ViewModelHelper
import com.zelgius.throwingthings.databinding.FragmentHomeBinding
import com.zelgius.throwingthings.viewModel.HomeViewModel

/**
 * Demonstrates use of Navigation and Action Drawers on Wear.
 */
class HomeFragment : Fragment(),
    WearableNavigationDrawerView.OnItemSelectedListener, MenuItem.OnMenuItemClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!

    private val navController by lazy { findNavController() }

    private val viewModel by lazy { ViewModelHelper.create<HomeViewModel>(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentHomeBinding
            .inflate(inflater, container, false)
            .let {
                _binding = it
                it.root
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topNavigationDrawer.setAdapter(
            NavigationAdapter(
                binding.context
            )
        )
        // Peeks navigation drawer on the top.
        //mWearableNavigationDrawer.controller.peekDrawer()
        binding.topNavigationDrawer.addOnItemSelectedListener(this)

        binding.bottomActionDrawer.apply {
            // Peeks action drawer on the bottom.
            //controller.peekDrawer()
            setOnMenuItemClickListener(this@HomeFragment)
            isPeekOnScrollDownEnabled = true
        }

        binding.root.setDrawerStateCallback(object : WearableDrawerLayout.DrawerStateCallback() {
            override fun onDrawerClosed(
                layout: WearableDrawerLayout?,
                drawerView: WearableDrawerView?
            ) {
                if (!binding.bottomActionDrawer.isClosed && !binding.bottomActionDrawer.isOpened)
                    binding.bottomActionDrawer.controller.closeDrawer()
            }
        })

        viewModel.ambient.observe(viewLifecycleOwner) {ambient ->
            viewModel.selected.value?.let {
                binding.image.setImageResource(
                    if (!ambient) it.drawable
                    else it.drawableAmbient
                )
            }
        }

        viewModel.selected.observe(viewLifecycleOwner) {
            binding.image.setImageResource(
                if (viewModel.ambient.value == false) it.drawable
                else it.drawableAmbient
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // Updates content when user changes between items in the navigation drawer.
    override fun onItemSelected(position: Int) {
        viewModel.change(position)
    }

    override fun onMenuItemClick(item: MenuItem) =
        when (item.itemId) {
            R.id.action_notification_settings -> {
                navController.navigate(R.id.notificationSettingsFragment)
                binding.bottomActionDrawer.controller.closeDrawer()

                true
            }
            else -> false
        }

    private inner class NavigationAdapter(private val context: Context) :
        WearableNavigationDrawerView.WearableNavigationDrawerAdapter() {

        val items = listOf(
            R.string.fire_flower to R.drawable.ic_menu_fire_flower_white,
            R.string.poke_ball to R.drawable.ic_menu_pokeball
        )

        override fun getCount(): Int {
            return items.size
        }

        override fun getItemText(pos: Int): String {
            return context.getString(items[pos].first)
        }

        override fun getItemDrawable(pos: Int): Drawable {

            return ContextCompat.getDrawable(context, items[pos].second)!!
        }

    }


    companion object {
        private const val TAG = "MainActivity"
    }


}