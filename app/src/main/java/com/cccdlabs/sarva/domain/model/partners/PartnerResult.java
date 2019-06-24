package com.cccdlabs.sarva.domain.model.partners;

import androidx.annotation.NonNull;

/**
 * Wrapper class for transferring data from P2P device pub/sub to the UI via an RxJava Flowable.
 * Rather than have separate objects representing the data flow types, this class will handle
 * the following object types passed in the Flowables:
 * <ul>
 * <li>{@link Partner} - A partner object</li>
 * <li>{@link Throwable} - An error occurring during pub/sub but allowing its continuation</li>
 * <li>boolean - True or false indicating if currently P2P publishing</li>
 * </ul>
 * Note that exactly one of the above can be set in this class and passed through the Flowable which
 * leaves just the three options: pass a Partner object, true/false if publishing or a Throwable.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class PartnerResult {

    /**
     * UUID from Partner object if one passed in constructor.
     */
    private final String uuid;

    /**
     * The Partner object if one passed in constructor.
     */
    private final Partner partner;

    /**
     * The Throwable error if one passed in constructor.
     */
    private final Throwable exception;

    /**
     * True if Partner object was passed in constructor.
     */
    private final boolean hasResult;

    /**
     * True if Throwable was passed in constructor.
     */
    private final boolean hasError;

    /**
     * The publish status: publishing, not publishing or invalid access.
     */
    private final PublishStatus publishStatus;

    /**
     * While the publish flag can return true false, if one not passed in this class constructor,
     * then it will be considered an invalid call to access this flag. Hence we have three options:
     * publishing (true), not publishing (false) and invalid (no value).
     */
    public enum PublishStatus {
        /**
         * Is publishing.
         */
        PUBLISHING(1),

        /**
         * Is not publishing.
         */
        NOT_PUBLISHING(0),

        /**
         * Not a valid call.
         */
        INVALID(-1);

        private final int value;

        /**
         * Constructor.
         *
         * @param value The enum value
         */
        PublishStatus(int value) {
            this.value = value;
        }

        /**
         * Probably little use but returns the PublishStatus given an int value.
         * <ul>
         * <li>&gt 0: PUBLISHING</li>
         * <li>0 : NOT_PUBLISHING</li>
         * <li>&lt; : INVALID</li>
         * </ul>
         *
         * @param value
         * @return
         */
        public static PublishStatus valueOf(int value) {
            PublishStatus ps = INVALID;
            if (value == 0) {
                ps = NOT_PUBLISHING;
            } else if (value > 0) {
                ps = PUBLISHING;
            }
            return ps;
        }
    }

    /**
     * Constructor, invalid, throws IllegalArgumentException.
     */
    public PartnerResult() {
        String message = "Class must be instantiated with PartnerResult(Partner), ";
        message += "PartnerResult(Throwable) or PartnerResult(boolean) only";
        throw new IllegalArgumentException(message);
    }

    /**
     * Constructor, sets the {@link Partner} value. If this constructor is not used,
     * {@link #getPartner()}, and {@link #getUuid()} will always return null.
     *
     * @param partner The Partner object
     */
    public PartnerResult(@NonNull Partner partner) {
        uuid = partner.getUuid();
        this.partner = partner;
        exception = null;
        publishStatus = PublishStatus.INVALID;
        hasResult = true;
        hasError = false;
    }

    /**
     * Constructor, sets the {@link PublishStatus} value from boolean parameter. If this
     * constructor is not used, {@link #getPublishStatus()} will always return
     * <code>PublishStatus.INVALID</code>.
     *
     * @param isPublishing True if publishing, false if not
     */
    public PartnerResult(boolean isPublishing) {
        publishStatus = isPublishing ? PublishStatus.PUBLISHING : PublishStatus.NOT_PUBLISHING;
        uuid = null;
        partner = null;
        exception = null;
        hasResult = false;
        hasError = false;
    }

    /**
     * Constructor, sets the {@link Throwable} error value. If this constructor is not used,
     * {@link #getException()} will always return null.
     *
     * @param exception The Throwable error
     */
    public PartnerResult(@NonNull Throwable exception) {
        this.exception = exception;
        uuid = null;
        partner = null;
        publishStatus = PublishStatus.INVALID;
        hasResult = false;
        hasError = true;
    }

    /**
     * Returns the UUID from a {@link Partner}, if set in constructor, otherwise null.
     *
     * @return The Partner UUID
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Returns the {@link Partner}, if set in constructor, otherwise null.
     *
     * @return The Partner object
     */
    public Partner getPartner() {
        return partner;
    }

    /**
     * Returns the {@link Throwable} error, if set in constructor, otherwise null.
     *
     * @return The Throwable error
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * Returns true if a {@link Throwable} was passed in constructor, false if not.
     *
     * @return True if a {@link Throwable} was passed in constructor, false if not
     */
    public boolean hasError() {
        return hasError;
    }

    /**
     * Returns true if a {@link Partner} was passed in constructor, false if not.
     *
     * @return True if a {@link Partner} was passed in constructor, false if not
     */
    public boolean hasResult() {
        return hasResult;
    }

    /**
     * Returns one of the following:
     * <ul>
     * <li><code>PublishStatus.PUBLISHING</code> if <code>true</code> passed into constructor</li>
     * <li><code>PublishStatus.NOT_PUBLISHING</code> if <code>false</code> passed into constructor</li>
     * <li><code>PublishStatus.INVALID</code> if {@link Partner} or {@link Throwable} passed into constructor</li>
     * </ul>
     *
     * @return The PublishStatus enum value
     */
    public PublishStatus getPublishStatus() {
        return publishStatus;
    }
}
