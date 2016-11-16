package player;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.VideoView;

import java.io.Serializable;

import player.model.Player;
import player.model.VideoViewInterface;
import widgets.MediaController;
import widgets.SubtitleView;

/**
 * Created by Hossein on 10/30/2016.
 */

public abstract class VideoPlayer implements Player , Serializable {

    protected View topPanel;
    protected Activity context;
    protected SubtitleView topSubtitle;
    protected SubtitleView bottomSubtitle;
    protected widgets.MediaController mediaController;
    protected String path;

    protected void hideToolbar() {
        if (context != null)
            context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    protected void ShowToolbar() {
        if (context != null)
            context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    protected boolean isToolbarHidden() {

        return false;
    }


    public static class AndroidVideoPlayer extends VideoPlayer {

        private Handler subtitleHandler;
        private VideoView videoView;
        private int from;
        private int to;
        private int duration;
        private int stopPosition;
        private int currentPosition;

        public AndroidVideoPlayer(VideoViewInterface androidVideoView, String path, int seekToTime, Activity context) {
            videoView = androidVideoView.getVideoView();

            from = 60;
            to = -60;

            this.context = context;
            this.topSubtitle = androidVideoView.getTopSub();
            this.bottomSubtitle = androidVideoView.getBottomSub();
            this.topPanel = androidVideoView.getTopPanel();
            this.mediaController = androidVideoView.getControllerPanel();
            mediaController.setPlayer(this);
            setVideoUri(path);

            subtitleHandler = new Handler();

            currentPosition = seekToTime ;

        }


        @Override
        public void setVideoUri(String path) {
            this.path = path;

            if (videoView != null) {
                videoView.setVideoPath(path);

            }
        }


        @Override
        public void initVideo() {
            if (videoView != null) {
                videoView.setKeepScreenOn(true);
                videoView.requestFocus();
                videoView.start();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()  {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        duration = videoView.getDuration();
                        seekTo(currentPosition);
                        setMediaControllerSeekBarMaxValue(duration);
                    }
                });
                subtitleHandler.post(subtitleSyncRunnable);
            }
        }

        private void setMediaControllerSeekBarMaxValue(int duration) {
            mediaController.setSeekBarMax(duration);
        }

        @Override
        public void playVideo() {
            if (videoView == null)
                return;

            if (!videoView.isPlaying()) {
                videoView.seekTo(getStopPosition());
                videoView.start();
                subtitleHandler.post(subtitleSyncRunnable);
                setMediaControllerPlayPauseButtonState(MediaController.STATE_PLAY);
            }
        }


        @Override
        public void pauseVideo() {
            if (videoView == null)
                return;
            stopPosition = videoView.getCurrentPosition();
            videoView.pause();
            subtitleHandler.removeCallbacks(subtitleSyncRunnable);
            setMediaControllerPlayPauseButtonState(MediaController.STATE_PAUSE);
        }


        private void setMediaControllerPlayPauseButtonState(boolean state) {
            if (mediaController != null)
                mediaController.setPlayPauseState(state);
        }

        @Override
        public void ffwd() {
           if( videoView.canSeekForward()) {
               currentPosition += 5000;
               videoView.seekTo(currentPosition);
           }
        }

        @Override
        public void frwd() {
            if( videoView.canSeekBackward()){
                currentPosition -= 5000;
                videoView.seekTo(currentPosition);
            }
        }


        @Override
        public void release() {
            if (videoView == null) {
                return;
            }
            subtitleHandler.post(subtitleSyncRunnable);

        }


        @Override
        public void setFirstSubtitle(String path) {

        }


        @Override
        public void setSecoundSubtitle(String path) {

        }


        @Override
        public void seekTo(int millisec) {
            getVideoView().seekTo(millisec);
        }


        private VideoView getVideoView() {
            return videoView;
        }


        @Override
        public void sinkSubtitle() {

        }


        @Override
        public void toggleTopPanel() {
            ValueAnimator animator = ValueAnimator.ofInt(from, to);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) topPanel.getLayoutParams();
                    params.setMargins(0, utils.Utils.dpToPx((int) animation.getAnimatedValue(), context), 0, 0);
                    topPanel.setLayoutParams(params);

                }
            });


            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    from = -(from);
                    to = -(to);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            animator.setDuration(3000);
            animator.start();
        }

        @Override
        public boolean isPlaying() {
            return videoView.isPlaying();
        }

        @Override
        public int getCurrentPosition() {
            return currentPosition;
        }

        @Override
        public void hideToolbar() {

        }

        @Override
        public void ShowToolbar() {

        }

        @Override
        protected boolean isToolbarHidden() {
            return super.isToolbarHidden();
        }


        public int getStopPosition() {
            return stopPosition;
        }

        Runnable subtitleSyncRunnable = new Runnable() {
            @Override
            public void run() {
                if (isPlaying()) {
                    currentPosition = videoView.getCurrentPosition();
                    topSubtitle.getSubtitleCoordinator().showSubtitle(getCurrentPosition());
                    bottomSubtitle.getSubtitleCoordinator().showSubtitle(getCurrentPosition());
                    mediaController.setSeekbarValue(getCurrentPosition());
                }

                subtitleHandler.postDelayed(subtitleSyncRunnable, 500);

            }
        };


    }


}
