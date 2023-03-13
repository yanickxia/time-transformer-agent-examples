<img align="right" src="/timetransformer.png?raw=true" alt="TimeTranformer logo">

The `TimeTransformer` is a Java agent to manipulate the time returned by `System.currentTimeMillis()` and `System.nanoTime()`. See also [the example project](https://github.com/TOPdesk/time-transformer-examples).

# Usage:
## Build Agent

```bash
mvn package
```

## Run jar with agent
```bash
FAKE_FIXED_TIME=2050-01-01T00:00:00Z java -javaagent:<agent.jar>  -jar <your jar>
```

or 

```bash
FAKE_OFFSET_TIME='+PT15M' java -javaagent:<agent.jar>  -jar <your jar>
```

[`Duration format`](http://www.java2s.com/Tutorials/Java/java.time/Duration/2960__Duration.toString_.htm)
