package widgets.model;

import java.util.ArrayList;

import model.VocabularyModel;

/**
 * Created by Hossein on 11/11/2016.
 */

public interface OnSubtitleTranslateListener {
    void onTranslateRequest(ArrayList<VocabularyModel> vocabularies);
}
