---
layout: default.md
title: "User Guide"
pageNav: 3
---

# Tuto User Guide

**Tuto** is a desktop app that helps **parents manage a list of freelance tutors** for their children. It is optimised for users who prefer typing commands over clicking through menus, while still providing a clean visual interface to view tutor information at a glance.

<box type="info" seamless>

**Who is this guide for?**
This guide is written for parents who are comfortable using a keyboard and want to manage tutor contacts efficiently. No prior technical experience is required — if you can open a terminal and type commands, you are ready to use Tuto.

</box>

---

## Table of Contents

- [Tuto User Guide](#tuto-user-guide)
  - [Table of Contents](#table-of-contents)
  - [Quick Start](#quick-start)
    - [Step 1 — Install Java](#step-1-install-java)
    - [Step 2 — Download Tuto](#step-2-download-tuto)
    - [Step 3 — Launch Tuto](#step-3-launch-tuto)
    - [Step 4 — Try Your First Commands](#step-4-try-your-first-commands)
  - [Understanding the Interface](#understanding-the-interface)
  - [Command Basics](#command-basics)
    - [Notes on Command Format](#notes-on-command-format)
    - [Understanding List Indices](#understanding-list-indices)
    - [Duplicate Tutors are Not Allowed](#duplicate-tutors-are-not-allowed)
  - [Commands](#commands)
    - [Viewing Help : `help`](#viewing-help-help)
    - [Adding a Tutor : `add`](#adding-a-tutor-add)
      - [Parameters](#parameters)
      - [Constraints](#constraints)
      - [Examples](#examples)
    - [Editing a Tutor Profile : `edit`](#editing-a-tutor-profile-edit)
      - [Parameters](#parameters-2)
      - [Constraints](#constraints-2)
      - [Examples](#examples-2)
      - [Invalid Usage](#invalid-usage)
    - [Deleting a Tutor : `delete`](#deleting-a-tutor-delete)
      - [Parameters](#parameters-3)
      - [Examples](#examples-3)
    - [Finding Tutors : `find`](#finding-tutors-find)
      - [Prefixes](#prefixes)
      - [Search Modes](#search-modes)
      - [How Matching Works](#how-matching-works)
      - [Examples](#examples-4)
      - [Invalid Usage](#invalid-usage-1)
    - [Sorting the Tutor List : `sort`](#sorting-the-tutor-list-sort)
      - [Parameters](#parameters-4)
      - [Examples](#examples-5)
      - [Invalid Usage](#invalid-usage-2)
    - [Listing All Tutors : `list`](#listing-all-tutors-list)
    - [Clearing All Entries : `clear`](#clearing-all-entries-clear)
    - [Exiting the Program : `exit`](#exiting-the-program-exit)
  - [Data Management](#data-management)
  - [FAQ](#faq)
  - [Known Issues](#known-issues)
  - [Command Summary](#command-summary)

---

## Quick Start

Follow these steps to get Tuto running on your computer in under 5 minutes.

### Step 1 — Install Java

Tuto requires **Java 17 or above**.

- **Windows / Linux:** Download Java 17 from [Adoptium](https://adoptium.net/).
- **Mac:** Follow the exact installation steps [here](https://se-education.org/guides/tutorials/javaInstallationMac.html), as the standard Mac JDK may not be compatible.

To verify your Java version, open a terminal and run:

```
java -version
```

You should see `17` or higher in the output.

### Step 2 — Download Tuto

Download the latest `tuto.jar` file from the [Tuto releases page](https://github.com/AY2526S2-CS2103T-T15-3/tp/releases).

Move the file into a dedicated folder (e.g. `~/tuto/`). This folder will store your data going forward.

### Step 3 — Launch Tuto

1. Open a terminal (Command Prompt on Windows, Terminal on Mac/Linux).
2. Navigate to the folder containing `tuto.jar`:
    ```
    cd ~/tuto
    ```
3. Input the following command to the terminal to start the application:
    ```
    java -jar tuto.jar
    ```

A window similar to the one below should appear within a few seconds.

![Tuto UI on first launch](images/empty_UI.png)

### Step 4 — Try Your First Commands

Type a command into the **Command Box** at the bottom and press **Enter** to run it. Here are a few to try:

| What you want to do       | Command to type                                                     |
| ------------------------- | ------------------------------------------------------------------- |
| View all tutors           | `list`                                                              |
| Add a new tutor           | `add n/Jane Smith p/91234567 e/jane@example.com s/Mathematics r/60` |
| Find tutors by subject    | `find s/Mathematics`                                                |
| Sort tutors by name (A–Z) | `sort name asc`                                                     |
| Delete the 1st tutor      | `delete 1`                                                          |
| Open help                 | `help`                                                              |
| Exit the app              | `exit`                                                              |

<box type="tip" seamless>

**Tip:** The [Command Summary](#command-summary) at the bottom of this guide is a handy reference once you are familiar with the commands.

</box>

---

## Understanding the Interface

![Annotated UI](images/user_interface_anno.png)

Tuto's interface has three main areas:

- **Tutor Profile List (Left)** — shows all Tutor Profiles saved
- **Result Display (Top Right)** — shows feedback after each command (success or error messages and records retrieved from searches)
- **Command Box (Bottom Right)** — where you type your commands

Each tutor card in the panel shows the tutor's name, phone number, email, subject, and hourly rate. Address and tags (if any) appear as labels on the card.

---

## Command Basics

### Notes on Command Format

<box type="info" seamless>

The following conventions apply to all commands in this guide:

- Words in `UPPER_CASE` are **values you supply**.<br>
  e.g. in `add n/NAME`, replace `NAME` with the tutor's actual name: `add n/John Doe`.

- Items in `[square brackets]` are **optional**.<br>
  e.g. `[a/ADDRESS]` means the address field can be left out.

- Items followed by `…` can be **used multiple times**.<br>
  e.g. `[t/TAG]…` allows zero, one, or more tags: `t/home`, `t/experienced t/recommended`.

- **Parameters can be given in any order.**<br>
  e.g. `n/NAME p/PHONE` and `p/PHONE n/NAME` are both valid.

- **Extra parameters are ignored** for commands that take none (such as `help`, `list`, `exit`, and `clear`).<br>
  e.g. `help 123` runs as `help`.

</box>

<box type="warning" seamless>

**Note for PDF users:** If you copy commands from a PDF, line breaks may introduce unexpected spaces. Re-type the command manually if it does not execute as expected.

</box>

---

### Understanding List Indices

Commands such as `edit` and `delete` use **`INDEX`**: the number shown directly next to the name of the tutor contact in the **Tutor List Panel**.

<box type="info" seamless>

**Indices refer to the overall Global List**
The index is the tutor's current position in your **full list of all saved tutors**.
When you search for a tutor (e.g., using `find`), the index numbers **do not restart from 1**. Instead, Tuto just hides the tutors that don't match, so the remaining tutors keep their numbers from the full list (for example, your search results might show tutors numbered 4, 9, and 12). The index shown during a search and the index shown on the full list are exactly the same.

</box>

<box type="warning" seamless>

**Displayed indices change after `sort` and `delete`, and may differ during `find`**

- After a **`sort`** command, tutors are reordered in the global list, so the same person may appear at a **different** index than before.
- After a **`delete`** command, the list becomes shorter and tutors below the removed row **shift up**, so their indices are **renumbered** (what was “tutor 5” may become “tutor 4”).
- During a **`find`** command, never assume the first result is index 1. Always use the exact index displayed directly next to the name of the tutor contact in the current search results.

**Always search with Find or look at the Tutor List Panel again** before typing the next `edit` or `delete` command. Do not assume indices from an earlier step are still correct.

</box>

---

### Duplicate Tutors are Not Allowed

Tuto protects your data by ensuring no two tutor profiles share the exact same **Phone number** or **Email address**. 

<box type="warning" seamless>

**Duplicate Error:** If an `add` or `edit` command results in a clash with an existing phone number or email, the command will be rejected to prevent duplicate profiles.

</box>

---

## Commands

### Viewing Help : `help`

Displays a summary of available commands and their accepted parameter values.

**Format:** `help`

**Expected output:** A help window opens containing command syntax, constraints, and a link to the User Guide. The result display shows `✨  Opened help window.`

![Help window](images/helpMessage.png)

---

### Adding a Tutor : `add`

Adds a new Tutor Profile to Tuto.

![add message](images/addMessage.png)

**Format:** `add n/NAME p/PHONE_NUMBER e/EMAIL s/SUBJECT1 [s/SUBJECT2]... r/RATE [a/ADDRESS] [t/TAG]…`

---

#### Parameters

| Prefix | Field             | Required | Accepted values                                                                                        |
| ------ | ----------------- | -------- |--------------------------------------------------------------------------------------------------------|
| `n/`   | Name              | Yes      | Alphanumeric text + spaces allowed only, no special characters are allowed                             |
| `p/`   | Phone number      | Yes      | 8 digits for Singapore number, 10 digits excluding '+' for international number                        |
| `e/`   | Email             | Yes      | Valid email format (e.g. `user@example.com`)                                                           |
| `s/`   | Subject           | Yes      | Alphanumeric text + spaces (e.g. `Advanced Mathematics`, `Biology`)                                    |
| `r/`   | Hourly rate (SGD) | Yes      | 0 or any decimal value e.g. `25` or `25.50`) |
| `a/`   | Address           | No       | Any text (must not contain valid prefixes such as s/, as these will be interpreted as separate fields) |
| `t/`   | Tag               | No       | Alphanumeric text, no spaces, maximum 20 characters                                                  |

<box type="tip" seamless>

**Tip:**

1. Tags are powerful ways to organise Tutor Profiles. You can use multiple tags to provide more detail, e.g. `t/home` and `t/weekend`.
2. A person can have any number of tags (including 0) and multiple subjects.
3. Command parameters can be entered in any order.
4. You cannot repeat the same subject or tag value in one command, even if the casing is different (e.g. s/Math s/math or t/friend t/friend is rejected).
5. Rate must be zero or positive; decimals are allowed. Negative rates are rejected.
</box>

---

#### Constraints

Tuto validates all inputs to ensure data integrity. If you provide an invalid field, such as an incorrectly formatted phone number or email address, Tuto will reject the command and display a specific error message detailing the accepted format.

Additionally, Tuto protects your data by natively preventing duplicate contacts. See [Duplicate Tutors are Not Allowed](#duplicate-tutors-are-not-allowed) for more details.

---

#### Examples

**Adding with minimum required fields**

```
add n/ elizabeth chang p/ 82516782 e/ elizabeth@example.com s/ piano r/ 76
```

![add minimum required](images/add_min_req.png)

---

**Adding with optional fields (address and tags)**

```
add n/ gabrielle chee p/ 87429246 e/ gabrielle@example.com s/ computing r/ 85 a/ 8 napier road t/ prodigy
```

![add optionals](images/add_optional.png)

---

### Editing a Tutor Profile : `edit`

Updates one or more fields of an existing Tutor Profile.

![Edit command hero image](images/editMessage.png)

**Format:** `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [s/SUBJECT]… [r/RATE] [t/TAG]…`

---

#### Parameters

- `INDEX` refers to the number shown directly next to the name of the tutor contact in the current list. It must be a **positive integer** (1, 2, 3 …).
- **Accepted Prefixes:** Accepts the same prefixes (`n/`, `p/`, `e/`, `s/`, `r/`, `a/`, `t/`) as the `add` command.
- After a **`sort`** or **`delete`**, indices may no longer match what you saw earlier — see [Understanding List Indices](#understanding-list-indices).
- At least one field must be provided.
- Existing values are replaced with the new values you provide.

---

#### Constraints

<box type="warning" seamless>

- **Subjects are replaced, not added:** Any new subjects you provide will completely overwrite the tutor's existing subjects. Use multiple `s/` prefixes for multiple subjects (e.g. `edit INDEX s/Math s/English`). A single `s/Math Physics` value is **one** subject whose name contains a space, not two separate subjects.
- **At least one subject:** You cannot clear all subjects with a bare `s/` or leave a profile with no subjects. If a data file is damaged and a tutor has no subjects, use `edit` with at least one valid `s/SUBJECT` to fix the profile.
- **Tags are replaced, not added:** Any new tags you provide will completely overwrite the tutor's existing tags. To clear all tags, use an empty prefix: `t/`. To append a new tag, you must retype the existing ones.
- **Identical edits are accepted:** Editing a tutor with values they already have (e.g. changing their rate to the same rate they currently charge) will be accepted as a valid command without throwing an error.
- **No duplicates:** You cannot edit a profile to have the exact same phone number or email address as another tutor. See [Duplicate Tutors are Not Allowed](#duplicate-tutors-are-not-allowed).

</box>

---

#### Examples

**Editing multiple fields at once**

```
edit 3 e/ qingrong@example.com t/ best
```

Updates the email and overwrites any existing tags of the 3rd tutor in the list.

**Removing tags**

```
edit 2 t/
```

Removes all of their tags of the 2nd tutor.

**Updating subject and rate**

```
edit 1 s/Physics r/30
```

Changes the 1st tutor's subjects to Physics only (replacing any previous subjects) and rate to $30/hr.

```
edit 2 s/Math s/English
```

Sets the 2nd tutor's subjects to Math and English only (replacing any previous subjects).

**Expected output:**

![edit_expected](images/edit_expected.png)

---

#### Invalid Usage

Tuto provides clear error messages if an `edit` command cannot be executed:

- **Duplicate Error:** "Another tutor already uses this phone number" *or* "Another tutor already uses this email" (The exact message will specify which field caused the clash).
- **Invalid Input Error:** "At least one field to edit must be provided." (Shown when the edit command is missing parameter fields).

---

### Deleting a Tutor : `delete`

Permanently removes a Tutor Profile from Tuto.

![Delete command hero image](images/deleteMessage.png)

**Format:** `delete INDEX`

---

#### Parameters

- `INDEX` must be a **positive integer** indicating the number shown directly next to the name of the tutor contact in the currently displayed list.
- After you delete someone, **every tutor below that row moves up** and gets a new index. After a **`sort`**, positions change too. See [Understanding List Indices](#understanding-list-indices).

<box type="warning" seamless>

**Caution:** Deletion is permanent and cannot be undone. Double-check the index before running this command.

</box>

---

#### Examples

**Deleting from the full list**

```
delete 2
```

Deletes the Tutor profile with index 2.

**Deleting after searching**

```
find s/Biology
delete [any index that appears]
```

Deletes the Tutor Profile matching the specified index.

<box type="warning" seamless>

**Verify the index:** Do not assume the first matching tutor in your search results is at index 1. Because Tuto uses **global list indexing**, search results maintain their original list numbers (for example, the first result might actually be index 5). Be sure to look at the exact index number returned directly next to the name of the tutor contact in the search results, and use that specific index in your `delete` command.
</box>

**Expected output:**

![Delete success output](images/delete_success.png)

---

### Finding Tutors : `find`

Search for tutors by keyword, name, subject, or hourly rate — or combine them for precise filtering.

![Find message](images/find_math_S_advancedmath.png)

---

#### Prefixes

| Prefix            | Filters by     | Behaviour                                                                                     |
| ----------------- | -------------- | --------------------------------------------------------------------------------------------- |
| `n/NAME_KEYWORDS` | Name           | Prefix match · Case-insensitive · Space-separate multiple keywords (OR logic) · Only **one `n/`** allowed |
| `s/SUBJECT`       | Subject taught | Prefix match · Case-insensitive · Multiple `s/` allowed (AND logic)                           |
| `r/RATE`          | Hourly rate    | Exact, range, or comparison match · Only **one `r/`** allowed        |
| `t/TAG`           | Tag            | Matches tag(s) · Case-insensitive · Multiple `t/` allowed (AND logic)                         |

<box type="tip" seamless>

**Tip:** Spaces after prefixes are optional — `find n/John` and `find n/ John` both work.

</box>

---

#### Search Modes

| Mode                 | Syntax                                                                                 | Returns                                                                |
| -------------------- | -------------------------------------------------------------------------------------- | ---------------------------------------------------------------------- |
| **General Search**   | `find KEYWORD [MORE_KEYWORDS]` (case-insensitive)                                      | Tutors where **any** attribute has a word starting with any keyword    |
| **Filtering**        | `find [PREFIXES]` (case-insensitive)                                                   | Tutors matching **all** prefix conditions                              |
| **General + Filter** | `find KEYWORD [MORE_KEYWORDS] [PREFIXES]` (case-insensitive; keywords before prefixes) | Tutors matching **any** keyword, narrowed by **all** prefix conditions |

<box type="warning" seamless>

**Keyword and Prefix Rules**

1. **Only exact lowercase prefixes (`n/`, `s/`, `r/`, `t/`) act as filters.** Unrecognised or uppercase prefixes (e.g., `N/`, `p/`) are treated as general keywords to search across all fields.
2. **Keywords MUST be typed before prefixes.** Any keywords typed after a valid prefix will be absorbed as part of that prefix's value.
    - ✅ `find x/abc n/Alex` — searches keyword `x/abc`, filters name by `Alex`
    - ❌ `find n/Alex x/abc` — Tuto reads the name as `"Alex x/abc"`


</box>

---

#### How Matching Works

**Name (`n/`)** — OR logic across keywords

- `n/Ed` matches "Eddy", "Edward", "Eddie" etc.
- `n/Dar Vic` matches tutors named "Dar…" **or** "Vic…" e.g. "Daren", "Victoria"

**Subject (`s/`)** — AND logic across prefixes

- `s/Mat` matches "Math", "Mathematics"
- `s/Math s/Chemistry` returns tutors teaching Math **and** Chemistry

**Rate (`r/`)** — supports four formats

| Format | Example         | Matches                                                 |
| ------ | --------------- | ------------------------------------------------------- |
| Exact  | `r/RATE`        | Tutors charging exactly `RATE`                          |
| Range  | `r/RATE1-RATE2` | Tutors charging between `RATE1` and `RATE2` (inclusive) |
| Above  | `r/>RATE`       | Tutors charging more than `RATE`                        |
| Below  | `r/<RATE`       | Tutors charging less than `RATE`                        |

For **exact** (`r/RATE`) and **range** (`r/RATE1-RATE2`), each numeric token must be **non-negative**, with an optional decimal part (e.g. `40.5`). For ranges, `RATE1` must be less than or equal to `RATE2`.

For **below** (`r/<RATE`), `RATE` must be **greater than zero** (`r/<0` and `r/<-1` is rejected); decimals are allowed.

For **above** (`r/>RATE`), `RATE` may be **negative, zero, or positive** (optional decimal part). Tutor rates stored in Tuto are never negative, so a negative threshold (e.g. `r/>-1`) matches anyone with a rate greater than that value.

**Mixed prefixes** — all conditions must be met

- `find n/Alex r/40 s/Math` → named "Alex…", rate $40, teaches Math

---

#### Examples

**General keyword search**

```
find math
```

Returns all tutors containing "math" in any field (name, subject, tag, address, etc.).

**Filtering by prefixes**

```
find n/Dar Vic
```

- `find n/Dar Vic` returns tutors whose name starts with "Dar..." **or** "Vic..." (OR logic is only for keywords in the `n/` prefix).

![Result for 'find n/Dar Vic'](images/find_N_Dar_Vic.png)

---

```
find s/Physics r/>40
```

- `find s/Physics r/>40` returns tutors teaching Physics **and** charging more than $40/hr (all other fields and combinations use AND logic).

![Result for 'find s/Physics r/>40'](images/find_S_phy_R_40.png)

---

**Combined search (Keyword + Filters)**

```
find n/Qi r/70 s/History
```

Narrows down results by combining multiple prefix conditions to find tutors named "Qi...", charging exactly $70, and teaching History.

![Result for 'find n/Qi r/70 s/History'](images/find_N_qi_R_70_S_history.png)

---

Matching tutors appear in the right panel. If no matches are found:

![Result for 'find spanish'](images/find_spanish.png)

<box type="tip" seamless>

**Tip:** After a `find`, run `list` to return to the full tutor list when running on CLI.

</box>

---

#### Invalid Usage

<box type="warning" seamless>

**Only ONE `n/` and one `r/` are allowed per command.**

| ❌ Invalid Example   | Reason                            |
| -------------------- | --------------------------------- |
| `find`               | Keywords and/or Prefixes required |
| `find n/Alice n/Bob` | Multiple `n/` not allowed         |
| `find r/16 r/17`     | Multiple `r/` not allowed         |

Tuto will reject invalid inputs and provide clear textual error messages to explain the issue:

- **Missing input:** "Invalid command format! find: Finds all tutors whose..."
- **Multiple names or rates:** "Multiple values specified for the following single-valued field(s): [n/ or r/]" (The exact message will dynamically specify whether the `n/` or `r/` prefix caused the error).

</box>


---

### Sorting the Tutor List : `sort`

Changes the **order** of tutors in the Tutor List Panel. Sorting is by **name** or **hourly rate** only; it does not remove or hide tutors.

<box type="info" seamless>

**Note:** Sorting is **temporary**! When you close and reopen Tuto, the list will always reset to its default original order (the exact order in which you added the tutors).

</box>

![Sort command hero image](images/sort_hero.png)

**Format:** `sort FIELD ORDER`

---

#### Parameters

| Part    | Meaning         | Allowed values                                              |
| ------- | --------------- | ----------------------------------------------------------- |
| `FIELD` | What to sort by | `name` or `rate` (case-insensitive)                         |
| `ORDER` | Sort direction  | `asc` (ascending) or `desc` (descending) (case-insensitive) |

- **Name:** Alphabetical order by full name (case-insensitive).
- **Rate:** Numeric order by hourly rate (including decimal rates). If two tutors have the **same rate**, they are ordered by **name** (ascending) as a tie-break.

---

#### Examples

**Sorting by name**

```
sort name asc
```

Shows tutors in alphabetical order from A → Z by name.

**Sorting by rate**

```
sort rate desc
```

Shows highest hourly rate first.

**Expected output:** A confirmation message in the Result Display, and the Tutor List Panel updates to the new order. The header above the list also reflects the active sort.

![Sort successful output](images/sort_successful.png)

<box type="warning" seamless>

**Indices update after sorting:** Because `edit` and `delete` use the position number in the **current** list, running `sort` changes which tutor is at each index. See [Understanding List Indices](#understanding-list-indices) before your next command.

</box>

---

#### Invalid Usage

Tuto will return an error if `sort` is used without any parameters, or if the parameters provided are unrecognised:

- **No Parameters:** "Invalid command format! sort: Sorts the tutors in the displayed list by the given field..."
- **Unrecognised Parameters:** "Sort expects exactly two parameters: field (name or rate) and order (asc or desc)..."

---

### Listing All Tutors : `list`

Displays all tutor profiles stored in Tuto. The GUI features a left panel that displays the contact list at all times, so this command is predominantly used in the CLI or to quickly clear search filters in the main view.

![List command hero image](images/listMessage.png)

**Format:** `list`

**Expected output:** The left panel automatically updates to show all stored tutors, and the result display shows `✨  Listed all tutors!`.

<box type="info" seamless>

**Note:** Running `list` on an empty address book will still display `✨  Listed all tutors!` because it successfully lists all zero of your saved tutors.

</box>

<box type="tip" seamless>

**Tip:** Run this command to reset the view after a `find` command in CLI.

</box>

---

### Clearing All Entries : `clear`

Permanently clears all tutor entries from Tuto.

![Clear command hero image](images/clearMessage.png)

**Format:** `clear`

**Expected output:** All tutor profiles are deleted, leaving the Tutor List Panel empty. The result display confirms that the data has been successfully cleared.

<box type="info" seamless>

**Note:** The `clear` command empties your tutor list, but it **does not reset the active sorting order**. If the list was previously sorted by rate, any newly added tutors will continue to be automatically sorted by rate.

</box>

<box type="warning" seamless>

**Caution:** This action permanently deletes all data and cannot be undone. Use with extreme care!

</box>

---

### Exiting the Program : `exit`

Closes the Tuto application.

**Format:** `exit`

When `exit` executes successfully, Tuto closes. Tutor data is saved automatically as part of command execution.

---

## Data Management

Tuto's data is saved automatically as a JSON file at `[JAR file location]/data/addressbook.json`. Advanced users may update data directly by editing this file using any text editor.

<box type="warning" seamless>

**Caution when editing the data file directly:**
- If your changes make the file format invalid, Tuto will discard all data and start with an empty data file on the next launch. **Always back up the file before making any edits.**
- Certain edits can cause Tuto to behave in unexpected ways (e.g., if a value is outside the acceptable range). Edit the data file only if you are confident that you can update it correctly.

</box>

---

## FAQ

**Q: How do I move my tutor data to a new computer?**

A: Install Tuto on the new computer and run it once to generate the default data folder. Then copy the `addressbook.json` file from your old computer into the `data/` folder on the new one, replacing the empty file.

---

**Q: Tuto opened off-screen after I disconnected an external monitor. What do I do?**

A: Delete the `preferences.json` file in the same folder as `tuto.jar`, then relaunch the app. This resets the window position.

---

**Q: I ran `help` again but the Help Window did not appear. Why?**

A: The Help Window may be minimised. Check your taskbar and restore it manually.

---

## Known Issues

1. **Off-screen window after disconnecting a monitor:** Delete `preferences.json` and relaunch Tuto to reset the window position.
2. **Help Window does not reappear:** If the Help Window is minimised, running `help` again will not open a new one. Restore the minimised window from your taskbar.

---

## Command Summary

| Action     | Format                                                                                                | Example                                                                                                              |
| ---------- | ----------------------------------------------------------------------------------------------------- |----------------------------------------------------------------------------------------------------------------------|
| **Help**   | `help`                                                                                                | `help`                                                                                                               |
| **Add**    | `add n/NAME p/PHONE_NUMBER e/EMAIL s/SUBJECT1 s/SUBJECT2 ... s/SUBJECTn r/RATE [a/ADDRESS] [t/TAG]…​` | `add n/James Ho p/+6512345678 e/jamesho@example.com a/123, Clementi Rd, 1234665 s/Biology r/45 t/friend t/colleague` |
| **Edit**   | `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [s/SUBJECT]… [r/RATE] [t/TAG]…`                  | `edit 2 n/James Lee e/james@example.com`                                                                             |
| **Delete** | `delete INDEX`                                                                                        | `delete 3`                                                                                                           |
| **Find**   | `find KEYWORD` \| `find [PREFIXES]` \| `find KEYWORD [PREFIXES]`                                      | `find geography`, `find s/Biology r/45`, `find korean r/>50`                                                         |
| **Sort**   | `sort FIELD ORDER`                                                                                    | `sort name asc`, `sort rate desc`                                                                                    |
| **List**   | `list`                                                                                                | `list`                                                                                                               |
| **Clear**  | `clear`                                                                                               | `clear`                                                                                                              |
| **Exit**   | `exit`                                                                                                | `exit`                                                                                                               |
