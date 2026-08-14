<!--
CST 338 Project 2 — README template.
Copy this file into the ROOT of your team's repository as README.md and keep it current.
This README is your project dashboard: it is the first thing the instructor reads when
grading, and a working, up-to-date README is part of your integration score.

GitHub Issues are your LIVE tracker — every slice task, enhancement, and scope decision is
an Issue: assigned to its owner, labeled (slice-1, testing, enhancement, will-not-do,
extra-credit), and closed by a PR via "Closes #N". The tables below link into those Issues
and PRs. Replace every <placeholder> and delete this comment before you submit.
-->

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
| 4 — Marketplace & Trading | Todd Gonzales    | toddgonzales-xg    | #9, #10, #11, #12        | `todd/trader`, `todd/trader-scene`, `todd/trader-service`, `todd/trader-notifications-testfx`, `todd/ui-design-change`                                                                      | [#25](https://github.com/LiNQ3ST/clash-of-claws/pull/25), [#37](https://github.com/LiNQ3ST/clash-of-claws/pull/37), [#38](https://github.com/LiNQ3ST/clash-of-claws/pull/38), [#43](https://github.com/LiNQ3ST/clash-of-claws/pull/43), [#50](https://github.com/LiNQ3ST/clash-of-claws/pull/50), [#51](https://github.com/LiNQ3ST/clash-of-claws/pull/51),                                                                                                                    | Notifications / alerts              | complete |
| 5- Creature Roster        | Luke McCormick   | lumccormick-collab | #17, #18, #19            | `luke/creature-skeleton`, `luke/creatureDex`, `luke/starterCat`, `luke/data-binding`, `luke/creature-png`, `luke/cleanup`                                                                   | [#28](https://github.com/LiNQ3ST/clash-of-claws/pull/28), [#34](https://github.com/LiNQ3ST/clash-of-claws/pull/34), [#35](https://github.com/LiNQ3ST/clash-of-claws/pull/35), [#39](https://github.com/LiNQ3ST/clash-of-claws/pull/39), [#42](https://github.com/LiNQ3ST/clash-of-claws/pull/42), [#47](https://github.com/LiNQ3ST/clash-of-claws/pull/47)                                                                                                                     | Data binding                        | complete |


_Status values: planned · in-progress · complete_

## Battle Engine Issue Progress

### Issue #2 — Wild Battle Integration
- Integrated Wild battles with the shared `BattleEngine`.
- Added Wild battle attack, escape, healing, catching, and victory/defeat flows.
- Added player inventory support to the Battle Bag.
- Added healing items with HP caps and item consumption.
- Added catching items using the marketplace `Toy Mouse` and `Tuna Can`.
- Added HP-based catch chance bonuses for weakened Wild cats.
- Added successful capture persistence to creature storage.
- Added Wild battle rewards and persistence.
- Added unit and UI coverage for Wild battle behavior.

**PR:** [#46 — Quinton/wild battles](https://github.com/LiNQ3ST/clash-of-claws/pull/46)

### Issue #3 — Arena Battle Integration
- Integrated Arena battles with the shared `BattleEngine`.
- Added Arena-specific battle behavior and action restrictions.
- Disabled Catch and Run behavior for Arena battles.
- Added Arena victory rewards and battle outcome persistence.
- Added Arena defeat handling and return-to-town flow.
- Added Arena-focused unit tests.

**Deferred:** Per-player Arena completion tracking will be handled separately because the required completion-status system is not currently part of the battle implementation.

### Issue #4 — Battle UI & Final Integration
- Added in-battle party cat switching.
- Switching updates the active cat's sprite, HP, and available abilities.
- Added opponent turns after switching and item use.
- Improved battle dialogue progression and the full-size clickable message area.
- Updated the battle layout and action menus for the shared Wild/Arena scene.
- Added and updated battle UI/TestFX coverage.
- Added a temporary opponent-click debug shortcut for testing battle inventory items.
- Cleaned up integration issues from the merged Wild and Arena battle branches.


## WILL NOT DO (declared scope cuts)
_Slices and beyond-scope items we are consciously NOT building. Move an item to a tracked
Issue if the team later decides to attempt it for extra credit._

- <Slice 1>: Sahtra Green, Accounts: User profiles and roles - out of scope.
- <Slice 2>: Nabiha Fatima, Admin & Arenas: Tournaments and seasonal areas — out of scope.
- <Slice 3>: Quinton Nisonger, Battle Engine: Type-effectiveness matrix, status effects, animations — out of scope.
- <Slice 4>: Todd Gonzales, Marketplace & Trading: Auctions, trade history, and REST imports — out of scope.
- <Slice 5>: Luke McCormick, Creature Roster: Building Evolutions - out of scope.

## Code Review Log
| PR | Author           | Human reviewer(s)                | AI review (link) | Outcome   |
|--|------------------|----------------------------------|------------------|-----------|
| #27 | Quinton Nisonger | Sahtra Green                     | -                | merged    |
| #28 | Luke McCormick   | Todd Gonzales                    | -                | merged    |
| #23 | Sahtra Green     | Quinton Nisonger                 | -                | merged    |
| #25 | Todd Gonzales    | Luke McCormick                   | -                | merged    |
| #26 | Sahtra Green     | Nabiha Fatima, Quinton Nisonger  | -                | merged    |
| #29 | Nabiha Fatima    | Todd Gonzales                    | -                | merged    |
| #32 | Sahtra Green     | Quinton Nisonger                 | -                | merged    |
| #33 | Quinton Nisonger | Luke McCormick                   | -                | merged    |
| #34 | Luke McCormick   | Quinton Nisonger                 | -                | merged    |
| #35 | Luke McCormick   | Sahtra Green                     | -                | merged    |
| #36 | Sahtra Green     | Luke McCormick, Quinton Nisonger | -                | merged    |
| #37 | Todd Gonzales    | Quinton Nisonger                 | -                | merged    |
| #38 | Todd Gonzales    | Quinton Nisonger                 | -                | merged    |
| #39 | Luke McCormick   | Sahtra Green                     | -                | merged    |
| #41 | Nabiha Fatima    | Quinton Nisonger, Sahtra Green   | -                | in review |
| #42 | Luke McCormick   | Sahtra Green                     | -                | merged    |
|  | Sahtra Green     |                                  | -                | in review |
| #33 | Quinton Nisonger | Luke McCormick | - | merged |
| #46 | Quinton Nisonger | Sahtra Green, Todd Gonzales | [AI review + adjudication](PASTE-DIRECT-COMMENT-LINK) | merged |
| #52 | Quinton Nisonger | Sahtra Green | - | merged |

## AI Usage Log
- **AI-drafted tests:** <link to TESTING.md / commit> — per owner.
- **AI code reviews:** <PR link + adjudication note> — per owner.
- **AI code reviews:** [PR #46 — Wild Battle AI review and adjudication](PASTE-DIRECT-COMMENT-LINK) — Quinton Nisonger.

## Extra Credit Log
| Item | Who | Evidence (Issue/PR) |
|------|-----|---------------------|
| Built Slice 5 | | #34 |

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
