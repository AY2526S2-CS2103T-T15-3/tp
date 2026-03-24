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

* [Quick Start](#quick-start)
* [Understanding the Interface](#understanding-the-interface)
* [Features](#features)
    * [Viewing Help](#viewing-help-help)
    * [Adding a Tutor](#adding-a-tutor-add)
    * [Listing All Tutors](#listing-all-tutors-list)
    * [Editing a Tutor Profile](#editing-a-tutor-profile-edit)
    * [Finding Tutors](#finding-tutors-find)
    * [Deleting a Tutor](#deleting-a-tutor-delete)
    * [Clearing All Tutors](#clearing-all-tutors-clear)
    * [Exiting Tuto](#exiting-tuto-exit)
    * [Saving Your Data](#saving-your-data)
    * [Editing the Data File Directly](#editing-the-data-file-directly)
* [FAQ](#faq)
* [Known Issues](#known-issues)
* [Command Summary](#command-summary)
---

## Quick Start

Follow these steps to get Tuto running on your computer in under 5 minutes.

### Step 1 — Install Java

Tuto requires **Java 17 or above**.

* **Windows / Linux:** Download Java 17 from [Adoptium](https://adoptium.net/).
* **Mac:** Follow the exact installation steps [here](https://se-education.org/guides/tutorials/javaInstallationMac.html), as the standard Mac JDK may not be compatible.

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
3. Run the application:
   ```
   java -jar tuto.jar
   ```

A window similar to the one below should appear within a few seconds, pre-loaded with sample tutor data.

![Tuto UI on first launch](images/Ui.png)

### Step 4 — Try Your First Commands

Type a command into the **Command Box** at the top and press **Enter** to run it. Here are a few to try:

| What you want to do | Command to type |
|---|---|
| View all tutors | `list` |
| Add a new tutor | `add n/Jane Smith p/91234567 e/jane@example.com s/Mathematics r/60` |
| Find tutors by subject | `find s/Mathematics` |
| Delete the 1st tutor | `delete 1` |
| Open help | `help` |
| Exit the app | `exit` |

<box type="tip" seamless>

**Tip:** The [Command Summary](#command-summary) at the bottom of this guide is a handy reference once you are familiar with the commands.

</box>

---

## Understanding the Interface

Tuto's interface has three main areas:

* **Command Box** — where you type your commands
* **Result Display** — shows feedback after each command (success messages or error details)
* **Tutor List Panel** — displays all tutor profiles matching the current view

Each tutor card in the panel shows the tutor's name, phone number, email, subject, and hourly rate. Tags (if any) appear as labels on the card.

---

## Features

### Notes on Command Format

<box type="info" seamless>

The following conventions apply to all commands in this guide:

* Words in `UPPER_CASE` are **values you supply**.<br>
  e.g. in `add n/NAME`, replace `NAME` with the tutor's actual name: `add n/John Doe`.

* Items in `[square brackets]` are **optional**.<br>
  e.g. `[a/ADDRESS]` means the address field can be left out.

* Items followed by `…` can be **used multiple times**.<br>
  e.g. `[t/TAG]…` allows zero, one, or more tags: `t/home`, `t/experienced t/recommended`.

* **Parameters can be given in any order.**<br>
  e.g. `n/NAME p/PHONE` and `p/PHONE n/NAME` are both valid.

* **Extra parameters are ignored** for commands that take none (such as `help`, `list`, `exit`, and `clear`).<br>
  e.g. `help 123` runs as `help`.

</box>

<box type="warning" seamless>

**Note for PDF users:** If you copy commands from a PDF, line breaks may introduce unexpected spaces. Re-type the command manually if it does not execute as expected.

</box>

---

### Viewing Help : `help`

Opens a link to this User Guide.

**Format:** `help`

**Expected output:** A pop-up window appears with a link to the online User Guide.

![Help window](images/helpMessage.png)

---

### Clearing all entries: `clear`

Clears all entries from the address book.

![clear message](images/clearMessage.png)

Format: `clear`

---
### Adding a Tutor : `add`

Adds a new tutor profile to Tuto.

![add message](images/addMessage.png)

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL s/SUBJECT1 s/SUBJECT2 ... s/SUBJECTn r/RATE [a/ADDRESS] [t/TAG]…​`

| Flag | Field | Required? | Accepted values |
|---|---|---|---|
| `n/` | Name | Yes | Any non-empty text |
| `p/` | Phone number | Yes | Digits only, at least 3 digits |
| `e/` | Email | Yes | Valid email format (e.g. `user@example.com`) |
| `a/` | Address | No | Any text |
| `s/` | Subject | Yes | Any non-empty text (e.g. `Mathematics`, `Biology`) |
| `r/` | Hourly rate (SGD) | Yes | Positive number |
| `t/` | Tag | No | Alphanumeric, no spaces |

<box type="tip" seamless>

**Tip:** 
  1. Tags are powerful ways to organise contacts. You can use multiple tags in a tutor contact to provide more information, e.g. `t/home` for home tutoring 
services. 
  2. A person can have any number of tags (including 0) and more than one subjects.
  3. The parameters of the command can be entered in any orders.

</box>

<box type="warning" seamless>

**Note:** Adding a tutor with the same name as an existing entry is not allowed. Tuto treats this as a duplicate. If two tutors happen to share a name, consider using a middle initial to differentiate them.

</box>

**Examples:**

```
add n/John Doe p/98765432 e/johnd@example.com s/Chemistry r/50
```
Adds John Doe as a Chemistry tutor charging $50/hr, with no address or tags.

```
add n/Betsy Crowe p/1234567 e/betsycrowe@example.com a/Newgate Prison s/Biology r/55 t/experienced t/recommended
```
Adds Betsy Crowe as a Biology tutor with an address and two tags.

**Expected output:**
```
New person added: John Doe; Phone: 98765432; Email: johnd@example.com; Address: ; Subject: Chemistry; Rate: 50; Tags:
```

---

### Editing a Tutor Profile : `edit`

Updates one or more fields of an existing tutor profile.

**Format:**
```
edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [s/SUBJECT] [r/RATE] [t/TAG]…
```

* `INDEX` refers to the number shown next to the tutor in the current list. It must be a **positive integer** (1, 2, 3 …).
* At least one field must be provided.
* Existing values are replaced with the new values you provide.

<box type="warning" seamless>

**Note:** Editing tags **replaces all existing tags** — it does not add to them. To remove all tags, use `t/` with nothing after it. To keep existing tags while adding a new one, you must retype all the tags you want to keep.

</box>

**Examples:**

```
edit 1 p/91234567 e/johndoe@example.com
```
Updates the phone number and email of the 1st tutor in the list.

```
edit 2 n/Betsy Crower t/
```
Renames the 2nd tutor and removes all of their tags.

```
edit 1 s/Physics r/30
```
Changes the 1st tutor's subject to Physics and rate to $30/hr.

**Expected output:**
```
Edited Person: John Doe; Phone: 91234567; Email: johndoe@example.com; Address: ; Subject: Chemistry; Rate: 50; Tags:
```

---
### Listing All Tutors : `list`

Displays all tutor profiles stored in Tuto.

![list message](images/listMessage.png)

**Format:** `list`

---
### Finding Tutors : `find`

Searches for tutors by name, subject, or hourly rate.

**Format:**
```
find n/NAME_KEYWORD [MORE_NAME_KEYWORDS]
find s/SUBJECT
find r/RATE
```

* Only **one search prefix** (`n/`, `s/`, or `r/`) can be used per command.
* Name searches are **case-insensitive** and match full words only.<br>
  e.g. `find n/Hans` will match `Hans Gruber` but not `Hansen`.
* Multiple name keywords return tutors matching **any** of the keywords (OR logic).<br>
  e.g. `find n/Hans Bo` returns all tutors named Hans or Bo.
* Subject searches return tutors whose subject matches exactly (case-insensitive).
* Rate searches return tutors whose rate matches exactly.
* Spaces after the prefix are optional: `find n/John` and `find n/ John` both work.

<box type="tip" seamless>

**Tip:** After a `find`, use `list` to return to the full tutor list.

</box>

**Examples:**

```
find n/John
```
Returns all tutors with "John" in their name, e.g. `John Doe`, `John Tan`.

```
find s/Mathematics
```
Returns all tutors who teach Mathematics.

```
find r/50
```
Returns all tutors charging exactly $50/hr.

```
find n/Alex David
```
Returns tutors named Alex or David.

![Result for 'find n/alex david'](images/findAlexDavidResult.png)

**Expected output:**
```
2 persons listed!
```

---

### Deleting a Tutor : `delete`

Permanently removes a tutor profile from Tuto.

![delete message](images/deleteMessage.png)

Format: `delete INDEX`

* `INDEX` must be a **positive integer** matching a tutor's position in the currently displayed list.

<box type="warning" seamless>

### Exiting the program : `exit`
**Caution:** Deletion is permanent and cannot be undone. Double-check the index before running this command.

</box>

**Examples:**

```
list
delete 2
```
Deletes the 2nd tutor in the full list.

```
find s/Biology
delete 1
```
Deletes the 1st tutor returned in the Biology search results.

**Expected output:**
```
Deleted Person: Betsy Crowe; Phone: 1234567; Email: betsycrowe@example.com; Address: Newgate Prison; Subject: Biology; Rate: 55; Tags: [experienced][recommended]
```

---
### Saving Your Data

Tuto's data are saved automatically as a JSON file `[JAR file location]/data/Tuto.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes to the data file makes its format invalid, Tuto will discard all data and start with an empty data file at the next run.  Hence, it is recommended to take a backup of the file before editing it.<br>
Furthermore, certain edits can cause the Tuto to behave in unexpected ways (e.g., if a value entered is outside the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>
--------------------------------------------------------------------------------------------------------------------

### Editing the Data File Directly

Advanced users may edit the data file manually using any text editor.

<box type="warning" seamless>

**Caution:** If the file is saved in an invalid format, Tuto will discard all data and start fresh on the next launch. **Back up the file before making any edits.** Additionally, values outside accepted ranges may cause Tuto to behave unexpectedly.

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

| Action | Format | Example                                                                                                            |
|---|---|--------------------------------------------------------------------------------------------------------------------|
| **Help** | `help` | `help`                                                                                                             |
| **Add** | `add n/NAME p/PHONE_NUMBER e/EMAIL s/SUBJECT1 s/SUBJECT2 ... s/SUBJECTn r/RATE [a/ADDRESS] [t/TAG]…​` |  `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 s/Biology r/45 t/friend t/colleague` |
| **List** | `list` | `list`                                                                                                             |
| **Edit** | `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [s/SUBJECT] [r/RATE] [t/TAG]…` | `edit 2 n/James Lee e/james@example.com`                                                                           |
| **Find** | `find n/KEYWORD [MORE_KEYWORDS]` \| `find s/SUBJECT` \| `find r/RATE` | `find n/James`, `find s/Biology`, `find r/45`                                                                      |
| **Delete** | `delete INDEX` | `delete 3`                                                                                                         |
| **Clear** | `clear` | `clear`                                                                                                            |
| **Exit** | `exit` | `exit`                                                                                                             |
