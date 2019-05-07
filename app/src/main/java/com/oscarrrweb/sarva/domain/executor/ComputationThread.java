package com.oscarrrweb.sarva.domain.executor;

import com.oscarrrweb.sarva.domain.executor.base.ThreadScheduler;

/**
 * Thread abstraction for an execution thread for handling large processes such as files
 * or network transfers.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public interface ComputationThread extends ThreadScheduler {}
