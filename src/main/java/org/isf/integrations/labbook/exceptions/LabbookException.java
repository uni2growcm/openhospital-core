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
package org.isf.integrations.labbook.exceptions;

import org.isf.generaldata.MessageBundle;
import org.springframework.http.HttpStatusCode;

/**
 * Labbook specific exception.
 * To display exceptions related to the execution of requests to the Labbook API
 *
 * @author Tatemsa B.
 */

public class LabbookException extends RuntimeException{

	private String message;

	private int statusCode;

	private boolean isFatal = false;

	/**
	 * Creates a new {@code LabbookException} with the specified cause
	 * @param cause the cause of the exception
	 */
	public LabbookException(Throwable cause) {
		super(cause);
		this.message = cause != null ? cause.getMessage() : MessageBundle.getMessage("angal.labbook.internalerrorserver.msg");
		this.statusCode = 500;
	}

	/**
	 * Creates a new {@code LabbookException} with the specified message and status.
	 * @param message the detail message
	 * @param status the status code
	 */
	public LabbookException(String message, int status) {
		super(message);
		this.statusCode = status;
		this.message = message;
	}


	/**
	 * Creates a new {@code LabbookException} with the specified message and HTTP status code
	 * @param message the detail message
	 * @param statusCode the HTTP status code
	 */
	public LabbookException(String message, HttpStatusCode statusCode) {
		this(message, statusCode.value());
	}
}
