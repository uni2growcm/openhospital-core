/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.orthanc.client;

import org.isf.settings.manager.SettingManager;
import org.isf.settings.model.Setting;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.stereotype.Component;

import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.slf4j.Slf4jLogger;

/**
 * @author Silevester D.
 * @since 1.15
 */
@Component
public class FeignClientFactory {

	/**
	 * ORTHANC base URL
	 */
	private String baseURL;

	/**
	 * Username of the user we'll be using for API calls
	 */
	private String username;

	/**
	 * Password of the user we'll be using for API calls
	 */
	private String password;

	private final SettingManager settingManager;

	public FeignClientFactory(SettingManager settingManager) {
		this.settingManager = settingManager;
	}

	/**
	 * Create a client to perform request
	 *
	 * @return an instance of {@link OrthancAPIClient}
	 * @throws OHServiceException See {@link #initConfig()}
	 */
	public OrthancAPIClient createClient() throws OHServiceException {
		initConfig();

		return Feign.builder()
			.encoder(new GsonEncoder())
			.decoder(new GsonDecoder())
			.requestInterceptor(new BasicAuthRequestInterceptor(username, password))
			.logger(new Slf4jLogger(OrthancAPIClient.class))
			.logLevel(feign.Logger.Level.BASIC)
			.contract(new SpringMvcContract())
			.target(OrthancAPIClient.class, baseURL);
	}

	/**
	 * Initialize configuration. Attempt to retrieve configuration from the database
	 *
	 * @throws OHServiceException When the configuration is invalid
	 */
	private void initConfig() throws OHServiceException {
		Setting baseUrlSetting = settingManager.getByCode("ORTHANCBASEURL");
		Setting usernameSetting = settingManager.getByCode("ORTHANCUSERNAME");
		Setting passwordSetting = settingManager.getByCode("ORTHANCPASSWORD");

		baseURL = baseUrlSetting != null ? baseUrlSetting.getValue() : "";
		username = usernameSetting != null ? usernameSetting.getValue() : "";
		password = passwordSetting != null ? passwordSetting.getValue() : "";

		if (!validateConfiguration()) {
			throw new OHServiceException(new OHExceptionMessage("ORTHANC server is not properly configured"));
		}
	}

	/**
	 * Validate ORTHANC configuration
	 * This method only ensures that necessary parameters have been set
	 *
	 * @return <code>true</code> if the configuration is valid, <code>false</code> otherwise
	 */
	public boolean validateConfiguration() {
		return baseURL != null && !baseURL.isEmpty() && username != null && !username.isEmpty() && password != null && !password.isEmpty();
	}
}
