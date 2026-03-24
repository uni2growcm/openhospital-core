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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Tsala
 */
@SpringBootTest(classes = OpenHospitalCoreApplication.class, properties = {"labbook.enabled=true", "labbook.base-url=http://localhost:5000/sigl", "labbook.oauth.client-id=OH2-API", "labbook.oauth.client-secret=OH2-API-SECRET"})
class LabBookPropertiesTest {

	@Autowired
	private LabBookProperties properties;

	@Test
	@DisplayName("Should bind enabled property to true")
	void shouldBindEnabledPropertyToTrue() {
		assertThat(properties.isEnabled()).isTrue();
	}

	@Test
	@DisplayName("Should bind baseUrl to default value")
	void shouldBindBaseUrlToDefaultValue() {
		assertThat(properties.getBaseUrl()).isEqualTo("http://localhost:5000/sigl");
	}

	@Test
	@DisplayName("Should bind oauth clientId to default value")
	void shouldBindOauthClientIdToDefaultValue() {
		assertThat(properties.getOauth().getClientId()).isEqualTo("OH2-API");
	}

	@Test
	@DisplayName("Should bind oauth clientSecret to default value")
	void shouldBindOauthClientSecretToDefaultValue() {
		assertThat(properties.getOauth().getClientSecret()).isEqualTo("OH2-API-SECRET");
	}
}