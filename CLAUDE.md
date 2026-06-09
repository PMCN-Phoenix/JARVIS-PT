# CLAUDE.md — JARVIS-PT

## 项目概述

**宿主白厄·个人战术管理系统**（代号 JARVIS-PT）—— 以游戏属性面板为顶层呈现、以手动签到硬核约束为底层驱动的 Android 单兵战术终端。

- **包名**：`com.usher.tactical`
- **技术栈**：Kotlin + Jetpack Compose + Room/SQLCipher + Hilt + MVVM
- **目标平台**：仅 Android（自用不上架），minSdk 26，targetSdk 35

**参考项目**：`D:\JARVIS\aitry_JARVIS` — 完整的 Android 智能语音助手，共享同一技术栈。

## 项目规则与教训

### 教训

> 以下教训来自 aitry_JARVIS 参考项目的开发经验。

- 2026-06-07：SQLCipher 依赖版本 `4.5.7` 不存在 → 正确版本是 `net.zetetic:sqlcipher-android:4.6.1`，工厂类为 `SupportOpenHelperFactory`
- 2026-06-07：Compose BOM `2024.06.00` 缺少 `PullToRefreshBox` 和 `animateItem()` → 必须使用 `2024.09.00`
- 2026-06-07：Material3 1.2+ 中 `Divider` 已改名为 `HorizontalDivider`
- 2026-06-07：`settings.gradle.kts` 中 `dependencyResolution` 少了 `Management` 后缀导致语法错误
- 2026-06-07：中国网络环境 Gradle/Maven 下载超时 → 使用阿里云镜像 + 本地 Gradle 分发
- 2026-06-07：AGP 8.5.2 + compileSdk 35 产生警告 → `android.suppressUnsupportedCompileSdk=35`
- 2026-06-07：`.gradle` 目录被 Java 进程锁 → `taskkill /f /im java.exe` 后删除

### 偏好

- **语言**：Kotlin（如为 Android 项目）
- **架构**：MVVM + Repository 模式
- **UI**：Jetpack Compose + Material 3
- **DI**：Hilt
- **数据库**：Room + SQLCipher（如需加密）
- **构建**：Gradle Kotlin DSL，版本目录 `gradle/libs.versions.toml`
- **错误处理**：必须打印详细日志，敏感信息不写入 logcat
- **提交规范**：遵循 Conventional Commits
- **API 密钥**：存 EncryptedSharedPreferences，不硬编码

### 关键约束（从 aitry_JARVIS 继承）

- 所有数据本地加密存储，绝不上传第三方云
- 数据主权归用户所有
- 优先联网模式，离线本地模型保底
- 仅适配主力机型，不上架应用商店

## 标准文件路径

### 计划档案（原始需求来源）
| 文件 | 路径 | 用途 |
|------|------|------|
| 产品设计文档 | `JARVIS-PT设计文档.md` | 产品设计：惩罚锁死系统、可配置任务引擎、战略报告 |
| 技术文档（基线） | `JARVIS-PT技术文档.md` | 完整技术文档v1.0：背景、功能、架构、数据模型、API、部署 |
| UI设计稿 | `JARVIS-PTui设计稿.md` | 像素级UI规范：11个页面、12个组件、7类动效 |

### 标准规范文档（docs/）
| 文件 | 路径 | 用途 |
|------|------|------|
| 开发需求规范 | `docs/开发需求规范.md` | 结构化的功能需求，含 P0/P1/P2 优先级 |
| 技术规范 | `docs/技术规范.md` | 技术选型、架构分层、数据库规范、代码规范、安全规范 |
| 设计规范 | `docs/设计规范.md` | UI/UX设计规范：色彩、排版、11个页面布局、组件库、动效 |
| 执行步骤 | `docs/执行步骤.md` | 7阶段开发计划，每阶段含目标/步骤/验收标准 |

### 追踪文件（项目根目录）
| 文件 | 路径 | 用途 |
|------|------|------|
| 项目进度 | `PROGRESS.md` | 当前进度、下一步计划、阻塞项 |
| 验收清单 | `CHECKLIST.md` | 功能验收清单、待优化项 |
| 调试记录 | `DEBUG.md` | 调试经验归档 |
| 开发日志 | `devlog/YYYY-MM-DD.md` | 每日开发记录 |

### 外部参考
| 文件 | 路径 | 用途 |
|------|------|------|
| 参考项目 | `D:\JARVIS\aitry_JARVIS` | 完整 Android 智能助手，参考架构和代码 |
| 参考技术文档 | `D:\JARVIS\aitry_JARVIS\README\技术文档v1.1.md` | 参考项目最权威基线 |
| Memory 索引 | `memory/MEMORY.md` | 项目记忆文件索引

## 记忆文件

本项目维护了一套记忆文件（位于 `memory/` 目录），在每次新会话中可快速恢复上下文：

- [aitry_JARVIS 参考项目](memory/aitry-jarvis-reference.md)
- [经验证的技术栈版本](memory/tech-stack-proven.md)
- [中国网络环境构建配置](memory/build-config-china.md)
- [MVVM + Repository 架构](memory/architecture-mvvm-repository.md)
- [Workflowy 任务树实现](memory/workflowy-task-tree.md)
- [LLM Provider 抽象层](memory/llm-provider-abstraction.md)
- [数据库加密方案](memory/database-encryption.md)
- [已知构建问题及修复](memory/known-build-issues-and-fixes.md)

## 工作说明

### 日常开发流程

1. **开始工作前**：读取 `PROGRESS.md`、`devlog/` 最新日志、`docs/执行步骤.md` 当前进度
2. **编码时**：遵循 `docs/技术规范.md` 和 `docs/设计规范.md`
3. **需求不确定时**：优先查阅 `docs/开发需求规范.md`，其次 `JARVIS-PT技术文档.md`
4. **结束工作前**：更新当天 `devlog/YYYY-MM-DD.md`，记录完成事项和待办，更新 `PROGRESS.md`
5. **遇到新坑时**：追加到本文件"教训"部分，并在 `DEBUG.md` 记录详细调试过程

### 项目状态

- **阶段**：项目初始化 — 文档体系搭建完成，待开始阶段一代码编写 (2026-06-10)
- **当前步**：阶段一 — 项目骨架 + 数据库
- **目标**：assembleDebug 构建成功 + 加密数据库可运行 + 5个导航Tab可切换

### 关键约束

- ✅ 纯离线核心闭环（任务、签到、奖惩、报告），AI功能按需联网
- ✅ 所有数据本地加密存储（SQLCipher AES-256）
- ✅ 仅 Android 平台，不上架应用商店
- ✅ 包名 `com.usher.tactical`，最低 SDK 26
- ✅ 所有决策以 `JARVIS-PT技术文档.md` 和 `docs/` 标准文档为权威来源
- ✅ 开发节奏：每阶段独立验收，不一口气做太多

### 关键约束（从 aitry_JARVIS 继承）

- 所有数据本地加密存储，绝不上传第三方云
- 数据主权归用户所有
- 仅适配主力机型，不上架应用商店

## 常用命令

```bash
# 构建（如为 Android 项目）
./gradlew assembleDebug

# 清理
./gradlew clean
```
