package com.sgk.myworldmogilev.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.gesture.GestureOverlayView.ORIENTATION_HORIZONTAL
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.AppBarLayout
import com.sgk.myworldmogilev.R
import com.sgk.myworldmogilev.adapters.ImageAdapter
import com.sgk.myworldmogilev.databinding.ActivityInfoPlaceBinding
import com.sgk.myworldmogilev.helper.ConnectionLiveData
import com.sgk.myworldmogilev.models.ImagesModel
import com.sgk.myworldmogilev.views.fragments.MapFragment.Companion.data
import kotlin.math.abs

class InfoPlaceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoPlaceBinding
    private lateinit var imageAdapter: ImageAdapter
    private val images = mutableListOf<ImagesModel>()
    private lateinit var cld: ConnectionLiveData
    private var scrollRange: Int = -1

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkNetworkConnection()

        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (scrollRange == -1)
                scrollRange = appBarLayout.totalScrollRange
            val sum2: Float = (scrollRange + verticalOffset).toFloat()
            binding.backgroundAppBar.alpha = (1 - sum2 / scrollRange)
            binding.back.alpha = (sum2 / scrollRange)
            binding.model.alpha = (sum2 / scrollRange)
        })

        binding.back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
        }

        binding.model.setOnClickListener {
            setIntentModelType(data.model)
        }

        binding.modelV.setOnClickListener {
            setIntentModelType(data.modelV)
        }

        with(binding) {
            title.text = data.name
            region.text = data.region
            address.text = data.adress
            desc.text = data.desc
            dataV.text = "${getString(R.string.dataV)} ${data.dataV}"

            if (data.img1 != "null")
                images.add(ImagesModel(data.img1))
            if (data.img2 != "null")
                images.add(ImagesModel(data.img2))
            if (data.img3 != "null")
                images.add(ImagesModel(data.img3))

            if (data.model != "null")
                model.visibility = View.VISIBLE

            if (data.modelV != "null")
                modelV.visibility = View.VISIBLE

            imageAdapter = ImageAdapter(images)
            viewPager.adapter = imageAdapter
            viewPager.clipChildren = false
            viewPager.clipToPadding = false
            viewPager.offscreenPageLimit = images.size
            viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            viewPager.currentItem = 0

            val transform = CompositePageTransformer()
            transform.addTransformer(MarginPageTransformer(30))
            transform.addTransformer { page, position ->
                page.scaleY = 1 - (0.25f * abs(position))

                val viewPager = page.parent.parent as ViewPager2
                val offset = position * -(2 * 10 + 30)
                if (viewPager.orientation == ORIENTATION_HORIZONTAL)
                    if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL)
                        page.translationX = -offset
                    else
                        page.translationX = offset
            }
            viewPager.setPageTransformer(transform)

            Glide
                .with(baseContext)
                .load(data.img1)
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
                        binding.progress.animate()
                            .alpha(0F)
                            .withEndAction {
                                binding.progress.visibility = View.GONE
                            }
                            .setDuration(500)
                            .start()
                        return false
                    }
                })
                .into(imageView)

        }
    }

    private fun setIntentModelType(model: String) {
        val ing = Intent(this, ModelPreviewerActivity::class.java)
        ing.putExtra("model", model)
        startActivity(ing)
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
    }

    private fun checkNetworkConnection() {
        cld = ConnectionLiveData(application)

        cld.observe(this) { isConnected ->
            if (!isConnected) {
                startActivity(Intent(this, NotInternetActivity::class.java))
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
    }
}
