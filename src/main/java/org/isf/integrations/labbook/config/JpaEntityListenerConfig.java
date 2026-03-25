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

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.isf.integrations.labbook.listener.LabbookPatientListener;
import org.isf.patient.model.Patient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationPropertiesScan("org.isf.integrations.labbook")
//@ConditionalOnProperty(name = "labbook.enabled", havingValue = "true")
public class JpaEntityListenerConfig {

	private final LabbookPatientListener labbookPatientListener;

	public JpaEntityListenerConfig(LabbookPatientListener labbookPatientListener) {
		this.labbookPatientListener = labbookPatientListener;
	}

	@Bean
	public Integrator auditingIntegrator() {
		return new Integrator() {

			@Override
			public void integrate(
				Metadata metadata,
				SessionFactoryImplementor sessionFactory,
				SessionFactoryServiceRegistry serviceRegistry
			) {

				EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);

				eventListenerRegistry.prependListeners(
					EventType.POST_INSERT,
					new PostInsertEventListener() {
						@Override
						public void onPostInsert(PostInsertEvent event) {
							if (event.getEntity() instanceof Patient patient) {
								labbookPatientListener.afterPersist(patient);
							}
						}

						@Override
						public boolean requiresPostCommitHandling(EntityPersister persister) {
							return false;   // false = exécution immédiate après l'insert
						}
					}
				);

				eventListenerRegistry.prependListeners(
					EventType.POST_UPDATE,
					new PostUpdateEventListener() {
						@Override
						public void onPostUpdate(PostUpdateEvent event) {
							if (event.getEntity() instanceof Patient patient) {
								labbookPatientListener.afterPersist(patient);
							}
						}

						@Override
						public boolean requiresPostCommitHandling(EntityPersister persister) {
							return false;
						}
					}
				);
			}

			@Override
			public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
			}
		};
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
		EntityManagerFactoryBuilder builder,
		DataSource dataSource
	) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.integrator_provider",
			(IntegratorProvider) () -> Collections.singletonList(auditingIntegrator()));

		return builder
			.dataSource(dataSource)
			.packages("org.isf")
			.properties(properties)
			.build();
	}
}