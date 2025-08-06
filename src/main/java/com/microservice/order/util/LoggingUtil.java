package com.microservice.order.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for demonstrating different logging patterns and levels
 */
public class LoggingUtil {
    
    private static final Logger logger = LogManager.getLogger(LoggingUtil.class);
    
    /**
     * Demonstrates different logging levels
     */
    public static void demonstrateLoggingLevels() {
        logger.trace("This is a TRACE level message - most detailed logging");
        logger.debug("This is a DEBUG level message - useful for debugging");
        logger.info("This is an INFO level message - general information");
        logger.warn("This is a WARN level message - warning information");
        logger.error("This is an ERROR level message - error information");
        logger.fatal("This is a FATAL level message - fatal error information");
    }
    
    /**
     * Demonstrates logging with parameters
     */
    public static void logWithParameters(String operation, String userId, Object result) {
        logger.info("Operation: {}, User ID: {}, Result: {}", operation, userId, result);
    }
    
    /**
     * Demonstrates logging exceptions
     */
    public static void logException(String operation, Exception e) {
        logger.error("Error occurred during operation: {}", operation, e);
    }
    
    /**
     * Demonstrates conditional logging
     */
    public static void conditionalLogging(boolean isDebugEnabled, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug("Debug message: {}", message);
        }
        
        if (logger.isTraceEnabled()) {
            logger.trace("Trace message: {}", message);
        }
    }
    
    /**
     * Demonstrates performance logging
     */
    public static void logPerformance(String operation, long startTime) {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        logger.info("Operation '{}' completed in {} ms", operation, duration);
    }
} 