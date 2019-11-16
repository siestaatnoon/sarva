package com.cccdlabs.sarva.presentation.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cccdlabs.sarva.R;
import com.cccdlabs.sarva.presentation.model.partners.PartnerUiModel;
import com.cccdlabs.sarva.presentation.ui.listeners.RecyclerViewClickListener;
import com.cccdlabs.sarva.presentation.views.partners.PartnerCheckView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PartnerCheckAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RecyclerViewClickListener {

    private List<PartnerUiModel> mPartnerList;
    private Context mContext;
    private PartnerCheckView mView;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView userImage;
        private TextView username;
        private TextView status;
        private ImageButton addButton;
        private ImageButton deleteButton;
        private ImageView checkImage;
        private Context context;
        private RecyclerViewClickListener listener;
        private View view;

        public ViewHolder(Context context, View v, final RecyclerViewClickListener listener) {
            super(v);
            this.context = context;
            view = v;
            this.listener = listener;
            view.setOnClickListener(this);

            userImage = view.findViewById(R.id.iv_user_image);
            username = view.findViewById(R.id.tv_username);
            status = view.findViewById(R.id.tv_status);
            addButton = view.findViewById(R.id.btn_partner_add);
            deleteButton = view.findViewById(R.id.btn_partner_delete);
        }

        public void bind(@NonNull PartnerUiModel uiModel) {
            username.setText(uiModel.getUsername());
            if (!uiModel.isActive()) {
                setNotActiveState();
            } else if (!uiModel.isEmitting()) {
                setNotFoundState();
            } else {
                setFoundState();
            }
        }

        @Override
        public void onClick(View v) {
            listener.onClickView(getAdapterPosition());
        }

        private void setFoundState() {
            Resources resources = context.getResources();
            int colorGreen = resources.getColor(R.color.colorGreen);
            userImage.setColorFilter(colorGreen, PorterDuff.Mode.MULTIPLY);
            username.setTextColor(colorGreen);
            status.setTextColor(colorGreen);
            status.setText(resources.getString(R.string.partner_state_found));
            addButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            checkImage.setColorFilter(colorGreen, PorterDuff.Mode.MULTIPLY);
        }

        private void setNotFoundState() {
            Resources resources = context.getResources();
            int colorRedFaded = context.getResources().getColor(R.color.colorRedFaded);
            userImage.setColorFilter(colorRedFaded, PorterDuff.Mode.MULTIPLY);
            username.setTextColor(colorRedFaded);
            status.setTextColor(colorRedFaded);
            status.setText(resources.getString(R.string.partner_state_not_found));
            addButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            checkImage.setColorFilter(colorRedFaded, PorterDuff.Mode.MULTIPLY);
        }

        private void setNotActiveState() {
            Resources resources = context.getResources();
            int colorWhite = resources.getColor(R.color.colorWhite);
            int colorWhiteFaded = context.getResources().getColor(R.color.colorWhiteAlpha25);
            userImage.setColorFilter(colorWhiteFaded, PorterDuff.Mode.MULTIPLY);
            username.setTextColor(colorWhite);
            status.setTextColor(colorWhite);
            status.setText(resources.getString(R.string.partner_state_not_active));
            addButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.VISIBLE);
            checkImage.setColorFilter(colorWhiteFaded, PorterDuff.Mode.MULTIPLY);
        }
    }


    @Inject
    public PartnerCheckAdapter(@NonNull Context context, @NonNull PartnerCheckView view) {
        mView = view;
        mContext = context;
        mPartnerList = new ArrayList<>();
    }

    public void addItems(@NonNull List<PartnerUiModel> modelList) {
        mPartnerList.clear();
        mPartnerList = modelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cardview_partner_check, parent, false);
        return new ViewHolder(mContext, view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        PartnerUiModel uiModel = mPartnerList.get(position);
        ((ViewHolder) viewHolder).bind(uiModel);
    }

    @Override
    public int getItemCount() {
        return mPartnerList.size();
    }

    @Override
    public void onClickView(int position) {
        PartnerUiModel uiModel = mPartnerList.get(position);
        if (!uiModel.isActive()) {
            mView.onClickPartnerDelete(uiModel);
        }
    }
}
