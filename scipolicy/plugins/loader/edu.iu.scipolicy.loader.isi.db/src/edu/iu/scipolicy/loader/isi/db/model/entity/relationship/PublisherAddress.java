package edu.iu.scipolicy.loader.isi.db.model.entity.relationship;

import java.util.Dictionary;
import java.util.Hashtable;

import edu.iu.cns.database.loader.framework.EntityRelationship;
import edu.iu.scipolicy.loader.isi.db.model.entity.Address;
import edu.iu.scipolicy.loader.isi.db.model.entity.Publisher;

public class PublisherAddress extends EntityRelationship<Publisher, Address> {
	public PublisherAddress(Publisher publisher, Address address) {
		super(publisher, address, createAttributes());
	}

	public Publisher getPublisher() {
		return getFromPrimaryKeyContainer();
	}

	public Address getAddress() {
		return getToPrimaryKeyContainer();
	}

	public static Dictionary<String, Object> createAttributes() {
		return new Hashtable<String, Object>();
	}
}