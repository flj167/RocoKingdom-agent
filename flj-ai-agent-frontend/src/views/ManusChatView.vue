<script setup lang="ts">
import { RouterLink } from "vue-router";
import ChatWindow from "../components/ChatWindow.vue";
</script>

<template>
  <div class="view-page">
    <RouterLink class="back-link" to="/">Back</RouterLink>
    <ChatWindow
      title="AI Manus Agent Chat"
      endpoint-path="/ai/manus/chat"
      :include-chat-id="false"
      :split-ai-chunks="true"
    />
  </div>
</template>
