/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.integrations.labbook.config;

import org.isf.integrations.labbook.ports.IOauthTokenService;
import org.isf.integrations.labbook.services.ITokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Configuration for LabBook REST clients.
 * Creates RestClient beans for OAuth token requests and API calls.
 *
 * @author Steve Tsala
 */

@Configuration
@ConfigurationPropertiesScan("org.isf.integrations.labbook")
@ConditionalOnProperty(name = "labbook.enabled", havingValue = "true")
public class LabBookConfig {

	/**
	 * RestClient for OAuth token requests (no authentication needed).
	 */
	@Bean(LabBookBeanNames.OAUTH_REST_CLIENT)
	public RestClient labbookOauthRestClient(LabBookProperties properties) {
		return RestClient.builder()
			.baseUrl(properties.getBaseUrl())
			.build();
	}

	/**
	 * HttpExchange proxy for OAuth token service.
	 */
	@Bean(LabBookBeanNames.OAUTH_TOKEN_SERVICE)
	public IOauthTokenService oauthTokenService(@Qualifier(LabBookBeanNames.OAUTH_REST_CLIENT) RestClient client) {
		return HttpServiceProxyFactory.builderFor(
			RestClientAdapter.create(client)
		).build().createClient(IOauthTokenService.class);
	}

	/**
	 * Main RestClient for LabBook API calls with automatic token injection.
	 */
	@Bean(LabBookBeanNames.REST_CLIENT)
	public RestClient labBookRestClient(LabBookProperties properties,
										@Qualifier(LabBookBeanNames.TOKEN_SERVICE) ITokenService tokenService) {
		return RestClient.builder()
			.baseUrl(properties.getBaseUrl())
			.requestInterceptor((request, body, execution) -> {
				String token = tokenService.getAccessToken();
				request.getHeaders().setBearerAuth(token);
				return execution.execute(request, body);
			})
			.build();
	}
}