/**
 * (C) Copyright 2021 Araf Karsh Hamid
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
package io.fusion.air.microservice.adapters.controllers.open;

// Custom
import io.fusion.air.microservice.adapters.logging.MetricsCounter;
import io.fusion.air.microservice.adapters.logging.MetricsPath;
import io.fusion.air.microservice.adapters.security.jwt.AuthorizationRequired;
import io.fusion.air.microservice.adapters.security.jwt.SingleTokenAuthorizationRequired;
import io.fusion.air.microservice.domain.entities.order.ProductEntity;
import io.fusion.air.microservice.domain.exceptions.AbstractServiceException;
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.domain.models.order.Product;
import io.fusion.air.microservice.domain.ports.services.ProductService;
import io.fusion.air.microservice.server.controllers.AbstractController;
// Swagger
import io.fusion.air.microservice.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
// Spring
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
// Java
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;
import java.util.*;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Product Controller for the Product Service
 * This is to demonstrate certain concepts in Exception Handling ONLY.
 * Order, Product, CartItem all must be part of 3 different Microservices.
 * 產品控制器用於產品服務
 * 此僅用於展示異常處理中的某些概念。
 * 訂單、產品、購物車項目都應該是三個不同的微服務的一部分。
 *
 * Only Selected Methods will be secured in this packaged - which are Annotated
 * with
 * 
 * @AuthorizationRequired
 * @Operation(summary = "Cancel Product", security = { @SecurityRequirement(name
 *                    = "bearer-key") })
 * 
 *                    在此套件中只有選定的方法會被保護 - 這些方法使用以下註解標記
 * @AuthorizationRequired
 * @Operation(summary = "Cancel Product", security = { @SecurityRequirement(name
 *                    = "bearer-key") })
 * 
 * @author arafkarsh
 * @version 1.0
 * 
 */
@Validated // This enables validation for method parameters
@RestController
// "/ms-vanilla/api/v1"
@RequestMapping("${service.api.path}/product")
@MetricsPath(name = "fusion.air.product")
@Tag(name = "Product API", description = "Search Products, Create Products, Activate / DeActivate, Delete & Update Product")
public class ProductControllerImpl extends AbstractController {

	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());

	private String serviceName;
	// @Autowired not required - Constructor based Autowiring
	private final ProductService productServiceImpl;

	/**
	 * Constructor based Autowiring
	 * 基於構造函數的自動裝配
	 * 
	 * @param productSvc
	 */
	public ProductControllerImpl(ProductService productSvc) {
		productServiceImpl = productSvc;
		serviceName = super.name();
	}

	/**
	 * Create the Product
	 * 創建產品
	 */
	@Operation(summary = "Create Product")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Create the Product", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", description = "Unable to Create the Product", content = @Content)
	})
	@PostMapping("/create")
	@MetricsCounter(endpoint = "/create")
	public ResponseEntity<StandardResponse> createProduct(@Valid @RequestBody Product product) {
		log.debug("| {} |Request to Create Product... {} ", serviceName, product);
		ProductEntity prodEntity = productServiceImpl.createProduct(product);
		StandardResponse stdResponse = createSuccessResponse("Product Created");
		stdResponse.setPayload(prodEntity);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * GET Method Call to Check the Product Status
	 * GET 方法調用以檢查產品狀態
	 * 
	 * @return
	 */
	@Operation(summary = "Get the Product By Product UUID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Product Retrieved for status check", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "Invalid Product ID.", content = @Content)
	})
	@GetMapping("/status/{productId}")
	@MetricsCounter(endpoint = "/status")
	public ResponseEntity<StandardResponse> getProductStatus(@PathVariable("productId") UUID productId,
			HttpServletRequest request,
			HttpServletResponse response) throws AbstractServiceException {
		log.debug("| {} |Request to Get Product Status. {} ", serviceName, productId);
		ProductEntity product = productServiceImpl.getProductById(productId);
		StandardResponse stdResponse = createSuccessResponse("Data Fetch Success!");
		stdResponse.setPayload(product);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * GET Method Call to Get All the Products - Without Any Tokens
	 * GET 方法調用以獲取所有產品 - 不需要任何令牌
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws AbstractServiceException
	 */
	@Operation(summary = "Get All the Products - without Any Tokens")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List All the Product without Any Tokens", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "No Products Found!", content = @Content)
	})
	@GetMapping("/all/public")
	@MetricsCounter(endpoint = "/all/public")
	public ResponseEntity<StandardResponse> getAllProducts(HttpServletRequest request,
			HttpServletResponse response) throws AbstractServiceException {
		return getAllProducts("Public");
	}

	/**
	 * Get All Products with Single Auth Token
	 * 使用單一認證令牌獲取所有產品
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws AbstractServiceException
	 */
	@SingleTokenAuthorizationRequired(role = "USER")
	@Operation(summary = "Get All the Products (Secured) using Single Auth Token", security = {
			@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List All the Product using Single Auth Token", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "No Products Found!", content = @Content)
	})
	@GetMapping("/all/secured/single")
	@MetricsCounter(endpoint = "/all/secured/single")
	public ResponseEntity<StandardResponse> getAllProductsSecured(HttpServletRequest request,
			HttpServletResponse response) throws AbstractServiceException {
		return getAllProducts("Auth-Token");
	}

	/**
	 * Get All Products with Auth and Tx Tokens
	 * 使用認證令牌和交易令牌獲取所有產品
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws AbstractServiceException
	 */
	@AuthorizationRequired(role = "USER")
	@Operation(summary = "Get All the Products (Secured) using Auth and Tx Token", security = {
			@SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List All the Product using Auth and Tx Token", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "No Products Found!", content = @Content)
	})
	@GetMapping("/all/secured/tx")
	@MetricsCounter(endpoint = "/all/secured/tx")
	public ResponseEntity<StandardResponse> getAllProductsWithTxToken(HttpServletRequest request,
			HttpServletResponse response) throws AbstractServiceException {
		return getAllProducts("Tx-Token");
	}

	/**
	 * Get All the Products
	 * 獲取所有產品
	 * 
	 * @return
	 */
	private ResponseEntity<StandardResponse> getAllProducts(String type) {
		log.info("|{} |Request ({}) Token to get All Products ... ", serviceName, type);
		List<ProductEntity> productList = productServiceImpl.getAllProduct();
		StandardResponse stdResponse = null;
		log.info("Products List = {} ", productList.size());
		if (productList == null || productList.isEmpty()) {
			productList = createFallBackProducts();
			stdResponse = createSuccessResponse("201", "Fallback Data!");
		} else {
			stdResponse = createSuccessResponse("Data Fetch Success! Records = " + productList.size());
		}
		stdResponse.setPayload(productList);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Search the Product by Product Name
	 * 按產品名稱搜索產品
	 */
	@Operation(summary = "Search Product By Product Name")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Product(s) Found!", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "Unable to Find the Product(s)!", content = @Content)
	})
	@GetMapping("/search/product/{productName}")
	@MetricsCounter(endpoint = "/search/product")
	public ResponseEntity<StandardResponse> searchProductsByName(
			@PathVariable("productName") @NotBlank(message = "The Product Name is  required.") @Size(min = 3, max = 32, message = "The length of Product Name must be between 3 and 32 characters.") String productName) {
		String safeProductName = HtmlUtils.htmlEscape(productName);
		log.debug("| {} |Request to Search the Product By Name ...  {} ", serviceName, safeProductName);
		List<ProductEntity> products = productServiceImpl.fetchProductsByName(safeProductName);
		StandardResponse stdResponse = createSuccessResponse("Products Found For Search Term = " + safeProductName);
		stdResponse.setPayload(products);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Search the Product by Product price
	 * 按產品價格搜索產品
	 */
	@Operation(summary = "Search Product By Product Price Greater Than or Equal To")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Product(s) Found!", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "Unable to Find the Product(s)!", content = @Content)
	})
	@GetMapping("/search/price/{price}")
	@MetricsCounter(endpoint = "/search/price")
	public ResponseEntity<StandardResponse> searchProductsByPrice(@PathVariable("price") BigDecimal price) {
		log.debug("| {} |Request to Search the Product By Price... {} ", serviceName, price);
		List<ProductEntity> products = productServiceImpl.fetchProductsByPriceGreaterThan(price);
		StandardResponse stdResponse = createSuccessResponse("Products Found for Price >= " + price);
		stdResponse.setPayload(products);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Search Active Products
	 * 搜索活躍產品
	 */
	@Operation(summary = "Search Active Products")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Product(s) Found!", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "Unable to Find the Product(s)!", content = @Content)
	})
	@GetMapping("/search/active/")
	@MetricsCounter(endpoint = "/search/active")
	public ResponseEntity<StandardResponse> searchActiveProducts() {
		log.debug("| {} |Request to Search the Active Products ... ", serviceName);
		List<ProductEntity> products = productServiceImpl.fetchActiveProducts();
		StandardResponse stdResponse = createSuccessResponse("Active Products Found = " + products.size());
		stdResponse.setPayload(products);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * De-Activate the Product
	 * 停用產品
	 */
	@AuthorizationRequired(role = "user")
	@Operation(summary = "De-Activate Product")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Product De-Activated", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "Unable to De-Activate the Product", content = @Content)
	})
	@PutMapping("/deactivate/{productId}")
	@MetricsCounter(endpoint = "/deactivate")
	public ResponseEntity<StandardResponse> deActivateProduct(@PathVariable("productId") UUID productId) {
		log.debug("| {} |Request to De-Activate the Product... {} ", serviceName, productId);
		ProductEntity product = productServiceImpl.deActivateProduct(productId);
		StandardResponse stdResponse = createSuccessResponse("Product De-Activated");
		stdResponse.setPayload(product);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Activate the Product
	 * 啟用產品
	 */
	@AuthorizationRequired(role = "user")
	@Operation(summary = "Activate Product")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Product Activated", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "Unable to Activate the Product", content = @Content)
	})
	@PutMapping("/activate/{productId}")
	@MetricsCounter(endpoint = "/activate")
	public ResponseEntity<StandardResponse> activateProduct(@PathVariable("productId") UUID productId) {
		log.debug("| {} |Request to Activate the Product... {} ", serviceName, productId);
		ProductEntity product = productServiceImpl.activateProduct(productId);
		StandardResponse stdResponse = createSuccessResponse("Product Activated");
		stdResponse.setPayload(product);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Update the Product Details
	 * This API Can be tested for Optimistic Lock Exceptions as the Entity is a
	 * Versioned Entity
	 * 更新產品詳情
	 * 此 API 可用於測試樂觀鎖異常，因為該實體是版本化實體
	 */
	@Operation(summary = "Update the Product")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Product Updated", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "Unable to Update the Product", content = @Content)
	})
	@PutMapping("/update/")
	@MetricsCounter(endpoint = "/update")
	public ResponseEntity<StandardResponse> updateProduct(@Valid @RequestBody ProductEntity product) {
		log.debug("| {} |Request to Update Product Details... {}  ", serviceName, product);
		ProductEntity prodEntity = productServiceImpl.updateProduct(product);
		StandardResponse stdResponse = createSuccessResponse("Product Updated!");
		stdResponse.setPayload(prodEntity);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Update the Product Price
	 * 更新產品價格
	 */
	@Operation(summary = "Update the Product Price")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Product Price Updated", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "Unable to Update the Product Price", content = @Content)
	})
	@PutMapping("/update/price")
	@MetricsCounter(endpoint = "/update/price")
	public ResponseEntity<StandardResponse> updatePrice(@Valid @RequestBody ProductEntity product) {
		log.debug("| {} |Request to Update Product Price... {} ", serviceName, product);
		ProductEntity prodEntity = productServiceImpl.updatePrice(product);
		StandardResponse stdResponse = createSuccessResponse("Product Price Updated");
		stdResponse.setPayload(prodEntity);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Update the Product Details
	 * 更新產品詳情
	 */
	@Operation(summary = "Update the Product Details")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Product Details Updated", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "Unable to Update the Product Details", content = @Content)
	})
	@PutMapping("/update/details")
	@MetricsCounter(endpoint = "/update/details")
	public ResponseEntity<StandardResponse> updateProductDetails(@Valid @RequestBody ProductEntity product) {
		log.debug("| {} |Request to Update Product Details... {} ", serviceName, product);
		ProductEntity prodEntity = productServiceImpl.updateProductDetails(product);
		StandardResponse stdResponse = createSuccessResponse("Product Details Updated");
		stdResponse.setPayload(prodEntity);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Delete the Product
	 * 刪除產品
	 */
	@AuthorizationRequired(role = "User")
	@Operation(summary = "Delete Product", security = { @SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Product Deleted", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "Unable to Delete the Product", content = @Content)
	})
	@DeleteMapping("/{productId}")
	@MetricsCounter(endpoint = "/delete")
	public ResponseEntity<StandardResponse> deleteProduct(@PathVariable("productId") UUID productId) {
		log.debug("| {} |Request to Delete Product... {} ", serviceName, productId);
		productServiceImpl.deleteProduct(productId);
		StandardResponse stdResponse = createSuccessResponse("Product Deleted!");
		return ResponseEntity.ok(stdResponse);
	}

	private static final String ZIP_CODE = "12345";

	/**
	 * Create Fall Back Product for Testing Purpose ONLY
	 * 僅為測試目的創建後備產品
	 * 
	 * @return
	 */
	private List<ProductEntity> createFallBackProducts() {
		List<ProductEntity> productList = new ArrayList<>();
		productList.add(new ProductEntity("iPhone 10", "iPhone 10, 64 GB", new BigDecimal(60000), ZIP_CODE));
		productList.add(new ProductEntity("iPhone 11", "iPhone 11, 128 GB", new BigDecimal(70000), ZIP_CODE));
		productList.add(
				new ProductEntity("Samsung Galaxy s20", "Samsung Galaxy s20, 256 GB", new BigDecimal(80000), ZIP_CODE));

		try {
			productServiceImpl.createProductsEntity(productList);
			productList = productServiceImpl.getAllProduct();
		} catch (Exception e) {
			log.debug(Utils.getStackTraceAsString(e));
		}
		return productList;
	}
}