import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import RocoChatView from "../views/RocoChatView.vue";
import ManusChatView from "../views/ManusChatView.vue";

type SeoMeta = {
  title: string;
  description: string;
  keywords: string;
  path: string;
};

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      name: "home",
      component: HomeView,
      meta: {
        seo: {
          title: "洛克王国手游AI应用首页",
          description: "洛克王国手游AI应用入口，包含AI对话应用与超级智能体攻略助手，支持实时流式问答。",
          keywords: "洛克王国,AI对话,游戏攻略,智能体,手游助手",
          path: "/"
        } as SeoMeta
      }
    },
    {
      path: "/roco",
      name: "roco",
      component: RocoChatView,
      meta: {
        seo: {
          title: "洛克王国手游AI对话应用",
          description: "洛克王国手游AI对话应用，支持基于SSE的实时问答，快速获取宠物、关卡和阵容建议。",
          keywords: "洛克王国AI对话,SSE聊天,宠物培养,关卡攻略",
          path: "/roco"
        } as SeoMeta
      }
    },
    {
      path: "/manus",
      name: "manus",
      component: ManusChatView,
      meta: {
        seo: {
          title: "洛克王国手游游戏攻略助手",
          description: "洛克王国手游超级智能体应用，按步骤输出策略建议，实时呈现每一步执行结果。",
          keywords: "洛克王国攻略,游戏智能体,步骤推理,实时SSE",
          path: "/manus"
        } as SeoMeta
      }
    }
  ]
});

function upsertMeta(attr: "name" | "property", key: string, content: string) {
  let meta = document.querySelector(`meta[${attr}="${key}"]`);
  if (!meta) {
    meta = document.createElement("meta");
    meta.setAttribute(attr, key);
    document.head.appendChild(meta);
  }
  meta.setAttribute("content", content);
}

function upsertCanonical(href: string) {
  let link = document.querySelector('link[rel="canonical"]');
  if (!link) {
    link = document.createElement("link");
    link.setAttribute("rel", "canonical");
    document.head.appendChild(link);
  }
  link.setAttribute("href", href);
}

router.afterEach((to) => {
  const seo = to.meta.seo as SeoMeta | undefined;
  if (!seo) {
    return;
  }
  const fullUrl = `${window.location.origin}${seo.path}`;
  document.title = seo.title;
  upsertMeta("name", "description", seo.description);
  upsertMeta("name", "keywords", seo.keywords);
  upsertMeta("property", "og:title", seo.title);
  upsertMeta("property", "og:description", seo.description);
  upsertMeta("property", "og:type", "website");
  upsertMeta("property", "og:url", fullUrl);
  upsertMeta("name", "twitter:card", "summary_large_image");
  upsertMeta("name", "twitter:title", seo.title);
  upsertMeta("name", "twitter:description", seo.description);
  upsertCanonical(fullUrl);
});

export default router;
