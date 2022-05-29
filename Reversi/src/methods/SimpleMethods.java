// Unused currently.

package methods;

public class SimpleMethods {

    public static boolean isInteger(String str) {
        try {
            int num = Integer.valueOf(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
