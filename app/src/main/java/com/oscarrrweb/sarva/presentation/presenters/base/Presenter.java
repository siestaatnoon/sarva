package com.oscarrrweb.sarva.presentation.presenters.base;

/**
 * Abstraction for presenters which handles data interaction between Activities and the
 * <code>data</code> package of the application.
 *
 * @author Johnny Spence
 *  * @version 1.0.0
 */
public interface Presenter {
    /**
     * Method that control the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onResume() method.
     */
    void resume();

    /**
     * Method that controls the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onPause() method.
     */
    void pause();

    /**
     * Method that controls the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onStop() method.
     */
    void stop();

    /**
     * Method that control the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onDestroy() method.
     */
    void destroy();

    /**
     * Method that control the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) showError(String) method.
     */
    void onError(String message);
}
