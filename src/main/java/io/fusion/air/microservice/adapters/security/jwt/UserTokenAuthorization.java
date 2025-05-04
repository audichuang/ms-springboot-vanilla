/**
 * (C) Copyright 2022 Araf Karsh Hamid
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
 */
package io.fusion.air.microservice.adapters.security.jwt;
// Custom
import io.fusion.air.microservice.adapters.security.core.UserRole;
import io.fusion.air.microservice.adapters.security.service.UserDetailsServiceImpl;
import io.fusion.air.microservice.domain.exceptions.*;
import io.fusion.air.microservice.security.jwt.client.JsonWebTokenValidator;
import io.fusion.air.microservice.security.jwt.core.TokenData;
import io.fusion.air.microservice.security.jwt.core.TokenDataFactory;
import static io.fusion.air.microservice.security.jwt.core.JsonWebTokenConstants.*;
// JWT
// Jakarta
import jakarta.servlet.http.HttpServletRequest;
// Aspect
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
// Log
import org.slf4j.Logger;
import org.slf4j.MDC;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;
// Spring
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * User Token Authorization for a Stateless Microservices Architecture
 * 用於無狀態微服務架構的使用者令牌授權
 *
 * 1. Why Set the Spring Security Context in a Stateless Architecture?
 *    為什麼在無狀態架構中設置 Spring Security 上下文？
 * 	•	Per-Request Authentication / 每個請求的驗證
 *     Even though you're not storing a session between requests, within the lifecycle of a single
 *     request you still need to tell Spring Security, "this user is authenticated." Setting the
 *     SecurityContextHolder.getContext().setAuthentication(...) ensures that any downstream checks
 *     (e.g., @PreAuthorize, SecurityContextHolder) in the same request know who the user is and
 *     what roles they have.
 *     雖然沒有在請求之間存儲會話，但在單個請求的生命週期內，仍然需要告訴 Spring Security "此用戶已驗證"。
 *     設置 SecurityContextHolder.getContext().setAuthentication(...) 確保同一請求中的任何下游檢查
 *     （例如 @PreAuthorize, SecurityContextHolder）知道用戶是誰以及他們擁有哪些角色。
 * 	•	Framework Integration / 框架整合
 *      Spring Security uses the SecurityContext to enforce method-level security (@PreAuthorize,
 *      @RolesAllowed) and to provide the principal object to controllers (@AuthenticationPrincipal)
 *      or any code that calls SecurityContextHolder.getContext().getAuthentication(). If you never
 *      set the authentication, Spring will treat the request as anonymous
 *      Spring Security 使用 SecurityContext 來執行方法級別的安全性（@PreAuthorize, @RolesAllowed）
 *      並為控制器提供主體對象（@AuthenticationPrincipal）或任何調用
 *      SecurityContextHolder.getContext().getAuthentication() 的代碼。如果從未設置身份驗證，
 *      Spring 將把請求視為匿名請求。
 *
 * 2. How Do Subsequent Calls Work Without Server-Side State?
 *    在沒有服務器端狀態的情況下，後續調用如何工作？
 * In a stateless system, each incoming request:
 * 在無狀態系統中，每個進入的請求：
 * 	1.	Arrives With a Token / 帶著令牌到達
 *     The client (often a browser or another service) includes the JWT in an HTTP header (e.g.,
 *     Authorization: Bearer <token>).
 *     客戶端（通常是瀏覽器或其他服務）在 HTTP 標頭中包含 JWT（例如 Authorization: Bearer <token>）。
 * 	2.	Token Is Parsed and Validated / 令牌被解析和驗證
 *      A filter or an aspect (like in this example) reads the token, validates it (signature, expiration,
 *      claims), and if valid, creates a UsernamePasswordAuthenticationToken (or a similar Authentication
 *      object).
 *      過濾器或切面（如本例中）讀取令牌，驗證它（簽名，過期時間，聲明），如果有效，則創建
 *      UsernamePasswordAuthenticationToken（或類似的 Authentication 對象）。
 * 	3.	Security Context Is Set / 設置安全上下文
 *     SecurityContextHolder.getContext().setAuthentication(...) is invoked for that request only.
 *     SecurityContextHolder.getContext().setAuthentication(...) 僅針對該請求調用。
 * 	    •	This means from now until the response is completed, Spring Security sees the user as
 * 	        authenticated.
 * 	        這意味著從現在到完成響應為止，Spring Security 將用戶視為已驗證。
 * 	    •	Once the request finishes, that context is discarded.
 * 	        請求完成後，該上下文將被丟棄。
 * 	4.	Next Request / 下一個請求
 *      The next request must repeat this process: it again includes the JWT, the filter re-validates
 *      the token, sets the new SecurityContext, and so on. There is no "session" to remember anything
 *      across requests—only the token that the client re-sends each time.
 *      下一個請求必須重複此過程：它再次包含 JWT，過濾器重新驗證令牌，設置新的 SecurityContext，
 *      等等。沒有"會話"來記住請求之間的任何內容——只有客戶端每次重新發送的令牌。
 *
 *  3. Key Point: Stateless Means No Server-Side Session
 *     關鍵點：無狀態意味著沒有服務器端會話
 * 	    •	No HTTP Session: In a truly stateless microservice, you typically disable or ignore the HTTP
 * 	        session. Spring Security's SessionCreationPolicy.STATELESS ensures Spring does not create
 * 	        or use an HTTP session. (Check out WebSecurityConfiguration in io.f.a.m.security.core)
 * 	        無 HTTP 會話：在真正的無狀態微服務中，通常禁用或忽略 HTTP 會話。
 * 	        Spring Security 的 SessionCreationPolicy.STATELESS 確保 Spring 不創建或使用 HTTP 會話。
 * 	        （查看 io.f.a.m.security.core 中的 WebSecurityConfiguration）
 * 	    •	Every Request Is Fresh: Authentication details have to be established each time. You do
 * 	        this by parsing the token and setting the security context again.
 * 	        每個請求都是新的：每次都必須建立身份驗證詳細信息。通過解析令牌並再次設置安全上下文來實現。
 *
 * 	Summary: / 摘要：
 * 	•	Set the security context in a stateless architecture, but it applies only to the current request.
 * 	    在無狀態架構中設置安全上下文，但它僅適用於當前請求。
 * 	•	No server-side session is used; instead, each request carries its own credentials (JWT), which
 * 	    are validated anew.
 * 	    不使用服務器端會話；相反，每個請求攜帶自己的憑證（JWT），這些憑證會重新驗證。
 * 	•	This keeps the service stateless, yet still leverages Spring Security's request-based authorization
 * 	    checks.
 * 	    這保持服務無狀態，同時仍然利用 Spring Security 基於請求的授權檢查。
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Component
public class UserTokenAuthorization {

    // Set Logger -> Lookup will automatically determine the class name.
    // 設置日誌器 -> 查找將自動確定類名。
    private static final Logger log = getLogger(lookup().lookupClass());

    // Autowired using the Constructor
    // 使用構造函數自動注入
    private final TokenDataFactory tokenFactory;

    // Autowired using the Constructor
    // 使用構造函數自動注入
    private final UserDetailsServiceImpl userDetailsService;

    // Autowired using the Constructor
    // 使用構造函數自動注入
    private final ClaimsManager claimsManager;

    /**
     * Autowired using the Constructor
     * 使用構造函數自動注入
     * @param tokenFactory
     * @param userService
     * @param claims
     */
    public UserTokenAuthorization(TokenDataFactory tokenFactory, UserDetailsServiceImpl userService,
                                  ClaimsManager claims ) {
        this.tokenFactory = tokenFactory;
        this.userDetailsService = userService;
        this.claimsManager = claims;
    }

    /**
     * Validate the Request
     * 驗證請求
     *
     * @param singleToken
     * @param tokenMode
     * @param joinPoint
     * @param tokenCtg
     * @return
     * @throws Throwable
     */
    public Object validateRequest(boolean singleToken, String tokenMode,
                                  ProceedingJoinPoint joinPoint, int tokenCtg) throws Throwable {
        // Get the request object
        // 獲取請求對象
        long startTime = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logTime(startTime, "Extracting & Validating Token", request.getRequestURI(), joinPoint);
        // Create Token Data from the TokenDataFactory
        // 從 TokenDataFactory 創建令牌數據
        final TokenData tokenData = tokenFactory.getTokenData( request.getHeader(AUTH_TOKEN), AUTH_TOKEN, joinPoint.toString());
        // Get the User (Subject) from the Token
        // 從令牌獲取用戶（主體）
        final String user = getUser(startTime, tokenData, joinPoint);
        log.debug("Validate Request: User Extracted... {} ", user);
        // If the User == NULL then ERROR is thrown from getUser() method itself
        // 如果用戶為空，則從 getUser() 方法本身拋出錯誤
        // Validate the Token when User is NOT Null
        // 當用戶不為空時驗證令牌
        UserDetails userDetails = validateToken(startTime, user, tokenMode, tokenData, joinPoint, tokenCtg);
        // Create Authorize Token
        // 創建授權令牌
        // UsernamePasswordAuthenticationToken: A core Spring Security class representing an
        // authentication request or a fully authenticated user
        // UsernamePasswordAuthenticationToken：代表身份驗證請求或完全驗證的用戶的核心 Spring Security 類
        // Parameters:
        // 參數：
        // 1.	principal: Set to userDetails (the authenticated user).
        // 主體：設置為 userDetails（已驗證的用戶）。
        // 2.	credentials: Passed as null because we already have a validated token (no password needed).
        // 憑證：傳遞為 null，因為我們已經有一個驗證過的令牌（不需要密碼）。
        // 3.	authorities: The roles/permissions extracted from UserDetails.
        // 授權：從 UserDetails 提取的角色/權限。
        // setDetails(...): Adds additional info from the HTTP request, such as remote IP or
        // session ID, for audit or security checks.
        // setDetails(...)：添加來自 HTTP 請求的額外信息，如遠程 IP 或會話 ID，用於審計或安全檢查。
        UsernamePasswordAuthenticationToken authorizeToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authorizeToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // Set the Security Context with current user as Authorized for the request, So it passes
        // the Spring Security Configurations successfully.
        // 將安全上下文設置為當前用戶已授權此請求，以成功通過 Spring Security 配置。
        // - SecurityContextHolder: The container where Spring Security stores the currently
        //   authenticated user's details.
        // - SecurityContextHolder：Spring Security 存儲當前已驗證用戶詳細信息的容器。
        //  - By placing authorizeToken here, downstream parts of the application (controllers,
        //    services, etc.) can call SecurityContextHolder.getContext().getAuthentication() to obtain:
        //  - 通過在此處放置 authorizeToken，應用程序的下游部分（控制器、服務等）可以調用
        //    SecurityContextHolder.getContext().getAuthentication() 來獲得：
        //    - The current user (userDetails).
        //    - 當前用戶（userDetails）。
        //    - Roles/authorities.
        //    - 角色/授權。
        //    - Whether the user is authenticated.
        //    - 用戶是否已驗證。
        //  - This effectively tells Spring Security, "We have a valid user for this request," and the
        //     framework will treat subsequent calls as authenticated.
        //  - 這有效地告訴 Spring Security，"我們有這個請求的有效用戶"，框架將把後續調用視為已驗證。
        SecurityContextHolder.getContext().setAuthentication(authorizeToken);
        logTime(startTime, SUCCESS, "User Authorized for the request",  joinPoint);
        // Check the Tx Token if It's NOT a SINGLE_TOKEN Request
        // 如果不是 SINGLE_TOKEN 請求，則檢查 Tx 令牌
        if(!singleToken ) {
            validateTxToken(startTime, user, request.getHeader(TX_TOKEN), joinPoint);
        }
        return joinPoint.proceed();
    }

    /**
     * Returns the user from the Token
     * 從令牌返回用戶
     *
     * @param startTime
     * @param token
     * @param joinPoint
     * @return
     */
    private String getUser(long startTime, TokenData token, ProceedingJoinPoint joinPoint) {
        try {
            String user = JsonWebTokenValidator.getSubjectFromToken(token);
            // Store the user info for logging
            // 儲存用戶信息用於日誌記錄
            MDC.put("user", user);
            return user;
        } catch (Exception e) {
            logTime(startTime, ERROR, e.getMessage(), joinPoint);
            throw e;
        }
    }

    /**
     * Validate Token
     * 驗證令牌
     * - User / 用戶
     * - Expiry Time / 過期時間
     *
     * @param startTime
     * @param user
     * @param tokenMode
     * @param tokenData
     * @param joinPoint
     * @param tokenCtg
     * @return
     */
    private UserDetails validateToken(long startTime, String user, String tokenMode,
                                      TokenData tokenData, ProceedingJoinPoint joinPoint, int tokenCtg) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user);
        String msg = null;
        try {
            // Validate the Token with the User details and Token Expiry
            // 使用用戶詳細信息和令牌過期時間驗證令牌
            if (JsonWebTokenValidator.validateToken(userDetails.getUsername(), tokenData)) {
                // Validate the Token Type
                // 驗證令牌類型
                String tokenType = JsonWebTokenValidator.getTokenType(tokenData);
                validateAuthTokenType( startTime,  user,  tokenType, tokenMode,  tokenCtg,  joinPoint);
                // Verify that the user role name matches the role name defined by the protected resource
                // 驗證用戶角色名稱是否與受保護資源定義的角色名稱匹配
                String role = JsonWebTokenValidator.getUserRoleFromToken(tokenData);
                verifyTheUserRole( role,  tokenMode,  joinPoint);
                return userDetails;
            } else {
                msg = "Auth-Token: Unauthorized Access: Token Validation Failed!";
                throw new AuthorizationException(msg);
            }
        } catch(AuthorizationException e) {
            msg = e.getMessage();
            throw e;
        } catch(Exception e) {
            msg = "Auth-Token: Unauthorized Access: Error: "+e.getMessage();
            throw new AuthorizationException(msg, e);
        } finally {
            // Error is Logged ONLY if msg != NULL
            // 只有在 msg != NULL 時才記錄錯誤
            logTime(startTime, ERROR, msg, joinPoint);
        }
    }

    /**
     * Validate the Token Type
     * 驗證令牌類型
     *
     * @param startTime
     * @param user
     * @param tokenCtg
     * @param joinPoint
     */
    private void validateAuthTokenType(long startTime, String user, String tokenType, String tokenMode,
                                       int tokenCtg, ProceedingJoinPoint joinPoint) {
        String msg = null;
        try {
            switch(tokenCtg) {
                case CONSUMERS:
                    if (tokenMode.equals(REFRESH_TOKEN_MODE) && !tokenType.equals(AUTH_REFRESH)) {
                        msg = "Invalid Refresh Token!  (" + tokenType + ")  " + user;
                        throw new AuthorizationException(msg);
                    }
                    if ( !tokenType.equals(AUTH)) {
                        msg = "Invalid Auth Token! (" + tokenType + ")  " + user;
                        throw new AuthorizationException(msg);
                    }
                    break;
                case INTERNAL_SERVICES:
                    if (tokenMode.equals(SECURE_PKG_MODE) && !tokenType.equals(TX_SERVICE )) {
                        msg = "Invalid Auth Token ("+tokenType+") For Internal Service! " + user;
                        throw new AuthorizationException(msg);
                    }
                    break;
                case EXTERNAL_SERVICES:
                    if (tokenMode.equals(SECURE_PKG_MODE) && !tokenType.equals(TX_EXTERNAL )) {
                        msg = "Invalid Auth Token ("+tokenType+") For External! " + user;
                        throw new AuthorizationException(msg);
                    }
                    break;
                default:
                    throw new AuthorizationException("Invalid Token Category.");
            }
        } finally {
            // Error is Logged ONLY if msg != NULL
            // 只有在 msg != NULL 時才記錄錯誤
            logTime(startTime, ERROR, msg, joinPoint);
        }
    }

    /**
     * Verify the User Role Matches the Claim
     * 驗證用戶角色是否與聲明匹配
     * @param role
     * @param tokenMode
     * @param joinPoint
     */
    private void verifyTheUserRole(String role, String tokenMode, ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String annotationRole = null;
        try {
            if (tokenMode.equalsIgnoreCase(MULTI_TOKEN_MODE)) {
                AuthorizationRequired annotation =  signature.getMethod().getAnnotation(AuthorizationRequired.class);
                annotationRole = annotation.role();
            } else if(tokenMode.equalsIgnoreCase(SINGLE_TOKEN_MODE)) {
                SingleTokenAuthorizationRequired annotation =  signature.getMethod().getAnnotation(SingleTokenAuthorizationRequired.class);
                annotationRole = annotation.role();
            } else {
                // Default Role in Secure Package Mode
                // 安全套件模式中的默認角色
                annotationRole = ROLE_USER;
            }
        } catch (Exception e) {
            log.error("Authorization Failed: Role Not Found!");
            throw new AuthorizationException("Unauthorized Access: Role Not Found!", e);
        }
        log.debug("Required Role = {},  User (Claims) Role = {} ", annotationRole, role);
        // If the Role in the Token is User and Required is Admin then Reject the request
        // 如果令牌中的角色是用戶而所需角色是管理員，則拒絕請求
        if(role.trim().equalsIgnoreCase(UserRole.USER.toString()) && annotationRole != null
                && annotationRole.equals(UserRole.ADMIN.toString())) {
            throw new AuthorizationException("Unauthorized Access: Invalid User Role!");
        }
    }

    /**
     * Validate Tx Token and Set the Claims in the ClaimsManager
     * 驗證交易令牌並在 ClaimsManager 中設置聲明
     *
     * @param startTime
     * @param user
     * @param joinPoint
     */
    private void validateTxToken(long startTime, String user, String token, ProceedingJoinPoint joinPoint) {
        final TokenData tokenData = tokenFactory.getTokenData(token, TX_TOKEN, joinPoint.toString());
        try {
            if (JsonWebTokenValidator.isTokenExpired(tokenData)) {
                String errorMsg = "TX-Token: Unauthorized Access: Token Expired!";
                logTime(startTime, ERROR, errorMsg, joinPoint);
                throw new AuthorizationException(errorMsg);
            }
            validateTxTokenType( user);
            logTime(startTime, SUCCESS, "TX-Token: User TX Authorized for the request",  joinPoint);
        } catch(Exception e) {
            logTime(startTime, ERROR, e.getMessage(), joinPoint);
            throw e;
        }
    }

    /**
     * Validates Token  Type
     * 驗證令牌類型
     * @param user
     * @return
     */
    private String validateTxTokenType(String user) {
        String tokenType = claimsManager.getTokenType();
        if (tokenType == null) {
            throw new AuthorizationException("Invalid Tx Token Type  (NULL) from Claims! for user: " + user);
        }
        if (!tokenType.equals(TX_USERS)) {
            throw new AuthorizationException("Invalid TX Token Type ("+tokenType+") ! " + user);
        }
        return tokenType;
    }

    /**
     * Log Time
     * 記錄時間
     * @param startTime
     * @param status
     * @param joinPoint
     */
    private void logTime(long startTime, String status, String msg, ProceedingJoinPoint joinPoint) {
        if(msg != null) {
            long timeTaken = System.currentTimeMillis() - startTime;
            switch(status) {
                case ERROR:
                    log.error("2|JA|TIME={} ms|STATUS={}|CLASS={}|Msg={}", timeTaken, status, joinPoint, msg);
                    break;
                case SUCCESS:
                    // fall thru
                default:
                    log.info("2|JA|TIME={} ms|STATUS={}|CLASS={}|Msg={}", timeTaken, status, joinPoint, msg);
                    break;
            }
        }
    }
}