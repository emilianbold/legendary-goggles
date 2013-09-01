namespace bug235120 {
    struct AAA_235120 {
        int foo();
    };

    struct BBB_235120 {
        int boo();
    };

    AAA_235120 operator+(AAA_235120 a, BBB_235120 b);

    BBB_235120 operator+(BBB_235120 a, AAA_235120 b);

    template <typename T1, typename T2>
    struct DDD_235120 {
        T1 a;
        T2 b;

        decltype(a + b) doo();
        decltype(b + a) goo();

        auto hoo() -> decltype(a + b);
        auto joo() -> decltype(b + a);
    };

    template <typename T1, typename T2>
    auto roo_235120(T1 a, T2 b) -> decltype(a + b);

    int zoo_235120() {
        DDD_235120<AAA_235120, BBB_235120> var;
        var.doo().foo();
        var.goo().boo();
        var.hoo().foo();
        var.joo().boo();

        AAA_235120 a;
        BBB_235120 b;
        roo_235120<AAA_235120, BBB_235120>(a, b).foo();
    }

    // ================= Unique ptr test case =================
    struct Foo_235120
    {
        typedef Foo_235120* Pointer;
        int abc;
    };

    struct Bar_235120
    {
        int abc;
    };

    template <typename T>
    class SFINAE_235120 //Substitution Failure Is Not An Error.
    {
        template <typename U>
        static typename U::Pointer test(typename U::Pointer);

        template <typename U>
        static T* test(...);

    public:
        typedef decltype(test<T>(nullptr)) Pointer;
    };

    int main_235120(int argc, char ** argv) 
    {
        SFINAE_235120<Foo_235120>::Pointer foo = new Foo_235120();
        foo->abc = 11; //Unable to resolve identifier abc.
        SFINAE_235120<Bar_235120>::Pointer bar = new Bar_235120();
        bar->abc = 11; //Unable to resolve identifier abc.
        return 0;
    } 
}