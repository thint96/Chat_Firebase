package fsi.studymyselft.nguyenthanhthi.chatapp.activities;

import android.content.Context;

/**
 * Created by thanhthi on 23/05/2018.
 */

public interface BaseView {

    void bindViews();

    Context getContext();

    void showErrorInternetCheckingIfExist(String tag);

    void showProgress(String title, String message);

    void hideProgress();

    void initImageLoader();

}
