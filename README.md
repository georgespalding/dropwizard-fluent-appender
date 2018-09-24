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
* `reconnectionDelay` - Duration (default `30s`
* `acceptConnectionTimeout` - Duration (default `5s`)
* `encoder`
   - `type` - encoder version, one of [`v0`, `v1`]
   - `tag` - Optional tag (default `dropwizard.` + application name)

Example config:
```yaml
logging:
  appenders:
  - type: fluent
    host: ${FLUENTD_HOST}
    port: ${FLUENTD_PORT}
    encoder:
      type: v0
      tag: "dropwizard.TestApplication"
```

*Note:* To make `${vars}` work, it is required that a `SubstitutingSourceProvider` is configured in your application.

### Access Logs
To be implemented

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/)