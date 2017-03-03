/**
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   This file is part of the LDP4j Project:
 *     http://www.ldp4j.org/
 *
 *   Center for Open Middleware
 *     http://www.centeropenmiddleware.com/
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Copyright (C) 2014 Center for Open Middleware.
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Artifact    : org.ldp4j.tutorial.frontend:frontend-core:1.0.0-SNAPSHOT
 *   Bundle      : frontend-core-1.0.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.ldp4j.tutorial.frontend;

import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.ext.Application;
import org.ldp4j.application.ext.Configuration;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
import org.ldp4j.application.setup.Bootstrap;
import org.ldp4j.application.setup.Environment;
import org.ldp4j.tutorial.application.api.ContactsService;
import org.ldp4j.tutorial.frontend.parking.ParkingContainerHandler;
import org.ldp4j.tutorial.frontend.parking.ParkingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public final class ContactsApplication extends Application<Configuration> {

	private static final Logger LOGGER=LoggerFactory.getLogger(ContactsApplication.class);



	//Parking
	private static final String PARKING_CONTAINER_NAME     = "ParkingContainer";
	private static final String ROOT_PARKING_CONTAINER_PATH= "parkings/";
	private Name<String> parkingContainerName;

	//Person
	private static final String PERSON_CONTAINER_NAME     = "PersonContainer";
	private static final String ROOT_PERSON_CONTAINER_PATH= "persons/";
	private Name<String> personContainerName;


	public ContactsApplication() {

		this.parkingContainerName=
			NamingScheme.
				getDefault().
					name(PARKING_CONTAINER_NAME);



		this.personContainerName=
				NamingScheme.
						getDefault().
						name(PERSON_CONTAINER_NAME);

	}

	@Override
	public void setup(Environment environment, Bootstrap<Configuration> bootstrap) {
		LOGGER.info("Starting Contacts Application configuration...");

		ContactsService service = ContactsService.getInstance();
		bootstrap.addHandler(new ParkingContainerHandler());
		bootstrap.addHandler(new ParkingHandler());

		//Parking
		environment.publishResource(this.parkingContainerName,ParkingContainerHandler.class,ROOT_PARKING_CONTAINER_PATH);



		LOGGER.info("Contacts Application configuration completed.");
	}

	@Override
	public void initialize(WriteSession session) {
		LOGGER.info("Initializing Contacts Application...");
		try {


			ContainerSnapshot containerSnapshot = (ContainerSnapshot)session.find(ResourceSnapshot.class, this.parkingContainerName,ParkingContainerHandler.class);

			
			Name <String> resource = NamingScheme.builder().withBase("http://www.google.com")
					.build().name("test");



			Name <String> resource1 = NamingScheme.builder().withBase("http://www.google.com")
					.build().name("test2");

			containerSnapshot.addMember(resource);
			containerSnapshot.addMember(resource1);





			session.saveChanges();
			LOGGER.info("Contacts Application initialization completed.");
		} catch (WriteSessionException e) {
			LOGGER.warn("Contacts Application initialization failed.",e);
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void shutdown() {
		LOGGER.info("Starting Contacts Application shutdown...");
		LOGGER.info("Contacts Application shutdown completed.");
	}

}