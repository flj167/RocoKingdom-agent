import axios from "axios";
import { API_BASE_URL } from "./apiBase";

const http = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000
});

export default http;
