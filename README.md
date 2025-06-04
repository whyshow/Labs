# Labs 项目

## 🏗️ 项目架构

    Labs (多模块化工程)
    ├── app (主模块)
    ├── module-basic (基础模块)
    ├── module-home (首页模块)
    ├── module-network (网络模块)
    ├── module-view (自定义View模块)
    ├── module-storage (存储模块)
    ├── module-iots (物联网模块)
    ├── module-aop (切面编程模块)
    └── module-utils (工具模块)

## 📦 模块功能说明

### 1. app (主模块)

- **核心功能**：
    - 应用入口及全局配置管理
    - 多环境构建配置（debug/release）
    - 签名及打包配置
  
- **关键文件**：
    - `SplashActivity`：启动页

### 2. module-basic (基础模块)
- **核心功能**：
    - 提供基础Activity/Fragment基类
    - 通用工具类（Toast、状态栏工具等）
  
### 3. module-home (首页模块)
- **核心功能**：
  - 实现应用主界面框架（底部导航+Fragment容器）
  - 新闻资讯展示系统（多Tab布局）
  - 内置WebView浏览器容器
  
- **技术实现**：
  - 使用Navigation组件管理Fragment导航
  - ViewPager2+TabLayout实现多页签切换
  - 基于DataBinding的列表项绑定
  
- **关键文件**：
  - `MainActivity`：主入口界面
  - `NewsFragment`：新闻列表页
  - `WebViewActivity`：网页容器

### 4. module-network (网络模块)
- **核心功能**：
  - 统一网络请求封装（Retrofit+RxJava3）
  - 多域名动态配置支持
  - 401等HTTP状态码统一处理

### 5. module-view (视图模块)
- **核心职责**：
  - 提供标准化UI组件库
  - 统一应用视觉风格

- **主要组件**：
  - `BaseDialogFragment`：对话框基类
    ```java
    public abstract class BaseDialogFragment<T> 
        extends DialogFragment {
        // 支持ViewBinding的对话框实现
    }
    ```
  - 加载状态管理：
    - 加载中/空数据/错误重试等状态视图
    - 支持自定义加载动画

- **技术特性**：
  - 全面支持ViewBinding/DataBinding

### 6. module-storage (存储模块)
- **核心职责**：
  - 统一数据持久化方案
  - 安全存储敏感信息
  - 多进程数据共享

- **主要功能**：
  - 用户偏好设置管理：
    ```java
    public interface IUserPreferences {
        void saveToken(String token);
        String getToken();
        void clearAll();
    }
    ```
- **技术特性**：
  - 基于SharedPreferences封装
  - 完善的类型安全支持