package widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hossein.movieleitner.R;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import fr.noop.subtitle.model.SubtitleCue;
import fr.noop.subtitle.model.SubtitleObject;
import fr.noop.subtitle.model.SubtitleParsingException;
import fr.noop.subtitle.srt.SrtObject;
import fr.noop.subtitle.srt.SrtParser;
import model.VocabularyModel;
import utils.Utils;
import widgets.model.OnSubtitleTranslateListener;

/**
 * Created by Hossein on 10/31/2016.
 */

public class SubtitleView extends LinearLayout implements View.OnClickListener , Serializable {

    private TextView tv_subtitle;
    private ImageButton ib_close;
    private Integer position;
    private OnSubtitleClickListener listener;
    SubtitleCoordinator subtitleCoordinator;
    private OnSubtitleTranslateListener onSubtitleTranslateListener;

    public void setOnSubtitleTranslateListener(OnSubtitleTranslateListener onSubtitleTranslateListener) {
        this.onSubtitleTranslateListener = onSubtitleTranslateListener;
    }


    public enum SubtitlePosition {top, bottom}

    public SubtitleView(Context context, Integer subtitlePosition) {
        super(context);

        initializeView();

        setSubtitlePosition(subtitlePosition);
    }


    public SubtitleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeView();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SubtitleView, 0, 0);

        Integer id = a.getInteger(R.styleable.SubtitleView_subtitle_position, 0);

        //do something with str
        setSubtitlePosition(id);

        a.recycle();

    }


    private void initializeView() {
        subtitleCoordinator = new SubtitleCoordinator();

        setOrientation(HORIZONTAL);
        LayoutParams layoutParams = (LayoutParams) new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setLayoutDirection(LAYOUT_DIRECTION_LTR);
        }
        setMinimumHeight(Utils.dpToPx(60, getContext()));
        setLayoutParams(layoutParams);

        inflateLayout();

    }


    private void inflateLayout() {
        View view = inflate(getContext(), R.layout.subtitle_view_layout, this);

        tv_subtitle = (TextView) view.findViewById(R.id.tv_Sub);
        ib_close = (ImageButton) view.findViewById(R.id.close);
        tv_subtitle.setOnClickListener(this);
        ib_close.setOnClickListener(this);
    }


    private void setSubtitleText(String subtitleText) {
        if (subtitleText.trim().isEmpty()) {
            tv_subtitle.setVisibility(INVISIBLE);
        } else {
            tv_subtitle.setVisibility(VISIBLE);
            tv_subtitle.setText(subtitleText);
        }
    }

    private void setSubtitlePosition(Integer subtitlePosition) {
        position = subtitlePosition;
    }


    private void setTypeFace(Typeface typeFace) {
        if (tv_subtitle != null) {
            tv_subtitle.setTypeface(typeFace);
        }
    }

    public void setTypeFace(String fontAssetsPath) {
        Typeface typeFace = loadTypeFace(fontAssetsPath);
        setTypeFace(typeFace);
    }


    private Typeface loadTypeFace(String fontAssetsPath) {
        //"fonts/BPreplay.otf"
        return Typeface.createFromAsset(getContext().getAssets(),
                fontAssetsPath);

    }

    public void setOnSubtitleClickListener(OnSubtitleClickListener listener) {
        this.listener = listener;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_Sub:
                if (listener != null)
                    listener.onSubTitleClick(position);
                else {
                    splitSubtitleVocabulariesForTranslation();
                }
                break;

            case R.id.close:
                this.setVisibility(GONE);
                Toast.makeText(getContext(), "شما میتوانید از طریق منوی سمت راست بالا، مجددا زیرنویس را فعال کنید.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void splitSubtitleVocabulariesForTranslation() {
        if(onSubtitleTranslateListener != null){
            onSubtitleTranslateListener.onTranslateRequest(getSubtitleVocs());
        }
    }

    private ArrayList<VocabularyModel> getSubtitleVocs() {

        VocabularyModel.setStartSubtitlePosition(subtitleCoordinator.getSubStartTime());
        VocabularyModel.setEndSubtitlePosition(subtitleCoordinator.getSubEndTime());

        String currentSubtitle = subtitleCoordinator.getCurrentSubtitleText() ;
        String[] words = currentSubtitle.split(" ") ;

        String word = "text vocabulary";
        String mean = "معنی آزمایشی";
        VocabularyModel voc;
        ArrayList<VocabularyModel> list = new ArrayList<>();
        for (int i = 0; i < words.length ; i++) {
            voc = new VocabularyModel();
            voc.setVocabulary(words[i]);
            voc.setVocMeaning(" ");
            if (i % 3 == 0)
                voc.setAddedToLeitner(true);
            else
                voc.setAddedToLeitner(false);

            list.add(voc);
        }

        return list;
    }


    public SubtitleCoordinator getSubtitleCoordinator() {
        return subtitleCoordinator;
    }


    public interface OnSubtitleClickListener {
        void onSubTitleClick(int ID);
    }



    /***
     * subtitle coordinator class
     */

    public class SubtitleCoordinator {

        private File subtitleFile;
        private ArrayList subtitleDataList;
        private SrtObject subtitle;
        private String currentSubtitleText;
        private long subStartTime;
        private long subEndTime;

        private SubtitleCoordinator() {

        }

        public long getSubStartTime() {
            return subStartTime;
        }

        public long getSubEndTime() {
            return subEndTime;
        }

        public File getSubtitleFile() {
            return subtitleFile;
        }

        public void setSubtitleFile(File subtitleFile) {
            this.subtitleFile = subtitleFile;

            ib_close.setVisibility(GONE);

            //غیر فعال کردن عملیات کلیک بر روی textview
            setOnSubtitleClickListener(null);

            if (position == SubtitlePosition.top.ordinal()) {
                Toast.makeText(getContext(), "زیرنویس اضافه شد.", Toast.LENGTH_SHORT).show();

            } else if (position == SubtitlePosition.bottom.ordinal()) {
                Toast.makeText(getContext(), "زیرنویس اضافه شد.", Toast.LENGTH_SHORT).show();
            }

            parseSubFile(this.subtitleFile);
        }

        public boolean isSubtitleLoaded() {
            return getSubtitleFile() != null;

        }

        private void parseSubFile(File file) {
            //Read text from file
            StringBuilder text = new StringBuilder();

            try {

                String encoding = getTextFileEncode(file);

                SrtParser parser = new SrtParser(encoding);
                subtitle = parser.parse(new FileInputStream(file));

            } catch (IOException e) {
                //You'll need to add proper error handling here
            } catch (SubtitleParsingException e) {
                e.printStackTrace();
            }

        }

        private String getTextFileEncode(File file) throws IOException {
            // (1)
            UniversalDetector detector = new UniversalDetector(null);

            // (2)
            int nread;
            byte[] buf = new byte[4096];
            FileInputStream fis = new FileInputStream(file);

            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            // (3)
            detector.dataEnd();

            // (4)
            String encoding = detector.getDetectedCharset();
            if (encoding != null) {
                System.out.println("Detected encoding = " + encoding);
            } else {
                Toast.makeText(getContext(), "برنامه قادر به شناسایی نوع انکود(encode) فایل زیرنویس نمیباشد.", Toast.LENGTH_SHORT).show();
            }

            // (5)
            detector.reset();

            return encoding;
        }


        private SubtitleObject getSubtitleObject() {
            return subtitle;
        }


        public void showSubtitle(int currentPosition) {
            if (isSubtitleLoaded()) {
                if (getSubtitleObject() != null && !getSubtitleObject().getCues().isEmpty()) {
                     currentSubtitleText = getCurrentSubtitleText(currentPosition);
                    setSubtitleText(currentSubtitleText);

                }
            }
        }

        private String getCurrentSubtitleText(){
            return currentSubtitleText;
        }

        private String getCurrentSubtitleText(int currentPosition) {
            SubtitleCue cue;
            for (int i = 0; i < getSubtitleObject().getCues().size(); i++) {
                cue = getSubtitleObject().getCues().get(i);
                if (doesCueTimeMatch(cue, currentPosition)) {
                    subStartTime = cue.getStartTime().getTime() ;
                    subEndTime = cue.getEndTime().getTime();
                    return cue.getText();
                }
            }

            return "";
        }

        private boolean doesCueTimeMatch(SubtitleCue cue, int milliseconds) {

            int hours = ((milliseconds / (1000 * 60 * 60)));
            milliseconds = milliseconds - hours * 1000 * 60 * 60;

            int minutes = ((milliseconds / (1000 * 60)));
            milliseconds = milliseconds - minutes * 1000 * 60;

            int seconds = (milliseconds / 1000);


            if (!(hours >= cue.getStartTime().getHour() && hours <= cue.getEndTime().getHour())) {
                return false;
            }

            if (!(minutes >= cue.getStartTime().getMinute() && minutes <= cue.getEndTime().getMinute())) {
                return false;
            }

            if (!(seconds >= cue.getStartTime().getSecond() && seconds <= cue.getEndTime().getSecond())) {
                return false;
            }

            return true;
        }
    }






}
