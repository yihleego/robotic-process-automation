package io.leego.rpa.repository;

import io.leego.rpa.entity.App;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Leego Yih
 */
public interface AppRepository extends JpaRepository<App, String>, QuerydslRepository<App> {

}
