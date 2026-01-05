# 生命周期钩子

除了静态的注解定义外，SAA Graph Composer 提供了 `GraphBuildLifecycle` 接口，允许你在图被最终编译成 `CompiledGraph` 之前，直接介入并修改底层的 `StateGraph` 构建器。

这为开发者提供了极大的灵活性，不仅可以解决注解无法表达的复杂连线问题，还能实现动态构建、结构校验等高级需求。

## 接口定义

实现该接口的 `@GraphComposer` 类将在扫描解析完成后、编译生成 `CompiledGraph` 之前，自动触发 `beforeCompile` 方法。

```java
public interface GraphBuildLifecycle {
    /**
     * 在编译前触发，暴露底层的 StateGraph 构建器。
     * 你可以在此方法中补充节点、添加连线或进行校验。
     *
     * @param builder 底层图构建器
     * @throws GraphStateException 如果构建逻辑有误
     */
    void beforeCompile(StateGraph builder) throws GraphStateException;
}

```

## 混合构建

**这是最常见的使用场景。**

当节点逻辑适合用 Spring Bean 管理（使用 `@GraphNode`），但连线逻辑过于复杂（例如动态条件、依赖配置）而不便在 `@GraphNode` 的 `next` 属性中硬编码时，可以采用“**注解定义节点 + 代码定义连线**”的混合模式。

### 示例代码

```java
@GraphComposer
public class HybridGraphComposer implements GraphBuildLifecycle {

    public static final String NODE_A = "nodeA";

    // 1. 使用注解定义节点，但故意省略 next 和 isStart 属性
    // 框架会自动注册此节点，但暂时不建立任何连接
    @GraphNode(id = NODE_A) 
    final AsyncNodeActionWithConfig actionA = (state, config) -> {
        String input = (String) state.value("input").orElse("");
        return CompletableFuture.completedFuture(Map.of("result", input + "-processed"));
    };

    @Override
    public void beforeCompile(StateGraph builder) throws GraphStateException {
        // 2. 在钩子中手动补充连线逻辑
        // 逻辑：Start -> NodeA -> End
        builder.addEdge(StateGraph.START, NODE_A);
        builder.addEdge(NODE_A, StateGraph.END);
    }
}

```

::: tip 💡 图构建 API
关于 `addNode`, `addEdge`, `addConditionalEdge` 等方法的详细参数说明和更多高级用法，请参阅 [Spring AI Alibaba 官方文档](https://java2ai.com/docs/frameworks/graph-core/quick-start)。
:::

## 动态修剪与扩展

利用 `beforeCompile`，你可以根据环境变量、配置中心的配置或 License 授权情况，动态地调整图结构。

### 示例：根据配置开启审计节点

```java
@Value("${app.audit.enabled:false}")
private boolean auditEnabled;

@Override
public void beforeCompile(StateGraph builder) {
    if (this.auditEnabled) {
        // 动态插入审计逻辑： Start -> Process -> Audit -> End
        builder.addEdge("process", "audit");
        builder.addEdge("audit", StateGraph.END);
    } else {
        // 跳过审计： Start -> Process -> End
        builder.addEdge("process", StateGraph.END);
    }
}

```

## 结构校验

在编译前进行防御性检查，防止错误的图定义上线。这在多人协作或大型复杂图谱中非常有用。

### 示例：强制检查安全节点

```java
@Override
public void beforeCompile(StateGraph builder) {
    // 检查图中是否包含必须的 "security_check" 节点
    // 假设 StateGraph 提供了 hasNode 方法
    if (!builder.hasNode("security_check")) {
        throw new GraphStateException("安全合规错误：业务流程必须包含 security_check 节点");
    }
}

```
