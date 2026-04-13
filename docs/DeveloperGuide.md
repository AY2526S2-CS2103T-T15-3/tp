---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# Tuto Developer Guide

<box type="info" seamless>

**Welcome to the Tuto Developer Guide!**
This document describes the architecture, software design decisions, and implementation details of Tuto.
It is intended for future developers, maintainers, and anyone interested in understanding the technical inner workings of the application.

</box>

<!-- * Table of Contents -->
<page-nav-print />

---

## Table of Contents

- [Tuto Developer Guide](#tuto-developer-guide)
  - [Table of Contents](#table-of-contents)
  - [**Acknowledgements**](#acknowledgements)
  - [**Setting up, getting started**](#setting-up-getting-started)
  - [**Design**](#design)
    - [Architecture](#architecture)
    - [UI component](#ui-component)
    - [Logic component](#logic-component)
    - [Model component](#model-component)
    - [Storage component](#storage-component)
    - [Common classes](#common-classes)
  - [**Implementation**](#implementation)
    - [Adding a Tutor: `add`](#adding-a-tutor-add)
      - [Alternative flows](#alternative-flows)
    - [Uniqueness Constraints](#uniqueness-constraints)
      - [Current Implementation](#current-implementation)
      - [Design Considerations](#design-considerations)
      - [Class Diagram](#class-diagram)
      - [Sequence Diagram](#sequence-diagram)
    - [Finding a Tutor: `find`](#finding-a-tutor-find)
      - [Alternative flows](#alternative-flows-1)
      - [Search Modalities](#search-modalities)
      - [Design Considerations](#design-considerations-1)
        - [Aspect: Search Modalities and User Experience](#aspect-search-modalities-and-user-experience)
        - [Aspect: UI Context and the Query Bar](#aspect-ui-context-and-the-query-bar)
  - [**Documentation, logging, testing, configuration, dev-ops**](#documentation-logging-testing-configuration-dev-ops)
  - [**Appendix: Requirements**](#appendix-requirements)
    - [Product scope](#product-scope)
    - [User stories](#user-stories)
    - [Use cases](#use-cases)
      - [Use Case: U1. View all Tutor Profile](#use-case-u1-view-all-tutor-profile)
      - [Use Case: U2. Delete a Tutor from Tuto](#use-case-u2-delete-a-tutor-from-tuto)
      - [Use Case: U3. Add a Tutor Profile](#use-case-u3-add-a-tutor-profile)
      - [Use Case: U4. Search for Tutors by Subject](#use-case-u4-search-for-tutors-by-subject)
      - [Use Case: U5. Edit a Tutor Profile](#use-case-u5-edit-a-tutor-profile)
      - [Use Case: U6. Find Tutors](#use-case-u6-find-tutors)
      - [Use Case: U7. Sort the Tutor List](#use-case-u7-sort-the-tutor-list)
    - [Non-Functional Requirements](#non-functional-requirements)
    - [Glossary](#glossary)
  - [**Appendix: Instructions for manual testing**](#appendix-instructions-for-manual-testing)
    - [Launch and shutdown](#launch-and-shutdown)
    - [Adding a person](#adding-a-person)
    - [Deleting a person](#deleting-a-person)
    - [Editing a person](#editing-a-person)
    - [Finding a person](#finding-a-person)
      - [Negative Cases \& Error Handling](#negative-cases-and-amp-error-handling)
      - [Positive Cases (Universal Search)](#positive-cases-universal-search)
      - [Complex Cases (Attribute Filtering \& Combinations)](#complex-cases-attribute-filtering-and-amp-combinations)
      - [Adversarial \& Edge Cases](#adversarial-and-amp-edge-cases)
    - [Sorting the tutor list](#sorting-the-tutor-list)
      - [Invalid commands and errors](#invalid-commands-and-errors)
    - [Saving data](#saving-data)

---

## **Acknowledgements**

- This project is based on the AddressBook-Level3 (AB3) codebase from [se-education/addressbook-level3](https://github.com/se-edu/addressbook-level3).

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

The **API** of this component is specified in [`Logic.java`](https://github.com/AY2526S2-CS2103T-T15-3/tp/tree/master/src/main/java/seedu/address/logic/Logic.java)

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

### Model component

The **API** of this component is specified in [`Model.java`](https://github.com/AY2526S2-CS2103T-T15-3/tp/tree/master/src/main/java/seedu/address/model/Model.java)

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

The **API** of this component is specified in [`Storage.java`](https://github.com/AY2526S2-CS2103T-T15-3/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

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

### Adding a Tutor: `add`

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
8. `AddCommand` performs validation checks if same phone and email already exists.
9. If all checks pass, the `Person` is added to the `Model`.
10. A `CommandResult` is created and returned to the user.

#### Alternative flows

- If the input format from user is invalid, a `ParseException` is thrown during parsing and the `command` object is not created.
- If the phone or email already exists in Tuto, a `CommandException` is thrown and the operation is aborted.

---

### Uniqueness Constraints

To ensure data integrity, the application enforces uniqueness constraints across tutor profiles.

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

This ensures that phone number conflict is detected first, followed by email address conflict.

#### Design Considerations

**Aspect: What defines a duplicate person**

- **Alternative 1 (current choice):** Use phone number, and email address as uniqueness constraints
    - Pros: Prevents duplicate entries effectively and maintains clean data
    - Cons: May be restrictive in cases where users share names or contact details
- **Alternative 2:** Use full object equality
    - Pros: Simpler implementation
    - Cons: Allows duplicate entries with identical contact details

**Aspect: Rates use non-negative numeric values (including decimals)**

- **Alternative 1 (current choice):** Rates accept non-negative integers or decimals (validated and compared as decimal values for `find` and `sort`)
    - Pros: Supports common cases such as $25.50/hour; sorting and rate filters follow true numeric order
    - Cons: Slightly more validation and parsing logic than integers-only
- **Alternative 2:** Rates restricted to whole numbers only
    - Pros: Simpler validation
    - Cons: Cannot represent fractional hourly rates; less flexible for real-world pricing

**Aspect: Address field is optional**

- **Alternative 1 (current choice):** Address field is optional
    - Pros: Accounts for the case where tuition may be done online and hence tutor's address is not needed
    - Cons: User may forget to add address field of tutor in cases where face-to-face tuition is arranged and address is needed to meet the tutor
- **Alternative 2:** Address field is mandatory
    - Pros: Serves as a reminder to user to record address of tutor in cases where face-to-face tuition is arranged
    - Cons: Does not account for the case where tuition may be done online and hence tutor's address is not needed

**Aspect: Phone number must either be 8 digits for Singapore number or 10 digits excluding '+' for international number**

- **Alternative 1 (current choice):** Phone number field must either be 8 digits for Singapore number or 10 digits excluding '+' for international number
    - Pros: Accounts for the case where tutor may only have international number (e.g. Tutor is from Malaysia)
    - Cons: Feature may be rarely used in practice due to the small number of international tutors
- **Alternative 2:** Phone number field follows the original implementation of at least 3 digits and no special characters
    - Pros: Simple to implement, no changes required
    - Cons: Does not account for the case where tutor may only have international number (e.g. Tutor is from Malaysia)

#### Class Diagram

The following class diagram shows the key classes involved in enforcing uniqueness constraints,
and how they interact structurally.

`AddCommand` and `EditCommand` depend on the `Model` interface to perform duplicate checks.
`ModelManager` implements `Model` and delegates to `AddressBook`, which contains a
`UniquePersonList` that stores all `Person` objects.

<puml src="diagrams/UniquenessConstraintClassDiagram.puml" />

#### Sequence Diagram

The following diagram illustrates how duplicate checks are performed during an `add` operation:

<puml src="diagrams/UniquenessConstraintSequence.puml" />

---

### Finding a Tutor: `find`

Finds a tutor profile saved in Tuto. The sequence diagrams below illustrate the interactions between the UI, Logic, and Model
components during the execution of the `find` command.

The `find` command is processed in two main phases: parsing the query to construct the appropriate predicate, and executing the search to update the `Model` and `UI`.

The sequence of interactions is as follows:

1. The user enters the `find` command with the search keywords, which is received by `LogicManager`.
2. `LogicManager` calls `AddressBookParser` to parse the input.
3. `AddressBookParser` identifies the command word and delegates parsing to `FindCommandParser`.
4. `FindCommandParser` parses the arguments and constructs a predicate based on the search criteria.
5. `FindCommandParser` creates a `FindCommand` object containing the constructed predicate.
6. The `FindCommand` is returned to `LogicManager`.
7. `LogicManager` executes the command by calling `FindCommand#execute(Model)`.
8. `FindCommand` calls `Model#updateFilteredPersonList(...)` using the predicate to filter the displayed list.
9. `FindCommand` generates a `CommandResult` containing the `foundPersons` (the filtered list) and a `description` of the search query. This result is returned to the user interface.
10. The UI receives the `CommandResult`. Notably, it updates the query bar (`ResultDisplay`) to display the description of what was searched, then lists the internally matched tutors.

The diagram below shows how the UI interactions are executed after the command returns.

<puml src="diagrams/UiFindSequenceDiagram.puml" />

The diagram below illustrates the interactions inside the Logic and Model components.

<puml src="diagrams/FindSequenceDiagram.puml" />

The diagram below shows the parser classes involved in handling the `find` command.

<puml src="diagrams/FindParserClassDiagram.puml" width="500"/>

#### Alternative flows

- If the search arguments are empty or invalid, a `ParseException` is thrown during parsing and the `Command` object is not created.
- If the search yields no matches, `FindCommand` still executes successfully. The displayed list is simply empty, but the query bar in the UI is still appropriately updated to show what keywords were searched.

#### Search Modalities

The `find` command supports three modalities of searching, enabling users to find tutor profiles flexibly:

**1. General Search (Universal Search)**
Users can provide keywords without any prefixes.

- **Example:** `find alice math`
- **Implementation:** The `FindCommandParser` treats these prefix-less keywords as the preamble. It extracts these keywords and constructs a `UniversalSearchPredicate`. This predicate evaluates if **any** of the tutor's relevant fields (such as name, subject, or tags) match the specified keywords.

**2. Attribute Filtering**
Users can search for specific attributes using prefixes (e.g., `n/`, `s/`, `r/`, `t/`).

- **Example:** `find n/alice r/10-30`
- **Implementation:** `FindCommandParser` parses the mapped values for each specified prefix.
    - It creates specific predicates for each attribute (e.g., `NameContainsKeywordsPredicate`, `RateRangePredicate`, `SubjectContainsKeywordsPredicate`).
    - Multiple occurrences of keywords or prefixes are supported. For names (`n/Alice Peter`), space-separated keywords are evaluated using OR logic (returns profiles matching any of the names). For subjects and tags (e.g., `s/Math s/Physics`), multiple occurrences of the prefix itself are evaluated using AND logic within their respective predicates (returns profiles matching all specified prefixes).
    - Rate filtering dynamically handles various mathematical boundaries (e.g., exact `r/50`, ranges `r/40-60`, or inequalities `r/<50`) using non-negative decimal bounds where applicable.

**3. General Search + Attribute Filtering**
Users can combine general search keywords with specific attribute filters to effectively refine their results.

- **Example:** `find alice r/<50`
- **Implementation:** When `FindCommandParser` detects both a preamble and specific prefixes, it validates that only supported refinement prefixes (`n/`, `s/`, `r/`, `t/`) are used to prevent logical conflicts. It then constructs a **combined predicate** by chaining the `UniversalSearchPredicate` and the attribute-specific predicates together using a logical `AND` (`Predicate.and()`). This ensures the displayed list heavily filters the broad keyword search against the strict attribute constraints.

#### Design Considerations

##### Aspect: Search Modalities and User Experience

The design of the `find` command heavily factors in natural human searching patterns and the progressive disclosure of complexity.

- **Low-information exploration (Typing less to discover more):** When a user is unsure of exact profile details, they logically gravitate towards broad, search-engine-style keyword searches. The **General (Universal) Search** caters to this by requiring zero prefixes, lowering the cognitive barrier and friction to entry. If you don't know exactly what you want, you type less.
- **High-information precision (Determinate filtering):** When a user knows exactly what they want (e.g., a specific name under a strict budget), they need structured tools to zero-in on candidates. **Attribute Filtering** provides rigorous control. The more you know, the more you filter.
- **Progressive refinement:** The **General Search + Attribute Filtering** modality allows users to cast a wide net initially, completely naturally, and optionally tack on filters to eliminate noise once they see the broad results.

**Alternative 1 (Current choice): Flexible multi-modal search (Universal + Attributes)**

- **Pros:** Scales dynamically with the user's familiarity and immediate needs. It models real-world workflows seamlessly.
- **Cons / Opportunity costs:** Significantly increases the architectural complexity of the `FindCommandParser`. It introduces edge-case ambiguities where universal keywords logically clash with prefix arguments, requiring us to enforce explicit validation rules (e.g., restricting which abstract prefixes can confidently be used alongside universal search) and write much more comprehensive unit tests for combinations.

**Alternative 2: Strict prefix-only search (Default behavior)**

- **Pros:** Extremely straightforward to implement and parse. It completely eliminates ambiguity in user intent.
- **Cons:** High friction UX. Users are forced to remember and strictly type specific prefixes (`n/`, `s/`) even for the absolute simplest queries, slowing down natural exploration.

**Alternative 3: Pure universal keyword search**

- **Pros:** Maximum simplicity for both the user interface and the underlying parser.
- **Cons:** Severely lacks precision. Evaluating mathematical bounds (like finding a tutor whose rate is `<50`) or filtering strictly by a subject is practically impossible to guarantee using flat string-matching.

##### Aspect: UI Context and the Query Bar

Standard commands return temporary text feedback. However, a `find` command shows a dynamically built list of Tutors, which risks heavily confusing the user if they forget their search modifiers (especially if no matches are found).

**Alternative 1 (Current choice): Dynamic Query Bar Description**

- **Pros:** Excellent UX. By parsing back the user's query into a human-readable `description` inside the `CommandResult`, the UI permanently displays the active search constraints above the list. It tells the user exactly _why_ the list looks the way it does.
- **Cons / Opportunity costs:** Modifies the standard `CommandResult` object to carry an optional `description` string, mildly coupling what is classically a Logic-tier object to the specific layout needs of the UI tier.

**Alternative 2: Standard text feedback only (e.g., "0 persons listed")**

- **Pros:** Keeps `CommandResult` pure and strictly decoupled from UI-specific display needs, reducing data passing.
- **Cons:** Severe usability flaw. If the user executes a complex filter like `find Math r/<50` and the app just says "0 persons listed" while showing an empty UI, the user immediately loses context of the active constraints governing the screen.

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

| Priority | As a ... | I can ...                                                                   | So that ...                                                         |
| -------- | -------- | --------------------------------------------------------------------------- | ------------------------------------------------------------------- |
| `* * *`  | parent   | add a tutor profile with name, phone, email, subject, rate                  | I can keep track of tutors for my child                             |
| `* * *`  | parent   | view all tutors profiles in a list                                          | I can have an overview of available tutors for my child             |
| `* * *`  | parent   | view a tutor's full profile details                                         | I can evaluate if the tutor is suitable for my child                |
| `* * *`  | parent   | delete a tutor profile                                                      | I can remove outdated or irrelevant tutors                          |
| `* * *`  | parent   | edit a tutor's details                                                      | I can keep tutor information up to date                             |
| `* * *`  | parent   | search tutors by subject                                                    | I can match tutors to my child's academic needs                     |
| `* * *`  | parent   | search tutors by name                                                       | I can quickly locate a specific tutor                               |
| `* * *`  | parent   | search tutors within a rate range                                           | I can shortlist affordable tutors within my budget                  |
| `* *`    | parent   | search tutors by multiple attributes (e.g. name, subject, rate)             | I can refine my search results                                      |
| `* *`    | parent   | search tutors by entering keyword(s)                                        | I can quickly find all tutors that matches my search                |
| `* *`    | parent   | search tutors by entering keyword(s) and narrow down my search with filters | I can have accurate and relevant search results                     |
| `* *`    | parent   | tag tutors with labels                                                      | I can categorise or leave a note on tutors for easier management    |
| `* *`    | parent   | clear all entries                                                           | I can reset the application to an empty state                       |
| `*`      | parent   | sort tutors by ascending or descending rate                                 | I can easily identify the most affordable and most expensive tutors |
| `*`      | parent   | sort tutors by alphabetical order                                           | I can browse through Tuto in a predictable manner                   |

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

#### Use Case: U2. Delete a Tutor from Tuto

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

#### Use Case: U3. Add a Tutor Profile

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
      use case ends
- 2c. A tutor with the same details (Duplicate) already exists.
    - 2c1. `Tuto` shows a duplicate entry error message.
      Use case ends

#### Use Case: U4. Search for Tutors by Subject

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

#### Use Case: U5. Edit a Tutor Profile

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

#### Use Case: U6. Find Tutors

Preconditions: `Tuto` is running

Guarantees: If MSS completes until step 3, `Tuto` searches across all stored Tutor Profiles and displays only the tutors matching the entered keywords and filters

**MSS:**

1. `Parent` enters the find command specifying keyword(s) and/or supported filters.
2. `Tuto` validates the entered details and constructs the search criteria.
3. `Tuto` searches all stored Tutor Profiles and updates the displayed list to the matching tutors.
4. `Tuto` displays the search description and the number of tutors found.
   Use Case ends

**Extensions**

- 2a. The command format is invalid, or the provided filters are not accepted.
    - 2a1. `Tuto` shows an error message.
      Use case ends
- 3a. No Tutor Profiles match the entered criteria.
    - 3a1. `Tuto` displays an empty result list together with the active search description.
      Use case ends

#### Use Case: U7. Sort the Tutor List

Preconditions: `Tuto` is running

Guarantees: If MSS completes until step 3, `Tuto` reorders the currently displayed Tutor Profiles by the specified field and order without adding or removing profiles

**MSS:**

1. `Parent` enters the sort command specifying the field and order.
2. `Tuto` validates the field and order.
3. `Tuto` sorts the currently displayed Tutor Profiles by name or rate in the requested order.
4. `Tuto` displays a confirmation message and updates the displayed sort description.
   Use Case ends

**Extensions**

- 2a. The command format is invalid, or the wrong number of parameters is provided.
    - 2a1. `Tuto` shows an error message.
      Use case ends
- 2b. The specified sort field is invalid.
    - 2b1. `Tuto` shows an error message.
      Use case ends
- 2c. The specified sort order is invalid.
    - 2c1. `Tuto` shows an error message.
      Use case ends

### Non-Functional Requirements

1. The system should work on mainstream operating systems such as Windows, Linux, and macOS, as long as Java `17` or above is installed.
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
- **Parameter**: The specific information provided after a Flag (e.g., John is the parameter for n/).
- **Person**: A data entity representing a tutor profile, containing attributes such as name, phone, email, address, subject, rate and tags.
- **Prefix**: A specific prefix (e.g., n/, p/, e/) used to identify the type of data following it.
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

### Adding a person

1. Adding a person with all fields given
    1. Prerequisites: No person in the list of tutor profiles has the contact number `91234567` and/or email address `jane@example.com`
       Reason: Tuto prohibits the addition of a person whose contact number and/or email address already exists within the current list of tutor profiles.
    2. Test case: `add n/Jane Smith p/91234567 e/jane@example.com a/Clementi 6th Street s/Mathematics r/60 t/friend`<br>
       Expected: A card displaying the newly added tutor details is displayed and the profile is added to list of tutor profiles

2. Adding a person with no fields given
    1. Test case: `add`<br>
       Expected: No person is added. An error message that displays the correct command format and an example is shown

3. Adding a person with any of the mandatory fields (e.g. name, phone number, email, subject, rate) missing
    1. Prerequisites: No person in the list of tutor profiles has the contact number `91234567` and/or email address `jane@example.com`
       Reason: Tuto prohibits the addition of a person whose contact number and/or email address already exists within the current list of tutor profiles.
    2. Test case: `add p/91234567 e/jane@example.com s/Mathematics r/60`<br>
       Expected: No person is added. An error message that displays the correct command format and an example is shown
    3. Test case: `add n/Jane Smith e/jane@example.com s/Mathematics r/60`<br>
       Expected: No person is added. An error message that displays the correct command format and an example is shown
    4. Test case: `add n/Jane Smith p/91234567 s/Mathematics r/60`<br>
       Expected: No person is added. An error message that displays the correct command format and an example is shown
    5. Test case: `add n/Jane Smith p/91234567 e/jane@example.com r/60`<br>
       Expected: No person is added. An error message that displays the correct command format and an example is shown
    6. Test case: `add n/Jane Smith p/91234567 e/jane@example.com s/Mathematics`<br>
       Expected: No person is added. An error message that displays the correct command format and an example is shown

4. Adding a person with any/all optional fields missing
    1. Prerequisites: No person in the list of tutor profiles has the contact number `91234567` and/or email address `jane@example.com`
       Reason: Tuto prohibits the addition of a person whose contact number and/or email address already exists within the current list of tutor profiles.
    2. Test case: `add n/Jane Smith p/91234567 e/jane@example.com s/Mathematics r/60 t/friend`<br>
       Expected: A card displaying the newly added tutor details is displayed and the profile is added to list of tutor profiles
    3. Test case: `add n/Jane Smith p/91234567 e/jane@example.com s/Mathematics r/60`<br>
       Expected: A card displaying the newly added tutor details is displayed and the profile is added to list of tutor profiles
    4. Test case: `add n/Jane Smith p/91234567 e/jane@example.com a/Clementi 6th Street s/Mathematics r/60`<br>
       Expected: A card displaying the newly added tutor details is displayed and the profile is added to list of tutor profiles

5. Adding a person with multiple subjects:
    1. Prerequisites: No person in the list of tutor profiles has the contact number `91234567` and/or email address `jane@example.com`
       Reason: Tuto prohibits the addition of a person whose contact number and/or email address already exists within the current list of tutor profiles.
    2. Test case: `add n/Jane Smith p/91234567 e/jane@example.com a/Clementi 6th Street s/Mathematics s/Korean r/60 t/friend`<br>
       Expected: A card displaying the newly added tutor details is displayed and the profile is added to list of tutor profiles

5b. Adding a person with duplicate subject or tag values in the same command:
    1. Prerequisites: No person in the list of tutor profiles has the contact number `91234567` and/or email address `jane@example.com`
       Reason: Tuto prohibits the addition of a person whose contact number and/or email address already exists within the current list of tutor profiles.
    2. Test case: `add n/Jane Smith p/91234567 e/jane@example.com s/Math s/Math r/60`<br>
       Expected: No person is added. An error message indicating that duplicate subject values were specified is shown.
    3. Test case: `add n/Jane Smith p/91234567 e/jane@example.com s/Math r/60 t/friend t/friend`<br>
       Expected: No person is added. An error message indicating that duplicate tag values were specified is shown.

6. Adding a person with multiple values specified for the same field:
    1. Prerequisites: No person in the list of tutor profiles has the contact number `91234567` and/or email address `jane@example.com`
       Reason: Tuto prohibits the addition of a person whose contact number and/or email address already exists within the current list of tutor profiles.
    2. Test case: `add n/Jane Smith n/jon p/91234567 e/jane@example.com a/Clementi 6th Street s/Mathematics s/Korean r/60 t/friend`<br>
       Expected: No person is added. An error message indicating that multiple values have been specified for the field `n/`
    3. Test case: `add n/Jane Smith p/91234567 p/98765443 e/jane@example.com a/Clementi 6th Street s/Mathematics s/Korean r/60 t/friend`<br>
       Expected: No person is added. An error message indicating that multiple values have been specified for the field `p/`
    4. Test case: `add n/Jane Smith p/91234567 e/jane@example.com e/jon@example.com a/Clementi 6th Street s/Mathematics s/Korean r/60 t/friend`<br>
       Expected: No person is added. An error message indicating that multiple values have been specified for the field `e/`
    5. Test case: `add n/Jane Smith p/91234567 e/jane@example.com a/Clementi 6th Street a/Clementi 7th Street s/Mathematics s/Korean r/60 t/friend`<br>
       Expected: No person is added. An error message indicating that multiple values have been specified for the field `a/`
    6. Test case: `add n/Jane Smith p/91234567 e/jane@example.com a/Clementi 6th Street s/Mathematics s/Korean r/60 r/70 t/friend`<br>
       Expected: No person is added. An error message indicating that multiple values have been specified for the field `r/`

7. Adding a person with inappropriate value for `phone number` field:
    1. Prerequisites: No person in the list of tutor profiles has the contact number `91234567` and/or email address `jane@example.com`
       Reason: Tuto prohibits the addition of a person whose contact number and/or email address already exists within the current list of tutor profiles.
    2. Test case: `add n/Jane Smith p/91s34567 e/jane@example.com a/Clementi 6th Street s/Mathematics s/Korean r/60 t/friend`<br>
       Expected: No person is added. An error message indicating that phone number can only contain numbers and should be at least 3 digits long is shown
    3. Test case: `add n/Jane Smith p/91 e/jane@example.com a/Clementi 6th Street s/Mathematics s/Korean r/60 t/friend`<br>
       Expected: No person is added. An error message indicating that phone number can only contain numbers and should be at least 3 digits long is shown

8. Adding a person with inappropriate value for `rate` field:
    1. Prerequisites: No person in the list of tutor profiles has the contact number `91234567` and email address `jane@example.com`
       Reason: Tuto prohibits the addition of a person whose contact number and/or email address already exists within the current list of tutor profiles.
    2. Test case: `add n/Jane Smith p/91234567 e/jane@example.com a/Clementi 6th Street s/Mathematics r/s t/friend`<br>
       Expected: No person is added. An error message indicating that rates can only contain numbers is shown
    3. Test case: `add n/Jane Smith p/91234567 e/jane@example.com a/Clementi 6th Street s/Mathematics r/-10 t/friend`<br>
       Expected: No person is added. An error message indicating that the rate cannot be negative is shown

9. Adding a person with duplicate phone or email
    1. Prerequisites: A person with phone number `91234567` already exists in the list, and a person with email `jane@example.com` already exists in the list.
    2. Test case: `add n/John Doe p/91234567 e/john@example.com s/Math r/50`<br>
       Expected: No person is added. An error message indicating that a tutor with that phone number already exists is shown.
    3. Test case: `add n/John Doe p/98765432 e/jane@example.com s/Math r/50`<br>
       Expected: No person is added. An error message indicating that a tutor with that email already exists is shown.

### Deleting a person

1. Deleting a person while all persons are being shown
    1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.
    2. Test case: `delete 1`<br>
       Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.
    3. Test case: `delete 0`<br>
       Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.
    4. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
       Expected: Similar to previous.

### Editing a person

1. Editing a person with no fields given
    1. Prerequisites: List all persons using the `list` command. At least one person exists in the list.
    2. Test case: `edit 1`<br>
       Expected: No person is edited. An error message that at least one field to edit must be provided is shown.

2. Editing a person by updating one field at a time
    1. Editing a person's name
        1. Prerequisites: List all persons using the `list` command. At least one person exists in the list.
        2. Test case: `edit 1 n/Jane Doe`<br>
           Expected: The first tutor's name is updated to `Jane Doe`. A success message showing the updated tutor details is displayed.
    2. Editing a person's phone number
        1. Prerequisites: List all persons using the `list` command. No other person in the list has the phone number `91234567`.
        2. Test case: `edit 1 p/91234567`<br>
           Expected: The first tutor's phone number is updated to `91234567`. A success message showing the updated tutor details is displayed.
    3. Editing a person's email
        1. Prerequisites: List all persons using the `list` command. No other person in the list has the email `janedoe@example.com`.
        2. Test case: `edit 1 e/janedoe@example.com`<br>
           Expected: The first tutor's email is updated to `janedoe@example.com`. A success message showing the updated tutor details is displayed.
    4. Editing a person's rate
        1. Prerequisites: List all persons using the `list` command. At least one person exists in the list.
        2. Test case: `edit 1 r/60`<br>
           Expected: The first tutor's rate is updated to `60`. A success message showing the updated tutor details is displayed.
    5. Editing a person's subject
        1. Prerequisites: List all persons using the `list` command. At least one person exists in the list.
        2. Test case: `edit 1 s/Physics`<br>
           Expected: The first tutor's subject is updated to `Physics`. A success message showing the updated tutor details is displayed.
        3. Test case: `edit 1 s/Math s/English`<br>
           Expected: The first tutor's subjects are replaced and overwritten with `Math` and `English`. A success message showing the updated tutor details is displayed.
3. Editing a person with missing values for required fields
    1. Prerequisites: List all persons using the `list` command. At least one person exists in the list.
    2. Test case: `edit 1 n/`<br>
       Expected: No person is edited. An error message indicating that the name cannot be blank is shown.
    3. Test case: `edit 1 p/`<br>
       Expected: No person is edited. An error message indicating that the phone number should be at least 3 digits long.
    4. Test case: `edit 1 e/`<br>
       Expected: No person is edited. An error message indicating that the email should be of the format `local-part@domain` is shown.
    5. Test case: `edit 1 r/`<br>
       Expected: No person is edited. An error message indicating that the rate cannot be blank is shown.
    6. Test case: `edit 1 s/`<br>
       Expected: No person is edited. An error message indicating that the subject cannot be blank is shown.
4. Editing a person with optional fields
    1. Editing a person's address
        1. Prerequisites: List all persons using the `list` command. At least one person exists in the list.
        2. Test case: `edit 1 a/Clementi Ave 3`<br>
           Expected: The first tutor's address is updated to `Clementi Ave 3`. A success message showing the updated tutor details is displayed.
    2. Editing a person's tag
        1. Prerequisites: List all persons using the `list` command. At least one person exists in the list.
        2. Test case: `edit 1 t/friend`<br>
           Expected: The first tutor's tags are replaced and overwritten with only `friend`. A success message showing the updated tutor details is displayed.
    3. Removing an existing address
        1. Prerequisites: List all persons using the `list` command. The first tutor currently has an address.
        2. Test case: `edit 1 a/`<br>
           Expected: The first tutor's address is removed. A success message showing the updated tutor details is displayed. The tutor card shows `Address:` with no value.
    4. Removing an existing tag
        1. Prerequisites: List all persons using the `list` command. The first tutor currently has at least one tag.
        2. Test case: `edit 1 t/`<br>
           Expected: The first tutor's existing tags are removed. A success message showing the updated tutor details is displayed. No tags are shown on the tutor card.
5. Editing multiple fields of a person
    1. Prerequisites: List all persons using the `list` command. No other person in the list has the phone number `91234567` and email `janedoe@example.com`.
    2. Test case: `edit 1 n/Jane Doe p/91234567 e/janedoe@example.com r/60 a/Clementi Ave 3`<br>
       Expected: The specified fields of the first tutor are updated accordingly. A success message showing the updated tutor details is displayed.
6. Editing a person's phone or email to that of an existing tutor
    1. Prerequisites: List all persons using the `list` command. At least two persons exist in the list. Another existing tutor already has phone number `91234567` and email `jane@example.com`.
    2. Test case: `edit 1 p/91234567`<br>
       Expected: No person is edited. A duplicate phone number error message is shown.
    3. Test case: `edit 1 e/jane@example.com`<br>
       Expected: No person is edited. A duplicate email error message is shown.
7. Editing a person with an invalid index
    1. Prerequisites: List all persons using the `list` command. Multiple persons exist in the list, and the list size is smaller than 999.
    2. Test case: `edit 0 n/Jane Doe`<br>
       Expected: No person is edited. An invalid index error message is shown.
    3. Test case: `edit 999 n/Jane Doe`<br>
       Expected: No person is edited. An invalid index error message is shown.
    4. Other incorrect edit commands to try: `edit x`, `edit 1x n/Jane Doe`, `edit -1 n/Jane Doe`<br>
       Expected: No person is edited. An error message that displays the correct command format is shown.
8. Editing with multiple values for single-valued fields
    1. Prerequisites: List all persons using the `list` command. At least one person exists in the list.
    2. Test case: `edit 1 n/Jane Doe n/Mary Doe`<br>
       Expected: No person is edited. An error message indicating that multiple values have been specified for the field `n/` is shown.
    3. Test case: `edit 1 p/91234567 p/98765432`<br>
       Expected: No person is edited. An error message indicating that multiple values have been specified for the field `p/` is shown.
    4. Test case: `edit 1 e/jane@example.com e/mary@example.com`<br>
       Expected: No person is edited. An error message indicating that multiple values have been specified for the field `e/` is shown.
    5. Test case: `edit 1 a/Clementi Ave 3 a/Orchard Road`<br>
       Expected: No person is edited. An error message indicating that multiple values have been specified for the field `a/` is shown.
    6. Test case: `edit 1 r/50 r/60`<br>
       Expected: No person is edited. An error message indicating that multiple values have been specified for the field `r/` is shown.
9. Editing a person with invalid field values
    1. Prerequisites: List all persons using the `list` command. At least one person exists in the list.
    2. Test case: `edit 1 p/91s34567`<br>
       Expected: No person is edited. An error message indicating that phone number can only contain numbers and should be at least 3 digits long is shown.
    3. Test case: `edit 1 p/91`<br>
       Expected: No person is edited. An error message indicating that phone number can only contain numbers and should be at least 3 digits long is shown.
    4. Test case: `edit 1 e/invalid-email`<br>
       Expected: No person is edited. An error message indicating that emails should be of the format `local-part@domain` is shown.
    5. Test case: `edit 1 r/s`<br>
       Expected: No person is edited. An error message indicating that rates can only contain numbers is shown.
    6. Test case: `edit 1 r/-5`<br>
       Expected: No person is edited. An error message indicating that the rate cannot be negative is shown.
10. Editing a person without changing any actual value
    1. Prerequisites: List all persons using the `list` command. The first tutor already has the name `Jane Doe`.
    2. Test case: `edit 1 n/Jane Doe`<br>
       Expected: A success message showing the updated tutor details is displayed. The tutor profile remains unchanged.

### Finding a person

#### Negative Cases & Error Handling

1. Finding when the contact list is empty
    1. Prerequisites: Tuto must have zero Tutor contacts saved. Execute `clear` to remove all existing contacts.
    2. Test case: `find geography`<br>
       Expected: A successful search occurs, but with no matches. The text feedback area is hidden. The blue search query bar should show `All fields: "geography"`. Below it, the result display list in the UI shows the placeholder text `No tutors found.`.
2. Attempting to restrict universal search with unsupported prefixes (Constraint Error)
    1. Prerequisites: Tuto is running (contents of the list do not matter).
    2. Test case: `find John p/91234567`<br>
       Expected: No search is performed. Instead of the result display list and query bar, the text feedback area is shown displaying an error with a red cross icon indicating that unsupported flags (`p/`) exist when using universal search. The blue search query bar and result list are hidden.
3. Executing find without any parameters (Format Error)
    1. Prerequisites: Tuto is running.
    2. Test case: `find`<br>
       Expected: No search is performed. The text feedback area is shown displaying an error about invalid command format. The blue search query bar and result list remain hidden.

#### Positive Cases (Universal Search)

1. Finding persons using universal search (General keywords)
    1. Prerequisites: Ensure the contact list has at least these two Tutors with these specific attributes (the rest can be any permitted value):
    - Name: "John Doe", Subject: "Math", Rate: "50"
        - Name: "Jane Doe", Subject: "Physics", Rate: "60"
    2. Test case: `find Doe`<br>
       Expected: The text feedback area is hidden. The blue search query bar should appear and show `All fields: "Doe"`. Below it, the list is updated to display the full tutor profiles of both 'John Doe' and 'Jane Doe' (and any other tutor profiles that contain the word `Doe`).
2. Finding a person using universal search with no matching results
    1. Prerequisites: Ensure the contact list does not have anyone teaching "Chemistry" or containing the word "Chemistry" in any attribute field.
    2. Test case: `find Chemistry`<br>
       Expected: The text feedback area is hidden. The blue search query bar should show `All fields: "Chemistry"`. Below it, the result list shows the placeholder text `No tutors found.`.

#### Complex Cases (Attribute Filtering & Combinations)

1. Finding persons using specific attribute filtering with inequalities
    1. Prerequisites: Ensure the contact list has at least these Tutors with these specific attributes (the rest can be any permitted value subject to uniqueness constraint):
    - Name: "Alice", Subject: "Physics", Rate: "40"
        - Name: "Bob", Subject: "Physics", Rate: "70"
    2. Test case: `find s/Physics r/<50`<br>
       Expected: The text feedback area is hidden. The blue search query bar should show `Subject: "Physics" • Rate: "<50"`. The result display list should show the tutor profile for "Alice" (and other tutor profiles if condition matches), as Bob is filtered out due to his rate.
2. Finding persons using multiple instances of the same attribute type (AND / OR Logic checks)
    1. Prerequisites: Ensure the contact list has at least these Tutors with these specific attributes (the rest can be any permitted value subject to uniqueness constraint):
    - Name: "Charlie", Subject: "Math", "Chemistry"
        - Name: "David", Subject: "Chemistry", "Physics"
        - Name: "David", Subject: "Math"
    2. Test case: `find n/Charlie David s/Math s/Chemistry`<br>
       Expected: The text feedback area is hidden. The blue search query bar should show `Name: "Charlie, David" • Subject: "Math, Chemistry"`. The result list displays only the tutor profile for "Charlie". The name prefix evaluates multiple words via OR logic (allowing either Charlie or David), but the multiple subject prefixes evaluate via AND logic simultaneously (requiring both Math AND Chemistry to be taught by the same tutor to pass).
3. Finding a person combining universal search and specific attribute filtering (AND Logic)
    1. Prerequisites: Ensure the contact list has at least these Tutors with these specific attributes (the rest can be any permitted value subject to uniqueness constraint):
    - Name: "Eve", Subject: "Math", Rate: "40"
        - Name: "Eve", Subject: "Physics", Rate: "60"
    2. Test case: `find Eve r/40-50`<br>
       Expected: The text feedback area is hidden. The blue search query bar shows `All fields: "Eve" • Rate: "40-50"`. The result list shows only the tutor profile for "Eve" with the 'Math' subject and rate of 40 (and any other matching Tutor profiles), conforming to keywords and the defined rate boundaries successfully.

#### Adversarial & Edge Cases

1. Finding using multiple invalid format prefixes (Duplicate prefix errors)
    1. Prerequisites: Tuto is running.
    2. Test case: `find n/Alice n/Bob r/40 r/50`<br>
       Expected: No search is performed. The text feedback area displays a red cross error indicating that multiple values were specified for single-valued fields (Name and Rate). The blue search query bar and result display list remain hidden.
2. Finding with trailing/leading whitespaces and empty attribute values
    1. Prerequisites: Tuto is running (contents of the list do not matter).
    2. Test case: `find s/`<br>
       Expected: No search is performed. The text feedback area displays an error about invalid subject formatting (Subject cannot be empty). The blue search query bar and result list remain hidden.
    3. Test case: `find [any amount of whitespaces given] Alice`<br>
       Expected: A successful search occurs. Extraneous surrounding spaces are ignored. The blue search query bar shows `All fields: "Alice"` and standard UI display behavior triggers showing all matching tutor profiles.
    4. Test case: `find n/ [any amount of whitespaces given] Alice [any amount of whitespaces given] r/ [any amount of whitespaces given] 50`<br>
       Expected: A successful search occurs. Arbitrary amounts of spaces between prefixes and values do not affect the parsing. The blue search query bar shows `Name: "Alice" • Rate: "50"`.
3. Finding with different ordering of parameters
    1. Prerequisites: Ensure the contact list has at least one Tutor with the name "Alice", teaching "Math", with a rate of "50".
    2. Test case: `find r/50 s/Math n/Alice`<br>
       Expected: A successful search occurs. The ordering of prefixes does not matter. The blue search query bar shows `Name: "Alice" • Subject: "Math" • Rate: "50"`. The result list displays the matching tutor profile.
    3. Test case: `find s/Math r/50 n/Alice`<br>
       Expected: Same as above. The blue search query bar and result list remain consistent regardless of the input order.
4. Stress testing the find logic (extreme number of keywords/prefixes)
    1. Prerequisites: Tuto is running.
    2. Test case: `find` followed by pasting a very long string of 10,000 alphanumeric characters without spaces.<br>
       Expected: The system should not crash or freeze. It will search for the exact 10,000-character keyword. The blue search query bar safely displays the truncated string or full string. Results will be shown if any tutor profile contains the search keyword
    3. Test case: `find n/Alice` repeated 100 times.<br>
       Expected: No search is performed. The text feedback area displays an error indicating multiple values specified for single-valued fields (Name), preventing resource exhaustion.
5. Finding with nonsensical rate boundary formatting
    1. Prerequisites: Tuto is running.
    2. Test case: `find r/ABC`<br>
       Expected: No search is performed. The text feedback area displays an error indicating invalid rate format for the find command. The blue search query bar and result list remain hidden.
    3. Test case: `find r/50-40`<br>
       Expected: No search is performed. The text feedback area displays an error indicating that for a rate range, the lower bound cannot be strictly greater than the upper bound. The blue search query bar and result list remain hidden.
6. Rate numeric bounds (very large values)
    1. Prerequisites: Tuto is running.
    2. Test case: `find r/>9999999999999`<br>
       Expected: The system should not crash. Rate bounds for `find` are parsed as decimal values, the search proceeds with the given bound or shows a format error if the value is invalid.
7. Prefix case sensitivity and preamble swallowing (Mistyping prefixes)
    1. Prerequisites: Ensure the contact list has at least one Tutor with the name "Alice".
    2. Test case: `find N/Alice`<br>
       Expected: The text feedback area is hidden. Because `N/` (capitalized) is not recognized as the official name prefix, the parser treats it as a generic keyword instead The blue search query bar shows `All fields: "N/Alice"`. Below it, the result list will show `No tutors found.` (unless a tutor profile literally contains "N/Alice").
8. Regex and Special Character injection attempts
    1. Prerequisites: Tuto is running.
    2. Test case: `find *[a-z]+* ?.* {}`<br>
       Expected: The system should not crash due to regex compilation errors or illegal character parsing. The text feedback area is hidden. The blue search query bar safely escapes and displays `All fields: "*[a-z]+* ?.* {}"`. The result list predictably shows `No tutors found.` as it interprets the inputs as exact string literals rather than executable regular expressions.
9. Conflicting valid constraints (Mathematically/Logically impossible sets)
    1. Prerequisites: Ensure the contact list has a tutor profile named "Jane" with a rate of `50` and subject `Math`.
    2. Test case: `find Jane r/<30`<br>
       Expected: A successful search occurs. The text feedback area is hidden. The blue search query bar shows `All fields: "Jane" • Rate: "<30"`. The result list displays `No tutors found.`. This definitively proves that even if the universal search positively matches the tutor profile "Jane", the restrictive rate filter acts as a pure AND gate to forcefully negate the inclusion, ensuring no false positives slip through.
10. Backward slashes and parser confusion techniques
    1. Prerequisites: Tuto is running.
    2. Test case: `find /n Alice /s Math \t friend`<br>
       Expected: No search is performed. The system strictly expects `n/`, `s/`, etc. as prefixes and subject should only be alphanumeric. Since the slashes are inverted or prefixed, the parser treats it as a generic keyword. The blue search query bar shows `All fields: "/n Alice /s Math \t friend"`. Below it, the result list will show tutor records that contains parts of the search keywords.
11. Empty prefix values injected before valid constraints (Syntax gap traps)
    1. Prerequisites: Tuto is running.
    2. Test case: `find n/ s/Math r/50`<br>
       Expected: No search is performed. The text feedback area displays a constraint error stating that the Name cannot be empty (or violates validation rules). The parser successfully detects the missing value for `n/` despite `s/` immediately following it. The blue search query bar and result list remain hidden.
12. Rate attribute formatting anomalies (Zero-padding and negatives)
    1. Prerequisites: Ensure the contact list contains a tutor profile with a rate of `0`.
    2. Test case: `find r/00000000`<br>
       Expected: A successful search occurs. The parser evaluates the heavily padded string as the numeric value `0`. The blue search query bar shows `Rate: "00000000"` (or mapped equivalent) and lists the matched tutor profiles matching the exact rate of 0.
    3. Test case: `find r/-5` or `find r/--50`<br>
       Expected: No search is performed. The text feedback area displays a red cross error strongly preventing negative rates or invalid mathematical delimiters.

### Sorting the tutor list

See also: [Sorting the Tutor List](UserGuide.md#sorting-the-tutor-list-sort) in the User Guide for command format. Sorting only changes the order of the **currently displayed** tutors; it does not add or remove entries. Because `edit` and `delete` use the index shown in the list, running `sort` changes which tutor is at each index.

1. Sorting by name in ascending order
    1. Prerequisites: Use `list` so all tutors are shown. Ensure there are at least three tutors whose names sort distinctly when compared case-insensitively (e.g. "Amy", "Bob", "Carl").
    2. Test case: `sort name asc`<br>
       Expected: The result display shows a success message such as `Sorted tutors by name in ascending order!` The Tutor List Panel lists tutors from A → Z by full name (case-insensitive). The header above the list indicates sorting by name (ascending).
2. Sorting by name in descending order
    1. Prerequisites: Same as test case 1.
    2. Test case: `sort name desc`<br>
       Expected: Success message mentions name and **descending** order. The list shows tutors from Z → A by full name. The list header reflects descending name order.
3. Sorting by rate in ascending order
    1. Prerequisites: Use `list` so all tutors are shown. Ensure there are at least three tutors with **different** hourly rates (decimal rates count as distinct numeric values where applicable).
    2. Test case: `sort rate asc`<br>
       Expected: Success message mentions rate and **ascending** order. The list shows lowest rate first, then increasing. The list header reflects sorting by rate (ascending).
4. Sorting by rate in descending order
    1. Prerequisites: Same as test case 3.
    2. Test case: `sort rate desc`<br>
       Expected: Success message mentions rate and **descending** order. The list shows highest rate first. The list header reflects sorting by rate (descending).
5. Tie-break when two tutors share the same rate
    1. Prerequisites: Use `list`. Ensure two tutors have the **same** rate but **different** names (e.g. rate `50`, names "Zoe" and "Amy"). Optionally add a third tutor with a different rate so ordering is easier to see.
    2. Test case: `sort rate asc`<br>
       Expected: Tutors are ordered by rate as usual; for the two with the same rate, the one whose name comes first alphabetically (case-insensitive) appears above the other (e.g. "Amy" before "Zoe").
6. Case-insensitive field and order tokens
    1. Prerequisites: Use `list` with multiple tutors.
    2. Test case: `sort NAME ASC` or `sort Rate Desc`<br>
       Expected: Same behaviour as `sort name asc` or `sort rate desc` respectively (tokens may be typed in any mixture of letter cases).
7. Sorting after a filtered list (`find`)
    1. Prerequisites: The address book has several tutors; a `find` command matches only **some** of them (e.g. same subject keyword). Run that `find` so the list shows a subset.
    2. Test case: `sort name asc`<br>
       Expected: Only the tutors still visible after `find` are reordered; the count of tutors shown stays the same as right before `sort`. The sort applies to the displayed subset, not the full address book.
8. Listing all tutors after sorting (`list` does not clear sort)
    1. Prerequisites: Run `sort rate desc` while viewing the full list (`list`).
    2. Test case: `list`<br>
       Expected: All tutors in the address book are shown again (any previous `find` filter is cleared). The **order** remains by rate descending (or whichever sort was last applied); the list header still describes the active sort. Success message is along the lines of `Listed all tutors!`

#### Invalid commands and errors

1. Empty or wrong number of parameters
    1. Prerequisites: Tuto is running.
    2. Test case: `sort`<br>
       Expected: No change to list order. An error explains invalid command format and shows usage for `sort`.
    3. Test case: `sort name`<br>
       Expected: No change to list order. Error message states that sort expects exactly two parameters (field and order), with usage.
    4. Test case: `sort name asc extra`<br>
       Expected: Same as the previous (wrong token count).
2. Invalid sort field or order
    1. Prerequisites: Tuto is running.
    2. Test case: `sort foo asc`<br>
       Expected: No change to list order. Error message states that the sort field is invalid and only `name` and `rate` are allowed.
    3. Test case: `sort name ascending`<br>
       Expected: No change to list order. Error message states that the sort order is invalid and only `asc` and `desc` are allowed.

### Saving data

1. **Missing data file**
    1. Prerequisites: Tuto is not running.
    2. Delete `data/addressbook.json`, or move it aside. You may delete the whole `data` directory instead if it only contains that file.
    3. Relaunch Tuto.
    4. Expected:
    - The app starts normally.
        - The tutor list shows the **built-in sample tutors**, not an empty list.
        - The in-memory data is that sample set until the next save.
        - A new `data/addressbook.json` appears after a command that persists data (any normal command that saves).
2. **Corrupted or unreadable data file**
    1. Prerequisites: Tuto is not running.
    2. Open `data/addressbook.json` in a text editor. Replace the entire file with invalid JSON (for example a single `{` or arbitrary non-JSON text). Save.
    3. Relaunch Tuto.
    4. Expected:
    - The app starts without crashing.
        - The tutor list is **empty**; the bad file is not loaded as valid data.
        - After a successful save, the file on disk is overwritten with valid JSON for the current in-memory book (usually empty until you add tutors).
3. Adversarial case of poisoned JSON file: invalid but well-formed JSON (passes parsing, fails validation/model rules)
    1. Prerequisites: Tuto is not running.
    2. Open `data/addressbook.json` in a text editor. Enter valid JSON such as an object with duplicate tutors in the `persons` array (i.e. two identical tutor entries). Save.
    3. Relaunch Tuto.
    4. Expected:
    - The app starts without crashing.
        - The tutor list is **empty** (no tutors are shown), as the file violates model rules.
        - After a successful save, the file on disk is overwritten with valid JSON for the current in-memory book (usually empty until you add tutors).
