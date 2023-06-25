package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@Validated
public class ReviewController {
    private final ReviewService reviewService;
    private final FeedService feedService;

    @Autowired
    public ReviewController(ReviewService reviewService, FeedService feedService) {
        this.reviewService = reviewService;
        this.feedService = feedService;
    }

    @PostMapping("/reviews")
    public Review addReview(@Valid @RequestBody Review review) {
        log.info("User {} added", review.getContent());
        review = reviewService.addReview(review);
        feedService.userAddReview(review.getUserId(), review.getReviewId());
        return review;
    }

    @PutMapping("/reviews")
    public Review changeReview(@RequestBody Review review) {
        log.info("Review = {} was changed userId = {}", review.getReviewId(), review.getUserId());
        Review changedReview = reviewService.changeReview(review);
        feedService.userUpdateReview(changedReview.getUserId(), changedReview.getReviewId());
        return changedReview;
    }

    @DeleteMapping("/reviews/{id}")
    public void deleteReview(@PathVariable long id) {
        log.info("Review {} was deleted", id);
        feedService.userRemoveReview(id);
        reviewService.deleteReview(id);
    }

    @GetMapping("/reviews/{id}")
    public Review getReviewById(@PathVariable long id) {
        log.info("Get review {}", id);
        return reviewService.getReviewById(id);
    }

    @GetMapping("/reviews")
    public List<Review> getReviewByFilmId(@RequestParam(required = false) Optional<Long> filmId,
                                          @RequestParam(defaultValue = "10") int count) {
        log.info("Get reviews {} count", count);
        return reviewService.getReviewByFilmId(filmId, count);
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public void addLike(@PathVariable long id,
                        @PathVariable long userId) {
        log.info("Add like review {} from user {} ", id, userId);
        reviewService.addLike(id, userId, true);
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id,
                           @PathVariable long userId) {
        log.info("Delete like review {} from user {} ", id, userId);
        reviewService.deleteLike(id, userId, true);
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public void addDislike(@PathVariable long id,
                           @PathVariable long userId) {
        log.info("Add dislike review {} from user {} ", id, userId);
        reviewService.addLike(id, userId, false);
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable long id,
                              @PathVariable long userId) {
        log.info("Delete dislike review {} from user {} ", id, userId);
        reviewService.deleteLike(id, userId, false);
    }
}