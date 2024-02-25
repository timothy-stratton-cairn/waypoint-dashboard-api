DROP TABLE IF EXISTS `homework`;
CREATE TABLE `homework` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_by` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `homework_template_id` bigint NOT NULL,
  `protocol_step_id` bigint DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_aq5ro203r4su35g2pi0enkuaw` (`homework_template_id`),
  UNIQUE KEY `UK_8c69r5lhdjg4h44fgfuw32ikg` (`protocol_step_id`),
  CONSTRAINT `FKsumwaex33g39yqw3i5rvjyh3u` FOREIGN KEY (`homework_template_id`) REFERENCES `homework_template` (`id`),
  CONSTRAINT `FKx0f5nhdpje3jvfxsxnebvx42` FOREIGN KEY (`protocol_step_id`) REFERENCES `protocol_step` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



DROP TABLE IF EXISTS `homework_question`;
CREATE TABLE `homework_question` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_by` varchar(255) DEFAULT NULL,
  `question_abbreviation` varchar(255) DEFAULT NULL,
  `question` varchar(255) DEFAULT NULL,
  `next_homework_question_id` bigint DEFAULT NULL,
  `parent_homework_template_id` bigint NOT NULL,
  `active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_88ej1jbl92o5ftq6b8orvcbrh` (`parent_homework_template_id`),
  UNIQUE KEY `UK_m36k1lw196u89kb781ew7tt3s` (`next_homework_question_id`),
  CONSTRAINT `FKk4776ya7tn1j64uwosh9984it` FOREIGN KEY (`next_homework_question_id`) REFERENCES `homework_question` (`id`),
  CONSTRAINT `FKmykehcokwv5bieihrwp5vgt1v` FOREIGN KEY (`parent_homework_template_id`) REFERENCES `homework_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



DROP TABLE IF EXISTS `homework_response`;
CREATE TABLE `homework_response` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_by` varchar(255) DEFAULT NULL,
  `response` varchar(255) DEFAULT NULL,
  `homework_id` bigint NOT NULL,
  `homework_question_id` bigint NOT NULL,
  `active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_c71yadhy8i564bjwo059g75il` (`homework_question_id`),
  KEY `FKp5h432bwdql5g96h4yclkvso5` (`homework_id`),
  CONSTRAINT `FK8kr97pv37hbfpbjumcw0td6ua` FOREIGN KEY (`homework_question_id`) REFERENCES `homework_question` (`id`),
  CONSTRAINT `FKp5h432bwdql5g96h4yclkvso5` FOREIGN KEY (`homework_id`) REFERENCES `homework` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



DROP TABLE IF EXISTS `homework_template`;
CREATE TABLE `homework_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_by` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `step_template_id` bigint NOT NULL,
  `active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_dp55xi289hsduh14r1oh7tq1w` (`step_template_id`),
  CONSTRAINT `FKc1ud7k0bpg6aig952w30wsw96` FOREIGN KEY (`step_template_id`) REFERENCES `protocol_step_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



DROP TABLE IF EXISTS `homework_template_homework_question`;
CREATE TABLE `homework_template_homework_question` (
  `homework_question_id` bigint NOT NULL,
  `homework_template_id` bigint NOT NULL,
  PRIMARY KEY (`homework_question_id`,`homework_template_id`),
  KEY `FKlsmp50ewlw802qyyavo1pqgrm` (`homework_template_id`),
  CONSTRAINT `FK587suy45fy6ipqxoxcvpyq4as` FOREIGN KEY (`homework_question_id`) REFERENCES `homework_question` (`id`),
  CONSTRAINT `FKlsmp50ewlw802qyyavo1pqgrm` FOREIGN KEY (`homework_template_id`) REFERENCES `homework_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



DROP TABLE IF EXISTS `homework_user`;
CREATE TABLE `homework_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_by` varchar(255) DEFAULT NULL,
  `homework_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKos6qejhkyqx3ugym74exyrc55` (`homework_id`),
  CONSTRAINT `FKos6qejhkyqx3ugym74exyrc55` FOREIGN KEY (`homework_id`) REFERENCES `homework` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



DROP TABLE IF EXISTS `protocol`;
CREATE TABLE `protocol` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_by` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



DROP TABLE IF EXISTS `protocol_step`;
CREATE TABLE `protocol_step` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_by` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `step_template_id` bigint NOT NULL,
  `linked_homework_id` bigint DEFAULT NULL,
  `linked_step_task_id` bigint DEFAULT NULL,
  `next_protocol_step_id` bigint DEFAULT NULL,
  `parent_protocol_id` bigint NOT NULL,
  `step_status_id` bigint DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_h84yo0x0lqbb4qh0asb48jqfw` (`step_template_id`),
  UNIQUE KEY `UK_2ix72wwuy147m1tc49wicu97h` (`linked_homework_id`),
  UNIQUE KEY `UK_1l13m8gdfclvkavlsn4c1uysq` (`linked_step_task_id`),
  UNIQUE KEY `UK_ai0b96aen4td85qx45wm22s46` (`next_protocol_step_id`),
  UNIQUE KEY `UK_njgnuoakqkdvg3rqwfb4tclub` (`step_status_id`),
  KEY `FKsuie9h1b5jwc3lx3kjd3n0ns5` (`parent_protocol_id`),
  CONSTRAINT `FK4l2y2xighdkjuf4fsiw6apt4n` FOREIGN KEY (`linked_homework_id`) REFERENCES `homework` (`id`),
  CONSTRAINT `FK4lbs8w4uppklsv5i66cs5r7i5` FOREIGN KEY (`step_status_id`) REFERENCES `protocol_step_status` (`id`),
  CONSTRAINT `FKbyta9ws8xsvjgdqc9dqx94c5` FOREIGN KEY (`linked_step_task_id`) REFERENCES `protocol_step_task` (`id`),
  CONSTRAINT `FKip5yi3w1e5embpvgygpmte0ln` FOREIGN KEY (`next_protocol_step_id`) REFERENCES `protocol_step` (`id`),
  CONSTRAINT `FKm1b5qn5t96nc5ermsf3uhhhxj` FOREIGN KEY (`step_template_id`) REFERENCES `protocol_step_template` (`id`),
  CONSTRAINT `FKsuie9h1b5jwc3lx3kjd3n0ns5` FOREIGN KEY (`parent_protocol_id`) REFERENCES `protocol` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



DROP TABLE IF EXISTS `protocol_step_status`;
CREATE TABLE `protocol_step_status` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_by` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



DROP TABLE IF EXISTS `protocol_step_task`;
CREATE TABLE `protocol_step_task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_by` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `executable_reference` varchar(1000) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



DROP TABLE IF EXISTS `protocol_step_task_protocol_step`;
CREATE TABLE `protocol_step_task_protocol_step` (
  `protocol_step_id` bigint NOT NULL,
  `protocol_step_task_id` bigint NOT NULL,
  KEY `FK3wjtbaxb7odfm9hjcli3k0ipm` (`protocol_step_id`),
  KEY `FK7ejvfexov6s7p2tpgj0fj982u` (`protocol_step_task_id`),
  CONSTRAINT `FK3wjtbaxb7odfm9hjcli3k0ipm` FOREIGN KEY (`protocol_step_id`) REFERENCES `protocol_step` (`id`),
  CONSTRAINT `FK7ejvfexov6s7p2tpgj0fj982u` FOREIGN KEY (`protocol_step_task_id`) REFERENCES `protocol_step_task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



DROP TABLE IF EXISTS `protocol_step_template`;
CREATE TABLE `protocol_step_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_by` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `linked_step_task_id` bigint DEFAULT NULL,
  `linked_homework_template_id` bigint DEFAULT NULL,
  `parent_protocol_template_id` bigint NOT NULL,
  `active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6soccmn4u4opv8o18061y16ic` (`parent_protocol_template_id`),
  UNIQUE KEY `UK_7xnipyhy28t261s2a1yremg3o` (`linked_homework_template_id`),
  UNIQUE KEY `UK_g7d58ogffmqa95mj0d6b1xsoq` (`linked_step_task_id`),
  UNIQUE KEY `UK_4xludf3um34uo7kjabpnk5orm` (`next_protocol_step_id`),
  CONSTRAINT `FK22tpvuk2d68kxt0qmydhlvt56` FOREIGN KEY (`linked_homework_template_id`) REFERENCES `homework_template` (`id`),
  CONSTRAINT `FK4g4uwhxfuvdd01wx304a4xbx8` FOREIGN KEY (`linked_step_task_id`) REFERENCES `protocol_step_task` (`id`),
  CONSTRAINT `FK57a8uqptt3c1vn1lyqs5ab1cg` FOREIGN KEY (`parent_protocol_template_id`) REFERENCES `protocol` (`id`),
  CONSTRAINT `FKa7ebtxjsw51c0ar3pbj3mgypr` FOREIGN KEY (`next_protocol_step_id`) REFERENCES `protocol_step` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



DROP TABLE IF EXISTS `protocol_step_template_category`;
CREATE TABLE `protocol_step_template_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_by` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `responsible_user_id` bigint DEFAULT NULL,
  `responsible_role_id` bigint DEFAULT NULL,
  `accountable_user_id` bigint DEFAULT NULL,
  `accountable_role_id` bigint DEFAULT NULL,
  `consulted_user_id` bigint DEFAULT NULL,
  `consulted_role_id` bigint DEFAULT NULL,
  `informed_user_id` bigint DEFAULT NULL,
  `informed_role_id` bigint DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



DROP TABLE IF EXISTS `protocol_user`;
CREATE TABLE `protocol_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_by` varchar(255) DEFAULT NULL,
  `protocol_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5cttlnehswd4hrchqqp0ocy5a` (`protocol_id`),
  CONSTRAINT `FK5cttlnehswd4hrchqqp0ocy5a` FOREIGN KEY (`protocol_id`) REFERENCES `protocol` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
