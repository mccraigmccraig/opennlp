///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2002 Mike Atkinson
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////

package opennlp.dictionary.wordnet.gui;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import opennlp.dictionary.wordnet.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


/**
 *  This class provides a Graphical User Interface for the OpenNLP WordNet dictionary
 *  package.
 *
 * @author     Mike Atkinson (mratkinson)
 * @since      0.2.0
 * @created    25 March 2002
 * @version    $Id: MainFrame.java,v 1.1 2002/03/26 19:06:20 mratkinson Exp $
 */
public class MainFrame extends JFrame {

    private JTextField entryField;
    private JScrollPane scrollPane;
    private JTextPane textPane;
    private JCheckBox glossButton;
    private JCheckBox lexButton;
    private JCheckBox synsetButton;
    private JCheckBox offsetButton;
    private JMenu nounButton;
    private JMenu verbButton;
    private JMenu adjectiveButton;
    private JMenu adverbButton;

    private JButton forwardButton;
    private JButton backButton;

    private WordNet wordNet;
    private Object wordNetLock = new Object();
    private String currentWord = "";
    private List history = new ArrayList();
    private int historyPtr = 0;
    
    private static final String license = 
        WNGlobal.lineSeparator +" Copyright (C) 2002 Mike Atkinson" +
        WNGlobal.lineSeparator +
        WNGlobal.lineSeparator +" This library is free software; you can redistribute it and/or" +
        WNGlobal.lineSeparator +" modify it under the terms of the GNU Lesser General Public" +
        WNGlobal.lineSeparator +" License as published by the Free Software Foundation; either" +
        WNGlobal.lineSeparator +" version 2.1 of the License, or (at your option) any later version." +
        WNGlobal.lineSeparator +
        WNGlobal.lineSeparator +" This library is distributed in the hope that it will be useful," +
        WNGlobal.lineSeparator +" but WITHOUT ANY WARRANTY; without even the implied warranty of" +
        WNGlobal.lineSeparator +" MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the" +
        WNGlobal.lineSeparator +" GNU Lesser General Public License for more details." +
        WNGlobal.lineSeparator +
        WNGlobal.lineSeparator +" You should have received a copy of the GNU Lesser General Public" +
        WNGlobal.lineSeparator +" License along with this program; if not, write to the Free Software" +
        WNGlobal.lineSeparator +" Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.";



    private CommandData[] commandData = {
            new CommandData("noun Antonyms", "-antsn", (1 << WNConsts.ANTPTR)),
            new CommandData("verb Antonyms", "-antsv", (1 << WNConsts.ANTPTR)),
            new CommandData("adjective Antonyms", "-antsa", (1 << WNConsts.ANTPTR)),
            new CommandData("adverb Antonyms", "-antsr", (1 << WNConsts.ANTPTR)),
            new CommandData("noun Hypernyms", "-hypen", (1 << WNConsts.HYPERPTR)),
            new CommandData("verb Hypernyms", "-hypev", (1 << WNConsts.HYPERPTR)),
            new CommandData("noun Hyponyms", "-hypon", (1 << WNConsts.HYPOPTR)),
            new CommandData("verb Hyponyms", "-hypov", (1 << WNConsts.HYPOPTR)),
            new CommandData("noun Hyponym Tree", "-treen", (1 << WNConsts.HYPOPTR)),
            new CommandData("verb Hyponym Tree", "-treev", (1 << WNConsts.HYPOPTR)),
            new CommandData("verb Verb Entailment", "-entav", (1 << WNConsts.ENTAILPTR)),
            new CommandData("noun Synonyms (ordered by frequency)", "-synsn", (1 << WNConsts.SIMPTR)),
            new CommandData("verb Synonyms (ordered by frequency)", "-synsv", (1 << WNConsts.SIMPTR)),
            new CommandData("adjective Synonyms (ordered by frequency)", "-synsa", (1 << WNConsts.SIMPTR)),
            new CommandData("adverb Synonyms (ordered by frequency)", "-synsr", (1 << WNConsts.SIMPTR)),
            new CommandData("noun Member of Holonyms", "-smemn", (1 << WNConsts.ISMEMBERPTR)),
            new CommandData("noun Substance of Holonyms", "-ssubn", (1 << WNConsts.ISSTUFFPTR)),
            new CommandData("noun Part of Holonyms", "-sprtn", (1 << WNConsts.ISPARTPTR)),
            new CommandData("noun Has Member Meronyms", "-membn", (1 << WNConsts.HASMEMBERPTR)),
            new CommandData("noun Has Substance Meronyms", "-subsn", (1 << WNConsts.HASSTUFFPTR)),
            new CommandData("noun Has Part Meronyms", "-partn", (1 << WNConsts.HASPARTPTR)),
            new CommandData("noun All Meronyms", "-meron", (1 << WNConsts.MERONYM)),
            new CommandData("noun All Holonyms", "-holon", (1 << WNConsts.HOLONYM)),
            new CommandData("verb Cause to", "-causv", (1 << WNConsts.CAUSETO)),
            new CommandData("adjective Pertainyms", "-perta", (1 << WNConsts.ANTPTR)),
            new CommandData("adverb Pertainyms", "-pertr", (1 << WNConsts.PERTPTR)),
            new CommandData("noun Attributes", "-attrn", (1 << WNConsts.ATTRIBUTE)),
            new CommandData("adjective Attributes", "-attra", (1 << WNConsts.ATTRIBUTE)),
            new CommandData("verb Nominalizations", "-nomnn", (1 << WNConsts.NOMINALIZATIONS)),
            new CommandData("verb Nominalizations", "-nomnv", (1 << WNConsts.NOMINALIZATIONS)),
            new CommandData("noun Familiarity & Polysemy Count", "-famln", (1 << WNConsts.FREQ)),
            new CommandData("verb Familiarity & Polysemy Count", "-famlv", (1 << WNConsts.FREQ)),
            new CommandData("adjective Familiarity & Polysemy Count", "-famla", (1 << WNConsts.FREQ)),
            new CommandData("adverb Familiarity & Polysemy Count", "-famlr", (1 << WNConsts.FREQ)),
            new CommandData("verb Verb Frames", "-framv", (1 << WNConsts.FRAMES)),
            new CommandData("noun Coordinate Terms (sisters)", "-coorn", (1 << WNConsts.COORDS)),
            new CommandData("verb Coordinate Terms (sisters)", "-coorv", (1 << WNConsts.COORDS)),
            new CommandData("verb Synonyms (grouped by similarity of meaning)", "-simsv", (1 << WNConsts.RELATIVES)),
            new CommandData("noun Hierarchical Meronyms", "-hmern", (1 << WNConsts.HHOLONYM)),
            new CommandData("noun Hierarchical Holonyms", "-hholn", (1 << WNConsts.HMERONYM))};

    private ActionListener[] actionListeners = {
            null,
            new POSActionListenter("noun"),
            new POSActionListenter("verb"),
            new POSActionListenter("adjective"),
            new POSActionListenter("adverb")
            };

    private Map commandMap = new HashMap();


    /**
     *  Constructor for the MainFrame object
     *
     * @since    0.2.0
     */
    public MainFrame() {
        addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    doExit();
                }
            });
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        // File Menu
        JMenu mFile = new JMenu("File");
        JMenuItem mFileExit = new JMenuItem("Exit", KeyEvent.VK_X);
        mFileExit.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doExit();
                }
            });
        mFile.add(mFileExit);
        menuBar.add(mFile);

        // Edit Menu - comment out for now
        //JMenu mEdit = new JMenu("Edit");
        //JMenuItem mEditPreferences = new JMenuItem("Preferences", KeyEvent.VK_P);
        //mEdit.add(mEditPreferences);
        //menuBar.add(mEdit);

        // Help Menu
        JMenu mHelp = new JMenu("Help");
        menuBar.add(mHelp);
        JMenuItem mHelpWNLicense = new JMenuItem("WordNet License", KeyEvent.VK_W);
        mHelpWNLicense.addActionListener( 
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    synchronized (wordNetLock) {
                        showLicense(wordNet.getLicense(), "WordNet license");
                    }
                }
            });
        mHelp.add(mHelpWNLicense);
        JMenuItem mHelpLicense = new JMenuItem("License", KeyEvent.VK_W);
        mHelpLicense.addActionListener( 
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showLicense(license, "license");
                }
            });
        mHelp.add(mHelpLicense);

        
        JPanel northPanel = new JPanel();
        JPanel statusPanel = new JPanel();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(northPanel, BorderLayout.NORTH);
        getContentPane().add(statusPanel, BorderLayout.SOUTH);

        // Create north panel, which has two rows; the top row consisting of
        // the input text field and global option buttons; the bottom row
        // consisting of the commands available for each part of speech.
        JPanel northTopPanel = new JPanel();
        JMenuBar northBotPanel = new JMenuBar();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(northTopPanel);
        northPanel.add(northBotPanel);

        entryField = new JTextField();
        entryField.setColumns(40);
        entryField.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleWord(e.getActionCommand().toLowerCase(), "-over");
                }
            });
        northTopPanel.add(entryField);
        glossButton = new JCheckBox("gloss");
        northTopPanel.add(glossButton);
        lexButton = new JCheckBox("lex");
        northTopPanel.add(lexButton);
        synsetButton = new JCheckBox("synset");
        northTopPanel.add(synsetButton);
        offsetButton = new JCheckBox("offset");
        northTopPanel.add(offsetButton);

        backButton = new JButton("back");
        backButton.setEnabled(false); 
        backButton.addActionListener( 
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (history.size()<=1) {
                        backButton.setEnabled(false);
                        forwardButton.setEnabled(false);
                    } else {
                        if ( historyPtr>0 ) {
                            historyPtr--;
                            currentWord = (String)history.get(historyPtr);
                            entryField.setText(currentWord);
                            lookupWord(currentWord, "-over");
                            whichPOS(currentWord);
                        }
                        forwardButton.setEnabled(true);
                        if (historyPtr<=0) {
                            backButton.setEnabled(false);
                        }
                    }
                }
            });
        northBotPanel.add(backButton);
        forwardButton = new JButton("forward");
        forwardButton.setEnabled(false); 
        forwardButton.addActionListener( 
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (history.size()<=1) {
                        backButton.setEnabled(false);
                        forwardButton.setEnabled(false);
                    } else {
                        if ( historyPtr<history.size()-1 ) {
                            historyPtr++;
                            currentWord = (String)history.get(historyPtr);
                            entryField.setText(currentWord);
                            lookupWord(currentWord, "-over");
                            whichPOS(currentWord);
                        }
                        backButton.setEnabled(true);
                        if (historyPtr>=history.size()-1) {
                            forwardButton.setEnabled(false);
                        }
                    }
                }
            });
        northBotPanel.add(forwardButton);
        northBotPanel.add(new JLabel("                                                          "));

            
        nounButton = new JMenu("noun");
        nounButton.setEnabled(false);
        northBotPanel.add(nounButton);
        verbButton = new JMenu("verb");
        verbButton.setEnabled(false);
        northBotPanel.add(verbButton);
        adverbButton = new JMenu("adverb");
        adverbButton.setEnabled(false);
        northBotPanel.add(adverbButton);
        adjectiveButton = new JMenu("adjective");
        adjectiveButton.setEnabled(false);
        northBotPanel.add(adjectiveButton);

        // Create document panel to hold output of the WordNet search.
        DefaultStyledDocument doc = new DefaultStyledDocument();
        textPane = new MyTextPane(doc);
        initStylesForTextPane(textPane);
        scrollPane = new MyScrollPane(textPane);
        textPane.setEditable(false);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        textPane.addMouseListener( new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {
                    String text = textPane.getSelectedText();
                    if (text!=null && text.length()>1) { 
                        entryField.setText(text);
                        handleWord(text.toLowerCase(), "-over");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        pack();
    }



    /**
     *  Initialise the wordnet library, this may take a short while.
     *
     * @param  argv  The command line arguments (passed to WordNet library).
     * @since        0.2.0
     */
    public void initWordNet(String[] argv) {
        synchronized (wordNetLock) {
            wordNet = new WordNet(argv);
            for (int i = 0; i < commandData.length; i++) {
                commandMap.put(commandData[i].text, commandData[i].command);
            }
        }
    }


    /**
     *  For the word, find the parts of speech for which wordNet has data, and create
     *  the POS menus for them.
     *
     * @param  word  The word to search WordNet for.
     * @since        0.2.0
     */
    public void whichPOS(String word) {
        Search searcher = wordNet.getSearcher();
        int isNoun = wordNet.isDefined(word, WNConsts.NOUN);
        int isVerb = wordNet.isDefined(word, WNConsts.VERB);
        int isAdjective = wordNet.isDefined(word, WNConsts.ADJ);
        int isAdverb = wordNet.isDefined(word, WNConsts.ADV);
        createPOSMenu(nounButton, isNoun, actionListeners[WNConsts.NOUN]);
        createPOSMenu(verbButton, isVerb, actionListeners[WNConsts.VERB]);
        createPOSMenu(adjectiveButton, isAdjective, actionListeners[WNConsts.ADJ]);
        createPOSMenu(adverbButton, isAdverb, actionListeners[WNConsts.ADV]);
    }


    /**
     *  Look up a word in WordNet and write result to the GUI.
     *
     * @param  word    The word to search WordNet for.
     * @param  action  The command to WordNet for the search type.
     * @since          0.2.0
     */
    public void lookupWord(String word, String action) {
        synchronized (wordNetLock) {
            StringBuffer command = new StringBuffer(word);
            if (glossButton.isSelected()) {
                command.append(" -g");
            }
            if (lexButton.isSelected()) {
                command.append(" -a");
            }
            if (offsetButton.isSelected()) {
                command.append(" -o");
            }
            if (synsetButton.isSelected()) {
                command.append(" -s");
            }
            command.append(" ").append(action);
            String[] argv = WNUtil.split(command.toString(), " ");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wordNet.setOut(new PrintStream(baos));
            wordNet.searchwn(argv);
            String result = baos.toString();
            Document doc = textPane.getStyledDocument();
            int start = doc.getStartPosition().getOffset();
            int end = doc.getEndPosition().getOffset() - 1;
            String lowerCaseResult = result.toLowerCase();
            try {
                doc.remove(start, end);
                end = 0;
                String collateral = word.replace('_',' ');
                while (true) {
                    start = lowerCaseResult.indexOf(word, end);
                    int start2 = lowerCaseResult.indexOf(collateral, end);
                    if (start2>=0 && start2<start) {
                        start=start2;
                    } else if (start<0) {
                        start=start2;
                    }
                    if (start < 0) {
                        doc.insertString(doc.getLength(),
                                         result.substring(end, result.length()),
                                         textPane.getStyle("regular"));
                        break;
                    } else if (start>0 && (   Character.isLetter(result.charAt(start-1))
                                           || Character.isLetter(result.charAt(start + word.length())))) {
                        doc.insertString(doc.getLength(),
                                         result.substring(end, start + word.length()),
                                         textPane.getStyle("regular"));
                                              
                    } else {
                        doc.insertString(doc.getLength(),
                                         result.substring(end, start),
                                         textPane.getStyle("regular"));
                        doc.insertString(doc.getLength(),
                                         result.substring(start, start + word.length()),
                                         textPane.getStyle("bold"));
                    }
                    end = start + word.length();
                }
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     *  Initialise various styles to be used in the Text pane.
     *
     * @param  textPane  Set styles in this pane.
     * @since            0.2.0
     */
    protected void initStylesForTextPane(JTextPane textPane) {
        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = textPane.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style s = textPane.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = textPane.addStyle("bold", regular);
        StyleConstants.setBold(s, true);

        s = textPane.addStyle("small", regular);
        StyleConstants.setFontSize(s, 10);

        s = textPane.addStyle("large", regular);
        StyleConstants.setFontSize(s, 16);
    }


    /**
     *  Exit from the GUI. Override this to perform extra processing on exit.
     *
     * @since    0.2.0
     */
    protected void doExit() {
        System.exit(0);
    }

    private JDialog dialog = null;
    protected void showLicense(String license, String title) {
        try {
            dialog = new JDialog(this, title);
            DefaultStyledDocument doc = new DefaultStyledDocument();
            JTextPane textPane = new JTextPane(doc);
            initStylesForTextPane(textPane);
            doc.insertString(doc.getLength(), license, textPane.getStyle("regular"));
            dialog.getContentPane().add(textPane);
            JButton okButton = new JButton("OK");
            JPanel panel = new JPanel();
            panel.add(okButton);
            dialog.getContentPane().add(panel, BorderLayout.SOUTH);
            okButton.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });
            dialog.pack();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void handleWord(String word, String action) {
        currentWord = word;
        lookupWord(currentWord, action);
        whichPOS(currentWord);
        history.add(currentWord);
        historyPtr=history.size()-1;
        if (history.size()>1) {
            backButton.setEnabled(true);
        }
        forwardButton.setEnabled(false);
    }

    /**
     *  Create the MenuItem options available for the part of speech Menu.
     *
     * @param  menu            The POS menu.
     * @param  wordNetData     The WordNet options available (as a bitset).
     * @param  actionListener  The run the action defined by this ActionListener
     *      on the menu item being selected.
     * @since                  0.2.0
     */
    private void createPOSMenu(JMenu menu, int wordNetData, ActionListener actionListener) {
        menu.setEnabled(wordNetData != 0);
        if (wordNetData != 0) {
            menu.removeAll();
            String lastText = "";
            for (int i = 0; i < commandData.length; i++) {
                String text = commandData[i].text.substring(1 + commandData[i].text.indexOf(' '));
                if (!text.equals(lastText)) {
                    if ((wordNetData & commandData[i].mask) != 0) {
                        ((JMenuItem)menu.add(new JMenuItem(text))).addActionListener(actionListener);
                    }
                }
                lastText = text;
            }
        }
    }


    /**
     *  The main program for the MainFrame class
     *
     * @param  argv  The command line arguments
     * @since        0.2.0
     */
    public static void main(String[] argv) {
        try {
            //System.setOut(new PrintStream(new FileOutputStream(new File("out.txt"))));
            //System.setErr(new PrintStream(new FileOutputStream(new File("err.txt"))));
            MainFrame mf = new MainFrame();
            mf.show();
            mf.initWordNet(argv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *  Description of the Class
     *
     * @author     Mike Atkinson (mratkinson)
     * @since      0.2.0
     * @created    25 March 2002
     */
    private class MyTextPane extends JTextPane {
        private Dimension dimension = new Dimension(800, 300);


        /**
         *  Constructor for the MyTextPane object
         *
         * @param  doc  Description of Parameter
         * @since       0.2.0
         */
        public MyTextPane(StyledDocument doc) {
            super(doc);
        }


        /**
         *  Gets the MinimumSize attribute of the MyTextPane object
         *
         * @return    The MinimumSize value
         * @since     0.2.0
         */
        public Dimension getMinimumSize() {
            return dimension;
        }
    }


    /**
     *  Just create a pane with minimum size.
     *
     * @author     Mike Atkinson (mratkinson)
     * @since      0.2.0
     * @created    25 March 2002
     */
    private class MyScrollPane extends JScrollPane {
        private Dimension dimension = new Dimension(800, 300);


        /**
         *  Constructor for the MyScrollPane object
         *
         * @param  c  Description of Parameter
         * @since     0.2.0
         */
        public MyScrollPane(Component c) {
            super(c);
        }


        /**
         *  Gets the MinimumSize attribute of the MyScrollPane object
         *
         * @return    The MinimumSize value
         * @since     0.2.0
         */
        public Dimension getMinimumSize() {
            return dimension;
        }


        /**
         *  Gets the PreferredSize attribute of the MyScrollPane object
         *
         * @return    The PreferredSize value
         * @since     0.2.0
         */
        public Dimension getPreferredSize() {
            return dimension;
        }
    }


    /**
     *  This holds data for the commands available to WordNet so that they are consistent
     *  across the GUI.
     *
     * @author     Mike Atkinson (mratkinson)
     * @since      0.2.0
     * @created    25 March 2002
     */
    private class CommandData {
        /**
         *  The command.
         *
         * @since    0.2.0
         */
        public String command;
        /**
         *  Description of the command, used for the POS menus.
         *
         * @since    0.2.0
         */
        public String text;
        /**
         *  Mask to apply to the result of WordNet.isDefined() to find whether command
         *  is available for the word.
         *
         * @since    0.2.0
         */
        public int mask;


        /**
         *  Constructor for the CommandData object
         *
         * @param  text     The command.
         * @param  command  Description of the command, used for the POS menus.
         * @param  mask     Mask to apply to the result of WordNet.isDefined() to
         *      find whether command is available for the word.
         * @since           0.2.0
         */
        public CommandData(String text, String command, int mask) {
            this.command = command;
            this.text = text;
            this.mask = mask;
        }
    }


    /**
     *  Defines the action to be performed for a Part of Speech.
     *
     * @author     Mike Atkinson (mratkinson)
     * @since      0.2.0
     * @created    25 March 2002
     */
    private class POSActionListenter implements ActionListener {
        private String pos;


        /**
         *  Constructor for the POSActionListenter object.
         *
         * @param  pos  The part of speech ("noun", "verb", "adverb", "adjective").
         * @since       0.2.0
         */
        public POSActionListenter(String pos) {
            this.pos = pos + " ";
        }


        /**
         *  Look up the current word using the defined action and then set the possible
         *  part of speech menu options.
         *
         * @param  e  Contains the action command of the menu item text.
         * @since     0.2.0
         */
        public void actionPerformed(ActionEvent e) {
            String command = pos + e.getActionCommand();
            String action = (String)commandMap.get(command);
            if (action != null) {
                handleWord(currentWord, action);
            }
        }
    }
}
