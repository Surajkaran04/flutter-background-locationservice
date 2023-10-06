import 'package:flutter/services.dart';
import 'package:get/get.dart';

class MainController extends GetxController{

 var text = "Start Service";

 static const  methodChannel =  MethodChannel("com.backgroundservice.methodchannel");

 @override
  void onInit() {

  super.onInit();
 }

 Future<void> callNativeCode() async {
  try {
   var data = await methodChannel.invokeMethod('getLocation');
   print(data);
  } on PlatformException catch (e) {
   print("Failed to Invoke: '${e.message}'.");
  }
 }

 Future<void> callReadNotification() async {
  try {
   var data = await methodChannel.invokeMethod('ReadNotification');
   print(data);
  } on PlatformException catch (e) {
   print("Failed to Invoke: '${e.message}'.");
  }
 }

 Future<void> callBatteryOptimizations() async {
  try {
   var data = await methodChannel.invokeMethod('BatteryOptimizations');
   print(data);
  } on PlatformException catch (e) {
   print("Failed to Invoke: '${e.message}'.");
  }
 }

 Future<void> callLocationPermission() async {
  try {
   var data = await methodChannel.invokeMethod('LocationPermission');
   print(data);
  } on PlatformException catch (e) {
   print("Failed to Invoke: '${e.message}'.");
  }
 }
}