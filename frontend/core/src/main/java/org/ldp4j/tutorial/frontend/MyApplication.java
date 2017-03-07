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

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.ldp4j.application.data.*;
import org.ldp4j.application.ext.Application;
import org.ldp4j.application.ext.Configuration;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
import org.ldp4j.application.setup.Bootstrap;
import org.ldp4j.application.setup.Environment;
import org.ldp4j.tutorial.frontend.busstation.BusStationContainerHandler;
import org.ldp4j.tutorial.frontend.parking.ParkingContainerHandler;
import org.ldp4j.tutorial.frontend.parking.ParkingHandler;
import org.ldp4j.tutorial.frontend.util.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MyApplication extends Application<Configuration> {

	private static final Logger LOGGER=LoggerFactory.getLogger(MyApplication.class);

	//Parking
	private static final String PARKING_CONTAINER_NAME     = "ParkingContainer";
	private static final String ROOT_PARKING_CONTAINER_PATH= "parkings/";
	private Name<String> parkingContainerName;

	//BusStation
	private static final String BUSSTATION_CONTAINER_NAME     = "BusStationContainer";
	private static final String ROOT_BUSSTATION_CONTAINER_PATH= "busstations/";
	private Name<String> busStationContainerName;



	public MyApplication() {

		this.parkingContainerName=NamingScheme.getDefault().name(PARKING_CONTAINER_NAME);
		this.busStationContainerName=NamingScheme.getDefault().name(BUSSTATION_CONTAINER_NAME);


	}

	@Override
	public void setup(Environment environment, Bootstrap<Configuration> bootstrap) {
		LOGGER.info("Starting Application configuration...");

		//####Handlers Definition########

		//Parking Handlers
		bootstrap.addHandler(new ParkingContainerHandler());
		bootstrap.addHandler(new ParkingHandler());

		//Bus Station Handlers
		bootstrap.addHandler(new BusStationContainerHandler());

		//#######End of Handler Definition######




		//Parking
		environment.publishResource(this.parkingContainerName,ParkingContainerHandler.class,ROOT_PARKING_CONTAINER_PATH);

		//BusStation
		environment.publishResource(this.busStationContainerName,BusStationContainerHandler.class,ROOT_BUSSTATION_CONTAINER_PATH);



		LOGGER.info("Application configuration completed.");
	}

	@Override
	public void initialize(WriteSession session) {
		LOGGER.info("Initializing Application...");
		try {

			ContainerSnapshot containerParkingSnapshot = (ContainerSnapshot)session.find(ResourceSnapshot.class, this.parkingContainerName,ParkingContainerHandler.class);
			ContainerSnapshot containerBusStationSnapshot = (ContainerSnapshot)session.find(ResourceSnapshot.class, this.busStationContainerName,BusStationContainerHandler.class);
			
			String parkingQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
					"PREFIX lgdo: <http://linkedgeodata.org/ontology/>\n" +
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
					"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
					"SELECT distinct ?parking {\n" +
					"  GRAPH <http://opensensingcity.emse.fr/OSM/strasbourg> {\n" +
					"    ?parking a ?s .\n" +
					"  }\n" +
					"  ?s rdf:type ?o.\n" +
					"  FILTER (?s in (lgdo:ParkingSpace,lgdo:ParkingMeter,lgdo:BicycleParking,lgdo:MotorcycleParking))\n" +
					"} ";
			ResultSet results = DataSource.getResources(parkingQuery);
			while (results.hasNext()){
				QuerySolution qs = results.next();
				String resourceURI = qs.getResource("?parking").getURI();
				Name <String> resourceName = NamingScheme.getDefault().name(resourceURI);
				containerParkingSnapshot.addMember(resourceName);
			}

			String busStation = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
					"PREFIX lgdo: <http://linkedgeodata.org/ontology/>\n" +
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
					"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
					"SELECT distinct ?busStation {\n" +
					"  GRAPH <http://opensensingcity.emse.fr/OSM/strasbourg> {\n" +
					"    ?busStation a lgdo:BusStation .\n" +
					"  }\n" +
					"} ";
			results = DataSource.getResources(busStation);
			while (results.hasNext()){
				QuerySolution qs = results.next();
				String resourceURI = qs.getResource("?busStation").getURI();
				Name <String> resourceName = NamingScheme.getDefault().name(resourceURI);
				containerBusStationSnapshot.addMember(resourceName);
			}



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