package com.oscarrrweb.sarva.data.p2p.nearby;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.oscarrrweb.sarva.data.p2p.nearby.base.AbstractNearbyPartnerEmitter;
import com.oscarrrweb.sarva.data.p2p.nearby.utils.NearbyUtils;
import com.oscarrrweb.sarva.data.repository.partners.PartnerRepository;
import com.oscarrrweb.sarva.domain.model.partners.Partner;
import com.oscarrrweb.sarva.domain.model.partners.PartnerMessage;
import com.oscarrrweb.sarva.domain.model.partners.PartnerResult;
import com.oscarrrweb.sarva.domain.repository.exception.RepositoryException;

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
        publish(PartnerMessage.Mode.CHECK);
    }

    public NearbyPartnerCheck(@NonNull Context context, @NonNull PartnerRepository repository) {
        super(context, repository);
        publish(PartnerMessage.Mode.CHECK);
    }

    @Override
    protected MessageListener getMessageListener(FlowableEmitter<PartnerResult> emitter) {
        return messageListener == null
            ? new PartnerCheckMessageListener(emitter)
            : messageListener;
    }
}
