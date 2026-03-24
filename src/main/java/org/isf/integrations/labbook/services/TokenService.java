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
package org.isf.integrations.labbook.services;

import org.isf.integrations.labbook.config.LabBookBeanNames;
import org.isf.integrations.labbook.config.LabBookProperties;
import org.isf.integrations.labbook.models.OauthTokenResponse;
import org.isf.integrations.labbook.ports.IOauthTokenService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Token service implementation with caching and automatic refresh.
 * Manages OAuth tokens for LabBook API authentication.
 *
 * @author Steve Tsala
 */
@Service(LabBookBeanNames.TOKEN_SERVICE)
@ConditionalOnProperty(name = "labbook.enabled", havingValue = "true")
public class TokenService implements ITokenService {

	private final IOauthTokenService oauthTokenService;
	private final LabBookProperties properties;

	private volatile String cachedToken;

	private volatile Instant tokenExpiration;

	public TokenService(IOauthTokenService oauthTokenService, LabBookProperties properties) {
		this.oauthTokenService = oauthTokenService;
		this.properties = properties;
	}

	@Override
	public String getAccessToken() {
		if (cachedToken != null && Instant.now().isBefore(tokenExpiration)) {
			return cachedToken;
		}

		// Token expired or not cached, fetch new one
		OauthTokenResponse tokenResponse = oauthTokenService.obtainToken(
			"client_credentials",
			properties.getOauth().getClientId(),
			properties.getOauth().getClientSecret()
		);

		cachedToken = tokenResponse.accessToken();
		// Set expiration to now + expires_in - 60 seconds buffer
		tokenExpiration = Instant.now().plusSeconds(tokenResponse.expiresIn() - 60);

		return cachedToken;
	}

	public String getCachedToken() {
		return cachedToken;
	}

	public Instant getTokenExpiration() {
		return tokenExpiration;
	}
}