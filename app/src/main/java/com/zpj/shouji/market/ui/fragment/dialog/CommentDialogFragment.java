package com.zpj.shouji.market.ui.fragment.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.zpj.fragmentation.dialog.base.BottomDragDialogFragment;
import com.zpj.http.core.IHttp;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.CommentApi;
import com.zpj.shouji.market.ui.fragment.profile.UserPickerFragment;
import com.zpj.shouji.market.ui.widget.DialogHeaderLayout;
import com.zpj.shouji.market.ui.widget.MaxHeightLayout;
import com.zpj.shouji.market.ui.widget.ReplyPanel;
import com.zpj.toast.ZToast;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.KeyboardObserver;
import com.zpj.utils.ScreenUtils;

public class CommentDialogFragment extends BottomDragDialogFragment<CommentDialogFragment>
        implements ReplyPanel.OnOperationListener,
        IHttp.OnStreamWriteListener {

    protected ReplyPanel replyPanel;
    protected MaxHeightLayout maxHeightLayout;

    protected String replyId;
    protected String replyUser;
    protected String contentType;

    protected Runnable successRunnable;

    public static CommentDialogFragment with(Context context, String replyId, String replyUser, String contentType, Runnable successRunnable) {
        CommentDialogFragment popup = new CommentDialogFragment();
        popup.setReplyId(replyId);
        popup.setReplyUser(replyUser);
        popup.setContentType(contentType);
        popup.successRunnable = successRunnable;
        return popup;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.dialog_fragment_comment;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setCornerRadiusDp(20);
        super.initView(view, savedInstanceState);

        if (TextUtils.isEmpty(replyId) || TextUtils.isEmpty(contentType)) {
            ZToast.warning("出错了");
            dismiss();
            return;
        }

        Activity activity = ContextUtils.getActivity(context);

        replyPanel = findViewById(R.id.panel_reply);
        replyPanel.setOnOperationListener(this);
        replyPanel.getEditor().setMinLines(6);
        replyPanel.getEditor().setMaxLines(36);

        replyPanel.addAction(R.drawable.ic_at_black_24dp, v -> {
            hideSoftInput();
            UserPickerFragment.start(content -> {
                replyPanel.getEditor().append(content);
                showSoftInput(replyPanel.getEditor());
            });
        });

        if (!TextUtils.isEmpty(replyUser)) {
            replyPanel.getEditor().setHint("回复：" + replyUser);
        }

        if ("review".equals(contentType)) {
            replyPanel.removeAppAction();
        }

        DialogHeaderLayout headerLayout = findViewById(R.id.layout_dialog_header);
        headerLayout.setOnCloseClickListener(view1 -> dismiss());

        maxHeightLayout = findViewById(R.id.layout_max_height);
//        LinearLayout llContent = findViewById(R.id.ll_scroll_content);
//        maxHeightLayout.post(() -> maxHeightLayout.setMaxHeight(ScreenUtils.getScreenHeight(context) - ScreenUtils.getStatusBarHeight(context)));


        KeyboardObserver.registerSoftInputChangedListener(activity, contentView, height -> {
            contentView.setTranslationY(0);
            Log.d("CommentPopup", "height=" + height);
            replyPanel.onKeyboardHeightChanged(height, 0);

            if (height > 0) {
                height = ScreenUtils.getScreenHeight(context) - height - ScreenUtils.getStatusBarHeight(context);
                height -= findViewById(R.id.ll_top).getMeasuredHeight();
                height -= findViewById(R.id.rl_actions).getMeasuredHeight();
                int maxHeight = height;
                maxHeightLayout.post(() -> maxHeightLayout.setMaxHeight(maxHeight));
            }
        });

        postDelayed(() -> {
            replyPanel.getEditor().requestFocus();
            showSoftInput(replyPanel.getEditor());
        }, 150);

    }

    @Override
    public void dismiss() {
        hideSoftInput();
        replyPanel.hideEmojiPanel();
        super.dismiss();
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        hideSoftInput();
        replyPanel.setOnOperationListener(null);
    }

//    @Override
//    protected int getMaxHeight() {
//        return ScreenUtils.getScreenHeight(context) - ScreenUtils.getStatusBarHeight(context);
//    }

    @Override
    public void sendText(String content) {
        hideSoftInput();
        Log.d("sendText", "content=" + content + " replyId=" + replyId + " contentType=" + contentType);
        if ("discuss".equals(contentType)) {
            CommentApi.discussCommentWithFileApi(
                    context,
                    content,
                    replyId,
                    replyPanel.getSelectedAppInfo(),
                    replyPanel.getImgList(),
                    "",
                    false,
                    () -> {
                        if (successRunnable != null) {
                            successRunnable.run();
                        }
                        dismiss();
                    },
                    this
            );
        } else if ("review".equals(contentType)) {
            CommentApi.appCommentWithFileApi(
                    context,
                    content,
                    replyId,
                    "",
                    "",
                    "",
                    replyPanel.getImgList(),
                    () -> {
                        if (successRunnable != null) {
                            successRunnable.run();
                        }
                        dismiss();
                    },
                    this
            );
        }

    }

    @Override
    public void onEmojiSelected(String key) {

    }

    @Override
    public void onStickerSelected(String categoryName, String stickerName, String stickerBitmapPath) {

    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public void setReplyUser(String replyUser) {
        this.replyUser = replyUser;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public void onBytesWritten(int bytesWritten) {

    }

    @Override
    public boolean shouldContinue() {
        return true;
    }

    @Override
    protected void onBeforeShow() {
        mSupportVisibleActionQueue.onDestroy();
        mEnterAnimationEndActionQueue.onDestroy();
        super.onBeforeShow();
    }
}
