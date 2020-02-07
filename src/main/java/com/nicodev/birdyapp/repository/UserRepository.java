package com.nicodev.birdyapp.repository;

import com.nicodev.birdyapp.model.entity.User;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    boolean existsByEmail(String email);

    Optional<User> findByIdAndEmail(String id, String email);

    void deleteByEmail(String email);
}
