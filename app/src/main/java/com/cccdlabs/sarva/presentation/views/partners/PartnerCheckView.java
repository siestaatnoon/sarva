package com.cccdlabs.sarva.presentation.views.partners;

import com.cccdlabs.sarva.presentation.model.partners.PartnerUiModel;
import com.cccdlabs.sarva.presentation.views.base.PartnerEmitterView;

public interface PartnerCheckView extends PartnerEmitterView {
    void onSetPartnerActive(PartnerUiModel uiModel);
}
