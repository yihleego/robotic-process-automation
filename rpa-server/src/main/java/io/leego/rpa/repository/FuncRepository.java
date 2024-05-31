package io.leego.rpa.repository;

import io.leego.rpa.entity.Func;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Leego Yih
 */
public interface FuncRepository extends JpaRepository<Func, Long>, QuerydslRepository<Func> {
}
