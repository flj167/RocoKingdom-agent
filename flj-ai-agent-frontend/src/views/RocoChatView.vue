<script setup lang="ts">
import { RouterLink } from "vue-router";
import ChatWindow from "../components/ChatWindow.vue";
</script>

<template>
  <div class="view-page roco-view-page">
    <div class="chat-shell">
      <RouterLink class="back-link" to="/">Back</RouterLink>
      <ChatWindow
        title="洛克王国手游AI对话应用"
        endpoint-path="/ai/rocokindom_app/chat/sse/emitter"
        :include-chat-id="true"
        ai-avatar-url="/static/avatars/roco-ai.webp"
      />
    </div>
  </div>
</template>
