package org.athletes.traineatrepeat.repository;

import org.athletes.traineatrepeat.repository.dto.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDTO, String> {
  UserDTO getUserByUuid(String uuid);
}
