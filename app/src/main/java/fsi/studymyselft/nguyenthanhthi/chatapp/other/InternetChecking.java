package fsi.studymyselft.nguyenthanhthi.chatapp.other;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by thanhthi on 28/05/2018.
 */

public class InternetChecking {

    public static void checkInternet(Context context, String tag) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d(tag, "has connected Internet");
        }
        else {
            Log.d(tag, "has not connected Internet");
            Toast.makeText(context, "Please check Internet connection!", Toast.LENGTH_SHORT).show();
        }
    }
}
