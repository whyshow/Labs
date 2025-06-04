## 📦 模块功能说明

### module-aop (切面编程模块)

- **核心功能**：
    - 提供非侵入式的功能增强
    - 统一权限申请处理
    - 方法执行耗时统计
    - 防止快速重复点击
    - 日志埋点和行为监控

    - **技术实现**：
      ```java:module-aop/src/main/java/club/ccit/aop/aspect/PermissionAspect.java
      @Aspect
      public class PermissionAspect {
          @Around("execution(@PermissionNeed * *(..))")
          public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
              // 1. 检查权限
              // 2. 无权限时自动申请
              // 3. 权限通过后执行原方法
              return joinPoint.proceed();
          }
      }
      ``` 
        - **使用**：
      ```  
      @SingleClick(interval = 1000) // 1秒内防重复点击
      public void onButtonClick(View view) {
         // 点击处理
      }
      ``` 
        - **集成方式**：
       ```  
      dependencies {
      
          // 引入模块依赖
          implementation project(':module-aop')
      
      }
      apply from: "../aspectj.gradle"