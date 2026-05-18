# 🐾 洛克王国手游 AI 助手（RocoKingdom AI Agent）

专为《洛克王国》手游玩家打造的 AI 助手，提供**即时问答**与**自主攻略智能体**能力，帮助你更高效地完成日常、活动与进阶玩法规划。

---

## ✨ 项目简介

本项目基于 **Java 21 + Spring Boot 3 + Spring AI** 构建，包含两个核心方向：

- **洛克精灵问答（AI Chat）**  
  支持多轮对话、记忆持久化、RAG 知识检索，聚焦游戏问题的精准回答。
- **超级攻略智能体（Super Agent）**  
  基于 ReAct 推理循环，可调用联网搜索、网页抓取、文件/PDF 工具，自动生成结构化攻略结果。

---

## 🔥 核心特性

- 🧠 **RAG 知识库问答**：结合本地文档与向量检索增强回答质量  
- 🔄 **对话记忆持久化**：支持文件与数据库记忆方案  
- 🔧 **Tool Calling 工具调用**：搜索、抓取、文件、终端、下载、PDF 等  
- 📡 **MCP 协议集成**：支持扩展第三方能力（含独立 MCP 子模块）  
- 🎯 **ReAct 智能体**：按“思考 → 行动 → 观察”循环完成任务  
- 📄 **攻略导出 PDF**：自动生成可分享文档  
- 📖 **Knife4j 文档**：便于接口调试与二次开发

---

## 🧱 技术栈

| 分类 | 技术 |
| --- | --- |
| 后端 | Java 21, Spring Boot 3, Spring AI |
| 前端 | Vue 3, Vite, TypeScript |
| 数据持久化 | MySQL, Kryo |
| 向量检索 | Spring AI RAG（项目含文档与检索模块） |
| 工具能力 | Tool Calling, Jsoup, iText |
| 协议扩展 | MCP (Model Context Protocol) |
| 接口文档 | Knife4j / OpenAPI |
| 部署 | Docker |

---

## 📁 项目结构

```text
RocoKingdom-agent/
├── src/
│   ├── main/
│   │   ├── java/com/flj/fljaiagent/
│   │   │   ├── agent/          # ReAct/工具调用智能体核心
│   │   │   ├── app/            # 应用编排（如 RocoKindomApp）
│   │   │   ├── rag/            # RAG 检索与增强
│   │   │   ├── tool/           # 工具实现（搜索、抓取、PDF、终端、文件等）
│   │   │   ├── chatmemory/     # 对话记忆实现（MySQL/文件）
│   │   │   ├── controller/     # API 接口层
│   │   │   ├── service/        # 业务服务层
│   │   │   ├── mapper/         # MyBatis Mapper
│   │   │   ├── config/         # Spring 配置
│   │   │   ├── advisor/        # AI Advisor 扩展
│   │   │   ├── entity/         # 数据实体
│   │   │   ├── exception/      # 异常定义
│   │   │   └── demo/invoke/    # 调用示例代码
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── document/       # RAG 文档源
│   │       └── mapper/         # MyBatis XML
│   └── test/java/com/flj/fljaiagent/
│       ├── agent/ app/ rag/ tool/ # 后端单元/集成测试
├── flj-ai-agent-frontend/
│   ├── src/
│   │   ├── views/              # 页面（Home/RocoChat/ManusChat）
│   │   ├── components/         # 通用组件
│   │   ├── services/           # HTTP/SSE/API 封装
│   │   ├── router/             # Vue Router
│   │   ├── types/              # TS 类型定义
│   │   ├── App.vue
│   │   └── main.ts
│   ├── package.json
│   └── nginx.conf
├── flj-image-search-mcp/
│   ├── src/main/java/com/flj/fljimagesearchmcp/tools/
│   ├── src/test/java/
│   └── pom.xml
├── sql/
│   └── create_chat_memory_message_table.sql
├── chat-memory/                # 本地 .kryo 对话记忆数据
├── Dockerfile
└── pom.xml
```

---

## 🚀 快速开始

### 1) 环境要求

- JDK 21
- Maven 3.8+
- Node.js 18+
- MySQL 8+

### 2) 启动后端

在项目根目录执行：

```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

默认配置下：

- 服务地址：`http://localhost:8123/api`
- 健康检查：`http://localhost:8123/api/health`
- Knife4j：`http://localhost:8123/api/doc.html`

### 3) 启动前端

```bash
cd flj-ai-agent-frontend
npm install
npm run dev
```

### 4) 快速调用示例（SSE）

```bash
curl "http://localhost:8123/api/ai/rocokindom_app/chat/sse?message=怎么抓稀有精灵露西亚？&chatId=demo-1"
```

---

## 🗺️ 路线图

- [x] 基础对话与 RAG 问答
- [x] 多轮对话记忆持久化
- [x] ReAct 智能体框架
- [x] 工具集（搜索、抓取、PDF 等）
- [x] MCP 服务对接
- [x] 前端对话界面
- [ ] 支持更多模型与工具生态
- [ ] 持续优化提示词与策略质量

---

## 📄 开源协议

本项目采用 **MIT License** 开源，详见 [LICENSE](./LICENSE)。

---

## 💬 联系方式

- 作者邮箱：`FLJ3478580998@outlook.com`

如果这个项目对你有帮助，欢迎点个 ⭐ Star 支持一下！
