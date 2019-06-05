package com.cccdlabs.sarva.domain.executor;

import com.cccdlabs.sarva.domain.executor.base.ThreadScheduler;

/**
 * Thread abstraction for the post-execution thread, typically the UI thread.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public interface MainThread extends ThreadScheduler {}
