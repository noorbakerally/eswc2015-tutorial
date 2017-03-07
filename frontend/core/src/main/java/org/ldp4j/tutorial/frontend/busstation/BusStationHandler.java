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
package org.ldp4j.tutorial.frontend.busstation;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.ldp4j.application.data.*;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.ext.ResourceHandler;
import org.ldp4j.application.ext.UnknownResourceException;
import org.ldp4j.application.ext.annotations.Resource;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.tutorial.frontend.parking.ParkingHandler;
import org.ldp4j.tutorial.frontend.util.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * Created by bakerally on 3/2/17.
 */
@Resource(
        id= BusStationHandler.ID
)
public class BusStationHandler implements ResourceHandler {
    public static final String ID="BusStationHandler";
    private static final Logger LOGGER= LoggerFactory.getLogger(BusStationHandler.class);


    private static void addObjectPropertyValue(DataSet dataSet, Name<String> name, String propertyURI, String uri) {
        if(uri==null) {
            return;
        }
        ManagedIndividualId individualId = ManagedIndividualId.createId(name, BusStationHandler.ID);
        ManagedIndividual individual = dataSet.individual(individualId, ManagedIndividual.class);
        URI propertyId = URI.create(propertyURI);
        ExternalIndividual external = dataSet.individual(URI.create(uri),ExternalIndividual.class);
        individual.addValue(propertyId,external);
    }
    private static void addDatatypePropertyValue(DataSet dataSet, Name<String> name, String propertyURI, Object rawValue,String dataTypeURI) {
        TypedLiteral<Object> datatype = DataSetUtils.newTypedLiteral(rawValue, URI.create(dataTypeURI));
        ManagedIndividualId individualId = ManagedIndividualId.createId(name, BusStationHandler.ID);
        ManagedIndividual individual = dataSet.individual(individualId, ManagedIndividual.class);
        individual.addValue(URI.create(propertyURI),datatype);
    }

    @Override
    public DataSet get(ResourceSnapshot resource) throws UnknownResourceException, ApplicationRuntimeException {
        LOGGER.info("Enters BusStationHandler get ======================"+resource.name().id().toString());
        LOGGER.info("Enters ParkingHandler get ======================"+resource.name().id().toString());

        Name<String> parkingName = NamingScheme.getDefault().name(resource.name().id().toString());
        DataSet dataSet = DataSetFactory.createDataSet(resource.name());

        String resourceIRI = resource.name().id().toString();
        ResultSet results = DataSource.getResourceDescription(resourceIRI);

        while (results.hasNext()){
            QuerySolution qs = results.next();
            String predicateURI = qs.getResource("?p").getURI();

            if (predicateURI.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
                addObjectPropertyValue(dataSet, parkingName, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", qs.getResource("?o").getURI());
            }

            if (predicateURI.equals("http://www.w3.org/2003/01/geo/wgs84_pos#long")) {
                String lexicalValue = qs.getLiteral("?o").getLexicalForm();
                String dataTypeURI = qs.getLiteral("?o").getDatatypeURI();
                addDatatypePropertyValue(dataSet, parkingName, "http://www.w3.org/2003/01/geo/wgs84_pos#long",lexicalValue,dataTypeURI);
            }

            if (predicateURI.equals("http://www.w3.org/2003/01/geo/wgs84_pos#lat")) {
                String lexicalValue = qs.getLiteral("?o").getLexicalForm();
                String dataTypeURI = qs.getLiteral("?o").getDatatypeURI();
                addDatatypePropertyValue(dataSet, parkingName, "http://www.w3.org/2003/01/geo/wgs84_pos#lat",lexicalValue,dataTypeURI);
            }

            if (predicateURI.equals("http://geovocab.org/geometry#geometry")) {
                addObjectPropertyValue(dataSet, parkingName, "http://geovocab.org/geometry#geometry", qs.getResource("?o").getURI());
            }
            if (predicateURI.equals("http://www.w3.org/2000/01/rdf-schema#")) {
                addObjectPropertyValue(dataSet, parkingName, "http://www.w3.org/2000/01/rdf-schema#", qs.getResource("?o").getURI());
            }
        }
        return dataSet;
    }
}
