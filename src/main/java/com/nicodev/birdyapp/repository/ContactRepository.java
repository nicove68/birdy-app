package com.nicodev.birdyapp.repository;

import com.nicodev.birdyapp.model.entity.Contact;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContactRepository extends MongoRepository<Contact, String> {

  void deleteAllByOwnerEmail(String ownerEmail);

  List<Contact> findByDayOfBirthAndMonthOfBirth(int dayOfBirth, int monthOfBirth);
}
