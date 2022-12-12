create table post(
    id serial primary key,
    name varchar(500),
    text text,
    link text unique,
    created timestamp
);
