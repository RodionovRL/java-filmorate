package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.api.FilmStorage;
import ru.yandex.practicum.filmorate.api.ReviewStorage;
import ru.yandex.practicum.filmorate.api.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
	private final ReviewStorage reviewStorage;
	private final UserStorage userStorage;
	private final FilmStorage filmStorage;

	@Autowired
	public ReviewService(ReviewStorage reviewStorage, UserStorage userStorage,
						 FilmStorage filmStorage) {
		this.reviewStorage = reviewStorage;
		this.userStorage = userStorage;
		this.filmStorage = filmStorage;
	}

	public Review addReview(Review review) {
		userStorage.getUserById ( review.getUserId () );
		filmStorage.getFilmById ( review.getFilmId () );
		long id = reviewStorage.addReview ( review );
		review.setReviewId ( id );
		return review;
	}

	public Review changeReview(Review review) {
		reviewStorage.changeReview ( review );
		return reviewStorage.getReviewById ( review.getReviewId () );
	}

	public void deleteReview(long id) {
		getReviewById ( id );
		reviewStorage.deleteReview ( id );
	}

	public Review getReviewById(long id) {
		try {
			return reviewStorage.getReviewById ( id );
		} catch (EmptyResultDataAccessException ex) {
			throw new NotFoundException ( String.format ( "Review with id %d isn't exist", id ) );
		}
	}

	public List < Review > getReviewByFilmId(Optional < Long > filmId, int count) {
		List < Review > allReviews;
		if (filmId.isPresent ()) {
			allReviews = reviewStorage.getReviewByFilmId ( filmId.get (), count );
		} else {
			allReviews = reviewStorage.getCountReview ( count );
		}
		allReviews.sort ( (o1, o2) -> Integer.compare ( o2.getUseful (), o1.getUseful () ) );
		return allReviews;
	}

	public void addLike(long id, long userId, boolean islike) {
		reviewStorage.addLike ( id, userId, islike );
	}

	public void deleteLike(long id, long userId, boolean isLike) {
		reviewStorage.deleteLike ( id, userId, isLike );
	}
}