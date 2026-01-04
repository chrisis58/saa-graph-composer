---
layout: home

hero:
  name: "SAA Graph Composer"
  text: "Spring AI Alibaba Graph 的声明式编排扩展"
  tagline: 拒绝样板代码 · 关注点分离 · 零额外依赖
  actions:
    - theme: brand
      text: 快速开始
      link: /guide/getting-started
    - theme: alt
      text: GitHub 源码
      link: https://github.com/chrisis58/saa-graph-composer

features:
  - title: 🔌 节点即适配器 (Node as Adaptor)
    details: 告别繁琐的手动连线。倡导将 Composer 作为纯粹的路由层，利用原生 Spring 依赖注入委托 Service 执行业务，实现编排逻辑与业务实现的自然解耦。
  - title: 🧩 代码即图表 (Code as Graph)
    details: 通过 @GraphComposer 和 @GraphNode 直观描述拓扑结构。让复杂的流转逻辑在代码层面一目了然，实现“所见即所得”的开发体验，像阅读流程图一样阅读代码。
  - title: 🍃 非侵入式原生扩展
    details: 拒绝黑魔法。基于 Spring 标准生命周期构建，本质是对原生 Builder 的透明封装。生成的图完全兼容官方 API，可以与原生写法无缝共存。
---

<style>
:root {
  --vp-home-hero-name-color: transparent;
  --vp-home-hero-name-background: -webkit-linear-gradient(120deg, #bd34fe 30%, #41d1ff);
}
</style>