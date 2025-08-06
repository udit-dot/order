# Log4j 2 Logging Implementation

This project has been configured with Log4j 2.x for comprehensive logging throughout the application.

## Dependencies

The following Log4j 2 dependencies have been added to `pom.xml`:

```xml
<!-- Log4j 2.x dependencies -->
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-api</artifactId>
    <version>2.20.0</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.20.0</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j-impl</artifactId>
    <version>2.20.0</version>
</dependency>
```

## Configuration

### Log4j 2 Configuration (`src/main/resources/log4j2.xml`)

The logging configuration includes:

1. **Console Appender**: Outputs logs to console
2. **File Appender**: Saves logs to `logs/order-service.log`
3. **Error File Appender**: Saves error logs to `logs/order-service-error.log`
4. **Async Appenders**: For better performance
5. **Rolling Policy**: Logs are rolled over daily and when they reach 10MB

### Log Levels

- **TRACE**: Most detailed logging (SQL parameter binding)
- **DEBUG**: Debug information (application flow)
- **INFO**: General information (startup, operations)
- **WARN**: Warning messages
- **ERROR**: Error messages
- **FATAL**: Fatal error messages

### Logger Configuration

- `com.microservice.order`: DEBUG level for application classes
- `org.springframework`: INFO level for Spring framework
- `org.hibernate.SQL`: DEBUG level for SQL queries
- `org.hibernate.type.descriptor.sql.BasicBinder`: TRACE level for SQL parameters

## Logging Implementation

### Classes with Logging

1. **OrderApplication.java**: Application startup logging
2. **OrderController.java**: Request/response logging with error handling
3. **OrderService.java**: Business logic logging with detailed flow
4. **RestTemplateConfig.java**: Bean creation logging
5. **ModelMapperConfig.java**: Bean creation logging
6. **LoggingUtil.java**: Utility class demonstrating different logging patterns

### Logging Patterns Used

1. **Info Level**: General application flow, successful operations
2. **Debug Level**: Detailed method entry/exit, data transformations
3. **Error Level**: Exception handling with stack traces
4. **Parameterized Logging**: Using `{}` placeholders for better performance
5. **Conditional Logging**: Checking log level before expensive operations

### Example Log Messages

```
2024-01-15 10:30:45 [main] INFO  c.m.o.OrderApplication - Starting Order Service Application...
2024-01-15 10:30:46 [main] INFO  c.m.o.OrderApplication - Order Service Application started successfully
2024-01-15 10:30:47 [http-nio-8085-exec-1] INFO  c.m.o.controller.OrderController - Received request to get order details for ID: 1
2024-01-15 10:30:47 [http-nio-8085-exec-1] DEBUG c.m.o.service.OrderService - Getting order details for ID: 1
2024-01-15 10:30:47 [http-nio-8085-exec-1] INFO  c.m.o.service.OrderService - Successfully retrieved user data: UserDto(id=1, name=John Doe)
```

## Testing Logging

### Test Endpoint

Access `GET /orders/test-logging` to see different logging levels in action.

### Manual Testing

1. Start the application
2. Check console output for startup logs
3. Make API calls to see request/response logging
4. Check `logs/` directory for log files

## Log Files

- `logs/order-service.log`: All application logs
- `logs/order-service-error.log`: Only error logs
- Log files are automatically rolled over daily and when they reach 10MB

## Best Practices Implemented

1. **Use appropriate log levels**: TRACE for detailed debugging, INFO for general flow, ERROR for exceptions
2. **Parameterized logging**: Use `{}` placeholders instead of string concatenation
3. **Exception logging**: Always include the exception object in error logs
4. **Conditional logging**: Check log level before expensive operations
5. **Structured logging**: Include relevant context (IDs, operation names)
6. **Performance logging**: Track operation duration for performance monitoring

## Configuration Properties

Additional logging configuration in `application.properties`:

```properties
logging.config=classpath:log4j2.xml
logging.level.com.microservice.order=DEBUG
logging.level.org.springframework=INFO
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

## Troubleshooting

1. **No logs appearing**: Check if log4j2.xml is in the classpath
2. **Log files not created**: Ensure `logs/` directory exists and is writable
3. **Performance issues**: Consider adjusting async appender configuration
4. **Too many logs**: Adjust log levels in log4j2.xml or application.properties 