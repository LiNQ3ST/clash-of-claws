# Clash of Claws
### Collect cats. Train champions. Throw paws.
**Clash of Claws** is a single-player, turn-based JavaFX battle game created by
Team **L!NQ3ST** for CST 338: Software Design.

Players create an account, choose a starter cat, build a party, explore the Wilds,
challenge increasingly difficult arenas, collect new cats, and spend earned coins
at the Trader.


CST 338 Project 2 — Team **L!NQ3ST**.

## Team & Slice Ownership
| Slice                     | Owner            | GitHub username    | Issues                   | Branch(es)                                                                                                                                                                                  | PR(s)                                                                                                                                                                                                                                                                                                                                                                                                                                                                          | Enhancement chosen                  | Status   |
|---------------------------|------------------|--------------------|--------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------|----------|
| 1 — Accounts              | Sahtra Green     | SahtraRG           | #5, #6, #7, #8, #20, #48 | `sahtra/project-scaffold`, `sahtra/player-data`, `sahtra/account-scenes`, `sahtra/account-auth`, `sahtra/account-tests`, `sahtra/images`, `sahtra/account-options`, `sahtra/player-updates` | [#21](https://github.com/LiNQ3ST/clash-of-claws/pull/21), [#23](https://github.com/LiNQ3ST/clash-of-claws/pull/23), [#26](https://github.com/LiNQ3ST/clash-of-claws/pull/26), [#32](https://github.com/LiNQ3ST/clash-of-claws/pull/32), [#36](https://github.com/LiNQ3ST/clash-of-claws/pull/36), [#45](https://github.com/LiNQ3ST/clash-of-claws/pull/45), [#49](https://github.com/LiNQ3ST/clash-of-claws/pull/49), [#53](https://github.com/LiNQ3ST/clash-of-claws/pull/53) | Custom reusable FXML component      | complete |
| 2 — Admin & Arenas        | Nabiha Fatima    | nfatima-csumb      | #13, #14, #15, #16, #40  | `nabiha/admin-arena-skeleton`, `nabiha/admin-arena-skeleton`                                                                                                                                | [#29](https://github.com/LiNQ3ST/clash-of-claws/pull/29), [#41](https://github.com/LiNQ3ST/clash-of-claws/pull/41), [#54](https://github.com/LiNQ3ST/clash-of-claws/pull/54)                                                                                                                                                                                                                                                                                                   | Confirmation and Validation Dialogs | complete |
| 3 — Battle Engine         | Quinton Nisonger | QuintonScripts     | #1, #2, #3, #4, #22      | `quinton/battle-skeleton`, `quinton/shared-battle-logic`, `quinton/wild-battles`, `quinton/arena-battles`, `quinton/battle-testfx`                                                          | [#27](https://github.com/LiNQ3ST/clash-of-claws/pull/27), [#33](https://github.com/LiNQ3ST/clash-of-claws/pull/33), [#46](https://github.com/LiNQ3ST/clash-of-claws/pull/46), [#52](https://github.com/LiNQ3ST/clash-of-claws/pull/52), [#55](https://github.com/LiNQ3ST/clash-of-claws/pull/55)                                                                                                                                                                               | Extra TestFX scene tests            | complete |
| 4 — Marketplace & Trading | Todd Gonzales    | toddgonzales-xg    | #9, #10, #11, #12        | `todd/trader`, `todd/trader-scene`, `todd/trader-service`, `todd/trader-notifications-testfx`, `todd/ui-design-change`, `toddgonzales-xg-patch-2`                                           | [#25](https://github.com/LiNQ3ST/clash-of-claws/pull/25), [#37](https://github.com/LiNQ3ST/clash-of-claws/pull/37), [#38](https://github.com/LiNQ3ST/clash-of-claws/pull/38), [#43](https://github.com/LiNQ3ST/clash-of-claws/pull/43), [#50](https://github.com/LiNQ3ST/clash-of-claws/pull/50), [#51](https://github.com/LiNQ3ST/clash-of-claws/pull/51),                                                                                                                    | Notifications / alerts              | complete |
| 5 — Creature Roster       | Luke McCormick   | lumccormick-collab | #17, #18, #19            | `luke/creature-skeleton`, `luke/creatureDex`, `luke/starterCat`, `luke/data-binding`, `luke/creature-png`, `luke/cleanup`                                                                   | [#28](https://github.com/LiNQ3ST/clash-of-claws/pull/28), [#34](https://github.com/LiNQ3ST/clash-of-claws/pull/34), [#35](https://github.com/LiNQ3ST/clash-of-claws/pull/35), [#39](https://github.com/LiNQ3ST/clash-of-claws/pull/39), [#42](https://github.com/LiNQ3ST/clash-of-claws/pull/42), [#47](https://github.com/LiNQ3ST/clash-of-claws/pull/47)                                                                                                                     | Data binding                        | complete |


_Status values: planned · in-progress · complete_

## Slice Implementation Progress

### Slice 1 — Accounts
- Added persistent player accounts with registration and login.
- Added account validation and authentication.
- Added starter-cat routing for players without an active cat.
- Added account options, logout, password update, and account deletion.
- Added persistent currency and active-cat state.
- Added reusable credential FXML.
- Added DAO, logic, and TestFX coverage.

### Slice 2 — Admin & Arenas
- Added the player-facing Arena selection scene.
- Added Easy, Medium, and Hard Arena options with distinct rewards.
- Added active-cat health validation before Arena challenges.
- Integrated Arena challenges with the shared `BattleEngine`.
- Added Arena-specific reward handling and navigation.
- Added confirmation and validation feedback.
- Added Arena DAO and UI coverage.

### Slice 3 — Battle Engine
- Integrated Wild and Arena battles with the shared `BattleEngine`.
- Added attack, healing, catching, escape, victory, and defeat flows.
- Added inventory use during battle.
- Added Wild capture persistence to creature storage.
- Added Arena-specific action restrictions and rewards.
- Added battle-result and health persistence.
- Added battle UI and TestFX coverage.

### Slice 4 — Marketplace & Trading
- Added the in-game Trader and persistent Trader inventory.
- Added item purchasing with quantity, stock, and currency validation.
- Added healing and catching items to player inventory.
- Added owned-cat selling and sale-value calculation.
- Prevented selling the active cat.
- Added transaction rollback for failed purchases and sales.
- Added notifications, validation messages, and TestFX coverage.

### Slice 5 — Creature Roster
- Added starter-cat selection.
- Added persistent player-owned cat data.
- Added Party, Storage, and CatDex scenes.
- Added moving cats between Party and Storage.
- Added persistent active-cat selection.
- Added Rest Party to restore current party health.
- Added JavaFX data binding and Creature Roster UI coverage.

## WILL NOT DO

- **Slice 1 — Accounts, Sahtra Green:** User profiles and roles.
- **Slice 2 — Admin & Arenas, Nabiha Fatima:** Tournaments and seasonal arenas.
- **Slice 3 — Battle Engine, Quinton Nisonger:** Type-effectiveness matrix, status effects, and advanced animations.
- **Slice 4 — Marketplace & Trading, Todd Gonzales:** Auctions, trade history, and REST imports.
- **Slice 5 — Creature Roster, Luke McCormick:** Creature evolutions.

## Code Review Log
| PR  | Author           | Human reviewer(s)                | AI review (link)                                                                                        | Outcome   |
|-----|------------------|----------------------------------|---------------------------------------------------------------------------------------------------------|-----------|
| #27 | Quinton Nisonger | Sahtra Green                     | -                                                                                                       | merged    |
| #28 | Luke McCormick   | Todd Gonzales                    | -                                                                                                       | merged    |
| #23 | Sahtra Green     | Quinton Nisonger                 | -                                                                                                       | merged    |
| #25 | Todd Gonzales    | Luke McCormick                   | -                                                                                                       | merged    |
| #26 | Sahtra Green     | Nabiha Fatima, Quinton Nisonger  | -                                                                                                       | merged    |
| #29 | Nabiha Fatima    | Todd Gonzales                    | -                                                                                                       | merged    |
| #32 | Sahtra Green     | Quinton Nisonger                 | -                                                                                                       | merged    |
| #33 | Quinton Nisonger | Luke McCormick                   | -                                                                                                       | merged    |
| #34 | Luke McCormick   | Quinton Nisonger                 | -                                                                                                       | merged    |
| #35 | Luke McCormick   | Sahtra Green                     | -                                                                                                       | merged    |
| #36 | Sahtra Green     | Luke McCormick, Quinton Nisonger | [AI review + adjudication](https://github.com/LiNQ3ST/clash-of-claws/pull/36#issuecomment-5275818143)   | merged    |
| #37 | Todd Gonzales    | Quinton Nisonger                 | -                                                                                                       | merged    |
| #38 | Todd Gonzales    | Quinton Nisonger                 | -                                                                                                       | merged    |
| #39 | Luke McCormick   | Sahtra Green                     | -                                                                                                       | merged    |
| #41 | Nabiha Fatima    | Quinton Nisonger, Sahtra Green   | [AI review and adjudication](https://github.com/LiNQ3ST/clash-of-claws/pull/41#issuecomment-5275512493) | merged    |
| #42 | Luke McCormick   | Sahtra Green                     | [AI review and adjudication](https://github.com/LiNQ3ST/clash-of-claws/pull/42)                         | merged    |
| #43 | Todd Gonzales    | Sahtra Green                     | [AI review and adjudication](https://github.com/LiNQ3ST/clash-of-claws/pull/43#issuecomment-5276036189) | merged    |
| #45 | Sahtra Green     | Todd Gonzales                    | -                                                                                                       | merged    |
| #46 | Quinton Nisonger | Sahtra Green, Todd Gonzales      | [AI review + adjudication](https://github.com/LiNQ3ST/clash-of-claws/pull/46#issuecomment-5275623986)   | merged    |
| #47 | Luke McCormick   | Sahtra Green                     | -                                                                                                       | merged    |
| #49 | Sahtra Green     | Todd Gonzales                    | -                                                                                                       | merged    |
| #50 | Todd Gonzales    | Nabiha Fatima, Sahtra Green      | -                                                                                                       | merged    |
| #51 | Todd Gonzales    | Nabiha Fatima                    | -                                                                                                       | merged    |
| #52 | Quinton Nisonger | Sahtra Green                     | -                                                                                                       | merged    |
| #53 | Sahtra Green     | Nabiha Fatima                    | -                                                                                                       | merged    |
| #54 | Nabiha Fatima    | Todd Gonzales                    | -                                                                                                       | merged    |
| #55 | Quinton Nisonger | Todd Gonzales                    | -                                                                                                       | merged    |
| #56 | Todd Gonzales    | Sahtra Green                     | -                                                                                                       | merged    |
| #57 | Sahtra Green     |                                  | -                                                                                                       | in review |

## AI Usage Log

### AI-Drafted Tests
- **Sahtra Green — Accounts**
    - [AI-generated test commit](https://github.com/LiNQ3ST/clash-of-claws/pull/36/changes/116ce7ad8b1d0afbce8780dad7a418411d2c7a79)
- **Luke McCormick — Creature Roster**
    - [PR #47](https://github.com/LiNQ3ST/clash-of-claws/pull/47).
- **Quinton Nisonger — Battle Engine**
    - [PR #55](https://github.com/LiNQ3ST/clash-of-claws/pull/55).
- **Nabiha Fatima — Admin & Arenas**
    - [PR #29](https://github.com/LiNQ3ST/clash-of-claws/pull/29).
- **Todd Gonzales — Marketplace & Trading**
    - [PR #38](https://github.com/LiNQ3ST/clash-of-claws/pull/38).

### AI Code Reviews
- **Sahtra Green — Accounts**
    - [PR #36 AI review and adjudication](https://github.com/LiNQ3ST/clash-of-claws/pull/36#issuecomment-5275818143)
- **Luke McCormick — Creature Roster**
    - [PR #42 AI review and adjudication](https://github.com/LiNQ3ST/clash-of-claws/pull/42)
- **Quinton Nisonger — Battle Engine**
    - [PR #46 AI review and adjudication](https://github.com/LiNQ3ST/clash-of-claws/pull/46#issuecomment-5275623986)
- **Nabiha Fatima — Admin & Arenas**
    - [PR #41 AI review and adjudication](https://github.com/LiNQ3ST/clash-of-claws/pull/41#issuecomment-5275512493)
- **Todd Gonzales — Marketplace & Trading**
    - [PR #43 AI review and adjudication](https://github.com/LiNQ3ST/clash-of-claws/pull/43#issuecomment-5276036189)

### AI-Assisted Visual Assets
- Used AI image generation to create the Town Hub background artwork used in the final application.

## Extra Credit Log
| Item         | Who          | Evidence (Issue/PR) |
|--------------|--------------|---------------------|
| None claimed | Team L!NQ3ST | —                   |

## Build & Run

**Requirements**

- JDK 25
- Git

The Gradle Wrapper is included. JavaFX, SQLite, H2, JUnit, and TestFX dependencies are
managed automatically by Gradle.

### Clone & Build

```bash
git clone https://github.com/LiNQ3ST/clash-of-claws.git
cd clash-of-claws
./gradlew build
```

Windows users can use `gradlew.bat` in place of `./gradlew`.

### Run

```bash
./gradlew run
```

The application opens to the Clash of Claws login screen. No manual database setup is
required; the local `clash-of-claws.db` SQLite database is initialized automatically.

### Tests

Run the standard test suite:

```bash
./gradlew test
```

Run the TestFX UI suite:

```bash
./gradlew testfx
```

TestFX opens and interacts with the JavaFX UI, so it may temporarily control the mouse and
keyboard while running.

> **macOS:** TestFX requires Accessibility permission for the terminal or IDE running Gradle:
> **System Settings → Privacy & Security → Accessibility**.
