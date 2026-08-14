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

CREATE TABLE IF NOT EXISTS player
(
    player_id        INTEGER PRIMARY KEY AUTOINCREMENT,
    username         TEXT    NOT NULL UNIQUE,
    password_hash    TEXT    NOT NULL,
    currency_balance INTEGER NOT NULL DEFAULT 100,
    active_cat_id    INTEGER,
    created_at       TEXT    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- Slice 3 - Battle Engine

-- battle
CREATE TABLE IF NOT EXISTS battle
(
    battle_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id   INTEGER NOT NULL,
    battle_type TEXT    NOT NULL,
    arena_id    INTEGER,
    status      TEXT    NOT NULL
);

-- ============================================================================
-- slice 4 - Admin/Arena
-- ============================================================================
CREATE TABLE IF NOT EXISTS arenas
(
    arena_id      INTEGER PRIMARY KEY AUTOINCREMENT,
    arena_name    TEXT    NOT NULL,
    town_name     TEXT    NOT NULL,
    difficulty    TEXT    NOT NULL,
    reward_amount INTEGER NOT NULL
        CHECK (reward_amount >= 0),
    active        INTEGER NOT NULL DEFAULT 1
);

-- ============================================================================
-- slice 5 - Creature Generation / CatDex
-- ============================================================================

CREATE TABLE IF NOT EXISTS cats
(
    cat_id    INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id INTEGER NOT NULL,
    cat_data  TEXT    NOT NULL,

    FOREIGN KEY (player_id)
        REFERENCES player (player_id)
);
-- TODO battle_turn
-- TODO battle_participant
-- TODO capture

-- ============================================================================
-- Marketplace & Trading
-- ============================================================================

CREATE TABLE IF NOT EXISTS trader_items
(
    item_id        INTEGER PRIMARY KEY AUTOINCREMENT,
    item_name      TEXT    NOT NULL,
    item_type      TEXT    NOT NULL,
    description    TEXT    NOT NULL,
    price          INTEGER NOT NULL CHECK (price >= 0),
    stock_quantity INTEGER NOT NULL CHECK (stock_quantity >= 0)
);

CREATE TABLE IF NOT EXISTS player_inventory
(
    inventory_id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id    INTEGER NOT NULL,
    item_id      INTEGER NOT NULL,
    quantity     INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0),

    UNIQUE (player_id, item_id),

    FOREIGN KEY (player_id)
        REFERENCES player (player_id),

    FOREIGN KEY (item_id)
        REFERENCES trader_items (item_id)
);

-- ============================================================================
-- Rename Original Catching Items
-- ============================================================================
-- These UPDATE statements allow existing game databases to keep their
-- current item IDs and inventory relationships while changing the names.

UPDATE trader_items
SET item_name   = 'Toy Mouse',
    description = 'A toy mouse used to catch cats.'
WHERE item_name = 'Basic Catching Item';

UPDATE trader_items
SET item_name   = 'Tuna Can',
    description = 'A tasty can of tuna with a better chance of catching cats.'
WHERE item_name = 'Strong Catching Item';

-- ============================================================================
-- Default Trader Items
-- ============================================================================
-- WHERE NOT EXISTS prevents duplicate default items when the game starts.

INSERT INTO trader_items (item_name,
                          item_type,
                          description,
                          price,
                          stock_quantity)
SELECT 'Small Potion',
       'HEALING',
       'Restores a small amount of health during battle.',
       25,
       10
WHERE NOT EXISTS (SELECT 1
                  FROM trader_items
                  WHERE item_name = 'Small Potion');

INSERT INTO trader_items (item_name,
                          item_type,
                          description,
                          price,
                          stock_quantity)
SELECT 'Large Potion',
       'HEALING',
       'Restores a large amount of health during battle.',
       60,
       5
WHERE NOT EXISTS (SELECT 1
                  FROM trader_items
                  WHERE item_name = 'Large Potion');

INSERT INTO trader_items (item_name,
                          item_type,
                          description,
                          price,
                          stock_quantity)
SELECT 'Toy Mouse',
       'CATCHING',
       'A toy mouse used to catch cats.',
       40,
       8
WHERE NOT EXISTS (SELECT 1
                  FROM trader_items
                  WHERE item_name = 'Toy Mouse');

INSERT INTO trader_items (item_name,
                          item_type,
                          description,
                          price,
                          stock_quantity)
SELECT 'Tuna Can',
       'CATCHING',
       'A tasty can of tuna with a better chance of catching cats.',
       90,
       3
WHERE NOT EXISTS (SELECT 1
                  FROM trader_items
                  WHERE item_name = 'Tuna Can');