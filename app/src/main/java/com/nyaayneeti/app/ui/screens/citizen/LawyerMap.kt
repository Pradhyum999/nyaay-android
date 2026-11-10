package com.nyaayneeti.app.ui.screens.citizen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.nyaayneeti.app.data.model.UserEntity
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun LawyerMap(lawyers: List<UserEntity>, userLocation: Pair<Double, Double>?) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(11.0)
        }
    }
    DisposableEffect(Unit) {
        mapView.onResume()
        onDispose { mapView.onPause() }
    }

    AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize()) { mv ->
        mv.overlays.clear()
        val geoLawyers = lawyers.filter { it.chamberLat != null && it.chamberLng != null }
        when {
            userLocation != null -> mv.controller.setCenter(GeoPoint(userLocation.first, userLocation.second))
            geoLawyers.isNotEmpty() -> mv.controller.setCenter(GeoPoint(geoLawyers.first().chamberLat!!, geoLawyers.first().chamberLng!!))
        }
        geoLawyers.forEach { l ->
            val marker = Marker(mv).apply {
                position = GeoPoint(l.chamberLat!!, l.chamberLng!!)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = l.name
                snippet = "${l.city ?: ""} · ${l.experienceYears} yrs"
            }
            mv.overlays.add(marker)
        }
        mv.invalidate()
    }
}
