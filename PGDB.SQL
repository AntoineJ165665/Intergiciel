create table messageBrute(
	id SERIAL primary KEY;
	sender TEXT not NULL,
	recipient TEXT not NULL,
	message TEXT not null,
);

create table messageTraduit(
	id SERIAL primary KEY;
	sender TEXT not NULL,
	recipient TEXT not NULL,
	message TEXT not null,
);

create table clientConnected(
	id SERIAL primary key,
	nom TEXT not NULL UNIQUE,
	status TEXT not null default 'connecté'
);

