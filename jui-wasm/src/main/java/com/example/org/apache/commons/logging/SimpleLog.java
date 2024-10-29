package com.example.org.apache.commons.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleLog implements Log {

	private final Logger logger;

	public SimpleLog(Logger logger) {
		this.logger = logger;
	}

	@Override
	public boolean isFatalEnabled() {
		return logger.isLoggable(Level.SEVERE);
	}

	@Override
	public boolean isErrorEnabled() {return logger.isLoggable(Level.SEVERE);}

	@Override
	public boolean isWarnEnabled() {return logger.isLoggable(Level.WARNING);}

	@Override
	public boolean isInfoEnabled() {return logger.isLoggable(Level.INFO);}

	@Override
	public boolean isDebugEnabled() {return logger.isLoggable(Level.FINE);}

	@Override
	public boolean isTraceEnabled() {return logger.isLoggable(Level.FINER);}

	@Override
	public void fatal(Object message) {
		logger.severe(message.toString());
	}

	@Override
	public void fatal(Object message, Throwable t) {
		logger.severe(message.toString() + "(" + t + ")");
	}

	@Override
	public void error(Object message) {
		logger.severe(message.toString());
	}

	@Override
	public void error(Object message, Throwable t) {
		logger.severe(message.toString() + "(" + t + ")");
	}

	@Override
	public void warn(Object message) {
		logger.warning(message.toString());
	}

	@Override
	public void warn(Object message, Throwable t) {
		logger.warning(message.toString() + "(" + t + ")");
	}

	@Override
	public void info(Object message) {
		logger.info(message.toString());
	}

	@Override
	public void info(Object message, Throwable t) {
		logger.info(message.toString() + "(" + t + ")");
	}

	@Override
	public void debug(Object message) {
		logger.fine(message.toString());
	}

	@Override
	public void debug(Object message, Throwable t) {
		logger.fine(message.toString() + "(" + t + ")");
	}

	@Override
	public void trace(Object message) {
		logger.finer(message.toString());
	}

	@Override
	public void trace(Object message, Throwable t) {
		logger.finer(message.toString() + "(" + t + ")");
	}

}
