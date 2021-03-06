namespace bug258143 {
  void printf(const char *);
  
  void foo(bool a) {
    printf("bool!\n");
  }

  void foo(short a) {
    printf("short!\n");
  }

  void foo(unsigned short a) {
    printf("short!\n");
  }

  void foo(int a) {
    printf("int!\n");
  }

  void foo(unsigned int a) {
    printf("unsigned int!\n");
  }

  void foo(long a) {
    printf("long!\n");
  }

  void foo(unsigned long a) {
    printf("unsigned long!\n");
  }

  float foo(float a) {
    printf("float!\n");
    return a;
  }

  void foo(double a) {
    printf("double!\n");
  }

  void foo(long double a) {
    printf("long double!\n");
  }

  namespace bla {
    float f;
  }

  int main258143() {
    static bool b = true;
    static char c = 0;
    static unsigned char uc = 0;
    static short s = 0;
    static unsigned short us = 0;
    static int i = 0;
    static unsigned int ui = 0;
    static long l = 0;
    static unsigned long ul = 0;
    static float f = 0;
    static double d = 0;
    static long double ld = 0;

    struct AAA {
      int x;

      int boo() {
        foo(bla::f * i); // float
        return 10;
      }
    };

    foo(c && c); // bool
    foo(c || s); // bool
    foo(ld && i); // bool
    foo(ld / ul); // long double
    foo(d - l); // double
    foo(bla::f * i); // float
    foo(f * i);  // float
    foo(s + ul); // unsigned long
    foo(l + ui); // long (unsigned long???)
    foo(i + l); // long
    foo(ui + i); // unsigned int
    foo(s + i); // int
    foo(c & s); // int
    foo(uc * us); // int  
    return 0;
  }

  int a = 1;
  float z = foo(bla::f * a); 
}