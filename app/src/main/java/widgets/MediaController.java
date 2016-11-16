package widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.hossein.movieleitner.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import player.model.Player;
import widgets.model.Controller;

/**
 * Created by Hossein on 11/4/2016.
 */

public class MediaController extends LinearLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private AttachControllerToPlayer attachControllerToPlayer;
    private Drawable playDrawable;
    private Drawable pauseDrawable;
    private boolean playPauseButtonState ;
    public static final boolean STATE_PAUSE = false;
    public static final boolean STATE_PLAY = true;
    private ImageButton playPause;
    private ImageButton ffwd;
    private ImageButton frwd;
    private SeekBar seekBar;
    private TextView tv_current_time;
    private TextView tv_total_time;

    public MediaController(Context context) {
        super(context);
    }

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    public MediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView();
    }

    private void initializeView() {
        View view = inflate(getContext(), R.layout.media_controller_widget_layout, this);
        setOrientation(VERTICAL);

        playDrawable = getContext().getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp);
        pauseDrawable = getContext().getResources().getDrawable(R.drawable.ic_pause_black_24dp);

        playPause = (ImageButton) view.findViewById(R.id.play_pause);
        ffwd = (ImageButton) view.findViewById(R.id.ffwd);
        frwd = (ImageButton) view.findViewById(R.id.frwd);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        tv_current_time = (TextView) view.findViewById(R.id.current_video_duration_time);
        tv_total_time = (TextView) view.findViewById(R.id.total_video_duration_time);

        playPause.setOnClickListener(this);
        ffwd.setOnClickListener(this);
        frwd.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(0);
        seekBar.incrementProgressBy(1);
        setPlayPauseState(STATE_PAUSE);

    }

    public void setSeekbarValue(int value) {
        seekBar.setProgress(value);
        tv_current_time.setText(formatTime(value));
    }

    public void setSeekBarMax(int max) {
        seekBar.setMax(max);
        tv_total_time.setText(formatTime(max));
    }

    private String formatTime(int millisecond){
        Date date = new Date(millisecond);
        DateFormat formatter = new SimpleDateFormat("H:mm:ss");
        return  formatter.format(date);

    }
    public void setPlayPauseState(boolean state) {
        playPauseButtonState = state;
        if (state == STATE_PAUSE) {
            playPause.setImageDrawable(playDrawable);
        } else {
            playPause.setImageDrawable(pauseDrawable);
        }

    }

    public void setPlayer(Player player) {
        attachControllerToPlayer = new AttachControllerToPlayer(player);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_pause:
                if (attachControllerToPlayer == null)
                    return;

                if (playPauseButtonState == STATE_PAUSE) {
                    attachControllerToPlayer.play();
                    setPlayPauseState(STATE_PLAY);
                }
                else if (playPauseButtonState == STATE_PLAY) {
                    attachControllerToPlayer.pause();
                    setPlayPauseState(STATE_PAUSE);

                }
                break;


            case R.id.ffwd:
                attachControllerToPlayer.ffwd();
                break;

            case R.id.frwd:
                attachControllerToPlayer.frwd();
                break;
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (fromUser)
            if (attachControllerToPlayer != null) {
                attachControllerToPlayer.seekTO(progress);
            }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    /**
     * inner class
     */

    private class AttachControllerToPlayer implements Controller{

        private final Player videoPlayer;

        public AttachControllerToPlayer(Player videoPlayer) {
            this.videoPlayer = videoPlayer;
        }


        @Override
        public void play() {
            videoPlayer.playVideo();
        }

        @Override
        public void pause() {
            videoPlayer.pauseVideo();
        }

        @Override
        public void ffwd() {
            videoPlayer.ffwd();
        }

        @Override
        public void frwd() {
            videoPlayer.frwd();
        }

        @Override
        public void seekTO(int position) {
            videoPlayer.seekTo(position);
        }
    }

}
