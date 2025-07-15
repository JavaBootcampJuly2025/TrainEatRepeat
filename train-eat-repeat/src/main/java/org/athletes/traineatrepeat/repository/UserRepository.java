package org.athletes.traineatrepeat.repository;

import org.athletes.traineatrepeat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserRepository extends JpaRepository<User, Long> {


    //    Optional<User> ...  ????


    public User getUserById(Long id);
}
