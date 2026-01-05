# 全局配置

`@EnableGraphComposer` 是 SAA Graph Composer 的启动开关。它的主要职责是激活 `GraphAutoRegistrar`，扫描指定包路径下的 `@GraphComposer` 组件，并将它们编译为可执行的 `CompiledGraph` Bean 注册到 Spring 容器中。

通常标注在 Spring Boot 的启动类或配置类上。

## 属性说明

| 属性 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `basePackages` | `String[]` | `{}` (空数组) | 指定要扫描的包路径。如果不指定，默认扫描**被标注类所在的包及其子包**。 |

## 使用示例

### 1. 默认扫描 (推荐)

最简配置。默认扫描 `MyApplication` 所在包（`com.example`）及其所有子包。

```java
package com.example;

import cn.teacy.ai.annotation.EnableGraphComposer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableGraphComposer // 👈 开启扫描
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}

```

### 2. 指定包路径

如果你的图定义类位于完全不同的包结构中，需要显式指定。

```java
@Configuration
@EnableGraphComposer(basePackages = {
    "com.example.module_a.graphs",
    "com.thirdparty.lib.graphs"
})
public class AppConfig {
    // ...
}

```

## Bean 注册与命名策略

当扫描到一个带有 `@GraphComposer` 注解的类时，框架会在 Spring 容器中注册**两个** Bean。了解这一机制对于依赖注入非常重要。

### 1. 注册的 Bean

假设你有一个图定义类 `MyHelloComposer`：

```java
@GraphComposer
public class MyHelloComposer { ... }

```

框架会执行以下操作：

1. **注册蓝图 Bean**：将 `MyHelloComposer` 自身注册为 Bean（如果它还不是 Bean）。
2. **注册编译图 Bean**：调用内部的 `GraphBuilder`，将蓝图编译为 `CompiledGraph` 类型的 Bean。

### 2. 编译图 Bean 的命名规则

为了方便注入，框架按照以下优先级来生成 `CompiledGraph` Bean 的名称：

1. **优先级 1（显式指定）**: 如果 `@GraphComposer` 注解中指定了 `targetBeanName` 属性，则直接使用该名称。

1. **优先级 2（去除后缀）**: 如果类名以 `Composer` 结尾，自动去除该后缀。

   * 类名：`LogAnalysisGraphComposer`
   * Bean 名：`logAnalysisGraph` (首字母小写)

1. **优先级 3（添加后缀）**: 如果类名不以 `Composer` 结尾，自动添加 `Compiled` 后缀。

   * 类名：`MyWorkflow`
   * Bean 名：`myWorkflowCompiled`

### 3. 注入示例

基于上述命名规则，你可以直接在业务代码中注入编译好的图：

```java
@Service
public class RunService {

    // 假设类名为 LogAnalysisGraphComposer
    // 自动生成的 Bean 名称为 "logAnalysisGraph"
    @Autowired
    @Qualifier("logAnalysisGraph") 
    private CompiledGraph logGraph;

    public void run() {
        logGraph.invoke(Map.of());
    }
}

```

::: warning ⚠️ 关于 IDE 报错提示 
由于 `CompiledGraph` Bean 是通过 `GraphAutoRegistrar` 在运行时动态注册的，IDE 的静态代码分析器可能无法识别这些 Bean，从而提示 `Could not autowire. No beans of 'CompiledGraph' type found`。

这不会影响实际运行。如果你希望获得更严格的类型检查并完全消除此提示，可以参考 [**手动与动态编译**](../advanced/dynamic-compilation.md) 手动在配置类中注册 Bean。
:::

::: tip 🔍 调试技巧
如果你不确定生成的 Bean 名称是什么，可以开启 Spring 的 Debug 日志，搜索 `GraphAutoRegistrar` 相关的注册信息。
:::