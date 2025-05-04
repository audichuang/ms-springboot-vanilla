/**
 * (C) Copyright 2021 Araf Karsh Hamid
 * (C) 版權所有 2021 Araf Karsh Hamid
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
import io.fusion.air.microservice.domain.exceptions.*;
import io.fusion.air.microservice.domain.exceptions.SecurityException;
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.server.config.ServiceConfig;
import io.fusion.air.microservice.utils.Utils;
// Spring
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
// Java
import org.slf4j.Logger;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import jakarta.persistence.*;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Exception Handler to handle All Exceptions at a centralized location using
 * AOP
 * 使用 AOP 在集中位置處理所有例外狀況的例外處理器
 *
 * @author: Araf Karsh Hamid / 作者：Araf Karsh Hamid
 * @version: / 版本：
 * @date: / 日期：
 */
@ControllerAdvice
@Order(2) // Make sure that the InputValidatorAdvice has the Highest Order Precedence
// 確保 InputValidatorAdvice 具有最高的順序優先級
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    // Set Logger -> Lookup will automatically determine the class name.
    // 設置記錄器 -> Lookup 將自動確定類名。
    private static final Logger log = getLogger(lookup().lookupClass());

    // ServiceConfig
    // @Autowired not required - Constructor based Autowiring
    // @Autowired 不需要 - 基於建構子的自動裝配
    private final ServiceConfig serviceConfig;

    /**
     * Constructor for Autowiring
     * 用於自動裝配的建構子
     * 
     * @param serviceConfig
     */
    public ExceptionHandlerAdvice(ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    /**
     * Handle All Exceptions
     * 處理所有例外狀況
     * 
     * @param ex
     * @param body
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body,
            HttpHeaders headers, HttpStatusCode status,
            WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            // Purpose: If the status is an internal server error (500), this block sets the
            // exception
            // as a request attribute (jakarta.servlet.error.exception). This is useful for
            // internal
            // logging or forwarding error details to a logging framework or view.
            // 目的：如果狀態是內部伺服器錯誤（500），此區塊將例外設置為請求屬性
            // （jakarta.servlet.error.exception）。這對於內部記錄或將錯誤詳細資訊轉發到記錄框架或視圖很有用。
            // request.setAttribute(): This sets an attribute on the WebRequest with the key
            // "jakarta.servlet.error.exception" and the value of the exception (ex). The 0
            // is the
            // scope, indicating that the attribute is available only for the current
            // request.
            // request.setAttribute()：這在 WebRequest 上設置一個屬性，鍵為
            // "jakarta.servlet.error.exception"，值為例外（ex）。0 是範圍，表示該屬性僅對當前請求可用。
            request.setAttribute("jakarta.servlet.error.exception", ex, 0);
        }
        return createErrorResponse(ex, headers, (HttpStatus) status, request);
    }

    /**
     * Build Error Response Entity
     * 建立錯誤回應實體
     * 
     * @param ex
     * @param status
     * @param request
     * @return
     */
    /**
     * private ResponseEntity<Object> createErrorResponse(Exception ex,
     * HttpStatus status,
     * WebRequest request) {
     * return createErrorResponse(ex, ex.getMessage(), "599",null, status, request);
     * }
     */

    /**
     * Build Error Response Entity
     * 建立錯誤回應實體
     * 
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    private ResponseEntity<Object> createErrorResponse(Exception ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        return createErrorResponse(ex, ex.getMessage(), "599", headers, status, request);
    }

    /**
     * Build Error Response Entity
     * 建立錯誤回應實體
     * 
     * @param ase
     * @param errorCode / 錯誤代碼
     * @param request
     * @return
     */
    private ResponseEntity<Object> createErrorResponse(AbstractServiceException ase,
            String errorCode,
            WebRequest request) {
        return createErrorResponse(ase, ase.getMessage(), errorCode, null, ase.getHttpStatus(), request);
    }

    /**
     * Build Error Response Entity
     * 建立錯誤回應實體
     * 
     * @param ase
     * @param errorCode / 錯誤代碼
     * @param headers
     * @param request
     * @return
     */
    /**
     * private ResponseEntity<Object> createErrorResponse(AbstractServiceException
     * ase,
     * String errorCode,
     * HttpHeaders headers,
     * WebRequest request) {
     * return createErrorResponse(ase, ase.getMessage(), errorCode, headers,
     * ase.getHttpStatus(), request);
     * }
     */

    /**
     * Unable to Save Persistence Exceptions
     * 無法儲存持久性例外
     * 
     * @param pEx
     * @param request
     * @return
     */
    private ResponseEntity<Object> createErrorResponse(PersistenceException pEx,
            String message,
            String errorCode,
            WebRequest request) {
        return createErrorResponse(pEx, message, errorCode, null, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Build Standard Error Response
     * 建立標準錯誤回應
     * 
     * @param exception
     * @param message    / 訊息
     * @param errorCode  / 錯誤代碼
     * @param headers
     * @param httpStatus / HTTP 狀態
     * @param request
     * @return
     */
    private ResponseEntity<Object> createErrorResponse(Throwable exception,
            String message,
            String errorCode,
            HttpHeaders headers,
            HttpStatus httpStatus,
            WebRequest request) {

        String errorPrefix = (serviceConfig != null) ? serviceConfig.getServiceApiErrorPrefix() : "AKH"; // Microservice
                                                                                                         // Prefix /
                                                                                                         // 微服務前綴
        String appErrorCode = errorPrefix + errorCode; // Error Code / 錯誤代碼
        if (exception instanceof AbstractServiceException ase) {
            ase.setErrorCode(appErrorCode); // Set the Error Code / 設置錯誤代碼
        }
        logException(appErrorCode, exception); // Log Exception / 記錄例外
        StandardResponse stdResponse = Utils.createErrorResponse(
                null, errorPrefix, errorCode, httpStatus, message); // Std Response / 標準回應
        String cp = request.getContextPath();
        log.debug("Error Web Request Context Path = {} ", cp);
        return (headers != null)
                ? new ResponseEntity<>(stdResponse, headers, httpStatus) // HTTP Response / HTTP 回應
                : new ResponseEntity<>(stdResponse, httpStatus);
    }

    // ================================================================================================================
    // SERVER EXCEPTIONS: ERROR CODES 590 - 599
    // 服務器例外：錯誤代碼 590 - 599
    // ================================================================================================================
    /**
     * Handle Runtime Exception
     * 處理執行時例外
     * 
     * @param runEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Object> runtimeException(RuntimeException runEx, WebRequest request) {
        return createErrorResponse(runEx, runEx.getMessage(), "590", null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Handle Any Exception
     * 處理任何例外
     * 
     * @param runEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = Throwable.class)
    public ResponseEntity<Object> throwable(Throwable runEx, WebRequest request) {
        return createErrorResponse(runEx, runEx.getMessage(), "599", null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // ================================================================================================================
    // STANDARD EXCEPTIONS: ERROR CODES 400 - 409
    // 標準例外：錯誤代碼 400 - 409
    // ================================================================================================================
    /**
     * Access Denied Exception
     * 拒絕訪問例外
     * 
     * @param adEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<Object> accessDeniedException(AccessDeniedException adEx, WebRequest request) {
        return createErrorResponse(adEx, adEx.getMessage(), "403", null, HttpStatus.FORBIDDEN, request);
    }

    /**
     * Request Rejected Exception
     * 請求被拒絕例外
     * 
     * @param adEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = RequestRejectedException.class)
    public ResponseEntity<Object> requestRejectedException(RequestRejectedException adEx, WebRequest request) {
        return createErrorResponse(adEx, adEx.getMessage(), "403", null, HttpStatus.FORBIDDEN, request);
    }

    /**
     * v
     * Exception if the Resource NOT Available!
     * 
     * @param rnfEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = ResourceException.class)
    public ResponseEntity<Object> resourceException(ResourceException rnfEx, WebRequest request) {
        return createErrorResponse(rnfEx, "404", request);
    }

    /**
     * v
     * Exception if the Resource IS NOT FOUND!
     * 
     * @param rnfEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Object> resourceNotFoundException(ResourceNotFoundException rnfEx, WebRequest request) {
        return createErrorResponse(rnfEx, "404", request);
    }

    // ================================================================================================================
    // SECURITY EXCEPTIONS: ERROR CODES 410 - 429
    // ================================================================================================================
    /**
     * Authorization Exception
     * 安全例外
     * 
     * @param adEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = SecurityException.class)
    public ResponseEntity<Object> securityException(SecurityException adEx, WebRequest request) {
        return createErrorResponse(adEx, "411", request);
    }

    /**
     * Authorization Exception
     * 
     * @param adEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = AuthorizationException.class)
    public ResponseEntity<Object> authorizationException(AuthorizationException adEx, WebRequest request) {
        return createErrorResponse(adEx, "413", request);
    }

    /**
     * JWT Token Extraction Exception
     * JWT 令牌提取例外
     * 
     * @param adEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = JWTTokenExtractionException.class)
    public ResponseEntity<Object> jwtTokenExtractionException(JWTTokenExtractionException adEx, WebRequest request) {
        return createErrorResponse(adEx, "414", request);
    }

    /**
     * JWT Token Expired Exception
     * JWT 令牌過期例外
     * 
     * @param adEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = JWTTokenExpiredException.class)
    public ResponseEntity<Object> jwtTokenExpiredException(JWTTokenExpiredException adEx, WebRequest request) {
        return createErrorResponse(adEx, "415", request);
    }

    /**
     * JWT Token Subject Exception
     * JWT 令牌主體例外
     * 
     * @param adEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = JWTTokenSubjectException.class)
    public ResponseEntity<Object> jwtTokenSubjectException(JWTTokenSubjectException adEx, WebRequest request) {
        return createErrorResponse(adEx, "416", request);
    }

    /**
     * JWT Invalid Signature Exception
     * JWT 無效簽名例外
     * 
     * @param adEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = JWTInvalidSignatureException.class)
    public ResponseEntity<Object> jwtInvalidSignatureException(JWTInvalidSignatureException adEx, WebRequest request) {
        return createErrorResponse(adEx, "417", request);
    }

    /**
     * JWT UnDefined Exception
     * JWT 未定義例外
     * 
     * @param adEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = JWTUnDefinedException.class)
    public ResponseEntity<Object> jwtUnDefinedException(JWTUnDefinedException adEx, WebRequest request) {
        return createErrorResponse(adEx, "429", request);
    }

    // ================================================================================================================
    // MESSAGING EXCEPTIONS: ERROR CODES 430 - 439
    // ================================================================================================================
    /**
     * Messaging Exception
     * 訊息傳遞例外
     * 
     * @param msgEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = MessagingException.class)
    public ResponseEntity<Object> handleMessagingException(MessagingException msgEx, WebRequest request) {
        return createErrorResponse(msgEx, "430", request);
    }

    // ================================================================================================================
    // DATABASE EXCEPTIONS: ERROR CODES 440 - 459
    // ================================================================================================================
    /**
     * Database Exception
     * 資料庫例外
     * 
     * @param dbEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = DatabaseException.class)
    public ResponseEntity<Object> handleDatabaseException(DatabaseException dbEx, WebRequest request) {
        return createErrorResponse(dbEx, "440", request);
    }

    /**
     * Unable to Query Due to Persistence Exception
     * 
     * @param pEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = NoResultException.class)
    public ResponseEntity<Object> handlePersistenceException(NoResultException pEx, WebRequest request) {
        return createErrorResponse(pEx, "No Result Found!", "441", request);
    }

    /**
     * Unable to Query Due to Persistence Exception
     * 
     * @param pEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = NonUniqueResultException.class)
    public ResponseEntity<Object> handlePersistenceException(NonUniqueResultException pEx, WebRequest request) {
        return createErrorResponse(pEx, "Duplicate Data!", "442", request);
    }

    /**
     * Data Not Found Exception
     * 資料未找到例外
     * 
     * @param dnfEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = DataNotFoundException.class)
    public ResponseEntity<Object> handleDataNotFoundException(DataNotFoundException dnfEx, WebRequest request) {
        return createErrorResponse(dnfEx, "444", request);
    }

    /**
     * Unable to Query Due to Persistence Exception
     * 
     * @param pEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<Object> handlePersistenceException(EntityNotFoundException pEx, WebRequest request) {
        return createErrorResponse(pEx, "Entity Not Found!", "446", request);
    }

    /**
     * Unable to Query Due to Persistence Exception
     * 
     * @param pEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = EntityExistsException.class)
    public ResponseEntity<Object> handlePersistenceException(EntityExistsException pEx, WebRequest request) {
        return createErrorResponse(pEx, "Duplicate Entity Found!", "447", request);
    }

    /**
     * Duplicate Data Exception
     * 重複資料例外
     * 
     * @param ddEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = DuplicateDataException.class)
    public ResponseEntity<Object> handleDuplicateDataException(DuplicateDataException ddEx, WebRequest request) {
        return createErrorResponse(ddEx, "448", request);
    }

    /**
     * Unable to Query Due to Persistence Exception
     * 
     * @param pEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = QueryTimeoutException.class)
    public ResponseEntity<Object> handlePersistenceException(QueryTimeoutException pEx, WebRequest request) {
        return createErrorResponse(pEx, "Query Timed out!", "449", request);
    }

    /**
     * Unable to Save Exception
     * 無法儲存例外
     * 
     * @param utEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = UnableToSaveException.class)
    public ResponseEntity<Object> handleUnableToSaveException(UnableToSaveException utEx, WebRequest request) {
        return createErrorResponse(utEx, "452", request);
    }

    /**
     * Unable to Save Due to Persistence Exception
     * 
     * @param pEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = TransactionRequiredException.class)
    public ResponseEntity<Object> handlePersistenceException(TransactionRequiredException pEx, WebRequest request) {
        return createErrorResponse(pEx, "Tx Required", "453", request);
    }

    /**
     * Unable to Save Due to Persistence Exception
     * 
     * @param pEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = RollbackException.class)
    public ResponseEntity<Object> handlePersistenceException(RollbackException pEx, WebRequest request) {
        return createErrorResponse(pEx, "Rollback Error!", "454", request);
    }

    /**
     * Unable to Save Due to Persistence Exception
     * 
     * @param pEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = LockTimeoutException.class)
    public ResponseEntity<Object> handlePersistenceException(LockTimeoutException pEx, WebRequest request) {
        return createErrorResponse(pEx, "Lock Timed out!", "455", request);
    }

    /**
     * Unable to Save Due to Dirty Read/Write
     * 
     * @param utEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = OptimisticLockException.class)
    public ResponseEntity<Object> handleOptimisticLockException(OptimisticLockException utEx, WebRequest request) {
        return createErrorResponse(utEx, "Version Mismatch (Optimistic Lock)!", "456", request);
    }

    /**
     * Unable to Save Due to Dirty Read/Write
     * 
     * @param utEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = PessimisticLockException.class)
    public ResponseEntity<Object> handleOptimisticLockException(PessimisticLockException utEx, WebRequest request) {
        return createErrorResponse(utEx, "Version Mismatch (Pessimistic Lock)!", "457", request);
    }

    /**
     * Unable to Save Due to Persistence Exception
     * 
     * @param pEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = PersistenceException.class)
    public ResponseEntity<Object> handlePersistenceException(PersistenceException pEx, WebRequest request) {
        return createErrorResponse(pEx, "Persistence Error: " + pEx.getMessage(), "458", request);
    }

    /**
     * Unable to Save Exception to SQL Exception
     * 
     * @param sqlEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = SQLException.class)
    public ResponseEntity<Object> handleSQLException(SQLException sqlEx, WebRequest request) {
        return createErrorResponse(sqlEx, sqlEx.getMessage(), "459", null, HttpStatus.BAD_REQUEST, request);
    }

    // ================================================================================================================
    // BUSINESS EXCEPTIONS: ERROR CODES 460 - 489
    // ================================================================================================================
    /**
     * Business Exception
     * 
     * @param buEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = BusinessServiceException.class)
    public ResponseEntity<Object> handleBusinessServiceException(BusinessServiceException buEx, WebRequest request) {
        return createErrorResponse(buEx, "460", request);
    }

    /**
     * InputDataException
     * 
     * @param idEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = InputDataException.class)
    public ResponseEntity<Object> handleInputDataException(InputDataException idEx, WebRequest request) {
        return createErrorResponse(idEx, "461", request);
    }

    /**
     * Mandatory Data Required Exception
     * 
     * @param mdrEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = MandatoryDataRequiredException.class)
    public ResponseEntity<Object> handleMandatoryDataRequiredException(MandatoryDataRequiredException mdrEx,
            WebRequest request) {
        return createErrorResponse(mdrEx, "463", request);
    }

    /**
     * Rate Limit Exceeded Exception
     * 
     * @param mdrEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = LimitExceededException.class)
    public ResponseEntity<Object> handleLimitExceededException(LimitExceededException mdrEx, WebRequest request) {
        return createErrorResponse(mdrEx, "464", request);
    }

    // ================================================================================================================
    // CONTROLLER EXCEPTIONS: ERROR CODES 490 - 499
    // ================================================================================================================
    /**
     * Controller Exception
     * 
     * @param coEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = ControllerException.class)
    public ResponseEntity<Object> handleControllerException(ControllerException coEx, WebRequest request) {
        return createErrorResponse(coEx, "490", request);
    }

    // ================================================================================================================
    // GENERIC Exception Handling
    // ================================================================================================================
    /**
     * Handle any unspecified Exceptions
     * 
     * @param runEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception runEx, WebRequest request) {
        return createErrorResponse(runEx, runEx.getMessage(), "500", null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // ================================================================================================================
    // LOG EXCEPTIONS
    // ================================================================================================================
    /**
     *
     * @param status
     * @param e
     */
    private void logException(String status, Throwable e) {
        String msg = getStackTraceAsString(e);
        log.trace(msg);
        msg = e.getMessage();
        log.info("2|EH|TIME=00|STATUS=Error: {}|CLASS={}|", status, msg);
    }

    /**
     * Get the Stack Trace
     * 
     * @param e
     * @return
     */
    private String getStackTraceAsString(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
