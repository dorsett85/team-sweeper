package com.cphillipsdorsett.teamsweeper.game.dao;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public class SessionGameDao implements SessionGameRepository {

    EntityManager em;
    SessionGameRepository sessionGameRepository;

    public SessionGameDao(EntityManager entityManager, SessionGameRepository sessionGameRepository) {
        em = entityManager;
        this.sessionGameRepository = sessionGameRepository;
    }

    @Transactional
    public void create(SessionGame sessionGame) {
        em
            .createNativeQuery("" +
                "INSERT INTO session_game (session_id, game_id) " +
                "VALUES (?, ?);"
            )
            .setParameter(1, sessionGame.sessionId)
            .setParameter(2, sessionGame.gameId)
            .executeUpdate();
    }

    @Override
    public <S extends SessionGame> S save(S entity) {
        return null;
    }

    @Override
    public <S extends SessionGame> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<SessionGame> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Integer integer) {
        return false;
    }

    @Override
    public Iterable<SessionGame> findAll() {
        return null;
    }

    @Override
    public Iterable<SessionGame> findAllById(Iterable<Integer> integers) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public void delete(SessionGame entity) {

    }

    @Override
    public void deleteAll(Iterable<? extends SessionGame> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
