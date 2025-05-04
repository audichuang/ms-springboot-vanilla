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
import io.fusion.air.microservice.adapters.security.jwt.UserTokenAuthorization;
import static io.fusion.air.microservice.security.jwt.core.JsonWebTokenConstants.*;
// Aspect
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
// Spring
import org.springframework.stereotype.Component;

/**
 * Authorize Request Aspect Authorizes User Request in the following 6 modes
 * 授權請求切面：以下列 6 種模式授權使用者請求
 *
 * 1. Multi Token Mode: An API Annotated with @AuthorizeRequest Annotation -
 * This expects two
 * tokens in the header Auth Token and Tx Token
 * 1. 多令牌模式：使用 @AuthorizeRequest 註解標記的 API - 這需要在標頭中包含兩個令牌：Auth Token 和 Tx Token
 * 2. Single Token Mode: An APi Annotated with @SingleTokenAuthorizationRequest
 * will expect
 * the Auth Token in the headers (Refresh Token will be always there with every
 * Auth token).
 * 2. 單令牌模式：使用 @SingleTokenAuthorizationRequest 註解標記的 API 將需要在標頭中包含 Auth Token
 * (每個 Auth token 都會附帶 Refresh Token)。
 * 3. Secure Package Mode: Any Rest Controllers under the controllers secured
 * packaged will
 * be automatically protected under JWT Token Authorization
 * 3. 安全套件模式：在 controllers secured 套件下的任何 Rest Controllers 都將自動受到 JWT 令牌授權的保護。
 * 4. Internal Package Mode: Expects Auth and Tx Token for internal Service to
 * Service
 * Communication
 * 4. 內部套件模式：預期內部服務對服務通訊需要 Auth 和 Tx Token。
 * 5. External Package Mode: For External Service Communication.
 * 5. 外部套件模式：用於外部服務通訊。
 * 6. Refresh Token Mode: To refresh the tokens when Auth Token is expired and
 * Refresh Token
 * is still valid.
 * 6. 刷新令牌模式：當 Auth Token 過期但 Refresh Token 仍然有效時，用於刷新令牌。
 *
 * @author: Araf Karsh Hamid / 作者：Araf Karsh Hamid
 * @version: / 版本：
 * @date: / 日期：
 */
@Component
@Aspect
public class AuthorizeRequestAspect {

    // Autowired using the Constructor
    private final UserTokenAuthorization userTokenAuthorization;

    /**
     * Autowired using the Constructor
     * 使用建構子自動裝配
     * 
     * @param userTokenAuthorization
     */
    public AuthorizeRequestAspect(UserTokenAuthorization userTokenAuthorization) {
        this.userTokenAuthorization = userTokenAuthorization;
    }

    /**
     * Validate REST Endpoints Annotated with @SingleTokenAuthorizationRequired
     * Annotation
     * 驗證使用 @SingleTokenAuthorizationRequired 註解標記的 REST 端點
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(io.fusion.air.microservice.adapters.security.jwt.SingleTokenAuthorizationRequired)")
    public Object validateSingleTokenRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        return validateRequest(true, SINGLE_TOKEN_MODE, joinPoint, CONSUMERS);
    }

    /**
     * Validate REST Endpoints Annotated with @AuthorizationRequired Annotation
     * 驗證使用 @AuthorizationRequired 註解標記的 REST 端點
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(io.fusion.air.microservice.adapters.security.jwt.AuthorizationRequired)")
    public Object validateAnnotatedRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        return validateRequest(false, MULTI_TOKEN_MODE, joinPoint, CONSUMERS);
    }

    /**
     * Validate REST Endpoint Annotated with @validateRefreshToken Annotation
     * 驗證使用 @validateRefreshToken 註解標記的 REST 端點
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(io.fusion.air.microservice.adapters.security.jwt.ValidateRefreshToken)")
    public Object validateRefreshRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        return validateRequest(false, REFRESH_TOKEN_MODE, joinPoint, CONSUMERS);
    }

    /**
     * Secure All the Consumers REST Endpoints in the Secured Packaged using JWT
     * 使用 JWT 保護 Secured 套件中的所有消費者 REST 端點
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "execution(* io.fusion.air.microservice.adapters.controllers.secured.*.*(..))")
    public Object validateAnyRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        return validateRequest(false, SECURE_PKG_MODE, joinPoint, CONSUMERS);
    }

    /**
     * Secure All the Internal REST Endpoints in the Secured Packaged using JWT
     * 使用 JWT 保護 Secured 套件中的所有內部 REST 端點
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "execution(* io.fusion.air.microservice.adapters.controllers.internal.*.*(..))")
    public Object validateInternalRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        return validateRequest(false, SECURE_PKG_MODE, joinPoint, INTERNAL_SERVICES);
    }

    /**
     * Secure All the External REST Endpoints in the Secured Packaged using JWT
     * 使用 JWT 保護 Secured 套件中的所有外部 REST 端點
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "execution(* io.fusion.air.microservice.adapters.controllers.external.*.*(..))")
    public Object validateExternalRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        return validateRequest(false, SECURE_PKG_MODE, joinPoint, EXTERNAL_SERVICES);
    }

    /**
     * Validate the Request
     * 驗證請求
     *
     * @param singleToken / 單一令牌
     * @param tokenMode   / 令牌模式
     * @param joinPoint
     * @param tokenCtg    / 令牌類別
     * @return
     * @throws Throwable
     */
    private Object validateRequest(boolean singleToken, String tokenMode, ProceedingJoinPoint joinPoint, int tokenCtg)
            throws Throwable {
        return userTokenAuthorization.validateRequest(singleToken, tokenMode, joinPoint, tokenCtg);
    }
}