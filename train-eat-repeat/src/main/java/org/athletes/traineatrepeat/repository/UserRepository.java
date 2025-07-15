package org.athletes.traineatrepeat.repository;

import org.athletes.traineatrepeat.repository.dto.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserRepository extends JpaRepository<UserDTO, Long> {


    //    Optional<User> ...  ????


    public UserDTO getUserById(Long id);
}
