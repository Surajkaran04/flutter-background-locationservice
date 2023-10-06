import 'package:flutter/material.dart';
import 'package:get/get.dart';

import 'main_binding.dart';
import 'main_controller.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      initialBinding: MainBinding(),
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends GetView<MainController> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: Column(
      mainAxisAlignment: MainAxisAlignment.center,
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [

        GestureDetector(
            onTap: () async {
              await controller.callReadNotification();
            },
            child: Container(
                decoration: BoxDecoration(color: Colors.blue, borderRadius: BorderRadius.circular(50)),
                alignment: Alignment.center,
                padding: const EdgeInsets.symmetric(vertical: 15),
                margin: const EdgeInsets.only(left: 45, right: 45, bottom: 18),
                child: Text("ReadNotification", style: const TextStyle(color: Colors.white)))),
        GestureDetector(
            onTap: () async {
              await controller.callBatteryOptimizations();
            },
            child: Container(
                decoration: BoxDecoration(color: Colors.blue, borderRadius: BorderRadius.circular(50)),
                alignment: Alignment.center,
                padding: const EdgeInsets.symmetric(vertical: 15),
                margin: const EdgeInsets.only(left: 45, right: 45, bottom: 18),
                child: Text("BatteryOptimizations", style: const TextStyle(color: Colors.white)))),
        GestureDetector(
            onTap: () async {
              await controller.callLocationPermission();
            },
            child: Container(
                decoration: BoxDecoration(color: Colors.blue, borderRadius: BorderRadius.circular(50)),
                alignment: Alignment.center,
                padding: const EdgeInsets.symmetric(vertical: 15),
                margin: const EdgeInsets.only(left: 45, right: 45, bottom: 18),
                child: Text("LocationPermission", style: const TextStyle(color: Colors.white)))),
        GestureDetector(
            onTap: () async {
              await controller.callNativeCode();
            },
            child: Container(
                decoration: BoxDecoration(color: Colors.blue, borderRadius: BorderRadius.circular(50)),
                alignment: Alignment.center,
                padding: const EdgeInsets.symmetric(vertical: 15),
                margin: const EdgeInsets.only(left: 45, right: 45, bottom: 18),
                child: Text(controller.text, style: const TextStyle(color: Colors.white)))),
      ],
    ));
  }
}
