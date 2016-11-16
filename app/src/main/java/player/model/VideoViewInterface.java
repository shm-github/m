package player.model;

import android.view.View;
import android.widget.VideoView;

import widgets.SubtitleView;

/**
 * Created by Hossein on 10/30/2016.
 */

public interface VideoViewInterface {

    View getTopPanel();

    widgets.MediaController getControllerPanel();

    SubtitleView getBottomSub();

    SubtitleView getTopSub();

    VideoView getVideoView();

    void showPanels(boolean state);

    void togglePanels();

    void setOnSubtitleClickListener(SubtitleView.OnSubtitleClickListener listener);

    void setOnVideoViewClickListener(View.OnClickListener listener);

}


