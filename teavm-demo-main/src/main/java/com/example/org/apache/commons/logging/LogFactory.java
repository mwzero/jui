package com.example.org.apache.commons.logging;

import java.util.logging.Logger;

public class LogFactory {

	public static Log getLog(Class<?> root) {
		return new SimpleLog(Logger.getLogger(root.getName()));
	}

	public static Log getLog(String root) {
		return new SimpleLog(Logger.getLogger(root));
	}

	@Deprecated
	public static LogFactory getFactory() {
		return new LogFactory() {
		};
	}

	@Deprecated
	public Log getInstance(Class<?> clazz) {
		return getLog(clazz);
	}

	@Deprecated
	public Log getInstance(String name) {
		return getLog(name);
	}

}
