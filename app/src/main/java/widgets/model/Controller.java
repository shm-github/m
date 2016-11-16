package widgets.model;

/**
 * Created by Hossein on 11/4/2016.
 */

public interface  Controller {
    void play();
    void pause();
    void ffwd();
    void frwd();
    void seekTO(int position);
}
