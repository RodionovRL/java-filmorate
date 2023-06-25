package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.api.FilmStorage;
import ru.yandex.practicum.filmorate.api.ReviewStorage;
import ru.yandex.practicum.filmorate.api.UserStorage;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.*;


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
		if (isFilmExist(review.getFilmId()) && isExistUser(review.getUserId())) {
			long id = reviewStorage.addReview(review);
			review.setReviewId(id);
			return review;
		} else {
			throw new ReviewNotFoundException("Model not found");
		}
	}

	public Review changeReview(Review review) {
		reviewStorage.changeReview(review);
		Review result = reviewStorage.getReviewById(review.getReviewId());
		return result;
	}

	public void deleteReview(long id) {
		Review review = getReviewById(id);
		reviewStorage.deleteReview(id);
	}

	public Review getReviewById(long id) {
		try {
			return reviewStorage.getReviewById(id);
		} catch (EmptyResultDataAccessException ex) {
			throw new ReviewNotFoundException(String.format("Review with id %d isn't exist", id));
		}
	}

	public List<Review> getReviewByFilmId(Optional<Long> filmId, int count) {
		List<Review> allReviews;
		if (filmId.isPresent()) {
			allReviews = reviewStorage.getReviewByFilmId(filmId.get(), count);
		} else {
			allReviews = reviewStorage.getCountReview(count);
		}
		allReviews.sort((o1, o2) -> Integer.compare(o2.getUseful(), o1.getUseful()));
		return allReviews;
	}

	public void addLike(long id, long userId, boolean islike) {
		reviewStorage.addLike(id, userId, islike);
	}

	public void deleteLike(long id, long userId, boolean isLike) {
		reviewStorage.deleteLike(id, userId, isLike);
	}

	private boolean isExistUser(long id) {
		Optional<User> user = Optional.ofNullable(userStorage.getUserById(id));
		return user.isPresent();
	}

	private boolean isFilmExist(long id) {
		try {
			filmStorage.getFilmById(id);
		} catch (EmptyResultDataAccessException ex) {
			return false;
		}
		return true;
	}
}