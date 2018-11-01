# blackjack

## Maven

### Build
```
mvn compile
```

### Run Demo App
```
mvn dependency:copy-dependenciees
java -cp target/classes jp.topse.swdev.bigdata.blackjack.demo.Demo
java -cp target/classes;target/dependency/weka-stable-3.8.1.jar jp.topse.swdev.bigdata.blackjack.demo.Demo
```

## Gradle

```
gradle compileJava
```

## Run Demo App
```
java -cp build/classes/java/main jp.topse.swdev.bigdata.blackjack.demo.Demo
```
