package com.pragmaticbitbucket.app.ws.io.repositories;

import com.pragmaticbitbucket.app.ws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
// the second argument is the type of the id field
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    // findBy and field name
    UserEntity findByEmail(String email);
    UserEntity findByUserId(String publicUserId);
}
