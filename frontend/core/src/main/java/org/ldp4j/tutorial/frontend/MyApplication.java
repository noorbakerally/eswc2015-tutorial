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

import org.ldp4j.application.data.*;
import org.ldp4j.application.ext.Application;
import org.ldp4j.application.ext.Configuration;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
import org.ldp4j.application.setup.Bootstrap;
import org.ldp4j.application.setup.Environment;
import org.ldp4j.tutorial.frontend.parking.Parking;
import org.ldp4j.tutorial.frontend.parking.ParkingContainerHandler;
import org.ldp4j.tutorial.frontend.parking.ParkingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.ldp4j.application.data.IndividualReferenceBuilder.newReference;
import java.awt.*;
import java.net.URI;
import java.util.Date;

public final class MyApplication extends Application<Configuration> {

	private static final Logger LOGGER=LoggerFactory.getLogger(MyApplication.class);

	//Parking
	private static final String PARKING_CONTAINER_NAME     = "ParkingContainer";
	private static final String ROOT_PARKING_CONTAINER_PATH= "parkings/";
	private Name<String> parkingContainerName;


	public MyApplication() {

		this.parkingContainerName=
			NamingScheme.
				getDefault().
					name(PARKING_CONTAINER_NAME);

	}

	@Override
	public void setup(Environment environment, Bootstrap<Configuration> bootstrap) {
		LOGGER.info("Starting Application configuration...");

		bootstrap.addHandler(new ParkingContainerHandler());
		bootstrap.addHandler(new ParkingHandler());

		//Parking
		environment.publishResource(this.parkingContainerName,ParkingContainerHandler.class,ROOT_PARKING_CONTAINER_PATH);



		LOGGER.info("Application configuration completed.");
	}

	@Override
	public void initialize(WriteSession session) {
		LOGGER.info("Initializing Application...");
		try {


			Parking p1 = new Parking();
			Name <String> parkingName = NamingScheme.getDefault().name(p1.getName());
			p1.setName("newParking");

			ContainerSnapshot containerSnapshot = (ContainerSnapshot)session.find(ResourceSnapshot.class, this.parkingContainerName,ParkingContainerHandler.class);
			ResourceSnapshot parkingSnapshot = containerSnapshot.addMember(parkingName);








			session.saveChanges();
			LOGGER.info("Application initialization completed.");
		} catch (WriteSessionException e) {
			LOGGER.warn("Application initialization failed.",e);
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void shutdown() {
		LOGGER.info("Starting Application shutdown...");
		LOGGER.info("Application shutdown completed.");
	}

}