# concurrent-collections

This project contains a concurrent hash map which is optimized for the method computeIfAbsent.
The algorithm is described in [this blog post](https://vmlens.com/articles/cp/computeIfAbsent_hashMap/). A benchmark is [available here](https://github.com/vmlens/concurrent-collections-benchmark).
And you can find the [Java doc here](https://vmlens.com/apidocs/concurrent-collections/).  

# Download

Use the following dependency for maven:

```xml
<dependency>
  <groupId>com.vmlens</groupId>
  <artifactId>concurrent-collections</artifactId>
  <version>1.0.0</version>
</dependency>
```

You can download the binaries [from maven central here](https://search.maven.org/remotecontent?filepath=com/vmlens/concurrent-collections/1.0.0/concurrent-collections-1.0.0.jar)
