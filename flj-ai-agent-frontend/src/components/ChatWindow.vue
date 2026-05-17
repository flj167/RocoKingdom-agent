<script setup lang="ts">
import { nextTick, onBeforeUnmount, ref, watch } from "vue";
import type { ChatMessage } from "../types/chat";
import { createSsePath, openSse } from "../services/sse";

const props = withDefaults(
  defineProps<{
    title: string;
    endpointPath: string;
    includeChatId: boolean;
    splitAiChunks?: boolean;
    aiAvatarUrl?: string;
  }>(),
  {
    splitAiChunks: false,
    aiAvatarUrl: ""
  }
);

const messages = ref<ChatMessage[]>([]);
const input = ref("");
const loading = ref(false);
const error = ref("");
const messageListRef = ref<HTMLElement | null>(null);
let activeSource: EventSource | null = null;

const chatId = ref(generateChatId());
const chatIdLabel = `Chat ID: ${chatId.value}`;

function generateChatId() {
  if (typeof crypto !== "undefined" && "randomUUID" in crypto) {
    return crypto.randomUUID();
  }
  return `${Date.now()}-${Math.random().toString(16).slice(2)}`;
}

function closeActiveSource() {
  if (activeSource) {
    activeSource.close();
    activeSource = null;
  }
}

function pushMessage(role: "user" | "ai", content: string) {
  const id = `${Date.now()}-${Math.random().toString(16).slice(2)}`;
  messages.value.push({ id, role, content });
  return id;
}

function appendAiChunk(aiMessageId: string, chunk: string) {
  const target = messages.value.find((item) => item.id === aiMessageId);
  if (!target) {
    return;
  }
  target.content += chunk;
}

function buildUrl(message: string) {
  const params: Record<string, string> = { message };
  if (props.includeChatId) {
    params.chatId = chatId.value;
  }
  return createSsePath(props.endpointPath, params);
}

function sendMessage() {
  const message = input.value.trim();
  if (!message || loading.value) {
    return;
  }

  error.value = "";
  loading.value = true;
  pushMessage("user", message);
  input.value = "";

  const aiMessageId = props.splitAiChunks ? "" : pushMessage("ai", "");
  const url = buildUrl(message);
  closeActiveSource();

  activeSource = openSse(url, {
    onMessage: (chunk) => {
      if (!chunk) {
        return;
      }
      if (props.splitAiChunks) {
        pushMessage("ai", `${chunk}\n`);
        return;
      }
      appendAiChunk(aiMessageId, chunk);
    },
    onError: (errorMessage) => {
      loading.value = false;
      error.value = errorMessage;
    },
    onDone: () => {
      loading.value = false;
    }
  });
}

watch(
  messages,
  async () => {
    await nextTick();
    if (!messageListRef.value) {
      return;
    }
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight;
  },
  { deep: true }
);

onBeforeUnmount(() => {
  closeActiveSource();
});
</script>

<template>
  <div class="chat-page">
    <header class="chat-header">
      <h1>{{ title }}</h1>
      <div class="chat-id">{{ chatIdLabel }}</div>
    </header>
    <section ref="messageListRef" class="message-list">
      <div v-for="item in messages" :key="item.id" class="message-row" :class="item.role">
        <div v-if="item.role === 'ai'" class="avatar">
          <img v-if="props.aiAvatarUrl" :src="props.aiAvatarUrl" alt="AI Avatar" class="avatar-img" />
          <span v-else>AI</span>
        </div>
        <div class="bubble">{{ item.content || (loading && item.role === 'ai' ? '...' : '') }}</div>
      </div>
    </section>
    <footer class="chat-input">
      <input v-model="input" type="text" placeholder="Type your message..." @keydown.enter="sendMessage" />
      <button :disabled="loading" @click="sendMessage">Send</button>
    </footer>
    <div v-if="error" class="error">{{ error }}</div>
  </div>
</template>
