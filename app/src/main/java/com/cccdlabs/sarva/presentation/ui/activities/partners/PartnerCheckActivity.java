package com.cccdlabs.sarva.presentation.ui.activities.partners;

import android.content.Context;

import com.cccdlabs.sarva.presentation.di.HasComponent;
import com.cccdlabs.sarva.presentation.di.components.PartnerComponent;
import com.cccdlabs.sarva.presentation.model.partners.PartnerUiModel;
import com.cccdlabs.sarva.presentation.presenters.partners.PartnerCheckPresenter;
import com.cccdlabs.sarva.presentation.ui.activities.base.BaseAppCompatActivity;
import com.cccdlabs.sarva.presentation.views.partners.PartnerCheckView;

import java.util.List;

import javax.inject.Inject;

public class PartnerCheckActivity extends BaseAppCompatActivity implements PartnerCheckView, HasComponent<PartnerComponent> {

    @Inject PartnerCheckPresenter mPresenter;
    private PartnerComponent mPartnerComponent;
    private Context mContext;

    @Override
    public void onSetPartnerActive(PartnerUiModel uiModel) {

    }

    @Override
    public void showPartners(List<PartnerUiModel> partners) {

    }

    @Override
    public void onEmissionStarted() {

    }

    @Override
    public void onEmissionStopped() {

    }

    @Override
    public Context context() {
        return mContext;
    }

    @Override
    public PartnerComponent getComponent() {
        return mPartnerComponent;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showRetry() {

    }

    @Override
    public void hideRetry() {

    }

    @Override
    public void showError(String message) {
        showMessage(message);
    }
}
