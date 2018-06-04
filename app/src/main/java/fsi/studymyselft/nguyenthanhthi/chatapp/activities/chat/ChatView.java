package fsi.studymyselft.nguyenthanhthi.chatapp.activities.chat;

import fsi.studymyselft.nguyenthanhthi.chatapp.activities.BaseMainView;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.BaseView;

/**
 * Created by thanhthi on 28/05/2018.
 */

public interface ChatView extends BaseView {

    void deleteMessage();

    void copyToClipBoard();

    void initMessageAdapter();

    void showMessagesList();

    void navigateToListUser();
}
