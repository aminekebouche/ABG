package io.github.oliviercailloux.abg.service;

import io.github.oliviercailloux.abg.model.MoveEntity;
import io.github.oliviercailloux.abg.utils.QueryHelper;
import io.github.oliviercailloux.abg.model.GameEntity;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@RequestScoped
public class ChessService {

  @Inject
  EntityManager em;
  @Inject
  QueryHelper helper;

  @Transactional
  public List<GameEntity> getAllGames() {
    return em.createQuery(helper.selectAll(GameEntity.class)).getResultList();
  }

  @Transactional
  public List<MoveEntity> getAllMoves() {
    return em.createQuery(helper.selectAll(MoveEntity.class)).getResultList();
  }

  @Transactional
  public GameEntity getGame(int gameId) {
    return em.createNamedQuery("Game.find", GameEntity.class).setParameter("id", gameId)
        .getSingleResult();
  }

  @Transactional
  public int getLastGameId() {
    return em.createNamedQuery("Game.getLastGameId", Integer.class).getSingleResult();
  }

  @Transactional
  public MoveEntity getMove(int idMove) {
    return em.createNamedQuery("Move.find", MoveEntity.class).setParameter("id", idMove)
        .getSingleResult();
  }

  /**
   * Gets the id of the last move played for the given game
   * 
   * @param idGame
   * @return id of the last Move played for the given game
   */
  @Transactional
  public int getLastMoveId(int idGame) {
    return em.createNamedQuery("Move.getLastMoveId", Integer.class).setParameter("id", idGame)
        .getSingleResult();
  }

  @Transactional
  public <T> void persist(T t) {
    // em.getTransaction().begin();
    em.persist(t);
    // em.getTransaction().commit();
  }
}
