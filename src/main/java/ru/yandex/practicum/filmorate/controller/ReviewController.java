package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;


@RestController
@Slf4j
@RequestMapping("/reviews")
public class ReviewController {

	private ReviewService reviewService;

	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@GetMapping
	public Collection<Review> findAll(@RequestParam(required = false) Long filmId,
									   @RequestParam(defaultValue = "10", required = false) Long count) {
		log.info("Запрос на получение отзывов, filmId = {}, count = {}", filmId == null ? "all" : filmId, count);
		return reviewService.getAllReviews(filmId, count);
	}

	@GetMapping("/{id}")
	public Review getReviewById(@PathVariable Long id) {
		log.info("Запрос на получение отзыва по id = {}", id);
		return reviewService.getReviewById(id);
	}

	@PostMapping
	public Review createReview(@Valid @RequestBody Review review) {
		log.info("Запрос на добавление отзыва {}", review);
		return reviewService.addReview(review);
	}

	@PutMapping
	public Review updateReview(@Valid @RequestBody Review review) {
		log.info("Запрос на обновление обзора {}", review);
		return reviewService.updateReview(review);
	}

	@DeleteMapping("/{id}")
	public void deleteReviewById(@PathVariable Long id) {
		log.info("Запрос на удаление отзыва от id = {}", id);
		reviewService.deleteReviewById(id);
	}

	@PutMapping("/{id}/like/{userId}")
	public void addUserLike(@PathVariable Long id, @PathVariable Long userId) {
		log.info("Запрос добавить \"Нравится\" к отзыву с помощью id = {} пользователем с userId = {}", id, userId);
		reviewService.addUserLike(id, userId);
	}

	@PutMapping("/{id}/dislike/{userId}")
	public void addUserDislike(@PathVariable Long id, @PathVariable Long userId) {
		log.info("Запрос добавить \"не нравится\" к отзыву с помощью id = {} пользователем с userId = {}", id, userId);
		reviewService.addUserDislike(id, userId);
	}

	@DeleteMapping("/{id}/like/{userId}")
	public void deleteUserLike(@PathVariable Long id, @PathVariable Long userId) {
		log.info("Запрос на удаление лайка из обзора с помощью id = {} пользователем с userId = {}", id, userId);
		reviewService.deleteUserLike(id, userId);
	}

	@DeleteMapping("/{id}/dislike/{userId}")
	public void deleteUserDislike(@PathVariable Long id, @PathVariable Long userId) {
		log.info("Запрос на удаление \"не нравится\" из обзора с помощью id = {} пользователем с userId = {}", id, userId);
		reviewService.deleteUserDislike(id, userId);
	}
}