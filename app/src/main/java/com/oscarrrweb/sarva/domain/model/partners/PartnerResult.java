package com.oscarrrweb.sarva.domain.model.partners;

import androidx.annotation.NonNull;

public class PartnerResult {

    private final String uuid;
    private final Partner partner;
    private final boolean hasPublished;
    private final Throwable exception;
    private final boolean hasResult;
    private final boolean hasError;

    public PartnerResult() {
        String message = "Class must be instantiated with PartnerResult(Partner) or PartnerResult(Throwable) only";
        throw new IllegalArgumentException(message);
    }

    public PartnerResult(@NonNull Partner partner) {
        uuid = partner.getUuid();
        this.partner = partner;
        exception = null;
        hasPublished = false;
        hasResult = true;
        hasError = false;
    }

    public PartnerResult(boolean hasPublished) {
        this.hasPublished = hasPublished;
        uuid = null;
        partner = null;
        exception = null;
        hasResult = false;
        hasError = false;
    }

    public PartnerResult(@NonNull Throwable exception) {
        this.exception = exception;
        uuid = null;
        partner = null;
        hasPublished = false;
        hasResult = false;
        hasError = true;
    }

    public String getUuid() {
        return uuid;
    }

    public Partner getPartner() {
        return partner;
    }

    public Throwable getException() {
        return exception;
    }

    public boolean hasError() {
        return hasError;
    }

    public boolean hasResult() {
        return hasResult;
    }

    public boolean hasPublished() {
        return hasPublished;
    }
}
