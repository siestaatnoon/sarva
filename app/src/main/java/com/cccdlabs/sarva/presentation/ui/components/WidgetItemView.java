package com.cccdlabs.sarva.presentation.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cccdlabs.sarva.R;
import com.cccdlabs.sarva.presentation.model.sample.DoodadUiModel;
import com.cccdlabs.sarva.presentation.model.sample.WidgetUiModel;
import com.cccdlabs.sarva.presentation.ui.utils.ViewUtils;

import java.util.List;

import androidx.core.content.ContextCompat;
import timber.log.Timber;

public class WidgetItemView extends RelativeLayout implements View.OnClickListener {

    /**
     * TODO: Convert to Butterknife
     */
    //@BindView(R.id.rl_widget)
    public RelativeLayout widgetView;

    /**
     * TODO: Convert to Butterknife
     */
    //@BindView(R.id.tv_widget_name)
    public TextView nameView;

    /**
     * TODO: Convert to Butterknife
     */
    //@BindView(R.id.tv_widget_description)
    public TextView descriptionView;

    /**
     * TODO: Convert to Butterknife
     */
    //@BindView(R.id.doodad_items)
    public LinearLayout doodadItems;

    /**
     * TODO: Convert to Butterknife
     */
    //@BindView(R.id.btn_widget_menu)
    public ImageButton buttonContract;

    private Context mContext;

    private WidgetUiModel mModel;


    public WidgetItemView(Context context) {
        super(context);
        mContext = context;
    }

    public WidgetItemView(Context context, AttributeSet attr) {
        super(context, attr);
        mContext = context;
    }

    public WidgetItemView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        mContext = context;
    }

    public void bindData(WidgetUiModel model) {
        mModel = model;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            Timber.e("LayoutInflater null value");
            return;
        }

        View widgetLayout = inflater.inflate(R.layout.view_widget, this);

        // TODO: Uncomment for Butterknife
        // ButterKnife.bind(this, widgetLayout);

        widgetView = findViewById(R.id.rl_widget);                      // TODO: Remove for Butterknife
        widgetView.setOnClickListener(this);

        nameView = findViewById(R.id.tv_widget_name);                   // TODO: Remove for Butterknife
        nameView.setText(mModel.getName());

        descriptionView = findViewById(R.id.tv_widget_description);     // TODO: Remove for Butterknife
        descriptionView.setText(mModel.getDescription());

        doodadItems = findViewById(R.id.doodad_items);                  // TODO: Remove for Butterknife
        doodadItems.setVisibility(View.GONE);

        buttonContract = findViewById(R.id.btn_widget_menu);            // TODO: Remove for Butterknife
        buttonContract.setOnClickListener(this);

        List<DoodadUiModel> doodads = mModel.getDoodads();
        if (doodads != null) {
            for (int i = 0; i < doodads.size(); i++) {
                DoodadUiModel item = doodads.get(i);
                RelativeLayout layout = new RelativeLayout(mContext);
                View view = inflater.inflate(R.layout.view_doodad, layout);
                ((TextView)view.findViewById(R.id.tv_doodad_name)).setText(item.getName());
                ((TextView)view.findViewById(R.id.tv_doodad_description)).setText(item.getDescription());

                // every other tights item will have a different background so its easier on the eyes
                if (i % 2 == 0) {
                    view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorLightGrey));
                }

                doodadItems.addView(view);
            }
        }
    }

    @Override
    public void onClick(View v) {
        List<DoodadUiModel> doodads = mModel.getDoodads();
        if (doodads == null || doodads.size() == 0) {
            return;
        }

        if (doodadItems.getVisibility() == View.VISIBLE) {
            buttonContract.setVisibility(View.GONE);
        } else {
            buttonContract.setVisibility(View.VISIBLE);
        }

        ViewUtils.toggleSlide(doodadItems, 500);
    }
}
