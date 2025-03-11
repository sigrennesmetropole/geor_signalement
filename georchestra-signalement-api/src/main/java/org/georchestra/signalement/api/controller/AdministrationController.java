package org.georchestra.signalement.api.controller;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.georchestra.signalement.api.AdministrationApi;
import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.core.dto.ConfigurationData;
import org.georchestra.signalement.core.dto.ProcessDefinition;
import org.georchestra.signalement.service.sm.ConfigurationService;
import org.georchestra.signalement.service.sm.InitializationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

/**
 * Controlleur pour la configuration.
 */
@RestController
@Api(tags = "administration")
@RequiredArgsConstructor
public class AdministrationController implements AdministrationApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdministrationController.class);

	private final ConfigurationService configurationService;

	private final InitializationService initializationService;

	@Override
	public ResponseEntity<ConfigurationData> getConfiguration() throws Exception {
		return new ResponseEntity<ConfigurationData>(configurationService.getApplicationConfiguration(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Boolean> initialize() throws Exception {
		try {
			initializationService.initialize();
			return ResponseEntity.ok(true);
		} catch (Exception e) {
			LOGGER.warn("Failed to initialize...", e);
			return ResponseEntity.ok(false);
		}
	}

	@PreAuthorize("@authentificationHelper.isAdmin()")
	@Override
	public ResponseEntity<Boolean> updateProcessDefinition(String deploymentName, MultipartFile file)
			throws Exception {
		File document = java.io.File.createTempFile("upload", ".bpmn");
		FileUtils.copyInputStreamToFile(file.getInputStream(), document);
		DocumentContent content = new DocumentContent(file.getOriginalFilename(), file.getContentType(), document);
		initializationService.updateProcessDefinition(deploymentName, content);
		return ResponseEntity.ok(true);
	}

	@PreAuthorize("@authentificationHelper.isAdmin()")
	@Override
	public ResponseEntity<Boolean> deleteProcessDefinition(String name, Integer version) throws Exception {
		return ResponseEntity.ok(initializationService.deleteProcessDefinition(name, version));
	}

	@PreAuthorize("@authentificationHelper.isAdmin()")
	@Override
	public ResponseEntity<List<ProcessDefinition>> searchProcessDefinition() throws Exception {
		return ResponseEntity.ok(initializationService.searchProcessDefinitions());
	}

	/**
	 * point d'entrée utilisé uniquement en mode développement
	 *
	 * @return une "js"
	 */
	@GetMapping(value = "/extension/index.js", produces = { "application/javascript" })
	public ResponseEntity<String> indexJs() {
		return ResponseEntity.ok("console.log(\"Sigm indexjs\");");
	}

	/**
	 * Point d'entrée utilisé uniquement en mode développement pour donner un accès
	 * de type proxy
	 *
	 * @param url l'url de redirection
	 * @return
	 * @throws URISyntaxException
	 */
	@GetMapping(value = "/proxy")
	public ResponseEntity<Void> proxy(String url)
			throws URISyntaxException {
		URI frontURI = new URI(url);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(frontURI);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Redirect to {}", frontURI);
		}
		return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
	}
}
