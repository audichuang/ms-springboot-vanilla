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
package io.fusion.air.microservice.adapters.controllers.open;

// Custom
import io.fusion.air.microservice.adapters.logging.MetricsCounter;
import io.fusion.air.microservice.adapters.logging.MetricsPath;
import io.fusion.air.microservice.domain.entities.order.CountryEntity;
import io.fusion.air.microservice.domain.entities.order.CountryGeoEntity;
import io.fusion.air.microservice.domain.exceptions.AbstractServiceException;
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.domain.ports.services.CountryService;
import io.fusion.air.microservice.server.controllers.AbstractController;
// Swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
// Spring
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
// Java
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Country Controller for the Country Service
 * This is to demonstrate certain concepts in Exception Handling ONLY.
 * Order, Product, CartItem all must be part of 3 different Microservices.
 * 國家控制器用於國家服務
 * 這只是為了展示例外處理中的某些概念。
 * 訂單、產品、購物車項目都必須是三個不同微服務的一部分。
 *
 * This is to demonstrate Certain Spring Data Capabilities like Pagination.
 * 這是為了展示某些 Spring Data 功能，如分頁。
 *
 * @author arafkarsh / 作者 arafkarsh
 * @version 1.0 / 版本 1.0
 * 
 */
@Validated // This enables validation for method parameters
// 這啟用方法參數的驗證
@RestController
// "/ms-vanilla/api/v1"
@RequestMapping("${service.api.path}/country")
@MetricsPath(name = "fusion.air.country")
@Tag(name = "Country API", description = "Spring Examples with Pagination")
public class CountryControllerImpl extends AbstractController {

	// Set Logger -> Lookup will automatically determine the class name.
	// 設置記錄器 -> Lookup 將自動確定類名。
	private static final Logger log = getLogger(lookup().lookupClass());

	// Autowired using the Constructor
	// 使用建構子自動裝配
	private CountryService countryService;
	private String serviceName;

	private static final String DATA_FETCH = "Data Fetch Success!";

	/**
	 * Autowired using the Constructor
	 * 使用建構子自動裝配
	 * 
	 * @param countrySvc
	 */
	public CountryControllerImpl(CountryService countrySvc) {
		countryService = countrySvc;
		serviceName = super.name();
	}

	/**
	 * GET Method Call to Get All the Geo Countries with Page and Size
	 * GET 方法呼叫以取得具有頁面和大小的所有地理國家
	 * 
	 * @return
	 */
	@Operation(summary = "Get All Geo Countries by Page Number and Page Size")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Geo Countries Retrieved!", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "Invalid Page and Size!.", content = @Content)
	})
	@GetMapping("/geo/page/{page}/size/{size}")
	@MetricsCounter(endpoint = "/geo/page/size")
	public ResponseEntity<StandardResponse> fetchCountriesByPageAndSize(@PathVariable("page") int page,
			@PathVariable("size") int size) throws AbstractServiceException {
		log.debug("| {} |Request to Get All Countries by page no {} & Size =  {}", serviceName, page, size);

		Page<CountryGeoEntity> countries = countryService.getAllGeoCountries(page, size);
		StandardResponse stdResponse = createSuccessResponse(DATA_FETCH);
		stdResponse.setPayload(countries);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * GET Method Call to Get All the Geo Countries
	 * GET 方法呼叫以取得所有地理國家
	 *
	 * @return
	 */
	@Operation(summary = "Get All the Geo Countries")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List All the Geo Countries", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "No Data Found", content = @Content)
	})
	@GetMapping("/geo/all/")
	@MetricsCounter(endpoint = "/geo/all")
	public ResponseEntity<StandardResponse> fetchAllGeoCountries(HttpServletRequest request,
			HttpServletResponse response) throws AbstractServiceException {
		log.debug("| {} |Request to get All Countries ... ", serviceName);
		Page<CountryGeoEntity> countries = countryService.getAllGeoCountries();
		StandardResponse stdResponse = createSuccessResponse(DATA_FETCH);
		stdResponse.setPayload(countries);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * GET Method Call to Get All the Countries
	 * GET 方法呼叫以取得所有國家
	 *
	 * @return
	 */
	@Operation(summary = "Get All the Countries")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List All the Countries", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "No Data Found", content = @Content)
	})
	@GetMapping("/all/")
	@MetricsCounter(endpoint = "/all")
	public ResponseEntity<StandardResponse> fetchAlCountries(HttpServletRequest request,
			HttpServletResponse response) throws AbstractServiceException {
		log.debug("| {} |Request to get All Countries ... ", serviceName);
		List<CountryEntity> countries = countryService.getAllCountries();
		StandardResponse stdResponse = createSuccessResponse(DATA_FETCH);
		stdResponse.setPayload(countries);
		return ResponseEntity.ok(stdResponse);
	}

}