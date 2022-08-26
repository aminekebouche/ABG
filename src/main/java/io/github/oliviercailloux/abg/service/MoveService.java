package io.github.oliviercailloux.abg.service;

import io.github.oliviercailloux.abg.model.MoveEntity;
import io.github.oliviercailloux.abg.utils.QueryHelper;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class MoveService {
  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(MoveService.class);

  @Inject
  EntityManager em;

  @Inject
  QueryHelper helper;

  @Transactional
  public List<MoveEntity> getAll() {
    return em.createQuery(helper.selectAll(MoveEntity.class)).getResultList();
  }

  @Transactional
  public void persist(MoveEntity move) {
    em.persist(move);
  }
}
