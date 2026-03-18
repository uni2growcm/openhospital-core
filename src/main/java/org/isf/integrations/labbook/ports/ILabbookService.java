package org.isf.integrations.labbook.ports;

import org.isf.integrations.labbook.models.CreatePatientRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/patient")
public interface ILabbookService {
	@PostExchange
	@Async
	public default void sendLabbookRequest(@RequestBody CreatePatientRequest request) {
	}
}
