-- 1. TABLES
-- Table: genres
CREATE TABLE IF NOT EXISTS genres (
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    image VARCHAR(255)
);

-- Table: albums
CREATE TABLE IF NOT EXISTS albums (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    image VARCHAR(255),
    release_date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    genre_id INTEGER REFERENCES genres(id) ON DELETE SET NULL
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
    listening_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    track_number INTEGER,
    album_id BIGINT REFERENCES albums(id) ON DELETE SET NULL,
    genre_id INTEGER REFERENCES genres(id) ON DELETE SET NULL
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

-- 2. INDEXES
CREATE INDEX idx_tokens_access_token ON tokens (access_token);
CREATE INDEX idx_tokens_refresh_token ON tokens (refresh_token);

CREATE INDEX idx_tracks_genre_id ON tracks (genre_id);
CREATE INDEX idx_tracks_album_id ON tracks (album_id);

CREATE INDEX idx_tracks_album_id_track_number ON tracks(album_id, track_number);

CREATE INDEX idx_playlists_id_owner_id ON playlists(id, owner_id);

-- 3. TRIGGERS
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

-- When delete track in album, adjust track number of the other tracks
CREATE OR REPLACE FUNCTION adjust_tracks_track_numbers_after_deletion()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE tracks
    SET track_number = track_number - 1
    WHERE album_id = OLD.album_id AND track_number > OLD.track_number;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER before_delete_track
BEFORE DELETE ON tracks
FOR EACH ROW
WHEN (OLD.album_id IS NOT NULL AND OLD.track_number IS NOT NULL)
EXECUTE FUNCTION adjust_tracks_track_numbers_after_deletion();

-- When update track from an album to another album, adjust track number of the other tracks in old album
CREATE OR REPLACE FUNCTION adjust_tracks_track_numbers_on_update()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.album_id IS DISTINCT FROM NEW.album_id THEN
        UPDATE tracks
        SET track_number = track_number - 1
        WHERE album_id = OLD.album_id AND track_number > OLD.track_number;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER before_update_track
BEFORE UPDATE ON tracks
FOR EACH ROW
WHEN (OLD.album_id IS NOT NULL AND OLD.track_number IS NOT NULL)
EXECUTE FUNCTION adjust_tracks_track_numbers_on_update();

-- When remove track from playlist, adjust track number of other tracks in playlist
CREATE OR REPLACE FUNCTION adjust_playlist_tracks_track_numbers_after_deletion()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE playlist_tracks
    SET track_number = track_number - 1
    WHERE playlist_id = OLD.playlist_id AND track_number > OLD.track_number;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER before_delete_playlist_track
BEFORE DELETE ON playlist_tracks
FOR EACH ROW
EXECUTE FUNCTION adjust_playlist_tracks_track_numbers_after_deletion();

-- 4. Insert Data
INSERT INTO roles (name)
VALUES 
('ADMIN'), 
('USER');

-- All default users password is "password"
INSERT INTO users (name, avatar, email, password, account_locked, enabled, role_id)
VALUES ('Admin', NULL, 'admin@xdpsx.com', '$2a$12$WmPNKXdVUNlK/9qFpG69wOD8O0iJ86UGRXeFioBnoB9f4zXXhbwtO', false, true, 1);