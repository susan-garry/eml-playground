package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import model.Model;

public class GUI extends JFrame {
    final Model M= new Model();
    String worldAt= "";
    String agentAt= "";

    /** This is a Map of all checkboxes associated with the atoms in the model <br>
     * It maps the name of an atom (key) to it's associated checkbox (value). <br>
     * Invariant: atomControls[i] controls the truth valuation of atoms[i] */
    LinkedList<String> atoms= new LinkedList<>();
    LinkedList<JCheckBox> atomControls= new LinkedList<>();

    // An empty label
    JLabel emptyLabel= new JLabel(" ");

    /** The 'add element' components */
    JLabel addWLabel= new JLabel("Add world: ");
    JLabel addALabel= new JLabel("Add agent: ");
    JLabel addPLabel= new JLabel("Add atom: ");
    JTextField wName= new JTextField("name");
    JTextField pName= new JTextField("name");
    JTextField aName= new JTextField("name");

    /** The 'add a relation' components */
    JLabel addRLabel0= new JLabel("Add a relation: at ");
    JTextField rAddW= new JTextField("w");
    JLabel addRLabel1= new JLabel(", R");
    JTextField rAddA= new JTextField("agent");
    JLabel addRLabel2= new JLabel(" ");
    JTextField rAddV= new JTextField("v");
    JButton addR= new JButton("add");

    /** The 'evaluate formula' components */
    JLabel evalLabel0= new JLabel("Evaluate a formula: M, ");
    JComboBox<String> evalW= new JComboBox<>();
    JLabel evalLabel1= new JLabel(" satisfies: ");
    JTextField evalFormula= new JTextField("");

    /** The 'display information' components */
    JLabel selectWA= new JLabel(" Select a world and an agent: ");
    JLabel vSetDisplay0= new JLabel("From " + worldAt + ", " + agentAt + " can access");
    JLabel vSetDisplay1= new JLabel("   ");
    JComboBox<String> displayWorld= new JComboBox<>();
    JComboBox<String> displayAgent= new JComboBox<>();
    JLabel pDisplayLabel= new JLabel(" There are currently no atoms to display. ");

    /** This box and these labels inform the user whether or not their actions have been <br>
     * executed successfully. */
    String nullReference= "  An element with one of those names does not exist.  ";
    String alreadyExists= "  An element with that name already exists.  ";

    Box infoBox= new Box(BoxLayout.Y_AXIS);
    JLabel l1= new JLabel(" Welcome to the Epistemic Modal Logic Playground! ");
    JLabel l2= new JLabel(" Try adding a world, agent, or atom to your model. ");

    /** This box displays the atoms and their valuations */
    Box atomInfo= new Box(BoxLayout.Y_AXIS);

    /** Show the gui */
    public static void main(String[] args) {
        GUI display= new GUI();
    }

    /** Here is a constructor call for the GUI */
    public GUI() {
        super("Epistemic Modal Model");
        addComponents();
        setFonts();
        pack();
        setResizable(true);
        setVisible(true);
    }

    public void setFonts() {
        Font addStuff= new Font("Dialog", Font.PLAIN, 22);
        Font evalStuff= new Font("Dialog", Font.PLAIN, 18);
        addWLabel.setFont(addStuff);
        addALabel.setFont(addStuff);
        addPLabel.setFont(addStuff);
        evalLabel0.setFont(evalStuff);
        evalLabel1.setFont(evalStuff);
        selectWA.setFont(addStuff);
        vSetDisplay0.setFont(new Font("Dialog", Font.PLAIN, 18));
        vSetDisplay1.setFont(new Font("Dialog", Font.PLAIN, 18));
        l1.setFont(evalStuff);
        l2.setFont(evalStuff);
    }

    /** This method initializes the display, setting up all labels and buttons */
    public void addComponents() {
        Box main= new Box(BoxLayout.X_AXIS);

        /** This creates the boxes that will allow the user to make changes to the world <br>
         * and evaluate it's formulas, and adds them to the main box. */
        // (1a) This is the row with the additive and evaluative user elements
        Box col1= new Box(BoxLayout.Y_AXIS);

        // (1b) This box contains the components for adding a world
        Box addWHelp= new Box(BoxLayout.X_AXIS);
        addWHelp.add(addWLabel);
        addWHelp.add(wName);
        wName.addActionListener(e -> {
            String newWName= wName.getText();
            if (M.addWorld(newWName) == true) {
                displayWorld.addItem(newWName);
                giveInfo("  A new world has been successfully added to the model.  ", " ");
            } else giveInfo(alreadyExists, "");
        });

        // (1c) This box contains the components for adding an atom
        Box addPHelp= new Box(BoxLayout.X_AXIS);
        addPHelp.add(addPLabel);
        addPHelp.add(pName);
        pName.addActionListener(e -> {
            pDisplayLabel
                .setText("Toggle the truth valuation of the following atoms at " + worldAt + ":");
            String p= pName.getText();
            if (M.hasSpace(p)) {
                giveInfo("Spaces are not allowed in atoms.", "Try a different name.");
                return;
            } else if (M.addAtom(p) == true) {
                JCheckBox pVal= new JCheckBox(p + ": True?");
                pVal.addActionListener(
                    ae -> {
                        M.toggleTruthAt(p, worldAt);
                    });
                atomInfo.add(pVal);
                atoms.add(p);
                atomControls.add(pVal);
                displayAtoms();
                giveInfo("  A new atom has been successfully added to the model.  ", " ");
            } else giveInfo(alreadyExists, " ");
        });

        // (1d) This box contains the components for adding an agent
        Box addAHelp= new Box(BoxLayout.X_AXIS);
        addAHelp.add(addALabel);
        addAHelp.add(aName);
        // addAHelp.add(aAdd);
        aName.addActionListener(e -> {
            String newAName= aName.getText();
            if (M.addAgent(newAName) == true) {
                displayAgent.addItem(newAName);
                giveInfo("  A new agent has been successfully added to the model.  ", "");
            }
        });

        // (1e) Allow for a relation to be added to the model
        Box addRHelp= new Box(BoxLayout.X_AXIS);
        addRHelp.add(addRLabel0);
        addRHelp.add(rAddW);
        addRHelp.add(addRLabel1);
        addRHelp.add(rAddA);
        addRHelp.add(addRLabel2);
        addRHelp.add(rAddV);
        addRHelp.add(addR);
        addR.addActionListener(e -> {
            if (M.addRelation(rAddA.getText(), rAddW.getText(), rAddV.getText()) == true) {
                giveInfo(" The relation was successfully added to this model ",
                    "  or already exists.");
                vSetDisplay1.setText(getWAtVSet());
            } else giveInfo(nullReference, "");
        });

        // (1f) Add these rows to the first column
        col1.add(addWHelp);
        col1.add(addPHelp);
        col1.add(addAHelp);
        col1.add(addRHelp);
        col1.add(Box.createHorizontalStrut(200));
        // col1.add(infoBox);

        /** This creates the interface that allows the user to see certain aspects <br>
         * of the Model. */
        // For each atom, add a label and a check button to toggle its truth valuation
        // Add a label that displays the worlds that the given agent can access from the given world

        // (2a) This is the second column that display information about the model
        Box col2= new Box(BoxLayout.Y_AXIS);

        // (2b) These components allow the user to select the world and agent that they want to see
        // information about
        col2.add(selectWA);

        Box c2r2= new Box(BoxLayout.X_AXIS);
        c2r2.add(displayWorld);
        displayWorld.addItem("all");
        worldAt= "all";
        displayWorld.addActionListener(e -> {
            worldAt= (String) displayWorld.getSelectedItem();
            if (!worldAt.contentEquals("all")) {
                vSetDisplay0.setText("From " + worldAt + ", " + agentAt + " can access");
                vSetDisplay1.setText(getWAtVSet());
            } else {
                vSetDisplay0.setText("Use the \"all\" button to obtain a list of");
                vSetDisplay1.setText("worlds in the model at which the formula is true.");
            }
            evalLabel0.setText("Evaluate a formula: M, " + worldAt);
            displayAtoms();
        });

        c2r2.add(displayAgent);
        displayAgent.addActionListener(e -> {
            // update the list of worlds accessible through this agent;
            agentAt= (String) displayAgent.getSelectedItem();
            if (!worldAt.contentEquals("all")) {
                vSetDisplay0.setText("From " + worldAt + ", " + agentAt + " can access");
                vSetDisplay1.setText(getWAtVSet());
            }
        });
        col2.add(c2r2);

        // (2c) This box contains the components for evaluating a formula
        Box evalHelp= new Box(BoxLayout.X_AXIS);
        evalHelp.add(evalLabel0);
        evalHelp.add(evalLabel1);
        evalHelp.add(evalFormula);
        evalFormula.addActionListener(e -> {
            String formula= evalFormula.getText();
            if (worldAt == "all") {
                HashSet<String> wVA= M.isValidAt(formula);
                if (wVA.equals(M.W)) {
                    giveInfo(formula + " is valid at all worlds.", "");
                } else giveInfo(formula + " is valid at the following worlds: ", wVA.toString());
                return;
            }
            Model.World w= M.getWorld(worldAt);
            if (w == null) {
                giveInfo(" Opperation cannot be completed. ", " Try adding a world the the model.");
                return;
            } else {
                String l2= " ";
                int v= w.isValid(formula);
                if (v == -1) {
                    giveInfo(formula + "is not a valid formula.", l2);
                    return;
                }
                boolean b= v == 1 ? true : false;
                giveInfo(formula + " is " + b + " at " + worldAt, l2);
            }
        });
        evalHelp.add(Box.createGlue());
        col2.add(evalHelp);

        // (2d) Add a column listing all of the atoms in the model and whether or not they are true
        // at the selected world.
        col2.add(pDisplayLabel);

        // (2e) Add a list of all the worlds accessible from the selected world to the selected
        // agent. This will be blank if there is not at least one world and one agent at the model
        col2.add(atomInfo);
        col2.add(vSetDisplay0);
        col2.add(vSetDisplay1);
        col2.add(Box.createGlue());
        col2.add(Box.createHorizontalStrut(400));
        // col2.add(Box.createVerticalStrut(50));
        // main.add(col1);
        // main.add(col2);

        // This box gives the user information about their actions. It will go in the south.
        infoBox.add(new JLabel(" "));
        infoBox.add(l1);
        infoBox.add(l2);
        infoBox.add(Box.createGlue());

        add(emptyLabel, BorderLayout.NORTH);
        add(col2, BorderLayout.EAST);
        add(infoBox, BorderLayout.SOUTH);
        add(col1, BorderLayout.WEST);
        add(new JLabel("      "), BorderLayout.CENTER);
    }

    /** This function displays the atoms that are true at the selected world */
    public void displayAtoms() {
        if (worldAt.contentEquals("all")) atomInfo.setVisible(false);
        else {
            atomInfo.setVisible(true);
            int len= atoms.size();
            for (int i= 0; i < len; i++ ) {
                atomControls.get(i).setSelected(M.valuation(atoms.get(i), worldAt));
            }
        }
    }

    public String getWAtVSet() {
        if (agentAt == "") return "";
        String vSet= M.getAgent(agentAt).getVSet(worldAt);
        return vSet;
    }

    /** This function will change the output on the infoBoard */
    public void giveInfo(String s1, String s2) {
        l1.setText(s1);
        l2.setText(s2);
    }

}
