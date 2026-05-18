import { API_BASE_URL } from "./apiBase";

const API_BASE = API_BASE_URL;

export interface SseHandlers {
  onMessage: (chunk: string) => void;
  onError: (error: string) => void;
  onDone: () => void;
}

export function createSsePath(path: string, params: Record<string, string>): string {
  const normalizedBase = API_BASE.replace(/\/+$/, "");
  const normalizedPath = path.startsWith("/") ? path : `/${path}`;
  const fullPath = `${normalizedBase}${normalizedPath}`;
  const url = API_BASE.startsWith("http")
    ? new URL(fullPath)
    : new URL(fullPath, window.location.origin);
  Object.entries(params).forEach(([key, value]) => {
    url.searchParams.set(key, value);
  });
  return url.toString();
}

export function openSse(url: string, handlers: SseHandlers): EventSource {
  const source = new EventSource(url);
  let hasReceivedChunk = false;

  source.onmessage = (event: MessageEvent) => {
    hasReceivedChunk = true;
    handlers.onMessage(event.data ?? "");
  };

  source.onerror = () => {
    if (source.readyState === EventSource.CLOSED || hasReceivedChunk) {
      handlers.onDone();
      source.close();
      return;
    }
    handlers.onError("SSE connection failed");
    source.close();
  };

  return source;
}
