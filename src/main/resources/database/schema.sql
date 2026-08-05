-- Clash of Claws
-- Shared database schema
-- Add tables for each feature slice below.

-- Formatting:
-- lowercase snake_case
-- One table per entity
-- PK: <table>_id
-- FK: <referenced_table>_id

-- Example:
-- battle_id INTEGER PRIMARY KEY AUTOINCREMENT
-- player_id INTEGER

PRAGMA foreign_keys = ON;

-- ============================================================================
-- Slice 1 - Accounts
-- ============================================================================

CREATE TABLE IF NOT EXISTS player (
    player_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    currency_balance INTEGER NOT NULL DEFAULT 0,
    experience INTEGER NOT NULL DEFAULT 0,
    active_cat_id INTEGER,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- Slice 3 - Battle Engine

-- battle
CREATE TABLE IF NOT EXISTS battle (
    battle_id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id INTEGER NOT NULL,
    battle_type TEXT NOT NULL,
    arena_id INTEGER,
    status TEXT NOT NULL
);


-- TODO battle_turn
-- TODO battle_participant
-- TODO capture
