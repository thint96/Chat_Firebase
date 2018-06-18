package fsi.studymyselft.nguyenthanhthi.chatapp.other;

/**
 * Created by thanhthi on 15/06/2018.
 */

public class ValidateString {

    public static String validate(String s) {
        return s != null ? s : "Chưa có thông tin";
    }

    public static String validatePosition(String position) {
        return position != null ? position : "Chưa xác định được vị trí";
    }

    public static String validatePhoneNumber(String phoneNumber) {
        return phoneNumber.length() != 0 ? phoneNumber : "18008168";
    }
}
