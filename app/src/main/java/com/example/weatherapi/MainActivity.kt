package com.example.weatherapi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    var mLocationManager: LocationManager? = null
    //var mLocationListener: LocationListener? = null
    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    val PERMISSIONS_REQUEST_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            getLocation()
        }

    }

    private fun getLocation(){
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        var userLocation: Location = getLatLng()
        if(userLocation != null){
            var latitude = userLocation.latitude // 위도 값
            var longitude = userLocation.longitude // 경도 값
            Log.d("CheckCurrentLocation", "현재 내 위치 값 -> 위도 : ${latitude}, 경도 : ${longitude}")
            textView.text = "위도 : " + latitude.toString() + "  경도 : " + longitude.toString()
            var mGeoCoder =  Geocoder(applicationContext, Locale.KOREAN)
            var mResultList: List<Address>? = null
            try{
                mResultList = mGeoCoder.getFromLocation(
                        latitude!!, longitude!!, 1
                )
            }catch(e: IOException){
                e.printStackTrace()
            }
            if(mResultList != null){
                // 내 주소 가져오기
                Log.d("내 주소 ", mResultList[0].getAddressLine(0))
                textView2.text = mResultList[0].getAddressLine(0)
            }
        }
    }

    /**
     * getLatLng() 함수: ACCESS_COARSE_LOCATION 권한과 ACCESS_FINE_LOCATION권한이 허용되어 있을 경우 좌표를 구하고,
     * 허용되있지 않을 경우  권한을 요청한 뒤, 다시 getLatLng함수를 호출하도록 하는 코드
     */
    private fun getLatLng(): Location{
        var currentLatLng: Location? = null
        var hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        var hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED){
            val locatioNProvider = LocationManager.GPS_PROVIDER
            currentLatLng = mLocationManager?.getLastKnownLocation(locatioNProvider)
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])){
                Toast.makeText(this, "앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
            }else{
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
            }
            currentLatLng = getLatLng()
        }
        return currentLatLng!!
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == PERMISSIONS_REQUEST_CODE && grantResults.size == REQUIRED_PERMISSIONS.size){
            var check_result = true
            for(result in grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){
                    check_result = false;
                    break;
                }
            }
            if(check_result){
            }else{
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])){
                    Toast.makeText(this, "권한 설정이 거부되었습니다.\n앱을 사용하시려면 다시 실행해주세요.", Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(this, "권한 설정이 거부되었습니다.\n설정에서 권한을 허용해야 합니다..", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }


}