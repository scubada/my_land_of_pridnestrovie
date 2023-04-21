package com.sgk.myworldmogilev.adapters

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sgk.myworldmogilev.R
import com.sgk.myworldmogilev.models.ImagesModel
import com.stfalcon.imageviewer.StfalconImageViewer
import me.virtualiz.blurshadowimageview.BlurShadowImageView

class ImageAdapter(
    private val arrayList: MutableList<ImagesModel> = mutableListOf(),
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val preview: BlurShadowImageView = itemView.findViewById(R.id.preview)
        val previewLoading: ImageView = itemView.findViewById(R.id.preview_loading)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
        ImageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image_info_place, parent, false)
        )

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .asBitmap()
            .load(arrayList[position].url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    holder.preview.setImageBitmap(resource)
                    holder.preview.visibility = View.VISIBLE
                    holder.previewLoading.visibility = View.GONE
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {}

                override fun onLoadCleared(placeholder: Drawable?) {}
            })

        holder.itemView.setOnClickListener {
            StfalconImageViewer.Builder(holder.itemView.context, arrayList) { view, image ->
                Glide
                    .with(holder.itemView.context)
                    .load(image.url)
                    .placeholder(R.drawable.progress_animation)
                    .into(view)
            }.withHiddenStatusBar(false)
                .withStartPosition(position)
                .withBackgroundColor(holder.itemView.context.getColor(R.color.black_t))
                .show()
        }
    }

    override fun getItemCount(): Int = arrayList.size

}