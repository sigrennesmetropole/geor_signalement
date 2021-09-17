package org.georchestra.signalement.service.common;

/**
 * Class with all available error messages
 * They can be used when errors are thrown
 * and be catch in the front-end with a
 * translation
 */
public final class ErrorMessageConstants {
    private ErrorMessageConstants() {
    }

    /**
     * Error message when a null object is provided
     */
    public final static String NULL_OBJECT = "serverErrors.nullData";

    /**
     * Error message when an non nullable attribute of an object is null
     * <br/><i>Example :</i> A User with a null login
     */
    public final static String NULL_ATTRIBUTE = "serverErrors.nullAttribute";

    /**
     * Error message when an attribute of an object is illegal
     * <br/><i>Example :</i> An UserRoleContext which has a not existing User
     */
    public final static String ILLEGAL_ATTRIBUTE = "serverErrors.illegalAttribute";

    /**
     * Error message when a data already exists
     */
    public final static String ALREADY_EXISTS = "serverErrors.alreadyExists";

    /**
     * Error message when a data doesn't exist and a null return is not allowed
     * <br/><i>Example :</i> Try to delete a data which doesn't exist should raise this
     */
    public final static String NOT_AVAILABLE = "serverErrors.notAvailable";

    /**
     * Error message when an object is used and this forbids this required action
     * <br/><i>Example :</i> An object is required to be delete but it is used
     */
    public final static String USED_OBJECT = "serverErrors.usedObject";
}
