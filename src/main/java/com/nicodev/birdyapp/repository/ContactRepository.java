package com.nicodev.birdyapp.repository;

import com.nicodev.birdyapp.model.entity.Contact;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContactRepository extends MongoRepository<Contact, String> {


}
