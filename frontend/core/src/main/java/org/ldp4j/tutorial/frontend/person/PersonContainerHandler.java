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
package org.ldp4j.tutorial.frontend.person;

import org.ldp4j.application.data.*;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.ext.ContainerHandler;
import org.ldp4j.application.ext.UnknownResourceException;
import org.ldp4j.application.ext.UnsupportedContentException;
import org.ldp4j.application.ext.annotations.BasicContainer;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
import org.ldp4j.tutorial.application.api.ContactsService;
import org.ldp4j.tutorial.application.api.Person;
import org.ldp4j.tutorial.frontend.contact.ContactContainerHandler;
import org.ldp4j.tutorial.frontend.util.FormatUtil;
import org.ldp4j.tutorial.frontend.util.IdentityUtil;
import org.ldp4j.tutorial.frontend.util.Serviceable;
import org.ldp4j.tutorial.frontend.util.Typed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

@BasicContainer(
	id = PersonContainerHandler.ID,
	memberHandler = PersonHandler.class
)
public class PersonContainerHandler extends Serviceable implements ContainerHandler {

	public static final String ID="PersonContainerHandler";
	private static final Logger LOGGER= LoggerFactory.getLogger(PersonContainerHandler.class);

	public PersonContainerHandler(ContactsService service) {
		super(service);
	}

	private static void addObjectPropertyValue(DataSet dataSet, Name<String> name, String propertyURI, String uri) {
		if(uri==null) {
			return;
		}
		ManagedIndividualId individualId = ManagedIndividualId.createId(name, PersonHandler.ID);
		ManagedIndividual individual = dataSet.individual(individualId, ManagedIndividual.class);
		URI propertyId = URI.create(propertyURI);
		ExternalIndividual external = dataSet.individual(URI.create(uri),ExternalIndividual.class);
		individual.addValue(propertyId,external);
	}

	@Override
	public DataSet get(ResourceSnapshot resource) throws UnknownResourceException, ApplicationRuntimeException {
		// For the time there is nothing to return
		LOGGER.info("Enters PersonContainerHandler get======================"+resource.name()+" test");

		return
			DataSetFactory.
				createDataSet(resource.name());

	}

	@Override
	public ResourceSnapshot create(
			ContainerSnapshot container,
			DataSet representation,
			WriteSession session)
					throws
						UnknownResourceException,
						ApplicationRuntimeException,
						UnsupportedContentException {
		LOGGER.info("Enters PersonContainerHandler create======================");
		trace("Requested person creation: %n%s",representation);

		Individual<?, ?> individual=
			IdentityUtil.
				personIndividual(representation,null);

		Typed<Person> typedPerson=PersonMapper.toPerson(individual);

		//PersonConstraints.validate(typedPerson);

		Person protoPerson=typedPerson.get();
		Person person=
			contactsService().
				createPerson(
					protoPerson.getEmail(),
					protoPerson.getName(),
					protoPerson.getLocation(),
					protoPerson.getWorkplaceHomepage());

		try {
			ResourceSnapshot personResource=
				container.addMember(NamingScheme.getDefault().name("mailto:mesteban@fi.upm.es"));

			personResource.
				createAttachedResource(
					ContainerSnapshot.class,
					PersonHandler.PERSON_CONTACTS,
						NamingScheme.getDefault().name("mailto:mesteban@fi.upm.es","contacts"),
					ContactContainerHandler.class);

			session.saveChanges();

			info("Created person %s : %s",person.getEmail(),FormatUtil.toString(person));
			return personResource;
		} catch (WriteSessionException e) {
			/*contactsService().
				deletePerson(person.getEmail());*/
			throw unexpectedFailure(e,"Could not create person %s");
		}
	}

}
