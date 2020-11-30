package com.zelgius.throwingthings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.SwipeDismissFrameLayout
import androidx.wear.widget.WearableLinearLayoutManager
import com.zelgius.livedataextensions.observe
import com.zelgius.throwingthings.*
import com.zelgius.throwingthings.databinding.AdapterFileBinding
import com.zelgius.throwingthings.databinding.FragmentMediaChooserBinding
import com.zelgius.throwingthings.viewModel.Media
import com.zelgius.throwingthings.viewModel.NotificationViewModel

class MediaChooserFragment : Fragment() {
    private var _binding: FragmentMediaChooserBinding? = null
    private val binding: FragmentMediaChooserBinding
        get() = _binding!!

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private val viewModel by lazy {
        ViewModelHelper.create<NotificationViewModel>(
            this,
            requireActivity().application
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return FragmentMediaChooserBinding
            .inflate(inflater, container, false)
            .let {
                _binding = it
                it.root
            }
    }

    val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.root.addCallback(object : SwipeDismissFrameLayout.Callback() {
            override fun onDismissed(layout: SwipeDismissFrameLayout?) {
                navController.navigate(R.id.notificationSettingsFragment)
            }
        })

        binding.recyclerView.apply {
            layoutManager =
                WearableLinearLayoutManager(binding.context)
            isEdgeItemsCenteringEnabled = true
            isCircularScrollingGestureEnabled = true
            setHasFixedSize(true)
            isCircularScrollingGestureEnabled = true

            requestFocus()
        }

        viewModel.getMediaFiles().observe(this) {
            binding.progressBar.visibility = View.GONE
            binding.recyclerView.adapter =
                Adapter(
                    it
                ) { media ->
                    viewModel.setCurrentNotification(media.uri).observe(this) {
                        navController.navigate(R.id.notificationSettingsFragment)
                    }
                }
        }


    }

    class Adapter(private val media: List<Media>, val listener: (Media) -> Unit) :
        RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(
                AdapterFileBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        override fun getItemCount(): Int =
            media.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = media[position]
            with(holder.binding) {
                name.text = item.title
                image.setImageResource(R.drawable.ic_baseline_description_24)

                root.setOnClickListener {
                    listener(item)
                }
            }
        }

    }

    class ViewHolder(val binding: AdapterFileBinding) : RecyclerView.ViewHolder(binding.root)
}