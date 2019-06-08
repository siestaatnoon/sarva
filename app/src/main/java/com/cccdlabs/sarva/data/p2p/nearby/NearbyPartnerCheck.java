package com.cccdlabs.sarva.data.p2p.nearby;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.cccdlabs.sarva.data.p2p.nearby.base.AbstractNearbyPartnerEmitter;
import com.cccdlabs.sarva.data.p2p.nearby.utils.NearbyUtils;
import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.domain.model.partners.PartnerMessage;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryException;

import io.reactivex.FlowableEmitter;

public class NearbyPartnerCheck extends AbstractNearbyPartnerEmitter {

    class PartnerCheckMessageListener extends PartnerMessageListener {

        PartnerCheckMessageListener(FlowableEmitter<PartnerResult> emitter) {
            super(emitter, PartnerMessage.Mode.CHECK, true);
        }

        @Override
        public void onFound(Message message) {
            if (checkInvalidState(message)) {
                // error already emitted in
                // Observable so exit
                return;
            }

            Partner model = NearbyUtils.toPartnerModel(message);
            try {
                model = repository.sync(model);
                repository.setActive(model.getUuid());
                model.setActive(true);
            } catch (RepositoryException e) {
                continueWithError(e);
                return;
            }

            emitter.onNext(new PartnerResult(model));
        }

        @Override
        public void onLost(Message message) {
            if (checkInvalidState(message)) {
                // error already emitted in
                // Observable so exit
                return;
            }

            Partner model = NearbyUtils.toPartnerModel(message);
            try {
                model.setActive(false);
                repository.setInactive(model.getUuid());
            } catch (RepositoryException e) {
                continueWithError(e);
                return;
            }

            emitter.onNext(new PartnerResult(model));
        }
    }


    public NearbyPartnerCheck(@NonNull Activity activity, @NonNull PartnerRepository repository) {
        super(activity, repository);
        publish();
    }

    public NearbyPartnerCheck(@NonNull Context context, @NonNull PartnerRepository repository) {
        super(context, repository);
        publish();
    }

    @Override
    protected MessageListener getMessageListener(FlowableEmitter<PartnerResult> emitter) {
        return messageListener == null
            ? new PartnerCheckMessageListener(emitter)
            : messageListener;
    }

    @Override
    protected PartnerMessage.Mode getPublishMode() {
        return PartnerMessage.Mode.CHECK;
    }
}
