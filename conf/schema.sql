CREATE TABLE myuser (
    id uuid NOT NULL,
    name character varying NOT NULL,
    email character varying NOT NULL,
    password_hash character varying NOT NULL,
    created_at timestamp with time zone NOT NULL
);



CREATE TABLE task (
    id uuid NOT NULL,
    name character varying NOT NULL,
    due_date timestamp with time zone,
    creator_id uuid NOT NULL,
    done boolean NOT NULL
);

COPY myuser (id, name, email, password_hash, created_at) FROM stdin;
4d54e9df-eb2a-4d7e-b72f-bec5dde89cab	John	john.doe@example.org		2013-10-31 00:00:00+01
\.


COPY task (id, name, due_date, creator_id, done) FROM stdin;
855b2952-6c23-4da2-a892-72b0cb51b099	test task	2013-12-01 00:00:00+01	4d54e9df-eb2a-4d7e-b72f-bec5dde89cab	t
\.


ALTER TABLE ONLY task
    ADD CONSTRAINT task_pkey PRIMARY KEY (id);

ALTER TABLE ONLY myuser
    ADD CONSTRAINT user_email_unique UNIQUE (email);

ALTER TABLE ONLY myuser
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);

ALTER TABLE ONLY task
    ADD CONSTRAINT task_creator_id_fkey FOREIGN KEY (creator_id) REFERENCES myuser(id);
