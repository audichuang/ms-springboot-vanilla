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

// Custom
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.server.config.ServiceConfig;
import io.fusion.air.microservice.utils.Utils;
// Spring
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
// Java
import jakarta.validation.ConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Input Validator Advice
 * 輸入驗證器建議
 *
 * This (AOP) Advice will validate all the inputs at a central location.
 * 這個（AOP）建議將在集中位置驗證所有輸入。
 *
 * @author: Araf Karsh Hamid / 作者：Araf Karsh Hamid
 * @version: / 版本：
 * @date: / 日期：
 */
@ControllerAdvice
@Order(1) // Don't Change this Order Precedences. Changing this affect the Error
          // Reporting.
// 請勿更改此順序優先級。更改此會影響錯誤報告。
public class InputValidatorAdvice {

    // Set Logger -> Lookup will automatically determine the class name.
    // 設置記錄器 -> Lookup 將自動確定類名。
    private static final Logger log = getLogger(lookup().lookupClass());

    // Autowired using Constructor
    // 使用建構子自動裝配
    private ServiceConfig serviceConfig;

    /**
     * Autowired using Constructor
     * 使用建構子自動裝配
     * 
     * @param serviceCfg
     */
    public InputValidatorAdvice(ServiceConfig serviceCfg) {
        serviceConfig = serviceCfg;
    }

    /**
     * Validating Complex Object Rules
     * 驗證複雜物件規則
     * 
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " | " + error.getDefaultMessage())
                .collect(Collectors.toCollection(ArrayList::new));
        log.debug("462: List Errors = {} ", errors);
        return createErrorResponse("462", "Errors: Invalid Method Arguments", errors);
    }

    /**
     * Validating Method Parameters
     * 驗證方法參數
     * 
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex,
            WebRequest request) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toCollection(ArrayList::new));
        log.debug("463: List Errors = {} ", errors);
        return createErrorResponse("463", "Errors: Input Constraint Violations", errors);
    }

    /**
     * Create the Error Response
     * 創建錯誤回應
     * 
     * @param errorCode / 錯誤代碼
     * @param errorMsg  / 錯誤訊息
     * @param errors    / 錯誤列表
     * @return
     */
    private ResponseEntity<Object> createErrorResponse(String errorCode, String errorMsg, List<String> errors) {
        String errorPrefix = (serviceConfig != null) ? serviceConfig.getServiceApiErrorPrefix() : "AKH";
        long startTime = System.currentTimeMillis();
        String status = "STATUS=ERROR: " + errorMsg;
        Collections.sort(errors);
        StandardResponse stdResponse = Utils.createErrorResponse(errors, errorPrefix, errorCode, HttpStatus.BAD_REQUEST,
                errorMsg);
        logTime(startTime, status);
        return new ResponseEntity<>(stdResponse, null, HttpStatus.BAD_REQUEST);
    }

    /**
     * Log Time with Input Validation Errors
     * 記錄輸入驗證錯誤的時間
     * 
     * @param startTime / 開始時間
     * @param status    / 狀態
     */
    private void logTime(long startTime, String status) {
        long timeTaken = System.currentTimeMillis() - startTime;
        log.info("2|IV|TIME={} ms|{}|CLASS=|", timeTaken, status);
    }
}
