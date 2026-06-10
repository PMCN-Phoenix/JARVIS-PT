# 验收清单

## 项目基础设施
- [x] 项目记忆系统（8 个 memory 文件）
- [x] 标准文件体系（CLAUDE.md, PROGRESS.md, CHECKLIST.md, DEBUG.md）
- [x] 构建系统搭建（Gradle AGP 8.5.2 + Kotlin 2.0.0）
- [x] 项目骨架初始化

## 阶段一：项目骨架 + 数据库
- [x] 8 个 Entity + 8 个 DAO 创建
- [x] RoomDatabase + SQLCipher 加密
- [x] Repository 层（Domain 接口 + Data 实现）
- [x] Hilt DI 模块
- [x] 底部导航壳（5个Tab）
- [x] `assembleDebug` 构建成功（零警告）

## 阶段二：宿主属性面板
- [x] TacticalCard 通用卡片组件
- [x] AttrProgressBar 属性进度条（渐变色 + 动画）
- [x] BreathingRing 呼吸光环动画
- [x] HostViewModel 数据绑定
- [x] DashboardScreen：主机ID栏 + 六维速览 + 首要任务占位
- [x] AttributeScreen：三标签（体能/排球/枪械）切换
- [x] `assembleDebug` 构建成功（零警告）

## 阶段三：日常任务闭环
- [ ] TaskViewModel 创建
- [ ] 日常任务签到 UI
- [ ] 奖励结算逻辑
- [ ] 属性更新 + 飘字动效
- [ ] 每日结算 Worker

## 阶段四：锁死惩罚
- [ ] 失格计数器 + 锁死触发
- [ ] 锁死 UI（破碎玻璃覆盖层）
- [ ] TOTP 令牌生成
- [ ] 双端编译流程

## 阶段五：主线/支线任务 + 配置中心
- [ ] 主线/支线任务 UI
- [ ] 子目标签到
- [ ] 任务配置编辑器
- [ ] 任务模板管理

## 阶段六：资源背包 + 战略报告
- [ ] 背包页面（4资源网格）
- [ ] 周报/月报页面
- [ ] Canvas 图表（环形图、雷达图、折线图）

## 阶段七：AI 参谋 + 日志 + 设置
- [ ] AI 生成任务草案
- [ ] 系统日志页面
- [ ] 设置/底层编译室
- [ ] 数据备份与恢复

## 待优化项
- [ ] 属性飘字动效（阶段三实现）
- [ ] 锁死破碎玻璃效果（阶段四实现）
- [ ] 电脑母块 Python 脚本（阶段四实现）
