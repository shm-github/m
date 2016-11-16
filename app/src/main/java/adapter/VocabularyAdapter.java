package adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hossein.movieleitner.R;

import java.util.ArrayList;

import model.VocabularyModel;

/**
 * Created by Hossein on 11/8/2016.
 */

public class VocabularyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int TYPE_FOOTER = 2;
    private final Context context;
    private final ArrayList<VocabularyModel> vocabularies;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private OnClickListener onClickListener;

    public VocabularyAdapter(Context context, ArrayList<VocabularyModel> vocabularies) {
        this.context = context;
        this.vocabularies = vocabularies;
    }

    protected Context getContext() {
        return context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == TYPE_ITEM) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.vocabularies_recycler_view_row, parent, false);
            return new VHItem(itemView);
        } else if (viewType == TYPE_HEADER) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.vocabularies_recycler_view_header, parent, false);
            return new VHHeader(itemView);
        } else if (viewType == TYPE_FOOTER) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.vocabularies_recycler_view_footer, parent, false);
            return new VHFooter(itemView);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VHItem)
            ((VHItem) holder).setDataToItemView(position);
//        else if (holder instanceof VHHeader)
//            ((VHHeader) holder).saveDataToItemView(position);
//        else if (holder instanceof VHFooter)
//            ((VHFooter) holder).saveDataToItemView(position);
    }


    @Override
    public int getItemCount() {
        return vocabularies.size() + 2;
    }

    private VocabularyModel getItem(int position) {
        return vocabularies.get(position - 1);
    }


    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        else if (isPositionFooter(position))
            return TYPE_FOOTER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }


    private boolean isPositionFooter(int position) {
        return position == vocabularies.size() + 1;
    }

    public void setOnClickListener(VocabularyAdapter.OnClickListener listener) {
        this.onClickListener = listener;
    }

    public interface OnClickListener {
        void onClick(int id);
    }


    private class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView tv_voacabulary;
        private final TextView tv_vocMeaning;
        private final TextView tv_addToLeitner;
        private final View row_wrapper;
        private VocabularyModel vocabularyModel;
        private int position;
        private boolean addedToLeitner;

        VHItem(View itemView) {
            super(itemView);

            tv_voacabulary = (TextView) itemView.findViewById(R.id.vocabulary);
            tv_vocMeaning = (TextView) itemView.findViewById(R.id.meaning);
            tv_addToLeitner = (TextView) itemView.findViewById(R.id.add_to_leitner);
            row_wrapper = itemView.findViewById(R.id.row_wrapper);

            tv_voacabulary.setOnClickListener(this);
            tv_vocMeaning.setOnClickListener(this);
            tv_addToLeitner.setOnClickListener(this);

        }

        void setDataToItemView(int position) {
            this.position = position;
            vocabularyModel = getItem(position);

            if (vocabularyModel.isAddedToLeitner()) {
                addedToLeitner = true;
                row_wrapper.setBackgroundColor(Color.LTGRAY);
                tv_addToLeitner.setText(" کلمه به لایتنر افزوده شده!");
            }


            tv_voacabulary.setText(vocabularyModel.getVocabulary());
            tv_vocMeaning.setText(vocabularyModel.getVocMeaning());

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.vocabulary:
                case R.id.meaning:

                    break;

                case R.id.add_to_leitner:
                    if (!addedToLeitner) {
                        tv_addToLeitner.setOnClickListener(null);
                        tv_addToLeitner.setText("افزوده شد!");
                        row_wrapper.setBackgroundColor(Color.GREEN);
                    } else {
                        Toast.makeText(context, "کلمه از قبل به لایتنر افزوده شده", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    private class VHHeader extends RecyclerView.ViewHolder implements View.OnClickListener {

        VHHeader(View itemView) {
            super(itemView);
        }

        void saveDataToItemView(int position) {

        }

        @Override
        public void onClick(View v) {

        }


    }


    private class VHFooter extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final View tv_addVideoSectionToLeitner;
        private final View tv_continueMovie;

        VHFooter(View itemView) {
            super(itemView);
            tv_continueMovie = itemView.findViewById(R.id.continue_movie);
            tv_addVideoSectionToLeitner = itemView.findViewById(R.id.add_movie_part_to_leitner);
            tv_continueMovie.setOnClickListener(this);
            tv_addVideoSectionToLeitner.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {

            if (onClickListener != null)
                onClickListener.onClick(v.getId());
        }
    }


}
