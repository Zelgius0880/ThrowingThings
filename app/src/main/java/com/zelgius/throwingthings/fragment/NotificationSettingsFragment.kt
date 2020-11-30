package com.zelgius.throwingthings.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import androidx.wear.widget.SwipeDismissFrameLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.zelgius.livedataextensions.observe
import com.zelgius.throwingthings.viewModel.NotificationViewModel
import com.zelgius.throwingthings.R
import com.zelgius.throwingthings.ViewModelHelper
import com.zelgius.throwingthings.databinding.FragmentNotificationSettingsBinding

const val CODE_WRITE_SETTINGS_PERMISSION = 123


class NotificationSettingsFragment : Fragment() {
    private var _binding: FragmentNotificationSettingsBinding? = null
    private val binding: FragmentNotificationSettingsBinding
        get() = _binding!!

    val navController by lazy { findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
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
        return FragmentNotificationSettingsBinding
            .inflate(inflater, container, false)
            .let {
                _binding = it
                it.root
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCurrentNotificationMedia().observe(this) {
            if(it != null) binding.current.text = it
        }

        binding.floatingActionButton.setOnClickListener {
            checkPermission(requireActivity()) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    Dexter
                        .withContext(binding.context)
                        .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        .withListener(object : MultiplePermissionsListener {

                            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                                if (report.areAllPermissionsGranted())
                                    navController.navigate(R.id.actionMediaChooserFragment)
                            }

                            override fun onPermissionRationaleShouldBeShown(
                                list: MutableList<PermissionRequest>?,
                                token: PermissionToken?
                            ) {
                            }
                        }).check()
                } else {
                    navController.navigate(R.id.actionMediaChooserFragment)
                }
            }
        }

        binding.root.addCallback(object : SwipeDismissFrameLayout.Callback() {
            override fun onDismissed(layout: SwipeDismissFrameLayout?) {
                navController.navigate(R.id.homeFragment)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun checkPermission(context: Activity, work: () -> Unit) {
        val permission: Boolean = Settings.System.canWrite(context)
        if (permission) {
            work()
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:" + context.packageName)
            context.startActivityForResult(intent,
                CODE_WRITE_SETTINGS_PERMISSION
            )

            Toast.makeText(
                binding.context,
                R.string.permission_modify_system_setting,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}

val ViewBinding.context
    get() = this.root.context!!