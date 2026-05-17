<template>
  <div class="home-page">
    <section class="hero">
      <div class="hero-badge">洛克王国手游 · AI 宇宙站</div>
      <h1>应用首页</h1>
      <p class="hero-subtitle">对话问答 + 智能体攻略，一站直达，陪你高效玩转洛克王国。</p>
    </section>
    <div class="app-links">
      <RouterLink class="app-card" to="/roco">
        <span class="app-card-tag">AI 对话</span>
        <span class="app-card-title">洛克王国手游AI对话应用</span>
        <span class="app-card-desc">流式聊天，实时回复，快速解答宠物培养与关卡问题。</span>
      </RouterLink>
      <RouterLink class="app-card" to="/manus">
        <span class="app-card-tag">超级智能体</span>
        <span class="app-card-title">洛克王国手游游戏攻略助手</span>
        <span class="app-card-desc">按步骤输出策略建议，清晰展示思考过程与执行结果。</span>
      </RouterLink>
    </div>
  </div>
</template>
