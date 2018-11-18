package ejbcdiunit2.cdiunit;

/**
 * @author aschoerk
 */
public class Classes {
    interface I {
        String call();
    }

    static class IImpl1 implements I {
        public String call() {
            return "IImpl1";
        }
    }

    static class IImpl2 implements I {
        public String call() {
            return "IImpl2";
        }
    }

}
