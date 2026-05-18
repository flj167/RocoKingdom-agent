// 根据环境变量设置 API 基础 URL
export const API_BASE_URL = import.meta.env.PROD
  ? "/api" // 生产环境使用相对路径，适用于前后端部署在同一域名下
  : "http://localhost:8123/api"; // 开发环境指向本地后端服务
