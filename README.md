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

CST 338 Project 2 — Team **L!NQ3ST**.

## Team & Slice Ownership
| Slice | Owner        | GitHub username | Issues         | Branch(es)                                                                                   | PR(s) | Enhancement chosen | Status |
|-------|--------------|-----------------|----------------|----------------------------------------------------------------------------------------------|-----|--------------------|--------|
| 1 — Accounts | Sahtra Green | SahtraRG | #5, #6, #7, #8 | `sahtra/player-data`, `sahtra/account-scenes`, `sahtra/account-auth`, `sahtra/account-tests` | [#23](https://github.com/LiNQ3ST/clash-of-claws/pull/23), [#26](https://github.com/LiNQ3ST/clash-of-claws/pull/26), [#32](https://github.com/LiNQ3ST/clash-of-claws/pull/32) | Custom reusable FXML component | in-progress |
| 2 — Admin & Arenas| Nabiha Fatima |nfatima-csumb | #13, #14, #15, #16 | `nabiha/admin-arena-skeleton`                                                                | [#29](https://github.com/LiNQ3ST/clash-of-claws/pull/29) | Confirmation and Validation Dialogs | in-progress |
| 3 — Battle Engine | Quinton Nisonger | QuintonScripts | #1, #2, #3, #4 | `quinton/battle-skeleton`, `quinton/shared-battle-logic`                                     | [#27](https://github.com/LiNQ3ST/clash-of-claws/pull/27) | Extra TestFX scene tests | in-progress |
| 4 — Marketplace & Trading | Todd Gonzales | toddgonzales-xg | #9, #10, #11, #12 | `todd/trader`                                                                                | [#25](https://github.com/LiNQ3ST/clash-of-claws/pull/25) | Notifications / alerts | in-progress|
| 5- Creature Roster| Luke McCormick |lumccormick-collab | #17, #18, #19  | `luke/creature-skeleton`, `luke/creatureDex`, `luke/starterCat`                               | [#28](https://github.com/LiNQ3ST/clash-of-claws/pull/28) | Data binding | in-progress |


_Status values: planned · in-progress · complete_

## WILL NOT DO (declared scope cuts)
_Slices and beyond-scope items we are consciously NOT building. Move an item to a tracked
Issue if the team later decides to attempt it for extra credit._

- <Slice 1>: Sahtra Green, Accounts: User profiles and roles - out of scope.
- <Slice 2>: Nabiha Fatima, Admin & Arenas: Tournaments and seasonal areas — out of scope.
- <Slice 3>: Quinton Nisonger, Battle Engine: Type-effectiveness matrix, status effects, animations — out of scope.
- <Slice 4>: Todd Gonzales, Marketplace & Trading: Auctions, trade history, and REST imports — out of scope.
- <Slice 5>: Luke McCormick, Creature Roster: Building Evolutions - out of scope.

## Code Review Log
| PR  | Author           | Human reviewer(s)                | AI review (link) | Outcome   |
|-----|------------------|----------------------------------|------------------|-----------|
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
| #36 | Sahtra Green     |                                  | -                | in review |
## AI Usage Log
- **AI-drafted tests:** <link to TESTING.md / commit> — per owner.
- **AI code reviews:** <PR link + adjudication note> — per owner.

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