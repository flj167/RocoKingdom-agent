<script setup lang="ts">
import { RouterLink } from "vue-router";
import ChatWindow from "../components/ChatWindow.vue";
</script>

<template>
  <div class="view-page">
    <RouterLink class="back-link" to="/">Back</RouterLink>
    <ChatWindow
      title="AI RocoKingdom App Chat"
      endpoint-path="/ai/rocokindom_app/chat/sse/emitter"
      :include-chat-id="true"
    />
  </div>
</template>
