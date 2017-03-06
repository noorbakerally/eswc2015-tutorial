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

import java.net.URI;
import java.util.Collection;

import org.ldp4j.application.data.*;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.ext.Deletable;
import org.ldp4j.application.ext.InconsistentContentException;
import org.ldp4j.application.ext.Modifiable;
import org.ldp4j.application.ext.ResourceHandler;
import org.ldp4j.application.ext.UnknownResourceException;
import org.ldp4j.application.ext.UnsupportedContentException;
import org.ldp4j.application.ext.annotations.Attachment;
import org.ldp4j.application.ext.annotations.Resource;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
import org.ldp4j.tutorial.application.api.ContactsService;
import org.ldp4j.tutorial.application.api.Contact;
import org.ldp4j.tutorial.application.api.Person;
import org.ldp4j.tutorial.frontend.contact.ContactContainerHandler;
import org.ldp4j.tutorial.frontend.util.FormatUtil;
import org.ldp4j.tutorial.frontend.util.IdentityUtil;
import org.ldp4j.tutorial.frontend.util.Serviceable;
import org.ldp4j.tutorial.frontend.util.Typed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Resource(
	id=PersonHandler.ID,
	attachments={
		@Attachment(
			id=PersonHandler.PERSON_CONTACTS,
			path="contacts/",
			handler=ContactContainerHandler.class),
	}
)
public class PersonHandler extends Serviceable implements ResourceHandler, Modifiable, Deletable {

	public static final String ID="PersonHandler";
	public static final String PERSON_CONTACTS="personContacts";
	private static final Logger LOGGER= LoggerFactory.getLogger(PersonHandler.class);


	//test
	static final String TYPE               = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

	static final String PERSON             = "http://xmlns.com/foaf/0.1/Person";

	static final String WORKPLACE_HOMEPAGE = "http://xmlns.com/foaf/0.1/workplaceHomepage";
	static final String LOCATION           = "http://xmlns.com/foaf/0.1/based_near";
	static final String NAME               = "http://xmlns.com/foaf/0.1/name";
	static final String EMAIL              = "http://xmlns.com/foaf/0.1/mbox";


	public PersonHandler(ContactsService service) {
		super(service);
	}

	private Person findPerson(String personId) throws UnknownResourceException {
		LOGGER.info("Enters PersonHandler findPerson ======================");
		Person person = contactsService().getPerson(personId);
		if(person==null) {
			throw unknownResource(personId,"Person");
		}
		return person;
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
	private static void addDatatypePropertyValue(DataSet dataSet, Name<String> name, String propertyURI, Object rawValue) {
		DataSetUtils.
				newHelper(dataSet).
				managedIndividual(name, PersonHandler.ID).
				property(propertyURI).
				withLiteral(rawValue);
	}
	@Override
	public DataSet get(ResourceSnapshot resource) throws UnknownResourceException {
		LOGGER.info("Enters PersonHandler get ======================"+resource);
		String personId = resource.name().toString();

		//trace("Requested person %s retrieval...",personId);

		Person person = findPerson(personId);
		person.setName("Noorani");

		//info("Retrieved person %s: %s",personId,FormatUtil.toString(person));

		//testing
		Name<String> personName=IdentityUtil.name(person);
		DataSet dataSet = DataSetFactory.createDataSet(personName);
		addObjectPropertyValue(dataSet,personName,TYPE,PERSON);

		//addObjectPropertyValue(dataSet,personName,EMAIL,person.getEmail());
		//addDatatypePropertyValue(dataSet,personName,NAME,person.getName());
		//addObjectPropertyValue(dataSet,personName,LOCATION,person.getLocation());
		//addObjectPropertyValue(dataSet,personName,WORKPLACE_HOMEPAGE,person.getWorkplaceHomepage());

		return dataSet;

		//return PersonMapper.toDataSet(person);
	}

	@Override
	public void delete(ResourceSnapshot resource, WriteSession session) throws UnknownResourceException, ApplicationRuntimeException {
		LOGGER.info("Enters PersonHandler delete ======================");
		String personId = IdentityUtil.personId(resource);
		trace("Requested person %s deletion...",personId);
		Person person=findPerson(personId);
		info("Deleting person %s...",personId);
		Collection<Contact> contacts = contactsService().listPersonContacts(personId);
		try {
			contactsService().deletePerson(personId);
			session.delete(resource);
			session.saveChanges();
			info("Deleted person %s : %s",personId,FormatUtil.toString(person));
			for(Contact contact:contacts) {
				info(" - Deleted contact %s",FormatUtil.toString(contact));
			}
		} catch (WriteSessionException e) {
			throw unexpectedFailure(e, "Person %s deletion failed",personId);
		}
	}

	@Override
	public void update(
			ResourceSnapshot resource,
			DataSet content,
			WriteSession session)
					throws
						UnknownResourceException,
						UnsupportedContentException,
						InconsistentContentException,
						ApplicationRuntimeException {
		LOGGER.info("Enters PersonHandler update ======================");
		String personId = IdentityUtil.personId(resource);
		trace("Requested person %s update using: %n%s",personId,content);

		Person currentPerson=findPerson(personId);

		Individual<?,?> individual=
			IdentityUtil.
				personIndividual(content,currentPerson);

		Typed<Person> updatedPerson=PersonMapper.toPerson(individual);
		PersonConstraints.validate(currentPerson,updatedPerson);
		PersonConstraints.checkConstraints(currentPerson, updatedPerson);

		Person backupPerson = PersonMapper.clone(currentPerson);
		PersonMapper.copy(updatedPerson.get(), currentPerson);
		try {
			session.modify(resource);
			session.saveChanges();
			info("Updated person %s : %s",personId,FormatUtil.toString(currentPerson));
		} catch (WriteSessionException e) {
			PersonMapper.copy(backupPerson, currentPerson);
			throw unexpectedFailure(e, "Person %s update failed",personId);
		}
	}

}