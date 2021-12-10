# Log4j2-Vaccine
一款用于`log4j2`漏洞的疫苗，基于`Instrumentation`机制进行RASP防护，Patch了
`org.apache.logging.log4j.core.net.JndiManager`的`lookup`方法，部分代码借用了`arthas`的实现

## Usage
启动Loader
```
java -jar loader.jar --agent agent.jar
```

选择需要Patch的进程(输入序号即可)
```
[INFO] Found existing java process, please choose one and hit RETURN.
* [1]: 50508 log4j2vuln3-0.0.1-SNAPSHOT.jar

```

Patch成功
![](https://dinfinite.oss-cn-beijing.aliyuncs.com/image/20211210162759.png)
