<?php
class BaseClass {
   public function test() {
       echo "BaseClass::test() called\n";
   }

   // Here it doesn't matter if you specify the function as final or not
   final public function moreTesting() {
       echo "BaseClass::moreTesting() called\n";
   }
}
final class ChildClass extends BaseClass {
    private $field1;
    var $field2;
    public function method1() {
    }
}






final class ChildClass2 extends BaseClass {




    
    public $field2 = 22;
}

?>
