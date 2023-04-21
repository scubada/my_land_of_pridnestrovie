package com.sgk.myworldmogilev.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.sgk.myworldmogilev.R
import com.stfalcon.imageviewer.StfalconImageViewer

class DragAdapter(
    private val array: MutableList<String>
) : RecyclerView.Adapter<DragAdapter.DragHolder>() {

    class DragHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val previewLoading: ImageView = itemView.findViewById(R.id.preview_loading)
        val relative: RelativeLayout = itemView.findViewById(R.id.relative)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DragHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_drag, parent, false)
        return DragHolder(view)
    }

    override fun onBindViewHolder(holder: DragHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(array[position])
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.previewLoading.visibility = View.GONE
                    return false
                }
            })
            .into(holder.image)

        holder.relative.tag = position + 1

        holder.relative.setOnClickListener {
            StfalconImageViewer.Builder(holder.itemView.context, array) { view, image ->
                Glide
                    .with(holder.itemView.context)
                    .load(image)
                    .placeholder(R.drawable.progress_animation)
                    .into(view)
            }.withHiddenStatusBar(false)
                .withStartPosition(position)
                .withBackgroundColor(holder.itemView.context.getColor(R.color.black_t))
                .show()
        }
    }

    override fun getItemCount(): Int = array.size
}