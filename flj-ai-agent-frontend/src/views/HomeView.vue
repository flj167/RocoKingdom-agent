<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from "vue";

type DocType = "feature" | "example" | null;

const activeDoc = ref<DocType>(null);
const canvasRef = ref<HTMLCanvasElement | null>(null);
let animFrameId = 0;

const openDoc = (type: Exclude<DocType, null>) => {
  activeDoc.value = type;
};

const closeDoc = () => {
  activeDoc.value = null;
};

// ── Particle system ──────────────────────────────────────────────
interface Particle {
  x: number;
  y: number;
  vx: number;
  vy: number;
  r: number;
  alpha: number;
  pulse: number;
  pulseSpeed: number;
}

let particles: Particle[] = [];
let mouseX = -1000;
let mouseY = -1000;

function createParticles(canvas: HTMLCanvasElement) {
  const count = Math.min(80, Math.floor((canvas.width * canvas.height) / 12000));
  particles = [];
  for (let i = 0; i < count; i++) {
    particles.push({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      vx: (Math.random() - 0.5) * 0.6,
      vy: (Math.random() - 0.5) * 0.6,
      r: Math.random() * 2.5 + 0.8,
      alpha: Math.random() * 0.5 + 0.25,
      pulse: Math.random() * Math.PI * 2,
      pulseSpeed: Math.random() * 0.02 + 0.008,
    });
  }
}

function drawParticles(canvas: HTMLCanvasElement, ctx: CanvasRenderingContext2D) {
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  for (let i = 0; i < particles.length; i++) {
    const p = particles[i];

    // Update position
    p.x += p.vx;
    p.y += p.vy;

    // Wrap around edges
    if (p.x < -10) p.x = canvas.width + 10;
    if (p.x > canvas.width + 10) p.x = -10;
    if (p.y < -10) p.y = canvas.height + 10;
    if (p.y > canvas.height + 10) p.y = -10;

    // Pulse alpha
    p.pulse += p.pulseSpeed;
    const currentAlpha = p.alpha + Math.sin(p.pulse) * 0.15;

    // Mouse proximity repulsion
    const dx = p.x - mouseX;
    const dy = p.y - mouseY;
    const dist = Math.sqrt(dx * dx + dy * dy);
    if (dist < 120 && dist > 0) {
      const force = (120 - dist) / 120;
      p.vx += (dx / dist) * force * 0.08;
      p.vy += (dy / dist) * force * 0.08;
    }

    // Speed damping
    p.vx *= 0.999;
    p.vy *= 0.999;

    // Speed cap
    const speed = Math.sqrt(p.vx * p.vx + p.vy * p.vy);
    if (speed > 1.5) {
      p.vx = (p.vx / speed) * 1.5;
      p.vy = (p.vy / speed) * 1.5;
    }

    // Draw connections
    for (let j = i + 1; j < particles.length; j++) {
      const q = particles[j];
      const ddx = p.x - q.x;
      const ddy = p.y - q.y;
      const d = Math.sqrt(ddx * ddx + ddy * ddy);
      if (d < 100) {
        ctx.beginPath();
        ctx.moveTo(p.x, p.y);
        ctx.lineTo(q.x, q.y);
        ctx.strokeStyle = `rgba(180, 200, 255, ${0.08 * (1 - d / 100)})`;
        ctx.lineWidth = 0.5;
        ctx.stroke();
      }
    }

    // Draw particle
    ctx.beginPath();
    ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
    ctx.fillStyle = `rgba(140, 180, 255, ${Math.max(0, Math.min(1, currentAlpha))})`;
    ctx.fill();

    // Glow
    ctx.beginPath();
    ctx.arc(p.x, p.y, p.r * 2.5, 0, Math.PI * 2);
    ctx.fillStyle = `rgba(160, 200, 255, ${Math.max(0, Math.min(1, currentAlpha * 0.2))})`;
    ctx.fill();
  }
}

function animate() {
  const canvas = canvasRef.value;
  if (!canvas) return;
  const ctx = canvas.getContext("2d");
  if (!ctx) return;
  drawParticles(canvas, ctx);
  animFrameId = requestAnimationFrame(animate);
}

function onMouseMove(e: MouseEvent) {
  const canvas = canvasRef.value;
  if (!canvas) return;
  const rect = canvas.getBoundingClientRect();
  mouseX = e.clientX - rect.left;
  mouseY = e.clientY - rect.top;
}

function onResize() {
  const canvas = canvasRef.value;
  if (!canvas) return;
  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;
  createParticles(canvas);
}

onMounted(() => {
  const canvas = canvasRef.value;
  if (!canvas) return;
  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;
  createParticles(canvas);
  animate();
  window.addEventListener("resize", onResize);
  window.addEventListener("mousemove", onMouseMove);
});

onBeforeUnmount(() => {
  cancelAnimationFrame(animFrameId);
  window.removeEventListener("resize", onResize);
  window.removeEventListener("mousemove", onMouseMove);
});
</script>

<template>
  <div class="home-page">
    <canvas ref="canvasRef" class="particle-canvas"></canvas>
    <div class="home-tools">
      <button class="home-tool-btn" @click="openDoc('feature')">功能介绍</button>
      <button class="home-tool-btn" @click="openDoc('example')">操作示例</button>
    </div>
    <section class="hero">
      <div class="hero-badge">洛克王国手游 · AI 宇宙站</div>
      <h1>应用首页</h1>
      <p class="hero-subtitle">对话问答 + 智能体攻略，一站直达，陪你高效玩转洛克王国。</p>
    </section>
    <div class="app-links">
      <RouterLink class="app-card" to="/roco">
        <span class="app-card-tag">AI 对话</span>
        <span class="app-card-title">洛克王国手游AI对话应用</span>
        <span class="app-card-desc">流式聊天，实时回复，快速解答宠物培养与关卡问题。</span>
      </RouterLink>
      <RouterLink class="app-card" to="/manus">
        <span class="app-card-tag">超级智能体</span>
        <span class="app-card-title">洛克王国手游游戏攻略助手</span>
        <span class="app-card-desc">按步骤输出策略建议，清晰展示思考过程与执行结果。</span>
      </RouterLink>
    </div>

    <div class="home-github-note">
      <a href="https://github.com/flj167" target="_blank" rel="noopener noreferrer">作者的github主页</a>
      <span>，如果使用遇到问题，可以跟我反馈；使用效果好，欢迎给我一个 Star。</span>
    </div>

    <Transition name="modal">
      <div v-if="activeDoc" class="doc-overlay" @click.self="closeDoc">
        <div class="doc-modal">
          <div class="doc-modal-header">
            <h2>{{ activeDoc === "feature" ? "功能说明文档" : "操作示例文档" }}</h2>
            <button class="doc-close-btn" @click="closeDoc">关闭</button>
          </div>
          <div class="doc-modal-content">
            <template v-if="activeDoc === 'feature'">
              <section class="doc-section">
                <h3>洛克王国手游AI对话应用</h3>
                <p>
                  这是一款专为洛克王国手游玩家打造的智能问答助手，基于 RAG
                  知识库检索技术，结合多轮对话与持久化记忆能力，能够快速准确地解答玩家在游戏过程中遇到的各类基础问题。支持调用外部工具和
                  MCP 服务获取实时数据，为玩家提供最新、最准确的游戏信息。
                </p>
                <p><strong style="color: #d0312d;">由于多种问题，AI对话应用的回答结果可能不近人意，后续将会改善升级，推荐使用AI智能体应用！</strong></p>
              </section>
              <section class="doc-section">
                <h3>洛克王国手游游戏攻略助手</h3>
                <p>
                  这是一款具备自主推理和行动能力的超级智能体攻略助手。它能够理解用户的复杂目标，自主规划任务步骤，调用多种强大工具（联网搜索、文件操作、网页抓取、资源下载、终端操作、PDF
                  生成）自动执行任务，直到完成用户指定的最终目标。无需用户一步步下达指令，真正实现
                  "一句话搞定复杂攻略任务"。
                </p>
              </section>
            </template>

            <template v-else>
              <section class="doc-section">
                <h3>洛克王国手游AI对话应用</h3>
                <ol class="doc-list">
                  <li>
                    基础游戏知识问答：解答关于游戏基本设定、精灵属性、技能效果、玩法规则等基础问题。示例
                    prompt："洛克王国手游中火系精灵克制哪些属性？被哪些属性克制？"
                  </li>
                  <li>
                    多轮对话与上下文记忆：能够记住之前对话内容，后续交流可保持上下文连贯。示例
                    prompt："那我刚才问的火系精灵，哪些比较适合新手培养？"
                  </li>
                  <li>
                    RAG 知识库精准检索：基于内置知识库快速检索并返回结构化游戏信息。示例
                    prompt："请列出洛克王国手游中所有拥有 '催眠'
                    技能的精灵及其获取方式。"
                  </li>
                  <li>
                    实时游戏数据查询：通过工具调用获取最新活动与版本信息。示例
                    prompt："洛克王国手游本周有哪些限时活动？活动持续到什么时候？"
                  </li>
                  <li>
                    MCP 服务调用：调用外部 MCP
                    服务获取更专业的数据分析。示例
                    prompt："帮我计算一下满级满天赋的火花各项属性值是多少？"
                  </li>
                </ol>
              </section>

              <section class="doc-section">
                <h3>洛克王国手游游戏攻略助手</h3>
                <ol class="doc-list">
                  <li>
                    多步骤复杂攻略自动生成：自动拆解任务并整合为完整攻略文档。示例
                    prompt："请为我生成一份洛克王国手游新手前 7
                    天的完整升级攻略，包括每日必做任务、精灵培养优先级和资源获取路线。将攻略保存为
                    'new_player_7day_guide.md' 文件。"
                  </li>
                  <li>
                    联网搜索与信息整合：自动检索并综合多来源信息。示例
                    prompt："搜索洛克王国手游最新版本的精灵强度排行榜，综合
                    3 个不同游戏社区的排名结果，生成一份综合排名 TOP20
                    的精灵列表，保存为 'tier_list_v1.2.txt'。"
                  </li>
                  <li>
                    网页内容抓取与 PDF 生成：自动抓取并生成规范 PDF。示例
                    prompt："抓取洛克王国手游官方 Wiki 中 '宠物图鉴'
                    页面的所有草系精灵信息，包括精灵名称、编号、属性、技能和进化路线，生成
                    PDF 文档 'grass_pokemon_guide.pdf'。"
                  </li>
                  <li>
                    游戏资源自动下载：自动下载高清图片、壁纸、视频等资源。示例
                    prompt："搜索并下载 5 张洛克王国手游中 '圣藤草王'
                    的高清立绘图片，保存到 'D:\洛克王国\精灵立绘\草系'
                    文件夹中。"
                  </li>
                  <li>
                    文件与文件夹操作：支持创建、删除、移动、重命名并自动整理。示例
                    prompt："在 D 盘创建一个名为 '洛克王国攻略合集'
                    的文件夹，然后在其中分别创建 '精灵攻略'、'副本攻略' 和
                    '活动攻略' 三个子文件夹。"
                  </li>
                  <li>
                    终端命令执行：能够执行系统终端命令完成复杂操作。示例
                    prompt："使用终端命令将 'D:\洛克王国\旧攻略'
                    文件夹中所有的.txt 文件批量转换为.md 格式，然后移动到
                    'D:\洛克王国攻略合集\精灵攻略' 文件夹中。"
                  </li>
                  <li>
                    全流程自动化任务执行：可按最终目标自主完成整套任务流。示例
                    prompt："请协助整理洛克王国手游「风眠省」区域的草系精灵完整攻略。具体任务顺序如下：使用搜索引擎查询"洛克王国手游
                    风眠省 草系精灵
                    刷新点"，将前5条结果的标题与链接写入文件
                    fengmian_province_search.txt；从上述搜索结果中提炼可靠的草系精灵名单、出现位置与捕捉条件，整理为结构化文本，保存为
                    fengmian_province_guide.txt；查找并抓取官方 Wiki
                    中风眠省背景故事页面（若不确定 URL，可通过搜索"洛克王国
                    风眠省 背景"获取），提取其核心文本内容，生成 PDF 文档
                    fengmian_province.pdf；搜索一张风眠省场景的高清壁纸，下载并保存为
                    fengmian_province_wallpaper.jpg；使用终端命令在 D
                    盘根目录创建文件夹 RockStrategy，然后将步骤
                    2、3、4中生成的三个文件移动至该文件夹内；全部任务完成后结束本次会话。"
                  </li>
                </ol>
              </section>

              <section class="doc-section">
                <h3>重要使用提示</h3>
                <ul class="doc-list">
                  <li>超级智能体具备自主决策能力，执行中可能根据实际情况调整步骤顺序或补充必要操作。</li>
                  <li>涉及文件操作和终端命令的任务，建议先在测试环境验证，避免误操作。</li>
                  <li>可随时向智能体询问任务执行进度，或在执行过程中修改需求。</li>
                  <li>若任务执行失败，智能体会自动重试或提供错误信息与解决方案。</li>
                </ul>
              </section>
            </template>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>
