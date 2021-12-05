# NBT API
A NBT library to allow the modification of NBT tags across different Minecraft servers. Currently supported are 1.8 up to 1.18.

## Using NBT API
The project isn't in the Central Repository yet, so specifying a repository is needed.<br>
To add this project as a dependency to your project, add the following to your pom.xml:

### Maven dependency

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
```xml
<dependency>
    <groupId>com.github.Despical</groupId>
    <artifactId>NBTAPI</artifactId>
    <version>1.0.1</version>
    <scope>compile</scope>
</dependency>
```

### Gradle dependency
```
repositories {
    maven { url 'https://jitpack.io' }
}
```
```
dependencies {
    compileOnly group: "com.github.Despical", name: "NBTAPI", version: "1.0.1";
}
```
