package com.cphillipsdorsett.teamsweeper.game.dao;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;
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
            .setParameter(1, sessionGame.getSessionId())
            .setParameter(2, sessionGame.getGameId())
            .executeUpdate();
    }

    @Transactional
    public SessionGame findCurrent(String sessionId, int gameId) {
        return (SessionGame) em
            .createNativeQuery(
                "" +
                    "SELECT * " +
                    "FROM session_game " +
                    "WHERE session_id = :sessionId" +
                    "    AND game_id = :gameId",
                SessionGame.class
            )
            .setParameter("sessionId", sessionId)
            .setParameter("gameId", gameId)
            .getSingleResult();
    }

    @Transactional
    public void update(SessionGame sessionGame) {
        em
            .createNativeQuery(
                "" +
                    "UPDATE session_game " +
                    "SET" +
                    "    uncovers = :uncovers " +
                    "WHERE id = :id"
            )
            .setParameter("uncovers", sessionGame.getUncovers())
            .setParameter("id", sessionGame.getId())
            .executeUpdate();
    }

    @Transactional
    public List<SessionGameStats> findSessionGameStats(String sessionId) {
        return (List<SessionGameStats>) em
            .createNativeQuery(
                "" +
                    "SELECT" +
                    "    ROW_NUMBER() OVER ( ORDER BY g.difficulty) as id," +
                    "    g.difficulty," +
                    "    g.status," +
                    "    COUNT(g.status) count," +
                    "    MIN(CalcDurationMS(g.started_at, g.ended_at)) as fastest_time," +
                    "    MAX(sg.uncovers) as most_uncovers," +
                    "    MAX(CalcScore(sg.uncovers, g.started_at, g.ended_at)) as highest_score," +
                    "    AVG(g.completion_pct) as avg_completion_pct " +
                    "FROM session_game sg " +
                    "INNER JOIN game g ON sg.game_id = g.id " +
                    "WHERE sg.session_id = :sessionId" +
                    "    AND g.started_at IS NOT NULL " +
                    "GROUP BY g.status, g.difficulty",
                SessionGameStats.class
            )
            .setParameter("sessionId", sessionId)
            .getResultList();
    }

    @Transactional
    public int deleteBySessionId(String sessionId) {
        em
            .createNativeQuery("" +
                "DELETE FROM session_game sg " +
                "WHERE sg.session_id = :sessionId"
            )
            .setParameter("sessionId", sessionId)
            .executeUpdate();
        BigInteger deletedCount = (BigInteger) em
            .createNativeQuery("" +
                "SELECT ROW_COUNT()"
            )
            .getSingleResult();
        return deletedCount.intValue();
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
