package com.sgk.myworldmogilev.adapters

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgk.myworldmogilev.R
import com.sgk.myworldmogilev.helper.GradientBackgroundHelper
import com.sgk.myworldmogilev.models.TestList
import com.sgk.myworldmogilev.views.activities.GeneralTestActivity
import com.sgk.myworldmogilev.views.activities.TestActivity

class TestAdapter(
    private val list: MutableList<TestList>,
    private val activity: FragmentActivity?
) : RecyclerView.Adapter<TestAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title_test)
        private val image: ImageView = itemView.findViewById(R.id.img_test)
        private val line: View = itemView.findViewById(R.id.line)
        private val general: LinearLayout = itemView.findViewById(R.id.general)
        val back: LinearLayout = itemView.findViewById(R.id.back)

        fun bind(title: String, image: String, activity: FragmentActivity?) {

            line.visibility = View.GONE
            general.visibility = View.GONE
            back.visibility = View.VISIBLE

            if (title == "") {
                line.visibility = View.VISIBLE
                general.visibility = View.VISIBLE
                back.visibility = View.GONE
            } else {
                this.title.text = title

                Glide.with(itemView.context)
                    .load(image)
                    .placeholder(R.drawable.progress_animation)
                    .into(this.image)

            }

            itemView.setOnClickListener {
                val intent = if (title == "")
                    Intent(activity, GeneralTestActivity::class.java)
                else Intent(
                    activity, TestActivity::class.java
                )
                intent.putExtra("url", if (title == "") "general/" else "test/$title")
                activity!!.startActivity(intent)
                activity.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_test, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position].title!!, list[position].image!!, activity)

        val bgDrawable = holder.back.background as LayerDrawable
        val shape = bgDrawable.findDrawableByLayerId(R.id.background_shape) as GradientDrawable
        shape.setColor(
            GradientBackgroundHelper().getGradientColors(
                holder.itemView.context.getColor(R.color.blue_color),
                holder.itemView.context.getColor(R.color.green_color),
                list.size,
                position
            )
        )
    }

    override fun getItemCount(): Int = list.size
}