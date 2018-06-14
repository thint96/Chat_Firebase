package fsi.studymyselft.nguyenthanhthi.chatapp.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;

import fsi.studymyselft.nguyenthanhthi.chatapp.other.InternetChecking;

/**
 * Created by thanhthi on 05/06/2018.
 */

public abstract class BaseActivity extends AppCompatActivity {

    public ProgressDialog progressDialog;
    protected ImageLoader imageLoader;

    public abstract void bindViews();

    public abstract Context getContext();

    public void showErrorInternetCheckingIfExist(String tag) {
        InternetChecking.checkInternet(getContext(), tag);
    }

    public void showProgress(String title, String message) {
        if (!((Activity) getContext()).isFinishing()) {
            progressDialog = ProgressDialog.show(getContext(), title, message);
        }
    }

    public void hideProgress() {
        progressDialog.dismiss();
    }

    public void initImageLoader() {
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.with(getContext()).load(url).into(imageView);
            }
        };
    }
}