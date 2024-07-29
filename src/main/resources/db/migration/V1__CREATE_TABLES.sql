DROP TABLE IF EXISTS urls;

CREATE TABLE urls(
  short_url varchar(32) Primary Key,
  long_url TEXT UNIQUE
);