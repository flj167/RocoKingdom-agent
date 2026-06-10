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
const textareaRef = ref<HTMLTextAreaElement | null>(null);
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

function autoResize() {
  const el = textareaRef.value;
  if (!el) return;
  el.style.height = "auto";
  el.style.height = el.scrollHeight + "px";
}

function onEnterKey(e: KeyboardEvent) {
  if (e.shiftKey) return; // Shift+Enter for newline
  e.preventDefault();
  sendMessage();
}

function onButtonClick(e: MouseEvent) {
  const btn = e.currentTarget as HTMLElement;
  const ripple = document.createElement("span");
  ripple.className = "ripple";
  const rect = btn.getBoundingClientRect();
  const size = Math.max(rect.width, rect.height);
  ripple.style.width = ripple.style.height = size + "px";
  ripple.style.left = (e.clientX - rect.left - size / 2) + "px";
  ripple.style.top = (e.clientY - rect.top - size / 2) + "px";
  btn.appendChild(ripple);
  ripple.addEventListener("animationend", () => ripple.remove());
  sendMessage();
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
  nextTick(() => autoResize());

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
        <div class="bubble" v-html="item.content || (loading && item.role === 'ai' ? '...' : '')"></div>
      </div>
    </section>
    <footer class="chat-input">
      <textarea
        ref="textareaRef"
        v-model="input"
        rows="1"
        placeholder="Type your message..."
        @keydown.enter="onEnterKey"
        @input="autoResize"
      ></textarea>
      <button :disabled="loading" @click="onButtonClick">Send</button>
    </footer>
    <div v-if="loading" class="thinking-bar">
      <span class="thinking-spinner" />
      <span>亲爱的小洛克，请你等待一下，让我仔细思考思考</span>
      <span class="thinking-dots">
        <span></span><span></span><span></span>
      </span>
    </div>
    <div v-if="error" class="error">{{ error }}</div>
  </div>
</template>
