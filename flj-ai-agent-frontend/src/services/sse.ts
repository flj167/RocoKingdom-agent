const API_BASE = "http://localhost:8123/api";

export interface SseHandlers {
  onMessage: (chunk: string) => void;
  onError: (error: string) => void;
  onDone: () => void;
}

export function createSsePath(path: string, params: Record<string, string>): string {
  const url = new URL(`${API_BASE}${path}`);
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
