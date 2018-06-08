package fsi.studymyselft.nguyenthanhthi.chatapp.activities.detailInfoUser;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.BaseMainActivity;

public class DetailInfoUserActivity extends BaseMainActivity implements DetailInfoUserView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info_user);

        bindViews();
    }

    @Override
    public void bindViews() {
        setTitle("Activity Detail Information of user");
    }

    @Override
    public Context getContext() {
        return DetailInfoUserActivity.this;
    }
}
