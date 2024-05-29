package io.leego.rpa.repository;

import io.leego.rpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Leego Yih
 */
public interface UserRepository extends JpaRepository<User, Long>, QuerydslRepository<User> {

    List<User> findByAccountIn(List<String> accounts);

}
