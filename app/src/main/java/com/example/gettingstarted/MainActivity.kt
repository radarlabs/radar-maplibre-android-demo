package com.example.gettingstarted

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap

import org.maplibre.android.MapLibre
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.module.http.HttpRequestUtil
import okhttp3.OkHttpClient

// optional 
import org.maplibre.android.plugins.annotation.SymbolManager
import org.maplibre.android.plugins.annotation.SymbolOptions

const val MARKER_NAME = "radar-marker"
val RADAR_LAT_LNG = LatLng(40.7342,-73.9911)

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var mapLibreMap: MapLibreMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create url (style + publishable key)
        val key = "<RADAR_PUBLISHABLE_KEY>"
        val style = "radar-default-v1"
        val styleURL = "https://api.radar.io/maps/styles/$style?publishableKey=$key"

        // init MapLibre
        MapLibre.getInstance(this)

        val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("X-Radar-Mobile-Origin", BuildConfig.APPLICATION_ID)
                    .build()
                chain.proceed(request)
            }
            .build()
        HttpRequestUtil.setOkHttpClient(httpClient)

        // init layout view
        val inflater = LayoutInflater.from(this)
        val rootView = inflater.inflate(R.layout.activity_main, null)
        setContentView(rootView)

        // init the MapView
        mapView = rootView.findViewById(R.id.mapView)

        mapView.getMapAsync { map ->
            // callback for map done loading
            mapLibreMap = map
            // remove default logo and replace with Radar logo in activity_main.xml
            map.uiSettings.isLogoEnabled = false

            // anchor attribution to bottom right
            map.uiSettings.attributionGravity = Gravity.RIGHT + Gravity.BOTTOM
            map.uiSettings.setAttributionMargins(0,0,24,24)
            
            map.setStyle(styleURL) {style ->
                val infoIconDrawable = ResourcesCompat.getDrawable(
                    this.resources,
                    // use imported marker resource
                    R.drawable.default_marker,
                    null
                )!!
                // create icon bmp
                val bitmapMarker = infoIconDrawable.toBitmap()
                style.addImage(MARKER_NAME, bitmapMarker)

                // Create a SymbolManager
                val symbolManager = SymbolManager(mapView, map, style)
                // Disable symbol collisions
                symbolManager.iconAllowOverlap = true
                symbolManager.iconIgnorePlacement = true

                // Add a new symbol at specified lat/lon.
                val symbol = symbolManager.create(
                    SymbolOptions()
                        .withLatLng(RADAR_LAT_LNG)
                        .withIconImage(MARKER_NAME)
                        .withIconSize(1.25f)
                        .withIconAnchor("bottom")
                )


                map.cameraPosition = CameraPosition.Builder()
                    .target(symbol.latLng)
                    .zoom(11.0)
                    .build()
                symbolManager.update(symbol)

                // Add a listener to trigger markers clicks.
                symbolManager.addClickListener {
                    // Display information
                    Toast.makeText(this, "Radar HQ", Toast.LENGTH_LONG).show();
                    true
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}
