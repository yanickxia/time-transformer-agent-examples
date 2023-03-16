<img align="right" src="/timetransformer.png?raw=true" alt="TimeTranformer logo">

The `TimeTransformer` is a Java agent to manipulate the time returned by `System.currentTimeMillis()` and `System.nanoTime()`. See also [the example project](https://github.com/TOPdesk/time-transformer-examples).

and over [libfaketime](https://github.com/wolfcw/libfaketime)


# Usage:
## Build Agent

```bash
mvn package
```

## Run jar with agent
```bash
AGENT_LD_PRELOAD=<your libefaketime path> java -javaagent:<agent.jar>  -jar <your jar>
```


# demo\

`simple-1.0-SNAPSHOT` just echo current time.

```bash
docker run --rm -it -e AGENT_LD_PRELOAD=/usr/local/lib/faketime/faketime.so \
-v faketime.so:/usr/local/lib/faketime/faketime.so \
-v time-transformer-agent-1.4.1-SNAPSHOT.jar:/time-transformer-agent-1.4.1-SNAPSHOT.jar \
-e JAVA_TOOL_OPTIONS=-javaagent:/time-transformer-agent-1.4.1-SNAPSHOT.jar \
-e FAKETIME="@2026-01-05 10:30:00" \
-v simple-1.0-SNAPSHOT.jar:/simple-1.0-SNAPSHOT.jar  \
adoptopenjdk/openjdk8:alpine-jre \
java -jar /simple-1.0-SNAPSHOT.jar
```

# How it works
1. Agent init will modify AGENT_LD_PRELOAD -> LD_PRELOAD
2. Agent call System command `date`, get libfaketime time
3. Agent compute realNow and fakeTimeNow duration saved
4. everytime call time will add duration