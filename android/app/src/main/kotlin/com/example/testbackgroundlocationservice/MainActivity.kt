package com.example.testbackgroundlocationservice

import android.Manifest
import android.app.AppOpsManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.testbackgroundlocationservice.databinding.ActivityMainBinding
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {

    private val channel = "com.backgroundservice.methodchannel";

    lateinit var binding: ActivityMainBinding
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private val INTERVAL_MS: Long = 2000 // 1 second interval
    lateinit var pref: SharedPreferences

    private val REQUEST_IGNORE_BATTERY_OPTIMIZATIONS: Int = 566
    private val REQUEST_ENABLE_BACKGROUND: Int = 568

    var methodChannelResult: MethodChannel.Result? = null

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            channel
        ).setMethodCallHandler { call, result ->
            methodChannelResult = result;
            MethodChannel(flutterEngine.dartExecutor, channel).setMethodCallHandler { call, result ->
//                binding = ActivityMainBinding.inflate(layoutInflater)
//                setContentView(binding.root)

                when (call.method) {
                    "getLocation" -> {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= 23) {
                                if (ActivityCompat.checkSelfPermission(
                                        this,
                                        android.Manifest.permission.ACCESS_FINE_LOCATION
                                    ) !=
                                    PackageManager.PERMISSION_GRANTED
                                ) {
                                    requestPermissions(
                                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                                        101
                                    )
                                }
                            }
                        }

                        pref = applicationContext.getSharedPreferences("location_background", MODE_PRIVATE)

                        val serviceIntent = Intent(this@MainActivity, BackgroundService::class.java)
                        startService(serviceIntent)

                        //is background service
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
                            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                                val intent = Intent().apply {
                                    action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                                    data = Uri.parse("package:$packageName")
                                }
                                startActivityForResult(intent, REQUEST_ENABLE_BACKGROUND)
                            }
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val channel = NotificationChannel(
                                "ProximitySensorService",
                                "Proximity Sensor Service",
                                NotificationManager.IMPORTANCE_DEFAULT
                            )
                            val notificationManager = getSystemService(NotificationManager::class.java)
                            notificationManager.createNotificationChannel(channel)
                        }


                        handler = Handler()
                        runnable = object : Runnable {
                            override fun run() {
                                // code block will repeat for every time
                                if (pref.getString("lat", "") != "") {
                                    Log.d("status_loc_status", "lat long set on textview")
                                    findViewById<TextView>(R.id.tvLat).text = pref.getString("lat", "")
                                    findViewById<TextView>(R.id.tvLongi).text = pref.getString("long", "")
                                }
                                handler!!.postDelayed(this, INTERVAL_MS)
                            }
                        }

                        handler!!.post(runnable!!)
                    }
                    "ReadNotification" -> actionOnReadNotification()
                    "BatteryOptimizations" -> actionOnBatteryOptimizations()
                    "LocationPermission" -> actionOnLocationPermission()
                }
            }

        }
    }

//    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
//        super.configureFlutterEngine(flutterEngine)
//        MethodChannel(flutterEngine.dartExecutor, channel)
//            .setMethodCallHandler { call, result ->
//                binding = ActivityMainBinding.inflate(layoutInflater)
//                setContentView(binding.root)
//
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    if ( Build.VERSION.SDK_INT >= 23){
//                        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
//                            PackageManager.PERMISSION_GRANTED  ){
//                            requestPermissions( arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101);
//                        }
//                    }
//                }
//
//                pref = applicationContext.getSharedPreferences("location_background", MODE_PRIVATE)
//
//
//                val serviceIntent = Intent(this@MainActivity, BackgroundService::class.java)
//                startService(serviceIntent)
//
//                //is background service
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
//                    if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
//                        val intent = Intent().apply {
//                            action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
//                            data = Uri.parse("package:${packageName}")
//                        }
//                        startActivityForResult(intent, REQUEST_ENABLE_BACKGROUND)
//                    }
//                }
//
//                binding.apply {
//                    appLaunch.setOnClickListener {
//                        actionOnReadNotification()
//                        actionOnLocationPermission()
//                    }
//                    tvBattery.setOnClickListener {
//                        actionOnBatteryOptimizations()
//                    }
//                }
//
//                handler = Handler()
//                runnable = object : Runnable {
//                    override fun run() {
//                        // code block will repeat for everytime
////                Log.d("status_loc_status", "repeating")
//
//                        if (pref.getString("lat","") != ""){
//                            Log.d("status_loc_status", "lat long set on textview")
//                            findViewById<TextView>(R.id.tvLat).text = pref.getString("lat","")
//                            findViewById<TextView>(R.id.tvLongi).text = pref.getString("long","")
//                        }
//                        handler!!.postDelayed(this, INTERVAL_MS)
//                    }
//                }
//
//
//                handler!!.post(runnable!!)
//
//            }
//    }


    fun actionOnReadNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!notificationManager.isNotificationPolicyAccessGranted) {
                // Prompt the user to grant the Read notifications permission
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                startActivityForResult(intent, 104)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
            if (mode != AppOpsManager.MODE_ALLOWED) {
                // Prompt the user to grant the Read notifications permission
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivityForResult(intent, 104)
//                startActivity(intent)
            }
        }
    }

    fun actionOnBatteryOptimizations() {
        // Request ignore battery optimizations Manifest.permission
        // Request allow app to stay connected in the background permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    data = Uri.parse("package:${packageName}")
                }
                startActivityForResult(intent, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            }
        }
    }

    fun actionOnLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101);
//                    return ;
                }
            }
        }
    }


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            if ( Build.VERSION.SDK_INT >= 23){
//                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
//                    PackageManager.PERMISSION_GRANTED  ){
//                    requestPermissions( arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101);
////                    return ;
//                }
//            }
//        }
//
//        pref = applicationContext.getSharedPreferences("location_background", MODE_PRIVATE)
//
//
//        val serviceIntent = Intent(this@MainActivity, BackgroundService::class.java)
//        startService(serviceIntent)
//
//        //is background service
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
//            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
//                val intent = Intent().apply {
//                    action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
//                    data = Uri.parse("package:${packageName}")
//                }
//                startActivityForResult(intent, REQUEST_ENABLE_BACKGROUND)
//            }
//        }
//
//        binding.apply {
//            appLaunch.setOnClickListener {
//                actionOnReadNotification()
//                actionOnLocationPermission()
//            }
//            tvBattery.setOnClickListener {
//                actionOnBatteryOptimizations()
//            }
//        }
//
//        handler = Handler()
//        runnable = object : Runnable {
//            override fun run() {
//                // code block will repeat for everytime
////                Log.d("status_loc_status", "repeating")
//
//                if (pref.getString("lat","") != ""){
//                    Log.d("status_loc_status", "lat long set on textview")
//                    findViewById<TextView>(R.id.tvLat).text = pref.getString("lat","")
//                    findViewById<TextView>(R.id.tvLongi).text = pref.getString("long","")
//                }
//                handler!!.postDelayed(this, INTERVAL_MS)
//            }
//        }
//
//
//        handler!!.post(runnable!!)
//
//    }
}
