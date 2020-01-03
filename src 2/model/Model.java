package model;

import java.util.HashMap;
import java.util.HashSet;

public class Model {

    // This set contains every world in this model
    public HashSet<World> W= new HashSet<>();
    public HashSet<Atom> V= new HashSet<>();
    public HashSet<Agent> A= new HashSet<>();

    /** Returns true iff the agent with name aName has access to v, <br>
     * the world with name vName, from w, the world with name wName. */
    public boolean R(String aName, String wName, String vName) {
        Agent a= getAgent(aName);
        World w1= getWorld(wName);
        World w2= getWorld(vName);
        if (a != null && w1 != null && w2 != null) return a.R.get(w1).contains(w2);
        return false;
    }

    /** Returns true iff the atom with name pName is true at the world with name wName */
    public boolean valuation(String pName, String wName) {
        Atom p= getAtom(pName);
        World w= getWorld(wName);
        return p != null && w != null && p.trueAt.contains(wName);
    }

    /** This method sets an atom with the given name true at the given world <br>
     * If an atom and world with the given names exist */
    public void setTrueAt(String pName, String wName) {
        Atom p= getAtom(pName);
        if (p != null) {
            if (getWorld(wName) != null) p.trueAt.add(wName);
        }
    }

    /** This method toggles the truth valuation of the atom with name pName <br>
     * at the world with name wName, if they exist */
    public void toggleTruthAt(String pName, String wName) {
        Atom p= getAtom(pName);
        if (p != null) {
            World w= getWorld(wName);
            if (w != null) {
                if (p.trueAt.contains(wName)) p.trueAt.remove(wName);
                else p.trueAt.add(wName);
            }
        }
    }

    /** Adds an arrow from the world with name w1name to the world <br>
     * with name w2name via agent a's accessibility relation, if all three <br>
     * exist. Returns true iff the action is successfully executed. */
    public boolean addRelation(String aName, String w1name, String w2name) {
        Agent a= getAgent(aName);
        if (a != null && getWorld(w1name) != null && getWorld(w2name) != null) {
            a.R.get(w1name).add(w2name);
            return true;
        }
        return false;
    }

    /** This method adds a new World to this model if it doesn't already exist <br>
     * It returns true if the world is added, false otherwise */
    public boolean addWorld(String name) {
        for (World w : W) {
            if (w.name.contentEquals(name)) return false;
        }
        W.add(new World(name));
        HashSet<String> vSet;
        for (Agent a : A) {
            vSet= new HashSet<>();
            vSet.add(name);
            a.R.put(name, vSet);
        }
        return true;
    }

    /** This method creates a new atom in this model, if it does not already exist <br>
     * It returns true if the world is added, false otherwise */
    public boolean addAtom(String name) {
        if (hasSpace(name) || name.charAt(0) == 'K' || name.contentEquals("")) return false;
        for (Atom p : V) {
            if (p.name.contentEquals(name)) return false;
        }
        V.add(new Atom(name));
        return true;
    }

    /** This method adds a new agent to this model, if one doesn't already exist with <br>
     * the same name. It returns true if one is added, false otherwise. */
    public boolean addAgent(String name) {
        for (Agent a : A) {
            if (a.name.contentEquals(name)) return false;
        }
        A.add(new Agent(name));
        return true;
    }

    /** This method returns the atom with the specified name, if one exists; null otherwise */
    public Atom getAtom(String name) {
        for (Atom p : V) {
            if (p.name.contentEquals(name)) return p;
        }
        return null;
    }

    /** This method returns the atom with the specified name, if one exists; null otherwise */
    public World getWorld(String name) {
        for (World w : W) {
            if (w.name.contentEquals(name)) return w;
        }
        return null;
    }

    /** This method returns the atom with the specified name, if one exists; null otherwise */
    public Agent getAgent(String name) {
        for (Agent a : A) {
            if (a.name.contentEquals(name)) return a;
        }
        return null;
    }

    public class World {
        public String name;

        /** Constructor with a name */
        public World(String name) {
            this.name= name;
            for (Agent a : A) {
                a.R.put(name, new HashSet<>());
            }
        }

        public String getName() {
            return name;
        }

        public boolean knows(String a, String p) {
            HashSet<String> pTrueAt= new HashSet<>(getAtom(p).trueAt);
            for (String w : getAgent(a).R.get(name)) {
                if (!pTrueAt.contains(w)) return false;
            }
            return true;
        }

        /** This function evaluates whether or not a formula is true or false at a given world. */
        public boolean isValid(String f) {
            // TODO: implement this method. It must
            // (1) be able to parse a string into different components that can be
            // evaluated recursively, and
            // (2) evaluate those components and return their truth value
            // (or implement a method that does)
            // if (f.contentEquals(" ")) return false;
            String f1= "";
            char f0= f.charAt(0);
            System.out.println("f is " + f);
            if (!hasSpace(f)) {
                System.out.println("f is an atom or its negation");
                Atom p;
                if (f0 == '~') {
                    p= getAtom(f.substring(1));
                    return !isValid(f.substring(1));
                }
                p= getAtom(f);
                return p != null ? p.trueAt.contains(name) : false;
            } else {
                System.out.println("f is a compound formula");
                int len= f.length();
                char[] fCA= f.toCharArray();
                // Invariant: f is of the form "~(" + formula1 + " " + formula 2 + ")"
                if (f0 == '~') {
                    if (fCA[1] == '~') {
                        System.out.println("f is a double negation");
                        return !isValid(f.substring(1));
                    }
                    if (fCA[1] == '(') {
                        System.out.println("f is the negation of an enclosed formula");
                        // System.out.println(f.substring(2, len - 1));
                        int cParen= findCP(f.substring(1)) + 1;
                        // System.out.println(cParen);
                        // System.out.println(fCA[len - 1]);
                        if (cParen == len - 1) return !isValid(f.substring(2, cParen));
                        // TODO: implement the method in the case that the ~() formula was just a
                        // building block.
                        // Invariant: f is of the form ~() & f2
                        if (fCA[cParen + 2] == '&')
                            return conj(f.substring(0, cParen), f.substring(cParen + 4));
                    }
                }
                if (f0 == '(') {
                    // TODO 2
                    System.out.println("This is a parenthetical formula");
                }
                if (f0 == 'K') {
                    System.out.println("f is a modal formula");
                    String agent= "";
                    for (int i= 1; i < len; i++ ) {
                        if (fCA[i + 2] == ' ') { return knows(agent, f.substring(i + 3)); }
                    }
                }
                System.out.println("f is the junction of two formulas");
                for (int i= 0; i < len; i++ ) {
                    if (fCA[i] == ' ') {
                        char opp= fCA[i + 1];
                        System.out.println(opp);
                        if (opp == '&') {
                            System.out.println(f1);
                            System.out.println(f.substring(i + 3));
                            return conj(f1, f.substring(i + 3));
                        }
                        if (opp == '|') {
                            System.out.println(f);
                            return disj(f1, f.substring(i + 3));
                        }
                        if (opp == '-' && fCA[i + 2] == '>')
                            return implies(f1, f.substring(i + 4));
                        if (opp == '<' && fCA[i + 2] == '-' && fCA[i + 3] == '>')
                            return isValid(f1) == isValid(f.substring(i + 5));
                    }
                    f1= f1 + fCA[i];
                    System.out.println(f1);
                }
            }
            return false;
        }

        public boolean conj(String f1, String f2) {
            return isValid(f1) && isValid(f2);
        }

        public boolean disj(String f1, String f2) {
            return isValid(f1) || isValid(f2);
        }

        public boolean implies(String f1, String f2) {
            return !isValid(f1) || isValid(f2);
        }

        public boolean iff(String f1, String f2) {
            return isValid(f1) == isValid(f2);
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o.getClass() == this.getClass()) {
                World q= (World) o;
                return name == q.name;
            } else return false;
        }
    }

    public class Atom {
        public String name;
        HashSet<String> trueAt= new HashSet<>();

        /** Constructor with a name */
        public Atom(String name) {
            this.name= name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o.getClass() == this.getClass()) {
                Atom q= (Atom) o;
                return name == q.name;
            } else return false;
        }
    }

    public class Agent {
        String name;
        /** This maps each world to a set of worlds that this agent has access to from that world.
         * <br>
         * Invariant: each world maps to a HashSet, but that HashSet may be empty. */
        public HashMap<String, HashSet<String>> R= new HashMap<>();

        /** Constructor with a name */
        public Agent(String name) {
            this.name= name;
            HashSet<String> vSet;
            for (World w : W) {
                String wName= w.getName();
                vSet= new HashSet<>();
                vSet.add(wName);
                R.put(wName, vSet);
            }
        }

        public String getName() {
            return name;
        }

        /** Precondition: w is not null */
        public String getVSet(String wName) {
            return R.get(wName).toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o.getClass() == this.getClass()) {
                Agent q= (Agent) o;
                return name == q.name;
            } else return false;
        }
    }

    public boolean hasSpace(String s) {
        char[] s0= s.toCharArray();
        for (char c : s0) {
            if (c == ' ') return true;
        }
        return false;
    }

    /** This method returns the index of the closing parenthesis that is paired <br>
     * with the opening parenthesis at the beginning of the expression, <br>
     * e.g. "()" returns 1, "(()) -> a" returns 3. <br>
     * Precondition: (1) each opening parenthesis has a closing pair, <br>
     * i.e. (there are an equal number of left and right parenthesis <br>
     * in a valid order, and (2) there is at least one pair of parenthesis. Invariant: the first
     * character of s is an opening parenthesis. */
    public int findCP(String s) {
        char[] sCA= s.toCharArray();
        if (sCA[0] != '(' && sCA[0] != '[') return -1;
        int openParen= 1;
        int i= 1;
        while (openParen != 0) {
            if (sCA[i] == ')' || sCA[i] == ']') openParen-- ;
            else if (sCA[i] == '(' || sCA[i] == '[') openParen++ ;
            i++ ;
        }
        return i - 1;
    }
}
