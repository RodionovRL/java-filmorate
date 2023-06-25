package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.api.ReviewStorage;
import ru.yandex.practicum.filmorate.model.Review;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long addReview(Review review) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("review")
                .usingGeneratedKeyColumns("id");
        return insert.executeAndReturnKey(review.toMap()).longValue();
    }

    @Override
    public void changeReview(Review review) {
        String sql = "UPDATE review SET content = ?, is_positive = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql, review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
    }

    @Override
    public void deleteReview(long id) {
        String sql = "DELETE FROM review " +
                "WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Review getReviewById(long id) {
        String sql = "SELECT * FROM review " +
                "WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, ReviewDbStorage::mapRowToReview, id);
    }

    @Override
    public List<Review> getReviewByFilmId(long filmId, int count) {
        String sql = "SELECT * FROM review " +
                "WHERE film_id = ? " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, ReviewDbStorage::mapRowToReview, filmId, count);
    }

    @Override
    public List<Review> getCountReview(int count) {
        String sql = "SELECT * FROM review " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, ReviewDbStorage::mapRowToReview, count);
    }

    @Override
    public List<Review> getAllReview() {
        String sql = "SELECT * FROM review";
        return jdbcTemplate.query(sql, ReviewDbStorage::mapRowToReview);
    }

    @Override
    public void addLike(long id, long userId, boolean isLike) {
        String sql = "MERGE INTO review_likes (id, user_id, is_like) KEY (id, user_id) VALUES ( ?, ?, ? )";
        jdbcTemplate.update(sql, id, userId, isLike);
        updateUseful(id);
    }

    @Override
    public void deleteLike(long id, long userId, boolean isLike) {
        String sql = "DELETE FROM review_likes " +
                "WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
        updateUseful(id);
    }

    private void updateUseful(long reviewId) {
        String sql = "UPDATE review SET useful = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql, calculateUseful(reviewId), reviewId);
    }

    private int calculateUseful(long reviewId) {
        String sqlQuery = "SELECT " +
                "(SELECT COUNT (*) FROM review_likes WHERE id = ? AND is_like) - " +
                "(SELECT COUNT (*) FROM review_likes WHERE id = ? AND NOT is_like)";
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class, reviewId, reviewId);
    }

    public static Review mapRowToReview(ResultSet resultSet, long rowNum) throws SQLException {
        long id = resultSet.getLong("id");
        String content = resultSet.getString("content");
        boolean isPositive = resultSet.getBoolean("is_positive");
        long userId = resultSet.getLong("user_id");
        long filmId = resultSet.getLong("film_id");
        int useful = resultSet.getInt("useful");
        return new Review(id, content, isPositive, userId, filmId, useful);
    }
}