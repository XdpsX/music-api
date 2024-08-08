-- TABLES
-- Table: genres
CREATE TABLE IF NOT EXISTS genres (
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    image VARCHAR(255) NOT NULL
);

-- Table: albums
CREATE TABLE IF NOT EXISTS albums (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    image VARCHAR(255),
    release_date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    genre_id INTEGER REFERENCES genres(id)
);

-- Table: artists
CREATE TABLE IF NOT EXISTS artists (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    avatar VARCHAR(255),
    gender VARCHAR(16) NOT NULL,
    description TEXT,
    dob DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: tracks
CREATE TABLE IF NOT EXISTS tracks (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    duration_ms INTEGER NOT NULL,
    image VARCHAR(255),
    url VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    track_number INTEGER,
    album_id BIGINT REFERENCES albums(id) ON DELETE SET NULL,
    genre_id INTEGER REFERENCES genres(id)
);

-- Table: roles
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL UNIQUE
);

-- Table: users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    avatar VARCHAR(255),
    email VARCHAR(128) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    account_locked BOOLEAN DEFAULT FALSE,
    enabled BOOLEAN DEFAULT FALSE,
    role_id INTEGER REFERENCES roles(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table: confirm_tokens
CREATE TABLE IF NOT EXISTS confirm_tokens (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    expired_at TIMESTAMP NOT NULL,
    validated_at TIMESTAMP,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

-- Table: tokens
CREATE TABLE IF NOT EXISTS tokens (
    id BIGSERIAL PRIMARY KEY,
    access_token VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(255) NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE
);

-- Table: playlists
CREATE TABLE IF NOT EXISTS playlists (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    owner_id BIGINT REFERENCES users(id) ON DELETE CASCADE
);

-- Table: likes
CREATE TABLE IF NOT EXISTS likes (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    track_id BIGINT REFERENCES tracks(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, track_id)
);

-- Table: playlist_tracks
CREATE TABLE IF NOT EXISTS playlist_tracks (
    playlist_id BIGINT REFERENCES playlists(id) ON DELETE CASCADE,
    track_id BIGINT REFERENCES tracks(id) ON DELETE CASCADE,
    track_number INTEGER NOT NULL,
    PRIMARY KEY (playlist_id, track_id)
);

-- Table: artist_albums
CREATE TABLE IF NOT EXISTS artist_albums (
    artist_id BIGINT REFERENCES artists(id) ON DELETE CASCADE,
    album_id BIGINT REFERENCES albums(id) ON DELETE CASCADE,
    PRIMARY KEY (artist_id, album_id)
);

-- Table: artist_tracks
CREATE TABLE IF NOT EXISTS artist_tracks (
    artist_id BIGINT REFERENCES artists(id) ON DELETE CASCADE,
    track_id BIGINT REFERENCES tracks(id) ON DELETE CASCADE,
    PRIMARY KEY (artist_id, track_id)
);

-- Add roles Data
INSERT INTO roles (name)
VALUES 
('ADMIN'), 
('USER');

-- TRIGGERS
-- When user creates new confirm token, set revoked of old confirm tokens = true
CREATE OR REPLACE FUNCTION revoke_old_confirm_tokens()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE confirm_tokens
    SET revoked = true
    WHERE user_id = NEW.user_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER before_insert_confirm_token
BEFORE INSERT ON confirm_tokens
FOR EACH ROW
EXECUTE FUNCTION revoke_old_confirm_tokens();

-- When user creates new token, set revoked of old tokens = true
CREATE OR REPLACE FUNCTION revoke_old_tokens()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE tokens
    SET revoked = true
    WHERE user_id = NEW.user_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER before_insert_token
BEFORE INSERT ON tokens
FOR EACH ROW
EXECUTE FUNCTION revoke_old_tokens();

-- When user is locked, the user's tokens must be revoked
CREATE OR REPLACE FUNCTION revoke_tokens_on_account_lock()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.account_locked = TRUE THEN
        UPDATE tokens
        SET revoked = TRUE
        WHERE user_id = NEW.id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_tokens_on_account_lock
AFTER UPDATE OF account_locked ON users
FOR EACH ROW
EXECUTE FUNCTION revoke_tokens_on_account_lock();