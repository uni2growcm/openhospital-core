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
package org.isf.integrations.labbook.annotations;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.*;

/**
 * Meta-annotation that activates a bean only when the LabBook integration is enabled.
 *
 * <p>Equivalent to:
 * <pre>{@code @ConditionalOnProperty(name = "labbook.enabled", havingValue = "true")}</pre>
 *
 * <p>Apply to any {@code @Component}, {@code @Service}, {@code @Configuration} class,
 * or {@code @Bean} factory method that should be absent from the Spring context when
 * {@code labbook.enabled} is {@code false} or not set.
 *
 * <p>Example:
 * <pre>{@code
 * @Service
 * @EnableLabBook
 * public class MyLabBookService { ... }
 * }</pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnProperty(name = "labbook.enabled", havingValue = "true")
public @interface EnableLabBook {

}
