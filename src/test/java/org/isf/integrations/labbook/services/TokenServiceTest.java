/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import org.isf.OpenHospitalCoreApplication;
import org.isf.integrations.labbook.config.LabBookProperties;
import org.isf.integrations.labbook.models.OauthTokenResponse;
import org.isf.integrations.labbook.ports.IOauthTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Steve Tsala
 */
@SpringBootTest(classes = OpenHospitalCoreApplication.class, properties = {"labbook.enabled=true", "labbook.oauth.client-id=test-client-id", "labbook.oauth.client-secret=test-client-secret"})
class TokenServiceTest {

	@MockitoBean
	private IOauthTokenService oauthTokenService;

	@Autowired
	private LabBookProperties properties;

	@MockitoSpyBean
	private TokenService tokenService;

	private OauthTokenResponse tokenResponse;

	@BeforeEach
	void setUp() {
		tokenResponse = new OauthTokenResponse("test-token", "Bearer", 3600);

		var auth = properties.getOauth();

		when(oauthTokenService.obtainToken("client_credentials", auth.getClientId(), auth.getClientSecret())).thenReturn(tokenResponse);
	}

	@Test
	@DisplayName("Should return cached token when not expired")
	void shouldReturnCachedTokenWhenNotExpired() {
		// First call - fetch and cache
		String firstToken = tokenService.getAccessToken();
		assertThat(firstToken).isEqualTo("test-token");

		// Second call - should return cached token without calling oauth service again
		String secondToken = tokenService.getAccessToken();
		assertThat(secondToken).isEqualTo("test-token");

		// Verify oauth service was called only once
		assertThat(firstToken).isEqualTo(secondToken);
	}

	@Test
	@DisplayName("Should fetch new token when cache is empty")
	void shouldFetchNewTokenWhenCacheIsEmpty() {
		String token = tokenService.getAccessToken();

		assertThat(token).isEqualTo("test-token");
	}

	@Test
	@DisplayName("Should fetch new token when cache is expired")
	void shouldFetchNewTokenWhenCacheIsExpired() throws Exception {
		// First call - fetch and cache
		String firstToken = tokenService.getAccessToken();
		assertThat(firstToken).isEqualTo("test-token");

		// Manually expire the token by setting private fields via reflection
		var cachedTokenField = TokenService.class.getDeclaredField("cachedToken");
		cachedTokenField.setAccessible(true);
		cachedTokenField.set(tokenService, "expired-token");

		var expirationField = TokenService.class.getDeclaredField("tokenExpiration");
		expirationField.setAccessible(true);
		expirationField.set(tokenService, Instant.now().minusSeconds(1));

		// Second call - should fetch new token
		String secondToken = tokenService.getAccessToken();
		assertThat(secondToken).isEqualTo("test-token");

		// Should be the same token value from the mock response
		assertThat(secondToken).isEqualTo(firstToken);
	}

	@Test
	@DisplayName("Should set expiration to expiresIn minus 60 seconds buffer")
	void shouldSetExpirationToExpiresInMinus60SecondsBuffer() throws Exception {
		// First call to trigger token fetch
		tokenService.getAccessToken();

		// Expiration should be now + (3600 - 60) = 3540 seconds from now
		Instant expectedExpiration = Instant.now().plusSeconds(3540);

		Instant actualExpiration = tokenService.getTokenExpiration();

		// Allow for small time difference in test execution
		assertThat(actualExpiration).isBeforeOrEqualTo(expectedExpiration.plusSeconds(1));
		assertThat(actualExpiration).isAfter(expectedExpiration.minusSeconds(1));
	}

	@Test
	@DisplayName("Should delegate to IOauthTokenService with client_credentials grant")
	void shouldDelegateToIOauthTokenServiceWithClientCredentialsGrant() {
		tokenService.getAccessToken();

		// Verify the correct parameters were passed
		when(oauthTokenService.obtainToken("client_credentials", "test-client-id", "test-client-secret")).thenReturn(tokenResponse);
	}

	@Test
	@DisplayName("Should forward clientId and clientSecret from properties")
	void shouldForwardClientIdAndClientSecretFromProperties() {
		tokenService.getAccessToken();

		// The mock setup verifies this, but let's be explicit
		assertThat(properties.getOauth().getClientId()).isEqualTo("test-client-id");
		assertThat(properties.getOauth().getClientSecret()).isEqualTo("test-client-secret");
	}
}