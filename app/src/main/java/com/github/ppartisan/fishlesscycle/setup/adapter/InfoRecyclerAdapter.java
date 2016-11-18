package com.github.ppartisan.fishlesscycle.setup.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.setup.model.InfoItem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;


public final class InfoRecyclerAdapter extends RecyclerView.Adapter<InfoRecyclerAdapter.BaseViewHolder> {

    private List<InfoItem> mItems;
    private final OnItemClickListener mListener;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({HEADER, PARENT, CHILD})
    @interface ViewType {}
    private static final int HEADER = 0;
    private static final int PARENT = 1;
    private static final int CHILD = 2;

    public InfoRecyclerAdapter(List<InfoItem> items, OnItemClickListener listener) {
        mItems = items;
        mListener = listener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        int resId;
        View v;
        BaseViewHolder vh = null;

        switch (viewType) {
            case HEADER:
                resId = R.layout.row_suw_info_heading;
                v = inflater.inflate(resId, parent, false);
                vh = new HeaderViewHolder(v);
                break;
            case PARENT:
                resId = R.layout.row_suw_info;
                v = inflater.inflate(resId, parent, false);
                vh = new ParentViewHolder(v, mListener);
                break;
            case CHILD:
                resId = R.layout.row_suw_info_child;
                v = inflater.inflate(resId, parent, false);
                vh = new ChildViewHolder(v);
                break;
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

        final InfoItem item = mItems.get(position);

        holder.content.setText(item.content);

        final int visibility = (item.hasChild()) ? View.VISIBLE : View.INVISIBLE;

        if(!item.isChild() && !item.isHeader()) {
            ((ParentViewHolder)holder).expand.setVisibility(visibility);
        }

    }

    @Override
    public int getItemCount() {
        return (mItems == null) ? 0 : mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        final InfoItem item = mItems.get(position);
        int viewType;
        if(item.isChild()) {
            viewType = CHILD;
        } else if(item.isHeader()) {
            viewType = HEADER;
        } else {
            viewType = PARENT;
        }
        return viewType;
    }

    public InfoItem getInfoItem(int index) {
        return mItems.get(index);
    }

    public void addItem(int index, InfoItem item) {
        mItems.add(index, item);
        notifyItemInserted(index);
    }

    public void removeItem(int index) {
        mItems.remove(index);
        notifyItemRemoved(index);
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder {

        TextView content;

        BaseViewHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.iwus_row_title);
        }

    }

    private static class ParentViewHolder extends BaseViewHolder implements View.OnClickListener{

        ImageButton expand;
        private OnItemClickListener mListener;

        ParentViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);

            mListener = listener;

            expand = (ImageButton) itemView.findViewById(R.id.iwus_row_expand);
            expand.setContentDescription(itemView.getContext().getString(R.string.wus_pl_acc_expand));
            expand.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onItemClick(getAdapterPosition());
        }

    }

    private static class ChildViewHolder extends BaseViewHolder {

        ChildViewHolder(View itemView) {
            super(itemView);
        }

    }

    private static class HeaderViewHolder extends BaseViewHolder {

        HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int adapterPosition);
    }

}
