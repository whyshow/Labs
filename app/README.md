## 📦 模块功能说明

### app (主模块)
- **核心职责**：
  - 应用入口和全局配置管理
  - 多环境构建配置（debug/release）
  - 应用签名和打包发布

- **主要功能**：
  - **启动管理**：
    - `SplashActivity`处理冷启动流程
    -  APP 名称，启动图标，主题等配置
    -  网络请求域名配置

  - **构建系统**：
    ```groovy:app/build.gradle
    release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            manifestPlaceholders = [
                    BASE_URL: "http://news.985111.cn", // 生产地址
                    MODEL   : "RELEASE"
            ]
        }
    ```
  - **混淆配置**：
    - 维护`proguard-rules.pro`文件
    - 保护关键类不被混淆（如ViewBinding类）

  - **资源管理**：
    - 统一应用图标和启动图