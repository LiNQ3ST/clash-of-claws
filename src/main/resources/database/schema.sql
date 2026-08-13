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

-- ============================================================================
-- slice 4 - Admin/Arena
-- ============================================================================
CREATE TABLE IF NOT EXISTS arenas (
                                      arena_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                      arena_name TEXT NOT NULL,
                                      town_name TEXT NOT NULL,
                                      difficulty TEXT NOT NULL,
                                      reward_amount INTEGER NOT NULL
                                      CHECK (reward_amount >= 0),
    active INTEGER NOT NULL DEFAULT 1
    );

-- ============================================================================
-- slice 5 - Creature Generation / CatDex
-- ============================================================================

CREATE TABLE IF NOT EXISTS cats (
    cat_id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id INTEGER NOT NULL,
    cat_data TEXT NOT NULL,

    FOREIGN KEY (player_id)
        REFERENCES player(player_id)
);
-- TODO battle_turn
-- TODO battle_participant
-- TODO capture
