# LibreSpeed Vue

一个使用 Vue + Spring Boot 开发的网络速度测试工具，仿照 LibreSpeed 的设计理念。

## 📋 项目简介

LibreSpeed Vue 是一个现代化的网络速度测试应用，前端采用 Vue.js 框架构建用户界面，后端使用 Spring Boot 提供 API 服务。该项目旨在提供一个轻量级、易部署的速度测试解决方案。

## 🛠️ 技术栈

### 前端
- **Vue.js** - 进行式用户界面框架
- **JavaScript** - 核心编程语言
- **HTML/CSS** - 页面结构和样式

### 后端
- **Spring Boot 3.3.4** - Java 应用框架
- **Java 17** - 后端编程语言
- **Spring Web** - RESTful API 开发

## 📦 项目结构

```
librespeed-vue/
├── src/                    # 源代码目录
├── pom.xml                 # Maven 项目配置文件
├── LICENSE                 # 许可证文件
└── README.md              # 项目文档
```

## 🚀 快速开始

### 前置要求
- Java 17 或更高版本
- Maven 3.6 或更高版本
- Node.js 14 或更高版本（用于前端开发）

### 安装与运行

#### 后端
```bash
# 克隆项目
git clone https://github.com/juyss/librespeed-vue.git
cd librespeed-vue

# 使用 Maven 构建
mvn clean install

# 运行应用
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

#### 前端
```bash
# 进入前端目录（如适用）
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run serve
```

## 📊 功能特性

- 🌐 网络速度测试（下载、上传、延迟等）
- 📈 实时进度显示和结果统计
- 💾 测试结果本地存储
- 📱 响应式设计，支持多设备访问
- 🎨 简洁现代的用户界面

## 🔧 配置说明

### 后端配置
主要配置文件位于 `src/main/resources/application.properties` 或 `application.yml`

### 前端配置
根据项目结构，前端配置通常在 `vue.config.js` 或相关配置文件中

## 📝 API 文档

详细的 API 接口文档将在后续更新。主要 API 端点包括：

- `POST /api/speed/test` - 启动速度测试
- `GET /api/speed/results` - 获取测试结果

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

## 📧 联系方式

如有任何问题或建议，欢迎通过以下方式联系：

- 提交 [GitHub Issues](https://github.com/juyss/librespeed-vue/issues)
- 发送邮件至项目维护者

## 🙏 致谢

感谢 LibreSpeed 项目的启发，以及所有为本项目做出贡献的开发者。
