package model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ModelTest {

    Model M1= new Model();

    @Test
    void testBox() {
        assertEquals(true, M1.addWorld("w1"));
        assertEquals(false, M1.addWorld("w1"));
        assertEquals(true, M1.addAgent("Alice"));
        assertEquals(false, M1.addAgent("Alice"));
        M1.getAgent("Alice");
        assertEquals(true, M1.addWorld("w2"));
        assertEquals(true, M1.addAtom("p"));
        M1.setTrueAt("p", "w1");
        assertEquals(true, M1.addRelation("Alice", "w1", "w2"));
        assertEquals(true, M1.valuation("p", "w1"));
        M1.toggleTruthAt("p", "w1");
        assertEquals(false, M1.valuation("p", "w1"));

        String s1= "w1";
        String s2= "w1";
        assertEquals(true, s1.contentEquals(s2));
    }

    @Test
    void testFindCP() {
        assertEquals(1, M1.findCP("()"));
        assertEquals(5, M1.findCP("(    )"));
        assertEquals(3, M1.findCP("(())"));
        assertEquals(8, M1.findCP("(a -> ())"));
        assertEquals(3, M1.findCP("(()) -> a"));
        assertEquals(2, M1.findCP("~()"));
        assertEquals(3, M1.findCP("[]()"));
    }

    @Test
    void testNegation() {
        M1.addWorld("w");
        M1.addAtom("p");
        M1.setTrueAt("p", "w");
        assertEquals(1, M1.getWorld("w").isValid("p"));
        assertEquals(0, M1.getWorld("w").isValid("~p"));
    }

    @Test
    void testConj() {
        M1.addWorld("w");
        M1.addAtom("p");
        M1.addAtom("q");
        M1.setTrueAt("p", "w");
        M1.setTrueAt("q", "w");
        assertEquals(1, M1.getWorld("w").conj("p", "q"));
        assertEquals(0, M1.getWorld("w").conj("~p", "q"));
        assertEquals(0, M1.getWorld("w").conj("p", "~q"));
        assertEquals(0, M1.getWorld("w").conj("~p", "~q"));
        M1.toggleTruthAt("p", "w");
        assertEquals(false, M1.valuation("p", "w"));
        assertEquals(0, M1.getWorld("w").conj("p", "q"));
        assertEquals(1, M1.getWorld("w").conj("~p", "q"));
        assertEquals(0, M1.getWorld("w").conj("p", "~q"));
        assertEquals(0, M1.getWorld("w").conj("~p", "~q"));
        M1.toggleTruthAt("p", "w");
        M1.toggleTruthAt("q", "w");
        assertEquals(0, M1.getWorld("w").conj("p", "q"));
        assertEquals(0, M1.getWorld("w").conj("~p", "q"));
        assertEquals(1, M1.getWorld("w").conj("p", "~q"));
        assertEquals(0, M1.getWorld("w").conj("~p", "~q"));
    }

    @Test
    void testisValidConj() {
        M1.addWorld("w");
        M1.addAtom("p");
        M1.addAtom("q");
        M1.setTrueAt("p", "w");
        M1.setTrueAt("q", "w");
        assertEquals(1, M1.getWorld("w").isValid("p & q"));
        assertEquals(0, M1.getWorld("w").isValid("~p & q"));
        assertEquals(0, M1.getWorld("w").isValid("p & ~q"));
        assertEquals(0, M1.getWorld("w").isValid("~p & ~q"));
        M1.toggleTruthAt("p", "w");
        assertEquals(false, M1.valuation("p", "w"));
        assertEquals(0, M1.getWorld("w").isValid("p & q"));
        assertEquals(1, M1.getWorld("w").isValid("~p & q"));
        assertEquals(0, M1.getWorld("w").isValid("p & ~q"));
        assertEquals(0, M1.getWorld("w").isValid("~p & ~q"));
        M1.toggleTruthAt("p", "w");
        M1.toggleTruthAt("q", "w");
        assertEquals(0, M1.getWorld("w").isValid("p & q"));
        assertEquals(0, M1.getWorld("w").isValid("~p & q"));
        assertEquals(1, M1.getWorld("w").isValid("p & ~q"));
        assertEquals(0, M1.getWorld("w").isValid("~p & ~q"));
        M1.toggleTruthAt("p", "w");
        assertEquals(0, M1.getWorld("w").isValid("p & q"));
        assertEquals(0, M1.getWorld("w").isValid("~p & q"));
        assertEquals(0, M1.getWorld("w").isValid("p & ~q"));
        assertEquals(1, M1.getWorld("w").isValid("~p & ~q"));
    }

    @Test
    void testisValidDisj() {
        M1.addWorld("w");
        M1.addAtom("p");
        M1.addAtom("q");
        M1.setTrueAt("p", "w");
        M1.setTrueAt("q", "w");
        assertEquals(1, M1.getWorld("w").isValid("p | q"));
        assertEquals(1, M1.getWorld("w").isValid("~p | q"));
        assertEquals(1, M1.getWorld("w").isValid("p | ~q"));
        assertEquals(0, M1.getWorld("w").isValid("~p | ~q"));
        M1.toggleTruthAt("p", "w");
        assertEquals(false, M1.valuation("p", "w"));
        assertEquals(1, M1.getWorld("w").isValid("p | q"));
        assertEquals(1, M1.getWorld("w").isValid("~p | q"));
        assertEquals(0, M1.getWorld("w").isValid("p | ~q"));
        assertEquals(1, M1.getWorld("w").isValid("~p | ~q"));
        M1.toggleTruthAt("p", "w");
        M1.toggleTruthAt("q", "w");
        assertEquals(1, M1.getWorld("w").isValid("p | q"));
        assertEquals(0, M1.getWorld("w").isValid("~p | q"));
        assertEquals(1, M1.getWorld("w").isValid("p | ~q"));
        assertEquals(1, M1.getWorld("w").isValid("~p | ~q"));
        M1.toggleTruthAt("p", "w");
        assertEquals(0, M1.getWorld("w").isValid("p | q"));
        assertEquals(1, M1.getWorld("w").isValid("~p | q"));
        assertEquals(1, M1.getWorld("w").isValid("p | ~q"));
        assertEquals(1, M1.getWorld("w").isValid("~p | ~q"));
    }

    @Test
    void testNegCompl() {
        M1.addWorld("w");
        M1.addAtom("p");
        M1.addAtom("q");
        assertEquals(1, M1.getWorld("w").isValid("~(p & q)"));
        M1.setTrueAt("p", "w");
        M1.setTrueAt("q", "w");
        assertEquals(0, M1.getWorld("w").isValid("~(p | q)"));
        assertEquals(1, M1.getWorld("w").isValid("~(p -> ~q)"));
        assertEquals(0, M1.getWorld("w").isValid("~(~q <-> ~p)"));
        assertEquals(0, M1.getWorld("w").isValid("p & ~(p -> q)"));
        assertEquals(0, M1.getWorld("w").isValid("~(p -> q) & p"));
    }

    @Test
    void testParen() {
        M1.addWorld("w");
        M1.addAtom("p");
        M1.addAtom("q");
        assertEquals(0, M1.getWorld("w").isValid("(p & q)"));
        M1.setTrueAt("p", "w");
        M1.setTrueAt("q", "w");
        assertEquals(0, M1.getWorld("w").isValid("~((~q <-> ~p))"));
        assertEquals(0, M1.getWorld("w").isValid("(p & ~(p -> q))"));
        assertEquals(1, M1.getWorld("w").isValid("(~q <-> ~p) -> p"));
        assertEquals(1, M1.getWorld("w").isValid("((p -> q) <-> p) -> q"));
    }

    @Test
    void testKnows() {
        M1.addWorld("w");
        M1.addWorld("v");
        M1.addWorld("u");
        M1.addAtom("p");
        M1.addAtom("q");
        M1.addAgent("Alice");
        M1.setTrueAt("p", "w");
        M1.setTrueAt("q", "v");
        assertEquals(1, M1.getWorld("w").knows("Alice", "p"));
        assertEquals(0, M1.getWorld("v").knows("Alice", "p"));
        assertEquals(1, M1.getWorld("w").knows("Alice", "p | q"));
        M1.addRelation("Alice", "w", "v");
        assertEquals(0, M1.getWorld("v").knows("Alice", "p"));
        assertEquals(0, M1.getWorld("v").might("Alice", "p"));
        assertEquals(1, M1.getWorld("w").might("Alice", "p"));
        assertEquals(0, M1.getWorld("v").knows("Alice", "p"));
        M1.addRelation("Alice", "w", "u");
        M1.setTrueAt("p", "v");
        M1.setTrueAt("p", "u");
        assertEquals(1, M1.getWorld("w").knows("Alice", "p"));
        assertEquals(1, M1.getWorld("v").might("Alice", "q"));
        assertEquals(1, M1.getWorld("w").might("Alice", "q"));
    }
}
