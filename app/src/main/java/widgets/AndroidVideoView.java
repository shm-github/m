package widgets;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.VideoView;

import com.example.hossein.movieleitner.R;

import player.model.VideoViewInterface;
import widgets.model.OnSubtitleTranslateListener;

/**
 * Created by Hossein on 10/30/2016.
 */

public class AndroidVideoView extends FrameLayout implements VideoViewInterface, View.OnClickListener, View.OnTouchListener {

    private static final long ANIMATION_TIME = 500;
    private OnClickListener OnVideoViewClickListener;

    private View topPanel;
    private MediaController controllerPanel;
    private SubtitleView bottomSub;
    private SubtitleView topSub;
    private VideoView videoView;
    private boolean panelState = true;
    private boolean wait = false;

    public AndroidVideoView(Context context) {
        super(context);
        initializeView();
    }

    public AndroidVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }


    private void initializeView() {
        inflate(getContext(), R.layout.video_view, this);

        videoView = (VideoView) findViewById(R.id.vv);
        topSub = (SubtitleView) findViewById(R.id.top_subtitle);
        bottomSub = (SubtitleView) findViewById(R.id.bottom_subtitle);
        topPanel = findViewById(R.id.topPanel);
        controllerPanel = (MediaController) findViewById(R.id.controllerPanel);
        videoView.setOnTouchListener(this);

        videoView.setOnClickListener(this);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        togglePanels();
        return false;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
//            case R.id.tv_topSub:
//                if(onSubtitleClickListener != null){
//                    onSubtitleClickListener.onSubTitleClick(SubtitleIds.TOP.ordinal());
//                }
//                break;
//
//            case R.id.tv_bottomSub:
//                if(onSubtitleClickListener != null){
//                    onSubtitleClickListener.onSubTitleClick(SubtitleIds.BOTTOM.ordinal());
//                }
//                break;

            case R.id.vv:
                OnVideoViewClickListener.onClick(v);
                break;
        }
    }

    @Override
    public View getTopPanel() {
        return topPanel;
    }

    public MediaController getControllerPanel() {
        return controllerPanel;
    }

    @Override
    public SubtitleView getBottomSub() {
        return bottomSub;
    }

    @Override
    public SubtitleView getTopSub() {
        return topSub;
    }

    @Override
    public VideoView getVideoView() {
        return videoView;
    }

    @Override
    public void showPanels(boolean state) {
        if (state) {
            startShowAnimation();
            startHideTimer();
        } else {
            startHideAnimation();
        }
    }


    @Override
    public void togglePanels() {
        if(!wait) {
            panelState = !panelState;
            showPanels(panelState);
        }
    }

    private void startShowAnimation() {
        if(!wait) {
            int height = topPanel.getHeight();
            height *= 2;
            topPanel.animate().translationYBy(topPanel.getHeight()).setDuration(ANIMATION_TIME).start();
            topSub.animate().translationYBy(topPanel.getHeight()).setDuration(ANIMATION_TIME).start();
            bottomSub.animate().translationYBy(-controllerPanel.getHeight()).setDuration(ANIMATION_TIME).start();
            controllerPanel.animate().translationYBy(-controllerPanel.getHeight()).setDuration(ANIMATION_TIME).start();

            wait = true;
            startWaitTimer();
        }
    }

    private void startHideTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (panelState)
                    togglePanels();
            }
        }, 5000);
    }

    private void startHideAnimation() {
        if(!wait) {
            int height = topPanel.getHeight();
            height *= 2;
            topPanel.animate().translationYBy(-topPanel.getHeight()).setDuration(ANIMATION_TIME).start();
            topSub.animate().translationYBy(-topPanel.getHeight()).setDuration(ANIMATION_TIME).start();
            bottomSub.animate().translationYBy(controllerPanel.getHeight()).setDuration(ANIMATION_TIME).start();
            controllerPanel.animate().translationYBy(controllerPanel.getHeight()).setDuration(ANIMATION_TIME).start();

            wait = true;
            startWaitTimer();
        }
    }

    private void startWaitTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                wait = false ;
            }
        }, ANIMATION_TIME * 2);
    }


    @Override
    public void setOnSubtitleClickListener(SubtitleView.OnSubtitleClickListener listener) {
        topSub.setOnSubtitleClickListener(listener);
        bottomSub.setOnSubtitleClickListener(listener);
    }

    @Override
    public void setOnVideoViewClickListener(OnClickListener listener) {
        this.OnVideoViewClickListener = listener;
    }


    public void setOnSubtitleTranslateListener(OnSubtitleTranslateListener onSubtitleTranslateListener) {
        topSub.setOnSubtitleTranslateListener (onSubtitleTranslateListener);
        bottomSub.setOnSubtitleTranslateListener(onSubtitleTranslateListener);
    }
}
