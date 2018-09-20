# dropwizard-fluent-appender

[Dropwizard](http://dropwizard.io/) logging add-on for dropwizard to log using the [fluent-logger](https://github.com/fluent/fluent-logger-node).
This is needed because Dropwizard overwrites the default mechanism for loading logback configuration (logback.xml) in favor of its application.yml files.
Configuration options are strongly inspired by the [dropwizard-logstash-encoder](https://github.com/Wikia/dropwizard-logstash-encoder).
The 0.12.x series of this library is tested against 

## Installation
Maven:
```xml
<dependency>
  <groupId>io.github.georgespalding</groupId>
  <artifactId>dropwizard-fluent-appender</artifactId>
  <version>${dropwizard-fluent-appender.version}</version>
</dependency>
```
(See release tags for the appropriate value for `dropwizard-fluent-appender.version`)

## Usage
### Application Logs
Configure dropwizard to use these appenders in your application.yml file:
```yml
logging:
  appenders:
    - type: fluent
      ...
```

Additional configuration keys for the appender
* `host` - string (default `localhost`)
* `port` - int (default `24224`)
* `include` Set of `IncludeOptions` (default [`Context`, `MDC`]). Options are:
   - `Context` - values from logger context
   - `MDC` - values from Message Diagnostic Context
   - `CallerData` - Stacktrace of the log-call-site, needs to be configured   
* `customFields` - map - key values hard coded in the config (default `{}`)

Example config:
```yaml
logging:
  appenders:
  - type: fluent
    tag: "dropwizard.TestApplication"
    include: 
      - Context
      - MDC
    customFields:
      realm: "production"
    sender:
      type: raw-socket
      host: ${LOGSTASH_HOST}
      port: ${LOGSTASH_PORT}
```

*Note:* To make `${vars}` work, it is required that a `SubstitutingSourceProvider` is configured in your application.

### Access Logs
To be implemented

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/)