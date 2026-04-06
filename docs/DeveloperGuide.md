---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# Tuto Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

---

## **Acknowledgements**

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

---

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

---

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The **_Architecture Diagram_** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S2-CS2103T-T15-3/tp/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2526S2-CS2103T-T15-3/tp/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.

- At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
- At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

- [**`UI`**](#ui-component): The UI of the App.
- [**`Logic`**](#logic-component): The command executor.
- [**`Model`**](#model-component): Holds the data of the App in memory.
- [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The _Sequence Diagram_ below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

- defines its _API_ in an `interface` with the same name as the Component.
- implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S2-CS2103T-T15-3/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)


<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.


The `UI` component uses the JavaFX UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S2-CS2103T-T15-3/tp/blob/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S2-CS2103T-T15-3/tp/blob/master/src/main/resources/view/MainWindow.fxml)


The `UI` component,

- executes user commands using the `Logic` component.
- listens for changes to `Model` data so that the UI can be updated with the modified data.
- keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
- depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

The following sequence diagram illustrates the interactions between the `UI` and `Logic` components when executing a `Find` name command attribute on the updated UI.

<puml src="diagrams/UiFindSequenceDiagram.puml" alt="Interactions between the UI and Logic Components for the `find n/john` command" />


### Logic component

**API** : [`Logic.java`](https://github.com/AY2526S2-CS2103T-T15-3/tp/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
2. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
3. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
4. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"></puml>

How the parsing works:

- When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
- All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Find command

The `find` command allows users to filter the displayed person list based on the specified search criteria.

The diagram below shows the parser classes involved in handling the `find` command.

<puml src="diagrams/FindParserClassDiagram.puml" width="500"/>

When the user enters a `find` command, `AddressBookParser` identifies the command word and creates a `FindCommandParser`.  
`FindCommandParser` then parses the input arguments and constructs a `FindCommand`, which is returned as a `Command` object for execution.

Upon execution, `FindCommand` calls `Model#updateFilteredPersonList(...)` to update the displayed list according to the given predicate.

The `find` command can support different forms of filtering through different predicates, making the search functionality flexible and extensible.
### Model component

**API** : [`Model.java`](https://github.com/AY2526S2-CS2103T-T15-3/tp/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />

The `Model` component,

- stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
- stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
- stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
- does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>

### Storage component

**API** : [`Storage.java`](https://github.com/AY2526S2-CS2103T-T15-3/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,

- can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
- inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
- depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

---

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Adding a Tutor : `add`

Adds a new tutor profile to Tuto. 
The sequence diagram below illustrates the interactions between the Logic and Model components during the execution of the `add` command.

<puml src="diagrams/AddSequenceDiagram.puml" width="550"></puml>

The `add` command is processed in two main phases: parsing and execution.
The input string from user is first parsed into an `Person` object, which is then wrapped in an `AddCommand` object and executed to update the `Model` object.

The sequence of interactions is as follows:
1. The user enters the `add` command with the correct arguments, which is received by `LogicManager`.
2. `LogicManager` calls `AddressBookParser` to parse the input.
3. `AddressBookParser` identifies the command word and delegates parsing to `AddCommandParser`.
4. `AddCommandParser` parses the arguments and constructs a `Person` object based on the given attributes stated in the arguments.
5. `AddCommandParser` creates an `AddCommand` object containing the `Person`.
6. The `AddCommand` is returned to `LogicManager`.
7. `LogicManager` executes the command by calling `AddCommand#execute(Model)`.
8. `AddCommand` performs validation checks:
   - checks if the person already exists, 
   - checks if the same phone and email already exists. 
9. If all checks pass, the `Person` is added to the `Model`.
10. A `CommandResult` is created and returned to the user.

#### Alternative flows
- If the input format from user is invalid, a `ParseException` is thrown during parsing and the `command` object is not created.
- If the person already exists in the address book, a `CommandException` is thrown and the operation is aborted.
- If the phone or email already exists, a `CommandException` is thrown and the operation is aborted.
### Uniqueness Constraints

To ensure data integrity, the application enforces uniqueness constraints on each `Person`.

#### Current Implementation

The uniqueness of a `Person` is determined by the following fields:
- Phone number
- Email address

Duplicate checks are performed in `AddCommand` and `EditCommand` before delegating to the model.
If a duplicate is detected, a `CommandException` is thrown immediately with an appropriate error message.

If any of the following conditions are met:
- A person with the same phone number already exists
- A person with the same email address already exists

The operation is rejected and an appropriate error message is shown to the user.

Duplicate checks are performed in the following priority:
1. Phone number
2. Email address

This ensures that name conflicts are detected first, followed by contact information conflicts.

#### Design Considerations

**Aspect: What defines a duplicate person**

- **Alternative 1 (current choice):** Use phone number, and email address as uniqueness constraints
    - Pros: Prevents duplicate entries effectively and maintains clean data
    - Cons: May be restrictive in cases where users share names or contact details

- **Alternative 2:** Use full object equality
    - Pros: Simpler implementation
    - Cons: Allows duplicate entries with identical contact details

#### Class Diagram

The following class diagram shows the key classes involved in enforcing uniqueness constraints,
and how they interact structurally.

`AddCommand` and `EditCommand` depend on the `Model` interface to perform duplicate checks.
`ModelManager` implements `Model` and delegates to `AddressBook`, which contains a
`UniquePersonList` that stores all `Person` objects.

<puml src="diagrams/UniquenessConstraintClassDiagram.puml" />

#### Sequence Diagram

The following diagram illustrates how duplicate checks are performed during an `add` or `edit` operation:

<puml src="diagrams/UniquenessConstraintSequence.puml" />

### \[Proposed\] Undo/redo feature


#### Design considerations:

**Aspect: How undo & redo executes:**

- **Alternative 1 (current choice):** Saves the entire address book.
    - Pros: Easy to implement.
    - Cons: May have performance issues in terms of memory usage.

- **Alternative 2:** Individual command knows how to undo/redo by
  itself.
    - Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
    - Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_

---

## **Documentation, logging, testing, configuration, dev-ops**

- [Documentation guide](Documentation.md)
- [Testing guide](Testing.md)
- [Logging guide](Logging.md)
- [Configuration guide](Configuration.md)
- [DevOps guide](DevOps.md)

---

## **Appendix: Requirements**

### Product scope

**Target user profile**:

- has a need to manage multiple freelance tutor profile for their children
- wants a structured way to store tutor information (subjects, rates)
- prefer desktop apps over other types
- prefers typing to mouse interactions
- is reasonably comfortable using CLI apps

**Value proposition**: Our address book allows Parents to easily manage a stored list of manually added freelance Tutors
contacts for their Children’s subjects. The address book will present useful data in a structured format for Parents to
make decisions of a Tutor for their Children.

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​ | I can…​                                                         | So that...​                                                         |
| ----- | ------- |-----------------------------------------------------------------|---------------------------------------------------------------------|
| `* * *` | parent  | add a tutor profile with name,phone,email,subject and rate      | I can keep track of tutors for my child                             |
| `* * *` | parent  | view all tutors profiles in a list                              | I can have an overview of available tutors for my child             |
| `* * *` | parent  | view a tutor's full profile details                             | I can evaluate if the tutor is suitable for my child                |
| `* * *` | parent  | delete a tutor profile                                          | I can remove outdated or irrelevant tutors                          |
| `* * *` | parent  | edit a tutor's details                                          | I can keep tutor information up to date                             |
| `* * *` | parent  | search tutors by subject                                        | I can match tutors to my child's academic needs                     |
| `* * *` | parent  | search tutors by name                                           | I can quickly locate a specific tutor                               |
| `* * *` | parent  | search tutors within a rate range                               | I can shortlist affordable tutors within my budget                  |
| `* * ` | parent  | search tutors by multiple attributes (e.g. name, subject, rate) | I can refine my search results                                      |
| `* * ` | parent  | tag tutors with labels                                          | I can categorise or leave a note on tutors for easier management    |
| `* * ` | parent  | clear all entries                                               | I can reset the application to an empty state                       |
| `*  ` | parent  | sort tutors by ascending or descending rate                     | I can easily identify the most affordable and most expensive tutors |
| `*  ` | parent  | sort tutors by alphabetical order                               | I can browse through Tuto in a predictable manner                   |

### Use cases

(For all use cases below, the **System** is the `Tuto` and the **Actor** is the `Parent`, unless specified otherwise)

#### Use Case: U1. View all Tutor Profile

Preconditions: `Tuto` is running

**MSS:**

1. `Parent` requests to list all tutor profile
2. `Tuto` returns a list of all stored Tutor Profiles
   Use Case ends

**Extensions**

- 1a. Wrong Command given
    - 1a1. `Tuto` returns an error message

        Use case ends

    - 2a. The contact list is empty
    - 2a1. `Tuto` displays a message indicating no contacts have been added yet.

        Use case ends.

#### Use Case: U2. View a specific Tutor Profile

Preconditions: `Tuto` is running

**MSS:**

1. `Parent` requests to list all Tutor Profile (U1)
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

Preconditions: `Tuto` is running and `Tuto` contains at least one Tutor Profile

Guarantees: The Tutor Profile is removed from storage upon successful completion.

**MSS:**

1. `Parent` requests to list all Tutor Profiles (U1)
2. `Parent` enters the delete command specifying the Index of the tutor.
3. `Tuto` deletes the contact
4. `Tuto` displays a confirmation message that the Tutor Profile has been deleted successfully
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

Guarantees: If MSS completes until step 3, Tutor Profile will be added to Tuto’s storage

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

Preconditions: `Tuto` is running and `Tuto` contains at least one Tutor Profile

Guarantees: If MSS completes until step 4, the Tutor Profile will be updated in `Tuto's` storage.

**MSS:**

1. `Parent` requests to list all Tutor Profile (U1).
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

- 3c. The update results in a Duplicate Entry of an existing Tutor Profile.
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

- **ArgumentMultimap**: A data structure that stores mappings between prefixes and their associated values after tokenization
- **Argument Tokenization**: The process of splitting user input into key-value pairs based on prefixes.
- **Command**: The entire line of text entered by the user (containing the command word, flags, and parameters) to execute a specific task.
- **CommandResult**: An object that encapsulate the result of a command execution, including feedback to be displayed to the user.
- **Command Word**: The first word of a Command (e.g., add, edit, delete) that identifies the action to be performed.
- **Duplicate Entry**: A tutor profile with the same phone number or email as an existing profile.
- **Exception**: An error condition (e.g. ParseException) that interrupts normal execution and is handled gracefully.
- **Human-readable**: A file format (like JSON or Text) that allows data to be read and edited using a standard text editor, without Tuto.
- **Index**: The number displayed next to a tutor's name in the contact list (e.g., the 3 in 3. John Doe). Used to select a specific tutor for commands like edit or delete.
- **JAR File**: The file format used to distribute Tuto, allowing it to run on any computer with Java installed without a setup wizard.
- **Model**: The component responsible for storing and managing application data.
- **Parameter**: The specific information provided after a Flag (e.g., John is the parameter for /n).
- **Person**: A data entity representing a tutor profile, containing attributes such as name, phone, email, address, subject, rate and tags.
- **Prefix**: A specific prefix (e.g., /n, /p, /e) used to identify the type of data following it.
- **Rate**: The cost per hour for hiring the tutor.
- **Subject**: The academic discipline a tutor teaches (e.g., Math, Physics).
- **Tag**: A customisable label user can put on a tutor profile.
- **Tutor Profile**: The complete set of data (Name, Phone, Email, etc.) stored for one specific tutor.
---

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more _exploratory_ testing.

</box>

### Launch and shutdown

1. Initial launch
    1. Download the jar file and copy into an empty folder
    2. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

2. Saving window preferences
    1. Resize the window to an optimum size. Move the window to a different location. Close the window.
    2. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

3. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown
    1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.
    2. Test case: `delete 1`<br>
       Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

    3. Test case: `delete 0`<br>
       Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

    4. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
       Expected: Similar to previous.

2. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files
    1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

2. _{ more test cases …​ }_
