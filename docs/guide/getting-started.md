# 快速开始

只需 5 分钟，你就能将第一个声明式 AI Agent 工作流运行起来。

本指南将带你构建一个最基础的 **"Hello World"** 图。为了演示 **"节点即适配器 (Node as Adaptor)"** 的核心理念，我们将模拟一个简单的业务场景：接收用户名字，调用 Service 处理，然后返回问候语。

## 1. 环境准备

在开始之前，请确保你的开发环境满足以下要求：

* **JDK**: 17 或更高版本
* **Spring Boot**: 3.3.x 或 3.4.x
* **Spring AI Alibaba**: 1.1.0.0-M4 (当前验证版本)

::: tip 版本说明
虽然 spring-ai-alibaba 官方已发布更新的 RC 版本，但本库核心功能目前已在 `1.1.0.0-M4` 环境下完成完整测试。
为了获得最稳定的体验，建议您在 `pom.xml` 中显式指定该版本。
:::

## 2. 引入依赖

将 `saa-graph-composer` 添加到你的项目中。

::: code-group

```xml [Maven]
<dependency>
    <groupId>cn.teacy</groupId>
    <artifactId>saa-graph-composer</artifactId>
    <version>0.1.0</version>
</dependency>

```

```groovy [Gradle]
implementation 'cn.teacy:saa-graph-composer:0.1.0'

```

:::

## 3. 编写业务逻辑 (Service)

我们倡导 **关注点分离**。请不要在图编排层直接编写业务逻辑，而是应该定义一个标准的 Spring Service。

```java
package com.example.service;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    public String generateGreeting(String name) {
        // 模拟一个耗时的 AI 或业务操作
        return "Hello, " + name + "! Welcome to SAA Graph Composer.";
    }
}

```

## 4. 编写图编排 (Composer)

现在，我们使用 **声明式注解** 来组装这个图。

注意看，`HelloWorldGraphComposer` 类充当了 **路由层** 的角色。它注入了 `GreetingService`，并将节点定义为服务的适配器。

```java
package com.example.graph;

import cn.teacy.ai.annotation.GraphComposer;
import cn.teacy.ai.annotation.GraphKey;
import cn.teacy.ai.annotation.GraphNode;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.example.service.GreetingService;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

// 1. 定义目标 Bean 名称，方便在其他地方注入
@GraphComposer(targetBeanName = "hello_world_graph")
public class HelloWorldGraphComposer {

    // === 常量定义区 ===

    @GraphKey // 标记这是状态中的 Key
    public static final String KEY_INPUT = "name";

    @GraphKey
    public static final String KEY_OUTPUT = "result";

    private static final String NODE_SAY_HELLO = "say_hello";

    // === 依赖注入区 ===

    private final GreetingService greetingService;

    // 构造器注入 Service
    public HelloWorldGraphComposer(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    // === 图结构定义区 ===

    // 定义节点：接收状态 -> 调用 Service -> 返回结果
    @GraphNode(id = NODE_SAY_HELLO, isStart = true, next = StateGraph.END)
    public AsyncNodeAction sayHello = state -> {
        // 使用常量提取参数
        String name = (String) state.value(KEY_INPUT).orElse("World");

        // 委托 Service 执行业务
        String result = greetingService.generateGreeting(name);

        // 返回异步结果
        return CompletableFuture.completedFuture(Map.of(KEY_OUTPUT, result));
    };
}

```

::: tip ✨ 最佳实践
在这个例子中，`sayHello` 节点仅仅包含几行**胶水代码**。这正是我们推荐的 **Node as Adaptor** 模式——保持编排层的轻量与纯粹。
:::
::: tip ✨ 最佳实践
虽然直接使用字符串（如 "say_hello"）也能工作，但我们强烈建议定义 static final 常量。这样做不仅能避免拼写错误，还能让 Composer 类成为一份自解释的图状态文档。 
:::

## 5. 运行与测试

`saa-graph-composer` 会自动扫描 `@GraphComposer` 注解，并将编译好的图注册为 Spring Bean。你可以直接注入并运行它。

```java
package com.example;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
public class GraphTest {

    @Autowired
    // 注入时使用注解中定义的 ID
    @Qualifier("hello_world_graph")
    private CompiledGraph graph;

    @Test
    public void testRun() throws Exception {
        // 1. 准备初始输入
        Map<String, Object> input = Map.of("name", "Developer");

        // 2. 执行图
        Map<String, Object> result = graph.invoke(input);

        // 3. 验证结果
        System.out.println("输出结果: " + result.get("result"));
        // Output: Hello, Developer! Welcome to SAA Graph Composer.
    }
}

```

## 下一步

恭喜！你已经成功运行了第一个声明式 Graph。

但这只是开始，接下来你可以探索更强大的功能：

* **[注解详解](../reference/configuration)**: 查阅 `@GraphComposer` 和 `@GraphNode` 等的所有参数。