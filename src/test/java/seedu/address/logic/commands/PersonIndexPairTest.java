package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TestUtil.makePerson;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonIndexPairTest {

    @Test
    public void equals_reflexive() {
        Person p = makePerson("Alice");
        CommandResult.PersonIndexPair a = new CommandResult.PersonIndexPair(p, 1);
        assertTrue(a.equals(a));
    }

    @Test
    public void equals_symmetricAndTransitive() {
        Person p = makePerson("Alice");
        CommandResult.PersonIndexPair a = new CommandResult.PersonIndexPair(p, 1);
        CommandResult.PersonIndexPair b = new CommandResult.PersonIndexPair(new PersonBuilder(p).build(), 1);
        CommandResult.PersonIndexPair c = new CommandResult.PersonIndexPair(new PersonBuilder(p).build(), 1);

        // symmetric
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));

        // transitive
        assertTrue(a.equals(b) && b.equals(c) && a.equals(c));
    }

    @Test
    public void equals_nullAndDifferentType() {
        Person p = makePerson("Alice");
        CommandResult.PersonIndexPair a = new CommandResult.PersonIndexPair(p, 1);
        assertFalse(a.equals(null));
        assertFalse(a.equals("not a pair"));
    }

    @Test
    public void equals_differentPersonOrIndex() {
        Person p = makePerson("Alice");
        CommandResult.PersonIndexPair a = new CommandResult.PersonIndexPair(p, 1);

        Person other = makePerson("Bob");
        CommandResult.PersonIndexPair differentPersonSameIndex = new CommandResult.PersonIndexPair(other, 1);
        assertFalse(a.equals(differentPersonSameIndex));

        CommandResult.PersonIndexPair samePersonDifferentIndex = new CommandResult.PersonIndexPair(p, 2);
        assertFalse(a.equals(samePersonDifferentIndex));
    }

    @Test
    public void hashCode_consistentWithEquals() {
        Person p = makePerson("Alice");
        CommandResult.PersonIndexPair a = new CommandResult.PersonIndexPair(p, 1);
        CommandResult.PersonIndexPair b = new CommandResult.PersonIndexPair(new PersonBuilder(p).build(), 1);
        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void toString_containsIndexAndName() {
        Person p = makePerson("Charlie");
        CommandResult.PersonIndexPair pair = new CommandResult.PersonIndexPair(p, 7);
        String expected = "7. " + p.getName();
        assertEquals(expected, pair.toString());
    }
}

