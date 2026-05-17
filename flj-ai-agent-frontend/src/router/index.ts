import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import RocoChatView from "../views/RocoChatView.vue";
import ManusChatView from "../views/ManusChatView.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", name: "home", component: HomeView },
    { path: "/roco", name: "roco", component: RocoChatView },
    { path: "/manus", name: "manus", component: ManusChatView }
  ]
});

export default router;
