import 'package:get/get.dart';
import 'package:testbackgroundlocationservice/main_controller.dart';

class MainBinding extends Bindings{
  @override
  void dependencies() {
   Get.lazyPut(() => MainController());
  }

}