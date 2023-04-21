package com.sgk.myworldmogilev.views.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.sgk.myworldmogilev.R
import com.sgk.myworldmogilev.databinding.FragmentMainBinding
import com.sgk.myworldmogilev.models.MarkerDataModel
import com.sgk.myworldmogilev.views.activities.InfoPlaceActivity
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider

class MapFragment : Fragment() {

    private lateinit var mapObjects: MapObjectCollection
    private lateinit var database: DatabaseReference
    private var lastMarker: PlacemarkMapObject? = null

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentMainBinding

        var data = MarkerDataModel()

        var laM: Double = 0.0
        var loM: Double = 0.0

        @JvmStatic
        fun newInstance() = MapFragment()

        fun hide() {
            binding.imgMap.setImageBitmap(getBitmapFromView(binding.mapView))
            binding.imgMap.visibility = View.VISIBLE
            binding.mapView.visibility = View.GONE
        }

        fun moveAnimatedTo(
            latitude: Double,
            longitude: Double,
            zoom: Float,
            duration: Float
        ) {
            binding.mapView.map.move(
                CameraPosition(Point(latitude, longitude), zoom, 0F, 0F),
                Animation(Animation.Type.SMOOTH, duration),
                null
            )
        }

        private fun getBitmapFromView(view: View): Bitmap? {
            val returnedBitmap =
                Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(returnedBitmap)
            val bgDrawable = view.background
            if (bgDrawable != null)
                bgDrawable.draw(canvas) else
                canvas.drawColor(Color.WHITE)
            view.draw(canvas)
            return returnedBitmap
        }
    }

    override fun onDestroyView() {
        MapKitFactory.getInstance().onStop()
        binding.mapView.onStop()
        super.onDestroyView()
    }

    override fun onStart() {
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
        super.onStart()
    }

    private fun isAttachedToActivity(): Boolean = isVisible && activity != null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        database = Firebase.database.getReference(getString(R.string.point_name))
        mapObjects = binding.mapView.map.mapObjects.addCollection()

        moveAnimatedTo(53.894548, 30.330654, 5F, 0F)

        binding.preview.setOnClickListener {
            val inm = Intent(context, InfoPlaceActivity::class.java)
            startActivity(inm)
            requireActivity().overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
        }

        binding.minus.setOnClickListener {
            binding.mapView.map.move(
                CameraPosition(
                    binding.mapView.map.cameraPosition.target,
                    binding.mapView.map.cameraPosition.zoom - 1, 0.0f, 0.0f
                ),
                Animation(Animation.Type.LINEAR, 0.25F),
                null
            )
        }

        binding.plus.setOnClickListener {
            binding.mapView.map.move(
                CameraPosition(
                    binding.mapView.map.cameraPosition.target,
                    binding.mapView.map.cameraPosition.zoom + 1, 0.0f, 0.0f
                ),
                Animation(Animation.Type.LINEAR, 0.25F),
                null
            )
        }

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    val test = data.getValue<MarkerDataModel>()

                    if (test != null) {
                        val marker = mapObjects.addPlacemark(
                            Point(test.latitude, test.longitude),
                        ).apply {
                            if (isAttachedToActivity()) {
                                val bitmap = resources.getDrawable(R.drawable.ic_mapsv)
                                    .toBitmap(100, 100)
                                setIcon(ImageProvider.fromBitmap(bitmap))
                            }
                        }
                        marker.isDraggable = true
                        marker.userData = test

                        marker.addTapListener(markerTapListener)
                    }

                    binding.progress.animate()
                        .alpha(0F)
                        .withEndAction {
                            binding.progress.visibility = View.GONE
                        }
                        .setDuration(500)
                        .start()

                    if (laM != 0.0 && loM != 0.0){
                        moveAnimatedTo(laM, loM, 10F, 1F)
                        laM = 0.0
                        loM = 0.0
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    @SuppressLint("ObjectAnimatorBinding", "Range")
    private val markerTapListener =
        MapObjectTapListener { mapObject, _ ->

            val placeMarker = mapObject as PlacemarkMapObject

            if (isAttachedToActivity()) {
                val bitmap = resources.getDrawable(R.drawable.ic_mapsv1)
                    .toBitmap(110, 110)
                placeMarker.setIcon(ImageProvider.fromBitmap(bitmap))
            }

            if (lastMarker != null && placeMarker != lastMarker) {
                if (isAttachedToActivity()) {
                    val bitmap = resources.getDrawable(R.drawable.ic_mapsv)
                        .toBitmap(100, 100)
                    lastMarker!!.setIcon(ImageProvider.fromBitmap(bitmap))
                }
            }

            lastMarker = placeMarker

            data = mapObject.userData as MarkerDataModel
            moveAnimatedTo(data.latitude, data.longitude, 17F, 1F)

            binding.previewName.text = data.name

            binding.preview.visibility = View.VISIBLE

            binding.progress1.visibility = View.VISIBLE

            Glide
                .with(this)
                .load(data.img1)
                .listener(object : RequestListener<Drawable> {

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progress1.visibility = View.GONE

                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(binding.previewImage)

            anim(binding.preview)

            true
        }

    private fun anim(view: View) {
        view.animate()
            .alpha(1F)
            .setDuration(500)
            .start()
    }


}
