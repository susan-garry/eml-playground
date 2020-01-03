package model;

import java.util.HashMap;
import java.util.HashSet;

public class Model {

    // This set contains every world in this model
    public HashSet<World> W= new HashSet<>();
    public HashSet<Atom> V= new HashSet<>();
    public HashSet<Agent> A= new HashSet<>();

    /** This function returns a list of worlds at which a give valuation function is true */
    public HashSet<String> isValidAt(String s) {
        HashSet<String> worlds= new HashSet<>();
        for (World w : W) {
            if (w.isValid(s) == 1) worlds.add(w.name);
        }
        return worlds;
    }

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
        if (name == "all") return false;
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
        if (hasSpace(name) || name.charAt(0) == 'K' || name.contentEquals("") || findCP(name) != -1)
            return false;
        for (Atom p : V) {
            if (p.name.contentEquals(name)) return false;
        }
        V.add(new Atom(name));
        return true;
    }

    /** This method adds a new agent to this model, if one doesn't already exist with <br>
     * the same name. It returns true if one is added, false otherwise. */
    public boolean addAgent(String name) {
        if (findCB(name) != -1) return false;
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

        /** This function evaluates whether or not a formula is true or false at this world. */
        public int isValid(String f) {
            // TODO: implement this method. It must
            // (1) be able to parse a string into different components that can be
            // evaluated recursively, and
            // (2) evaluate those components and return their truth value
            String f1= "";
            char f0= f.charAt(0);
            int len= f.length();
            char[] fCA= f.toCharArray();
            if (f0 == 'K') {
                int cb= findCB(f);
                String agent= f.substring(2, cb);
                if (getAgent(agent) == null) return 0;
                int cParen= findCP(f);
                f1= f.substring(cb + 2, cParen);
                if (cParen == len - 1) return knows(agent, f1);
                char opp= fCA[cParen + 2];
                String f2;
                if (opp == '&') {
                    f2= f.substring(cParen + 4);
                    return conj(f1, f2);
                }
                if (opp == '|') {
                    f2= f.substring(cParen + 4);
                    return disj(f1, f2);
                }
                // Precondition: fCA[cParen + 3] == '>'
                if (opp == '-') {
                    f2= f.substring(cParen + 5);
                    return implies(f1, f2);
                }
                // Precondition: fCA[cParen + 3] == '-' and fCA[cParen + 4] == '>'
                if (opp == '<') {
                    f2= f.substring(cParen + 6);
                    return iff(f1, f2);
                }

            }
            if (f0 == 'M') {
                int cb1= findCB(f);
                String agent= f.substring(2, cb1);
                if (getAgent(agent) == null) return 0;
                int cp1= findCP(f);
                f1= f.substring(cb1 + 2, cp1);
                return might(agent, f1);
            }
            if (!hasSpace(f)) {
                // This condition is entered if f is either an atom of the negation of an atom
                Atom p;
                if (f0 == '~') {
                    int valid= isValid(f.substring(1));
                    if (valid == 1) return 0;
                    if (valid == 0) return 1;
                    return -1;
                }
                p= getAtom(f);
                if (p != null) { return p.trueAt.contains(name) ? 1 : 0; }
                return -1;
            } else {
                // This condition is entered if f is a compound fomula
                // System.out.println("f is a compound formula");
                // Invariant: f is of the form "~(" + formula1 + " " + formula 2 + ")"
                if (f0 == '~' && fCA[1] == '(' || f0 == '(') {
                    // TODO 2
                    int cParen= findCP(f);
                    if (cParen == len - 1) {
                        if (f0 == '~') return isValid(f.substring(2, len - 1)) == 1 ? 0 : 1;
                        return isValid(f.substring(1, len - 1));

                    }
                    char opp= fCA[cParen + 2];
                    f1= f.substring(0, cParen + 1);
                    String f2;
                    if (opp == '&') {
                        f2= f.substring(cParen + 4);
                        return conj(f1, f2);
                    }
                    if (opp == '|') {
                        f2= f.substring(cParen + 4);
                        return disj(f1, f2);
                    }
                    // Precondition: fCA[cParen + 3] == '>'
                    if (opp == '-') {
                        f2= f.substring(cParen + 5);
                        return implies(f1, f2);
                    }
                    // Precondition: fCA[cParen + 3] == '-' and fCA[cParen + 4] == '>'
                    if (opp == '<') {
                        f2= f.substring(cParen + 6);
                        return iff(f1, f2);
                    }
                }

                // System.out.println("f is the junction of two formulas");
                for (int i= 0; i < len; i++ ) {
                    if (fCA[i] == ' ') {
                        char opp= fCA[i + 1];
                        // System.out.println(opp);
                        if (opp == '&') {
                            // System.out.println(f1);
                            // System.out.println(f.substring(i + 3));
                            return conj(f1, f.substring(i + 3));
                        }
                        if (opp == '|') {
                            // System.out.println(f);
                            return disj(f1, f.substring(i + 3));
                        }
                        if (opp == '-' && fCA[i + 2] == '>')
                            return implies(f1, f.substring(i + 4));
                        if (opp == '<' && fCA[i + 2] == '-' && fCA[i + 3] == '>')
                            return iff(f1, f.substring(i + 5));
                    }
                    f1= f1 + fCA[i];
                    // System.out.println(f1);
                }
                return -1;
            }
        }

        public int conj(String f1, String f2) {
            if (isValid(f1) == 1 && isValid(f2) == 1) return 1;
            if (isValid(f1) == -1 || isValid(f2) == -1) return -1;
            return 0;
        }

        public int disj(String f1, String f2) {
            if (isValid(f1) == 1 || isValid(f2) == 1) return 1;
            if (isValid(f1) == -1 || isValid(f2) == -1) return -1;
            return 0;
        }

        public int implies(String f1, String f2) {
            if (isValid(f1) == -1 || isValid(f2) == -1) return -1;
            if (isValid(f1) == 0 || isValid(f2) == 1) return 1;
            return 0;
        }

        public int iff(String f1, String f2) {
            if (isValid(f1) == -1 || isValid(f2) == -1) return -1;
            if (isValid(f1) == isValid(f2)) return 1;
            return 0;
        }

        public int knows(String a, String f) {
            for (String w : getAgent(a).R.get(name)) {
                int valid= getWorld(w).isValid(f);
                if (valid == 0) return 0;
                if (valid == -1) return -1;
            }
            return 1;
        }

        public int might(String a, String f) {
            for (String w : getAgent(a).R.get(name)) {
                int valid= getWorld(w).isValid(f);
                if (valid == 1) return 1;
                if (valid == -1) return -1;
            }
            return 0;
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

    /** Returns true iff there are no spaces within the string */
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
     * in a valid order, and (2) there is at least one pair of parenthesis. */
    public int findCP(String s) {
        char[] sCA= s.toCharArray();
        int openParen= 0;
        int i= 0;
        int len= s.length();
        while (openParen == 0 && i < len) {
            if (sCA[i] == '(') openParen++ ;
            i++ ;
        }
        if (i == len) return -1;
        while (openParen != 0 && i < len) {
            if (sCA[i] == ')') openParen-- ;
            else if (sCA[i] == '(') openParen++ ;
            i++ ;
        }
        if (openParen != 0) return -1;
        return i - 1;
    }

    /** This method returns the index of the closing braces that is paired <br>
     * with the first opening brace to appear in the string. If this is not <br>
     * a valid pairing or no braces exist, return -1 */
    public int findCB(String s) {
        char[] sCA= s.toCharArray();
        int openParen= 0;
        int i= 0;
        int len= s.length();
        while (openParen == 0 && i < len) {
            if (sCA[i] == '[') openParen++ ;
            i++ ;
        }
        if (i == len) return -1;
        while (openParen != 0 && i < len) {
            if (sCA[i] == ']') openParen-- ;
            else if (sCA[i] == '[') openParen++ ;
            i++ ;
        }
        if (openParen != 0) return -1;
        return i - 1;
    }
}
