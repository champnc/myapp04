package com.example.myapp04

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.geometry.GeometryEngine
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import java.util.concurrent.ExecutionException
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment() {

    private lateinit var mMapView: MapView

    private lateinit var covidLayer: GraphicsOverlay

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMapView = view.findViewById<MapView>(R.id.mapView);
        setupMap();
    }

    private fun setupMap() {
        ArcGISRuntimeEnvironment.setLicense(resources.getString(R.string.arcgis_license_key));
        val basemapType = Basemap.Type.STREETS_VECTOR
        val latitude = 13.7458521
        val longitude = 100.5655998
        val levelOfDetail = 6
        val map = ArcGISMap(basemapType, latitude, longitude, levelOfDetail)
        mMapView.isAttributionTextVisible = false
        mMapView.map = map

        covidLayer = GraphicsOverlay()
        mMapView.graphicsOverlays.add(covidLayer)

        var attribute = mutableMapOf("Title" to "Case 1", "Type" to 1)
        addImagePoint(13.7458521, 100.5655998, attribute)

        attribute = mutableMapOf("Title" to "Case 2", "Type" to 2)
        addImagePoint(16.254680, 99.990321, attribute)

        attribute = mutableMapOf("Title" to "Case 3", "Type" to 1)
        addImagePoint(12.173512, 99.277468, attribute)

        attribute = mutableMapOf("Title" to "Case 4", "Type" to 2)
        addImagePoint(14.668357, 102.147632, attribute)

        mMapView.onTouchListener = object : DefaultMapViewOnTouchListener(context, mMapView) {
            override fun onSingleTapConfirmed(motionEvent: MotionEvent): Boolean {
                // Check popup is showing?
                if (mMapView.callout.isShowing) {
                    mMapView.callout.dismiss()
                }
                // get the point that was clicked and convert it to a point in map coordinates
                val screenPoint = android.graphics.Point(
                    motionEvent.x.roundToInt(),
                    motionEvent.y.roundToInt()
                )
                Log.d("MyTag", "screenPoint: $screenPoint")
                // create a map point from screen point
                val mapPoint = mMapView.screenToLocation(screenPoint)
                Log.d("MyTag", "mapPoint: $mapPoint")
                // Convert to Lat long
                val wgs84Point = GeometryEngine.project(mapPoint, SpatialReferences.getWgs84()) as Point
                Log.d("MyTag", "onSingleTapConfirmed: $wgs84Point")
                val identifyTask = mMapView.identifyGraphicsOverlayAsync(covidLayer, screenPoint, 10.0, false, 10)
                identifyTask.addDoneListener{
                    try {
                        // get the list of graphics returned by identify
                        val identifiedGraphics: List<Graphic> = identifyTask.get().graphics
                        // create show content
                        val showContent = LayoutInflater.from(context).inflate(R.layout.callout, null, false)
                        // iterate the graphics
                        for (graphic in identifiedGraphics) { // Use identified graphics as required, for example access attributes or geometry, select, build a table, etc...
                            // list of clicked graphic here
                            Log.d("MyTag", "graphic: ${graphic.attributes}")
                            showContent.findViewById<TextView>(R.id.calloutTitleTxt)?.text = ("Title: " + graphic.attributes["Title"]?.toString())
                            showContent.findViewById<TextView>(R.id.calloutTypeText)?.text = ("Type: " + graphic.attributes["Type"]?.toString())
                        }
                        if (identifiedGraphics.isNotEmpty()) {
                            mMapView.callout.content = showContent
                            mMapView.callout.location = mapPoint
                            mMapView.callout.show()
                            // In short..
//                            mMapView.callout.apply {
//                                location = mapPoint
//                                content = showContent
//                                show()
//                            }
                        }
                    } catch (ex: InterruptedException) {
                        Log.d("MyTag", "InterruptedException: ${ex.message}")
                    } catch (ex: ExecutionException) {
                        Log.d("MyTag", "ExecutionException: ${ex.message}")
                    }
                }
                return true
            }
        }
    }

    fun addImagePoint(lat: Double, long: Double, attributes: MutableMap<String, Any>? = null) {
        // Create new Point
        val newPoint = Point(long, lat, SpatialReferences.getWgs84())
        // Get Picture

        val confirmDrawable = ContextCompat.getDrawable(context!!, R.drawable.confirm) as BitmapDrawable?
        val suspectDrawable = ContextCompat.getDrawable(context!!, R.drawable.suspect) as BitmapDrawable?

        try {
            // Create Picture Symbol
            if(attributes?.get("Type") == 1){
                val pinSourceSymbol: PictureMarkerSymbol = PictureMarkerSymbol.createAsync(confirmDrawable).get()
                pinSourceSymbol.height = 36F;
                pinSourceSymbol.width = 36F;
                // Load Picture
                pinSourceSymbol.loadAsync()
                // Set Callback
                pinSourceSymbol.addDoneLoadingListener {
                    // When Picture is loaded,
                    // Create New Graphic with Picture Symbol
                    val newGraphic = Graphic(newPoint, pinSourceSymbol)
                    // Add Attribute to Graphic
                    if (attributes != null) {
                        attributes["Type"] = "Confirm!"
                        newGraphic.attributes.putAll(attributes)
                    }
                    // Add to Graphic Overlay Layer
                    covidLayer.graphics.add(newGraphic)
                }
                // Sey Y position of picture from ground
                pinSourceSymbol.offsetY = 20f
            }else{
                val pinSourceSymbol: PictureMarkerSymbol = PictureMarkerSymbol.createAsync(suspectDrawable).get()
                pinSourceSymbol.height = 36F;
                pinSourceSymbol.width = 36F;
                // Load Picture
                pinSourceSymbol.loadAsync()
                // Set Callback
                pinSourceSymbol.addDoneLoadingListener {
                    // When Picture is loaded,
                    // Create New Graphic with Picture Symbol
                    val newGraphic = Graphic(newPoint, pinSourceSymbol)
                    // Add Attribute to Graphic
                    if (attributes != null) {
                        attributes["Type"] = "Suspect!"
                        newGraphic.attributes.putAll(attributes)
                    }
                    // Add to Graphic Overlay Layer
                    covidLayer.graphics.add(newGraphic)
                }
                // Sey Y position of picture from ground
                pinSourceSymbol.offsetY = 20f
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        mMapView.pause();
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mMapView.resume();
    }

    override fun onDestroy() {
        mMapView.dispose();
        super.onDestroy()
    }

}