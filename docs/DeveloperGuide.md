---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# Tuto Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S2-CS2103T-T15-3/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFX UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S2-CS2103T-T15-3/tp/blob/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S2-CS2103T-T15-3/tp/blob/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

The following sequence diagram illustrates the interactions between the `UI` and `Logic` components when executing a `Find` name command attribute on the updated UI.

<puml src="diagrams/UiFindSequenceDiagram.puml" alt="Interactions between the UI and Logic Components for the `find n/john` command" />


### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* has a need to manage multiple freelance tutor contacts for their children
* wants a structured way to store tutor information (subjects, rates, availability)
* prefer desktop apps over other types
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Our address book allows Parents to easily manage a stored list of manually added freelance Tutors
contacts for their Children’s subjects. The address book will present useful data in a structured format for Parents to
make decisions of a Tutor for their Children.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​ | I can…​                              | So that...​                                          |
|----------|---------|--------------------------------------|------------------------------------------------------|
| `* * *`  | parent  | view a tutor profile                 | I can decide whether the tutor is good for my child  |
| `* * *`  | parent  | delete a tutor from the address book | the address book stays relevant and uncluttered      |
| `* * *`  | parent  | add a tutor profile                  | I can keep track of tutors                           |
| `* * *`  | parent  | view a tutor's hourly rate           | I can find someone who fits within my family's budget |
| `* * `   | parent  | search for tutors by subject         | I can match a tutor to my child's academic needs     |

*{More to be added}*
### Use cases

(For all use cases below, the **System** is the `Tuto` and the **Actor** is the `Parent`, unless specified otherwise)

### Use cases

#### Use Case: U1. View all Tutor Contacts

Preconditions: `Tuto` is running

**MSS:**

1. `Parent` requests to list all tutor contacts
2. `Tuto` returns a list of all stored Tutor Profiles
   Use Case ends

**Extensions**

- 1a. Wrong Command given
    - 1a1. `Tuto` returns an error message

        Use case ends

    - 2a. The contact list is empty
    - 2a1. `Tuto` displays a message indicating no contacts have been added yet.

        Use case ends.

#### Use Case: U2. View a specific Tutor Contact

Preconditions: `Tuto` is running

**MSS:**

1. `Parent` requests to list all Tutor Contacts (U1)
2. `Parent` enters the command specifying the Index
3. `Tuto` displays the tutor's full profile
   Use Case ends

**Extensions**

- 2a. The provided index is invalid
    - 2a1. `Tuto` shows an error message

        Use case ends

- 2b. The command format is invalid
    - 2b1. `Tuto` shows an error message.

        Use case ends

#### Use Case: U3. Delete a Tutor from Tuto

Preconditions: `Tuto` is running and `Tuto` contains at least one Tutor Contact

Guarantees: The Tutor Contact is removed from storage upon successful completion.

**MSS:**

1. `Parent` requests to list all Tutor Contacts (U1)
2. `Parent` enters the delete command specifying the Index of the tutor.
3. `Tuto` deletes the contact
4. `Tuto` displays a confirmation message that the Tutor Contact has been deleted successfully
   Use Case ends

**Extensions**

- 2a. The provided index is invalid.
    - 2a1. `Tuto` shows an error message.

        Use case ends

- 2b. The command format is invalid
    - 2b1. `Tuto` shows an error message.

        Use case ends

#### Use Case: U4. Add a Tutor Profile

Preconditions: `Tuto` is running

Guarantees: If MSS completes until step 3, Tutor contact will be added to Tuto’s storage

**MSS:**

1. `Parent` enters the add command specifying the required Parameters
2. `Tuto` validates the parameters.
3. `Tuto` adds the Tutor Profile to the contact list
4. `Tuto` displays a success message on the addition
   Use Case ends

**Extensions**

- 2a. One or more compulsory Parameters are missing
    - 2a1. `Tuto` returns an error message indicating the missing fields.

        Use case ends

- 2b. One or more Parameters are in an invalid format
    - 2b1. `Tuto` returns an error message indicating the constraint violation.

        Use case ends

- 2c. A tutor with the same details (Duplicate) already exists.
    - 2c1. `Tuto` shows a duplicate entry error message.

        Use case ends

#### Use Case: U5. Search for Tutors by Subject

Preconditions: `Tuto` is running

Guarantees: If MSS completes until step 3, `Tuto` displays all tutors in the contact list with matching subjects

**MSS:**

1. `Parent` enters the find command specifying a Subject keyword
2. `Tuto` validates the entered details
3. `Tuto` searches and displays a list of tutors matching the subject.
   Use Case ends

**Extensions**

- 2a. The command format is invalid
    - 2a1. `Tuto` shows an error message.

        Use case ends

- 3a. No tutors found in contacts list matching the keyword
    - 3a1. `Tuto` shows a message indicating no results found.

        Use case ends

#### Use Case: U6. Edit a Tutor Profile

Preconditions: `Tuto` is running and `Tuto` contains at least one Tutor Contact

Guarantees: If MSS completes until step 4, the Tutor Contact will be updated in `Tuto's` storage.

**MSS:**

1. `Parent` requests to list all Tutor Contacts (U1).
2. `Parent` enters the edit command, specifying the Index of the tutor and the Parameters to update.
3. `Tuto` validates the index and the new parameters.
4. `Tuto` updates the Tutor Profile
5. `Tuto` displays the updated details of the tutor.
   Use Case ends

**Extensions**

- 3a. The provided Index is invalid (e.g., 0, negative, or out of bounds).
    - 3a1. `Tuto` shows an error message.

        Use case ends

- 3b. The provided Parameters violate validation rules.
    - 3b1. `Tuto` returns an error message indicating the invalid format.

        Use case ends

- 3c. The update results in a Duplicate Entry of an existing Tutor Contact.
    - 3c1. `Tuto` shows a duplicate entry error message.

        Use case ends

### Non-Functional Requirements

1. The system should work on mainstream operating systems　such as Windows, Linux, macOS as long as Java `17` or above installed.
2. The system should be able to load and display up to 1000 Tutor Profiles within 5 seconds of application start up.
3. A user with above average typing speed should be able to perform common tasks (such as adding or editing tutor profiles) faster using commands and flags than using the mouse.
4. The system should respond to user commands within 1 second for typical usage, excluding file loading and saving operations.
5. The system should handle invalid Flags, Indices, or Parameters gracefully by displaying a clear error message instead of crashing.
6. The text file containing tutor profile data should be transferable and human-readable for portability across different machines.
7. The application should be usable without an internet connection.
8. The application should not require an installer and should run from a single directory without external dependencies beyond Java `17`.
9. The application should be packaged into a single JAR file to simplify distribution and testing.


### Glossary

* **Command**: The entire line of text entered by the user (containing the command word, flags, and parameters) to execute a specific task.
* **Command Word**: The first word of a Command (e.g., add, edit, delete) that identifies the action to be performed.
* **Flag**: A specific prefix (e.g., /n, /p, /e) used to identify the type of data following it.
* **Human-readable**: A file format (like JSON or Text) that allows data to be read and edited using a standard text editor, without Tuto.
* **Index**: The number displayed next to a tutor's name in the contact list (e.g., the 3 in 3. John Doe). Used to select a specific tutor for commands like edit or delete.
* **JAR File**: The file format used to distribute Tuto, allowing it to run on any computer with Java installed without a setup wizard.
* **Parameter**: The specific information provided after a Flag (e.g., John is the parameter for /n).
* **Subject**: The academic discipline a tutor teaches (e.g., Math, Physics).
* **Tutor Profile**: The complete set of data (Name, Phone, Email, etc.) stored for one specific tutor.


--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
