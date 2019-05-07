package com.oscarrrweb.sarva.domain.executor;

import com.oscarrrweb.sarva.domain.executor.base.ThreadScheduler;

/**
 * Thread abstraction for the post-execution thread, typically the UI thread.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public interface MainThread extends ThreadScheduler {}
