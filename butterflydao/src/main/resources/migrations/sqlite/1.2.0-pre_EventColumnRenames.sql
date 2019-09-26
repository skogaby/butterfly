-- This script should be run *only* if you're running SQLite, and
-- *only* if you happened to run a from-source version pre-release
-- and your tables were created with the old names for these columns.
-- Essentially, only if you ran something between the 2019/09/25 and
-- 2019/09/26 commits (probably only me).

-- Rename the eventsSaveData field

ALTER TABLE `ddr_16_event_save_data` RENAME TO `ddr_16_event_save_data_orig`;

CREATE TABLE `ddr_16_event_save_data` (
  `id`	bigint NOT NULL,
  `comp_time`	bigint,
  `event_condition`	bigint,
  `event_id`	integer,
  `event_no`	integer,
  `event_type`	integer,
  `reward`	integer,
  `save_data`	bigint,
  `user_id`	bigint,
  PRIMARY KEY(`id`)
);

INSERT INTO ddr_16_event_save_data(id, comp_time, event_condition, event_id, event_no, event_type, reward, save_data, user_id) SELECT id, comp_time, condition, event_id, event_no, event_type, reward, save_data, user_id FROM ddr_16_event_save_data_orig;

DROP TABLE ddr_16_event_save_data_orig;

-- Rename the globalEvents field

ALTER TABLE `ddr_16_global_events` RENAME TO `ddr_16_global_events_orig`;

CREATE TABLE `ddr_16_global_events` (
  `event_id`	integer NOT NULL,
  `event_condition`	bigint,
  `event_no`	integer,
  `event_type`	integer,
  `reward`	integer,
  PRIMARY KEY(`event_id`)
);

INSERT INTO ddr_16_global_events(event_id, event_condition, event_no, event_type, reward) SELECT event_id, condition, event_no, event_type, reward FROM ddr_16_global_events_orig;

DROP TABLE ddr_16_global_events_orig;
