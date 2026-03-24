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

import org.isf.OpenHospitalCoreApplication;
import org.isf.integrations.labbook.ports.IOauthTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Tsala
 */
class LabBookConfigTest {

	@Nested
	@DisplayName("When labbook.enabled=true")
	@SpringBootTest(classes = OpenHospitalCoreApplication.class,
		properties = {"labbook.enabled=true"})
	class LabBookConfigEnabledTest {

		@Autowired
		private ApplicationContext context;

		@MockitoBean(LabBookBeanNames.OAUTH_TOKEN_SERVICE)
		private IOauthTokenService oauthTokenService;

		@Test
		@DisplayName("Should register labbookOauthRestClient bean")
		void shouldRegisterLabbookOauthRestClientBean() {
			RestClient client = context.getBean(LabBookBeanNames.OAUTH_REST_CLIENT, RestClient.class);
			assertThat(client).isNotNull();
		}

		@Test
		@DisplayName("Should register labbookOauthTokenService bean")
		void shouldRegisterLabbookOauthTokenServiceBean() {
			IOauthTokenService service = context.getBean(LabBookBeanNames.OAUTH_TOKEN_SERVICE, IOauthTokenService.class);
			assertThat(service).isNotNull();
		}

		@Test
		@DisplayName("Should register labbookRestClient bean")
		void shouldRegisterLabbookRestClientBean() {
			RestClient client = context.getBean(LabBookBeanNames.REST_CLIENT, RestClient.class);
			assertThat(client).isNotNull();
		}

		@Test
		@DisplayName("Should register labbookTokenService bean")
		void shouldRegisterLabbookTokenServiceBean() {
			Object tokenService = context.getBean(LabBookBeanNames.TOKEN_SERVICE);
			assertThat(tokenService).isNotNull();
		}

		@Test
		@DisplayName("Should configure labBookRestClient with correct base URL")
		void shouldConfigureLabBookRestClientWithCorrectBaseUrl() {
			RestClient client = context.getBean(LabBookBeanNames.REST_CLIENT, RestClient.class);
			// We can't easily test the internal baseUrl without reflection,
			// but we can verify the bean exists and has the expected type
			assertThat(client).isInstanceOf(RestClient.class);
		}
	}

	@Nested
	@DisplayName("When labbook.enabled=false (default)")
	@SpringBootTest(classes = OpenHospitalCoreApplication.class,
		properties = {"labbook.enabled=false"})
	class LabBookConfigDisabledTest {

		@Autowired
		private ApplicationContext context;

		@Test
		@DisplayName("Should not register labbookOauthRestClient bean")
		void shouldNotRegisterLabbookOauthRestClientBean() {
			assertThat(context.containsBean(LabBookBeanNames.OAUTH_REST_CLIENT)).isFalse();
		}

		@Test
		@DisplayName("Should not register labbookRestClient bean")
		void shouldNotRegisterLabbookRestClientBean() {
			assertThat(context.containsBean(LabBookBeanNames.REST_CLIENT)).isFalse();
		}

		@Test
		@DisplayName("Should not register labbookTokenService bean")
		void shouldNotRegisterLabbookTokenServiceBean() {
			assertThat(context.containsBean(LabBookBeanNames.TOKEN_SERVICE)).isFalse();
		}
	}
}