package com.example.notebookpc.testinggithug

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderApi
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : FragmentActivity(),OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener{

    private var mMap:GoogleMap?=null
    private var client:GoogleApiClient?=null
    private var locationReuqest:LocationRequest?=null
    private var lastLocation:Location?=null
    private var currentLocation:Marker?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission()
        }

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {

        mMap=googleMap

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            buildGoogleApiClient()
            mMap!!.isMyLocationEnabled=true
        }
    }

    @Synchronized protected fun buildGoogleApiClient(){
        client = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener (this)
                .addApi(LocationServices.API)
                .build()
        client!!.connect()
    }
    override fun onConnected(bundle: Bundle?) {
        locationReuqest = LocationRequest()

        locationReuqest!!.interval = 1000
        locationReuqest!!.fastestInterval = 1000
        locationReuqest!!.priority=LocationRequest.PRIORITY_HIGH_ACCURACY

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
        LocationServices.FusedLocationApi.removeLocationUpdates(client, locationReuqest!!, this)
        }
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onLocationChanged(location: Location) {
        lastLocation = location

        if(currentLocation !=null){
            currentLocation!!.remove()
        }

        val latLng = LatLng(location.latitude,location.longitude)

        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("home")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

        currentLocation= mMap!!.addMarker(markerOptions)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.animateCamera(CameraUpdateFactory.zoomBy(10f))

        if(client != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(client!!,this)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode){
            REQUEST_LOCATION_CODE ->{
                if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        if(client !=null){
                            buildGoogleApiClient()
                        }
                        mMap!!.isMyLocationEnabled=true
                    }
                }else
                {
                    Toast.makeText(this,"something went wrong",Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }
    fun checkLocationPermission():Boolean{
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_LOCATION_CODE)
            }else
            {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_LOCATION_CODE)
            }
            return false
        }else
            return true
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {

    }

    companion object {
        val REQUEST_LOCATION_CODE = 99
    }
}

private fun FusedLocationProviderApi.removeLocationUpdates(client: GoogleApiClient, mainActivity: MainActivity) {}

private fun FusedLocationProviderApi.removeLocationUpdates(client: GoogleApiClient?, locationReuqest: LocationRequest, mainActivity: MainActivity) {}
