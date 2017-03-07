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

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;


/**
 * Created by bakerally on 3/4/17.
 */
public class main {
    public static void main(String args[]){
        

            String s2 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "PREFIX lgdo: <http://linkedgeodata.org/ontology/>\n" +
                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                    "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                    "SELECT ?p ?o {\n" +
                    "  GRAPH <http://opensensingcity.emse.fr/OSM/strasbourg> {\n" +
                    "    <http://linkedgeodata.org/triplify/node1933697197> ?p ?o .\n" +
                    "  }\n" +
                    "} ";

            Query query = QueryFactory.create(s2); //s2 = the query above
            QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://localhost:3030/OSM/sparql", query );
            ResultSet results = qExe.execSelect();
           /* while (results.hasNext()){
                QuerySolution qs = results.next();
                String predicateURI = qs.getResource("?p").getURI();
                RDFNode object = qs.get("?o");
                System.out.println(predicateURI + "------------"+object);
            }*/

        while (results.hasNext()){
            QuerySolution qs = results.next();
            String predicateURI = qs.getResource("?p").getURI();
           /* if (predicateURI.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
                System.out.println(qs.getResource("?o").getURI());
            }*/
            if (predicateURI.equals("http://www.w3.org/2003/01/geo/wgs84_pos#long")) {
                Literal longitude = qs.getLiteral("?o");
                System.out.println(qs.getLiteral("?o").getLexicalForm());
                System.out.println(longitude.getDatatypeURI());
            }
            //RDFNode object = qs.get("?o");
            //System.out.println(predicateURI + "------------"+object);
        }


    }
}
