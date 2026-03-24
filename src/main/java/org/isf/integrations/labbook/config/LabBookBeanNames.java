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

/**
 * Constants for bean names used in the LabBook integration.
 * Used to avoid hardcoded bean names scattered across annotations.
 *
 * @author Steve Tsala
 */
public final class LabBookBeanNames {
	public static final String LABBOOK_PROPERTIES = "labBookProperties";
	public static final String OAUTH_REST_CLIENT = "labbookOauthRestClient";
	public static final String OAUTH_TOKEN_SERVICE = "labbookOauthTokenService";
	public static final String TOKEN_SERVICE = "labbookTokenService";
	public static final String REST_CLIENT = "labbookRestClient";
	private LabBookBeanNames() {
	}
}