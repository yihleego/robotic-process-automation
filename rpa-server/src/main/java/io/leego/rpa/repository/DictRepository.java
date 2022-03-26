package io.leego.rpa.repository;

import io.leego.rpa.entity.Dict;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Leego Yih
 */
public interface DictRepository extends JpaRepository<Dict, Void>, QuerydslRepository<Dict> {

    List<Dict> findByTypeIn(List<String> type);

    List<Dict> findByTypeStartingWith(String type);

}
