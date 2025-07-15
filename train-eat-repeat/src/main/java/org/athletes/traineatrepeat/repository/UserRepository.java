package org.athletes.traineatrepeat.repository;

import org.athletes.traineatrepeat.repository.model.UserDto;
import org.springframework.stereotype.Component;

@Component
public interface UseRepository extends JpaRepository<User, Long> {


    //    Optional<User> ...  ????


    public UserDto getUserById(Long id);
}
