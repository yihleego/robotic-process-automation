package io.leego.rpa.repository;

import io.leego.rpa.entity.App;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Leego Yih
 */
public interface AppRepository extends JpaRepository<App, Long>, QuerydslRepository<App> {

    List<App> findByCodeIn(List<String> codes);

}
