package fsi.studymyselft.nguyenthanhthi.chatapp.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.other.InternetChecking;

/**
 * Created by thanhthi on 05/06/2018.
 */

public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    protected ProgressDialog progressDialog;

    public void showErrorInternetCheckingIfExist(String tag) {
        InternetChecking.checkInternet(getContext(), tag);
    }

    public void showProgress(String title, String message) {
        progressDialog = ProgressDialog.show(getContext(), title, message);
    }

    public void hideProgress() {
        progressDialog.dismiss();
    }

    protected void initImageLoader(ImageLoader imageLoader) {
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.with(getContext()).load(url).into(imageView);
            }
        };
    }
}
