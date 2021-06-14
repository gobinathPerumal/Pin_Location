package com.core.pin_location

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.pin_location.GPSUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.admin_filter.*
import java.util.*


class AdminFilterMapActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerDragListener {

    private var mMap: GoogleMap? = null
    private var currentLatitude: Double = 0.toDouble()
    private var currentLongitude: Double = 0.toDouble()
    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()
    private var mCustomMarkerView: View? = null
    private var mMarkerImageView: ImageView? = null
    override fun onMarkerDragEnd(p0: Marker?) {
        mMap?.animateCamera(CameraUpdateFactory.newLatLng(p0?.position))
    }

    override fun onMarkerDragStart(p0: Marker?) {
    }

    override fun onMarkerDrag(p0: Marker?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_filter)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fivekmButton.setOnClickListener {
            fivekmButton.setBackgroundColor(Color.GREEN)
            tenkmButton.setBackgroundColor(Color.WHITE)
            twentyFivekmButton.setBackgroundColor(Color.WHITE)
            fiftykmButton.setBackgroundColor(Color.WHITE)
            mMap?.clear()
            setCurrentLocation(5)
        }
        twentyFivekmButton.setOnClickListener {
            twentyFivekmButton.setBackgroundColor(Color.GREEN)
            tenkmButton.setBackgroundColor(Color.WHITE)
            fivekmButton.setBackgroundColor(Color.WHITE)
            fiftykmButton.setBackgroundColor(Color.WHITE)
            mMap?.clear()
            setCurrentLocation(25)
        }
        tenkmButton.setOnClickListener {
            tenkmButton.setBackgroundColor(Color.GREEN)
            fivekmButton.setBackgroundColor(Color.WHITE)
            twentyFivekmButton.setBackgroundColor(Color.WHITE)
            fiftykmButton.setBackgroundColor(Color.WHITE)
            mMap?.clear()
            setCurrentLocation(10)
        }
        fiftykmButton.setOnClickListener {
            fiftykmButton.setBackgroundColor(Color.GREEN)
            tenkmButton.setBackgroundColor(Color.WHITE)
            twentyFivekmButton.setBackgroundColor(Color.WHITE)
            fivekmButton.setBackgroundColor(Color.WHITE)
            mMap?.clear()
            setCurrentLocation(50)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.setOnMarkerDragListener(this)
        fivekmButton.setBackgroundColor(Color.GREEN)
        setCurrentLocation(5)
    }

    private fun setCurrentLocation(kiloMeters: Int) {
        val gpsTracker = GPSUtils(this)
        if (gpsTracker.canGetLocation()) {
            currentLatitude = gpsTracker.latitude
            currentLongitude = gpsTracker.longitude
            setMarker(currentLatitude, currentLongitude, true, "","admin")
        } else {
            gpsTracker.showSettingsAlert()
        }
    }

    private fun setMarker(latitude: Double, longitude: Double, isAdmin: Boolean, path: String, name:String) {
        val DEFAULT_ZOOM = 10f
        val cameraPosition = CameraPosition.Builder().target(
            LatLng(
                latitude,
                longitude
            )
        )
            .zoom(DEFAULT_ZOOM)
            .tilt(0F)
            .build()
        if (isAdmin) {
            mMap?.addMarker(
                MarkerOptions().position(LatLng(latitude, longitude)).draggable(
                    true
                ).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).title(name)
            )
            mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        } else {
            mMap?.addMarker(
                MarkerOptions().position(LatLng(latitude, longitude)).draggable(
                    true
                ).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(name)
            )
            mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }

        enableFeatures()
    }

    private fun enableFeatures() {
        mMap?.uiSettings?.isZoomControlsEnabled = true
        mMap?.uiSettings?.isIndoorLevelPickerEnabled = true
        mMap?.uiSettings?.isScrollGesturesEnabled = true
        mMap?.uiSettings?.isRotateGesturesEnabled = true
        mMap?.isBuildingsEnabled = true
    }

    fun submitLocation() {

    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    fun createCustomMarker(path: String, latlng: LatLng) {
        Glide.with(applicationContext)
            .asBitmap()
            .load(Uri.parse(path))
            .into(object : SimpleTarget<Bitmap?>() {

                override fun onResourceReady(
                    resource: Bitmap?,
                    transition: Transition<in Bitmap?>?
                ) {
                    mMap?.addMarker(
                        MarkerOptions().position(latlng)
                            .icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    mCustomMarkerView?.let {
                                        resource?.let { it1 ->
                                            getMarkerBitmapFromView(
                                                it,
                                                it1
                                            )
                                        }
                                    }
                                )
                            )
                    )
                }
            })
    }

    private fun getMarkerBitmapFromView(view: View, bitmap: Bitmap): Bitmap? {
        mMarkerImageView?.setImageBitmap(bitmap)
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight())
        view.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            view.getMeasuredWidth(), view.getMeasuredHeight(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable: Drawable = view.getBackground()
        if (drawable != null) drawable.draw(canvas)
        view.draw(canvas)
        return returnedBitmap
    }

}