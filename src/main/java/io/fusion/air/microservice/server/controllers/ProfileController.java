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
package io.fusion.air.microservice.server.controllers;
// Custom
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.server.config.DatabaseConfig;
import io.fusion.air.microservice.server.config.ServiceConfig;
// Swagger
import io.fusion.air.microservice.server.service.ProfileService;
import io.fusion.air.microservice.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
// Spring
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// Java
import org.slf4j.Logger;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Profile Controller for the Service
 * 
 * @author arafkarsh
 * @version 1.0
 * 
 */
@RestController
// "/service-name/api/v1/service"
@RequestMapping("${service.api.path}"+ ServiceConfig.HEALTH_PATH)
@Tag(name = "System - Health", description = "Shows the Current Active Profile Details")
public class ProfileController extends AbstractController {

	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());

	// Autowired using the Constructor
	private final ServiceConfig serviceConfig;
	private final DatabaseConfig dbConfig;
	// Autowired using the Constructor
	private ConfigurableEnvironment environment;
	private ProfileService profileService;

	private final String serviceName;

	/**
	 * Autowired using the Constructor Injection
	 * @param serviceConfig
	 * @param dbConfig
	 */
	public ProfileController(ServiceConfig serviceConfig, DatabaseConfig dbConfig,
							 ConfigurableEnvironment environment, ProfileService profileService) {
		this.serviceConfig = serviceConfig;
		this.dbConfig = dbConfig;
		this.environment = environment;
		this.profileService = profileService;
		this.serviceName = serviceConfig.getServiceName();
	}

	/**
	 * Get Method Call to Show the Current Active Profiles
	 * WARNING:
	 * THIS is ONLY FOR DEMO PURPOSE.
	 * THIS METHOD MUST BE DISABLED IN PRODUCTION ENVIRONMENT FOR SECURITY REASONS
	 * @return
	 */
    @Operation(summary = "Profile Details of the Order Service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "Profile Retrieved OK",
            content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
            description = "Failed to Retrieve Profile.",
            content = @Content)
    })
	@GetMapping("/profile")
	public ResponseEntity<StandardResponse> retrieveProfile() throws SecurityException {
		log.info("{} |Request to  Retrieve Active Profile ... ", LocalDateTime.now());
		Map<String, Object> data = new LinkedHashMap<>();
		StandardResponse stdResponse = createSuccessResponse("Profile Data Retrieved!");
		data.put("Service", serviceName);
		data.put("Active Profile", profileService.getActiveProfile());
		data.put("Service Version", serviceConfig.getServerVersion());
		data.put("Build Date", serviceConfig.getBuildDate());
		data.put("Build No", serviceConfig.getBuildNumber());
		data.put("Java Version",System.getProperty("java.version"));
		data.put("Spring Version", SpringBootVersion.getVersion());
		data.put("OS Version", Utils.getOSDetails());
		stdResponse.setPayload(data);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Get Method Call to Show the Current Database Profiles
	 * WARNING:
	 * THIS is ONLY FOR DEMO PURPOSE.
	 * THIS METHOD MUST BE DISABLED IN PRODUCTION ENVIRONMENT FOR SECURITY REASONS
	 * @return
	 */
	@Operation(summary = "Profile DB Details of the Order Service")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Profile DB Retrieved OK",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "404",
					description = "Failed to Retrieve DB Profile.",
					content = @Content)
	})
	@GetMapping("/profile/db")
	public ResponseEntity<StandardResponse> retrieveDBProfile() throws SecurityException {
		log.info("{} |Request to Database Profile ... ", LocalDateTime.now());
		Map<String, Object> data = new LinkedHashMap<>();
		StandardResponse stdResponse = createSuccessResponse("Profile Data Retrieved!");
		data.put("Service", serviceName);
		data.put("Active Profile", profileService.getActiveProfile());
		data.put("Service Version", serviceConfig.getServerVersion());
		data.put("Build Date", serviceConfig.getBuildDate());
		data.put("Build No", serviceConfig.getBuildNumber());
		data.put("Database", dbConfig.getDataSourceVendor());
		data.put("Dialect", dbConfig.getDataSourceDialect());
		data.put("DB Server", dbConfig.getDataSourceServer());
		data.put("DB Port", dbConfig.getDataSourcePort());
		data.put("DB URL", dbConfig.getDataSourceURL());
		stdResponse.setPayload(data);
		return ResponseEntity.ok(stdResponse);
	}
 }

