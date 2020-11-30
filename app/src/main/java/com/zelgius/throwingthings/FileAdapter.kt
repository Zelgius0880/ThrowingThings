package com.zelgius.throwingthings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zelgius.throwingthings.databinding.AdapterFileBinding
import java.io.File
import java.io.FileFilter

class FileAdapter(
    val startPath: String,
    private val filter: FileFilter? = null,
    private val listener: (File) -> Boolean = { true }
) : RecyclerView.Adapter<FileAdapter.ViewHolder>() {
    var files = listOf<File?>()
    var parent: File? = null

    init {
        setFiles(File(startPath), false)
    }

    private fun setFiles(file: File, update: Boolean = true) {
        val list: List<File?> =
            if (!file.isDirectory) file.parentFile?.listFiles(filter)?.toMutableList()?.apply {
                if(file.path != startPath) add(0, null)
            } ?: listOf()
            else file.listFiles(filter)?.toMutableList()?.apply {
                if(file.path != startPath) add(0, null)
            } ?: listOf()

        if(update) updateList(list)
        files = list
        parent = file.parentFile
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(AdapterFileBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = files.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = files[position]

        holder.binding.apply {
            if (item == null) {
                image.visibility = View.INVISIBLE
                name.text = ".."


            } else {
                image.visibility = View.VISIBLE
                image.setImageResource(
                    if (item.isDirectory)
                        R.drawable.ic_baseline_folder_24
                    else
                        R.drawable.ic_baseline_description_24
                )

                name.text = item.name

            }

            root.setOnClickListener {
                if(item != null) {
                    if (item.isDirectory && listener(item)) {
                        setFiles(item)
                    } else {
                        listener(item)
                    }
                } else {
                    parent?.let { setFiles(it) }
                }
            }

        }
    }

    private fun updateList(newList: List<File?>) {
        val diffResult =
            DiffUtil.calculateDiff(MyDiffCallback(files, newList))

        notifyItemRangeRemoved(0, files.size)
        notifyItemRangeInserted(0, newList.size)
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(val binding: AdapterFileBinding) :
        RecyclerView.ViewHolder(binding.root)


    class MyDiffCallback(
        private val new: List<File?>,
        private val old: List<File?>
    ) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return old.size
        }

        override fun getNewListSize(): Int {
            return new.size
        }

        override fun areItemsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean {
            return old[oldItemPosition]?.path === new[newItemPosition]?.path
        }

        override fun areContentsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
        }
    }
}