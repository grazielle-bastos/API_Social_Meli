SELECT * FROM social_meli.follower;

CREATE TABLE `follower` (
  `follower_id` int NOT NULL AUTO_INCREMENT,
  `user_follower_id` int NOT NULL,
  `user_to_follow_id` int NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`follower_id`)
);

ALTER TABLE follower
  ADD CONSTRAINT fk_follower_user
    FOREIGN KEY (user_follower_id) REFERENCES user(user_id),
  ADD CONSTRAINT fk_followed_user
    FOREIGN KEY (user_to_follow_id) REFERENCES user(user_id);
