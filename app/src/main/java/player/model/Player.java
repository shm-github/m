package player.model;

/**
 * Created by Hossein on 10/30/2016.
 */

public interface Player {
    abstract void setVideoUri(String path);
    abstract void playVideo();
    abstract void pauseVideo();
    abstract void initVideo();
    abstract void ffwd();
    abstract void frwd();
    abstract void release();
    abstract void setFirstSubtitle(String path);
    abstract void setSecoundSubtitle(String path);
    abstract void seekTo(int millisec);
    abstract void sinkSubtitle();
    abstract void toggleTopPanel();
    abstract boolean isPlaying();
    abstract int getCurrentPosition();
}
