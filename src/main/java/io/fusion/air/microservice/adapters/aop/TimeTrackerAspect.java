/**
 * (C) Copyright 2022 Araf Karsh Hamid
 * (C) 版權所有 2022 Araf Karsh Hamid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 根據 Apache 授權條款 2.0 版（以下簡稱「授權條款」）授權；
 * 除非遵守授權條款，否則您不得使用此檔案。
 * 您可以在以下網址取得授權條款的副本：
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非適用法律要求或書面同意，否則根據授權條款分發的軟體
 * 是基於「按原樣」提供的，不附帶任何明示或暗示的擔保或條件。
 * 有關授權條款下的特定語言管理權限和限制，請參閱授權條款。
 */
package io.fusion.air.microservice.adapters.aop;

// Aspect J
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
// Spring
import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Time Tracker Aspect
 * Log Messages
 * 時間追蹤切面
 * 記錄訊息
 *
 * Keep Track of Time for Every Category Function Calls like:
 * 追蹤每個類別函數調用的時間，如：
 *
 * 1. WS = Rest Controller (Pkg =
 * io.fusion.air.microservice.adapters.controllers.*)
 * 1. WS = REST 控制器 (套件 = io.fusion.air.microservice.adapters.controllers.*)
 * 2. BS = Business Services (Pkg =
 * io.fusion.air.microservice.adapters..services.*)
 * 2. BS = 業務服務 (套件 = io.fusion.air.microservice.adapters..services.*)
 * 3. DS = Database Services (SQL / NoSQL) (Pkg =
 * io.fusion.air.microservice.adapters.repository.*)
 * 3. DS = 資料庫服務 (SQL / NoSQL) (套件 =
 * io.fusion.air.microservice.adapters.repository.*)
 * 4. ES = External Services (External Calls like REST, GRPC, SOAP etc) (Pkg =
 * io.fusion.air.microservice.adapters.external.*)
 * 4. ES = 外部服務 (如 REST、GRPC、SOAP 等外部呼叫) (套件 =
 * io.fusion.air.microservice.adapters.external.*)
 *
 * Throw Exceptions (Throwable) for the Exception Handler Advice to Handle
 * 拋出例外（Throwable）讓例外處理器建議處理
 *
 * @author Araf Karsh Hamid / 作者 Araf Karsh Hamid
 * @version: / 版本：
 * @date: / 日期：
 */
@Aspect
@Configuration
public class TimeTrackerAspect {

    // Set Logger -> Lookup will automatically determine the class name.
    // 設置記錄器 -> Lookup 將自動確定類名。
    private static final Logger log = getLogger(lookup().lookupClass());

    /**
     * Log Message before the Log Execution
     * 在日誌執行前記錄訊息
     * For All Classes = "execution(*
     * io.fusion.air.microservice.adapters.controllers.*.*(..))")
     * With Sub Pkgs = "execution(*
     * io.fusion.air.microservice.adapters.controllers..*.*(..))")
     * 對於所有類別 = "execution(*
     * io.fusion.air.microservice.adapters.controllers.*.*(..))")
     * 包含子套件 = "execution(*
     * io.fusion.air.microservice.adapters.controllers..*.*(..))")
     * 
     * @param joinPoint
     */
    @Before(value = "execution(* io.fusion.air.microservice.adapters.controllers..*.*(..))")
    public void logStatementBefore(JoinPoint joinPoint) {
        log.debug("1|TT|TIME=|STATUS=START|CLASS={}", joinPoint);
    }

    /**
     * Log Message after the Method Execution
     * 在方法執行後記錄訊息
     * For All Classes = "execution(*
     * io.fusion.air.microservice.adapters.controllers.*.*(..))")
     * With Sub Pkgs = "execution(*
     * io.fusion.air.microservice.adapters.controllers..*.*(..))")
     * 對於所有類別 = "execution(*
     * io.fusion.air.microservice.adapters.controllers.*.*(..))")
     * 包含子套件 = "execution(*
     * io.fusion.air.microservice.adapters.controllers..*.*(..))")
     * 
     * @param joinPoint
     */
    @After(value = "execution(* io.fusion.air.microservice.adapters.controllers..*.*(..))")
    public void logStatementAfter(JoinPoint joinPoint) {
        log.debug("5|TT|TIME=|STATUS=END|CLASS={}", joinPoint);
    }

    /**
     * Capture Overall Method Execution Time For Controllers
     * 擷取控制器方法的整體執行時間
     * For All Classes = "execution(*
     * io.fusion.air.microservice.adapters.controllers.*.*(..))")
     * With Sub Pkgs = "execution(*
     * io.fusion.air.microservice.adapters.controllers..*.*(..))")
     * 對於所有類別 = "execution(*
     * io.fusion.air.microservice.adapters.controllers.*.*(..))")
     * 包含子套件 = "execution(*
     * io.fusion.air.microservice.adapters.controllers..*.*(..))")
     * 
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "execution(* io.fusion.air.microservice.adapters.controllers..*.*(..))")
    public Object timeTrackerRest(ProceedingJoinPoint joinPoint) throws Throwable {
        return trackTime(4, "WS", joinPoint);
    }

    /**
     * Capture Overall Method Execution Time for Business Services
     * 擷取業務服務方法的整體執行時間
     * For All Classes = "execution(*
     * io.fusion.air.microservice.adapters.controllers.*.*(..))")
     * With Sub Pkgs = "execution(*
     * io.fusion.air.microservice.adapters.controllers..*.*(..))")
     * 對於所有類別 = "execution(*
     * io.fusion.air.microservice.adapters.controllers.*.*(..))")
     * 包含子套件 = "execution(*
     * io.fusion.air.microservice.adapters.controllers..*.*(..))")
     * 
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "execution(* io.fusion.air.microservice.adapters.service..*.*(..))")
    public Object timeTrackerBusinessService(ProceedingJoinPoint joinPoint) throws Throwable {
        return trackTime(3, "BS", joinPoint);
    }

    /**
     * Capture Overall Method Execution Time for Repository Services
     * 擷取儲存庫服務方法的整體執行時間
     * 
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "execution(* io.fusion.air.microservice.adapters.repository..*.*(..))")
    public Object timeTrackerRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        return trackTime(2, "DS", joinPoint);
    }

    /**
     * Capture Overall Method Execution Time for External Services
     * 擷取外部服務方法的整體執行時間
     * 
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "execution(* io.fusion.air.microservice.adapters.external..*.*(..))")
    public Object timeTrackerExternal(ProceedingJoinPoint joinPoint) throws Throwable {
        return trackTime(3, "ES", joinPoint);
    }

    /**
     * Track Time
     * 追蹤時間
     * 
     * @param level     / 層級
     * @param method    / 方法
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    private Object trackTime(int level, String method, ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String status = "STATUS=SUCCESS";
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            status = "STATUS=ERROR:" + e.getMessage();
            throw e;
        } finally {
            logTime(level, method, startTime, status, joinPoint);
        }
    }

    /**
     * Log Time Taken to Execute the Function
     * 記錄執行函數所花費的時間
     * 
     * @param level     / 層級
     * @param method    / 方法
     * @param startTime / 開始時間
     * @param status    / 狀態
     * @param joinPoint
     */
    private void logTime(int level, String method, long startTime, String status, ProceedingJoinPoint joinPoint) {
        long timeTaken = System.currentTimeMillis() - startTime;
        log.info("{}|{}|TIME={} ms|{}|CLASS={}|", level, method, timeTaken, status, joinPoint);
    }
}
