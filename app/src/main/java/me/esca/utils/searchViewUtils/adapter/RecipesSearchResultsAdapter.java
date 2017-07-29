package me.esca.utils.searchViewUtils.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.util.Util;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import me.esca.R;
import me.esca.model.Recipe;
import me.esca.utils.searchViewUtils.data.SearchResultsEntity;

/**
 * Created by Me on 19/07/2017.
 */

public class RecipesSearchResultsAdapter extends RecyclerView.Adapter<RecipesSearchResultsAdapter.ViewHolder>  {

    private List<SearchResultsEntity> mDataSet = new ArrayList<>();
    private Context viewContext;
    private int mLastAnimatedItemPosition = -1;

    public interface OnItemClickListener{
        void onClick(SearchResultsEntity searchResultsEntity);
    }

    private RecipesSearchResultsAdapter.OnItemClickListener mItemsOnClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mColorName;
        public final TextView mColorValue;
        public final View mTextContainer;
        public final ImageView searchResultImage;
        public final TextView cookName;
        public final TextView cookedBy;


        public ViewHolder(View view) {
            super(view);
            mColorName = (TextView) view.findViewById(R.id.color_name);
            mColorValue = (TextView) view.findViewById(R.id.color_value);
            mTextContainer = view.findViewById(R.id.text_container);
            searchResultImage = (ImageView) view.findViewById(R.id.searchResultImage);
            cookName = (TextView) view.findViewById(R.id.cookName);
            cookedBy = (TextView) view.findViewById(R.id.cooked_by);
        }
    }

    public void swapData(List<SearchResultsEntity> mNewDataSet) {
        mDataSet = mNewDataSet;
        notifyDataSetChanged();
    }

    public void setItemsOnClickListener(RecipesSearchResultsAdapter.OnItemClickListener onClickListener){
        this.mItemsOnClickListener = onClickListener;
    }

    @Override
    public RecipesSearchResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_results_list_item, parent, false);
        viewContext = parent.getContext();
        return new RecipesSearchResultsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipesSearchResultsAdapter.ViewHolder holder, final int position) {

        SearchResultsEntity searchResultsEntity = mDataSet.get(position);
        holder.mColorName.setText(searchResultsEntity.getHeaderContent());
        holder.mColorValue.setText(searchResultsEntity.getDescriptionContent());

        if(searchResultsEntity.getEntityType() == 1){
            holder.mColorName.setTextColor(viewContext.getResources().getColor(R.color.colorAccent, null));
            holder.searchResultImage.setImageResource(R.drawable.icon_cooking);
            holder.cookName.setVisibility(View.VISIBLE);
            holder.cookedBy.setVisibility(View.VISIBLE);
            holder.cookName.setText(searchResultsEntity.getCookName());
        }
        else if(searchResultsEntity.getEntityType() == 2){
            holder.mColorName.setTextColor(viewContext.getResources().getColor(R.color.black, null));
            holder.searchResultImage.setImageResource(R.drawable.profile_photo_cook);
            holder.cookName.setVisibility(View.GONE);
            holder.cookedBy.setVisibility(View.GONE);
        }

        if(mLastAnimatedItemPosition < position){
            animateItem(holder.itemView);
            mLastAnimatedItemPosition = position;
        }

        if(mItemsOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemsOnClickListener.onClick(mDataSet.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    private void animateItem(View view) {
        view.setTranslationY(Util.getScreenHeight((Activity) view.getContext()));
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }
}
