package org.athletes.traineatrepeat.repository;

import org.athletes.traineatrepeat.model.response.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserRepository extends JpaRepository<UserResponse, Long> {


    //    Optional<UserResponse> ...  ????


    public UserResponse getUserById(Long id);
}
