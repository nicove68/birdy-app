package com.nicodev.birdyapp.repository;

import com.nicodev.birdyapp.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {


}
