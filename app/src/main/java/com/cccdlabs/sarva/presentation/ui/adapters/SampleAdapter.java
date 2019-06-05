package com.cccdlabs.sarva.presentation.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cccdlabs.sarva.R;
import com.cccdlabs.sarva.presentation.model.sample.GizmoUiModel;
import com.cccdlabs.sarva.presentation.model.sample.WidgetUiModel;
import com.cccdlabs.sarva.presentation.ui.components.WidgetItemView;
import com.cccdlabs.sarva.presentation.ui.listeners.RecyclerViewClickListener;
import com.cccdlabs.sarva.presentation.ui.utils.ViewUtils;
import com.cccdlabs.sarva.presentation.views.MainView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SampleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RecyclerViewClickListener {

    private List<GizmoUiModel> mModelList;
    private Context mContext;
    private MainView mView;
    List<ViewHolder> mViewHolders;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**
         * TODO: Convert to Butterknife
         */
        //@BindView(R.id.rl_gizmo)
        final public RelativeLayout gizmoView;

        /**
         * TODO: Convert to Butterknife
         */
        //@BindView(R.id.tv_gizmo_name)
        final public TextView nameView;

        /**
         * TODO: Convert to Butterknife
         */
        //@BindView(R.id.tv_gizmo_description)
        final public TextView descriptionView;

        /**
         * TODO: Convert to Butterknife
         */
        //@BindView(R.id.widget_items)
        final public LinearLayout widgetItems;

        /**
         * TODO: Convert to Butterknife
         */
        //@BindView(R.id.btn_gizmo_menu)
        final public ImageButton buttonContract;

        private RecyclerViewClickListener mListener;

        public ViewHolder(final View view, final RecyclerViewClickListener listener) {
            super(view);
            mListener = listener;

            // TODO: Uncomment for Butterknife
            // ButterKnife.bind(this, view);

            // TODO: Remove the following for Butterknife
            gizmoView = view.findViewById(R.id.rl_gizmo);
            nameView = view.findViewById(R.id.tv_gizmo_name);
            descriptionView = view.findViewById(R.id.tv_gizmo_description);
            widgetItems = view.findViewById(R.id.widget_items);
            buttonContract = view.findViewById(R.id.btn_gizmo_menu);

            gizmoView.setOnClickListener(this);
            buttonContract.setOnClickListener(this);
        }

        public void bindData(final GizmoUiModel gizmo) {
            Context context = nameView.getContext();
            nameView.setText(gizmo.getName());
            descriptionView.setText(gizmo.getDescription());
            widgetItems.setVisibility(View.GONE);

            widgetItems.removeAllViews();
            List<WidgetUiModel> widgets = gizmo.getWidgets();
            if (widgets != null) {
                for (WidgetUiModel item: widgets) {
                    WidgetItemView view = new WidgetItemView(context);
                    view.bindData(item);
                    widgetItems.addView(view);
                }
            }
        }

        @Override
        public void onClick(View v) {
            mListener.onClickView(getAdapterPosition());
        }
    }

    public SampleAdapter(MainView view, Context context) {
        mView = view;
        mContext = context;
        mModelList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.card_gizmo, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        GizmoUiModel item = mModelList.get(position);
        ((ViewHolder) viewHolder).bindData(item);
        if (position < mViewHolders.size()) {
            mViewHolders.remove(position);
        }
        mViewHolders.add(position, (ViewHolder) viewHolder);
    }

    @Override
    public int getItemCount() {
        return mModelList.size();
    }

    @Override
    public void onClickView(int position) {
        ViewHolder viewHolder = mViewHolders.get(position);

        if (viewHolder.widgetItems.getVisibility() == View.VISIBLE) {
            viewHolder.buttonContract.setVisibility(View.GONE);
        } else {
            viewHolder.buttonContract.setVisibility(View.VISIBLE);
        }

        // closes up the child doodads of the widgets, nice and tidy
        int count = viewHolder.widgetItems.getChildCount();
        for (int i=0; i < count; i++) {
            WidgetItemView view = (WidgetItemView) viewHolder.widgetItems.getChildAt(i);
            if (view.doodadItems.getVisibility() == View.VISIBLE) {
                view.onClick(view);
            }
        }

        ViewUtils.toggleSlide(viewHolder.widgetItems, 500);

        // Boy does this wreak havoc
        // notifyItemChanged(position);
    }

    public void addItems(@NonNull List<GizmoUiModel> modelList) {
        mModelList.clear();
        mModelList = modelList;
        mViewHolders = new ArrayList<>(mModelList.size());
        notifyDataSetChanged();
    }
}
