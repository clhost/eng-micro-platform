CREATE TABLE word_definition (
    word           TEXT        NOT NULL,
    examples       JSONB       NULL,
    synonyms       JSONB       NULL,
    translations   JSONB       NULL,
    meanings       JSONB       NULL,
    tags           JSONB       NULL,
    pronunciations JSONB       NULL,
    created_at     TIMESTAMPTZ NOT NULL,
    updated_at     TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_word_definition_word PRIMARY KEY (word)
);

CREATE INDEX ix_word_definition_created_at ON word_definition (created_at);
