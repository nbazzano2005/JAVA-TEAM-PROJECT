package visualnovel.view;

import visualnovel.model.*;
import visualnovel.media.ScenePanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.IntConsumer;
import java.util.prefs.Preferences;

/** Illustrated visual-novel view with chapter pacing, stat meters, shortcuts and reading settings. */
public final class VisualNovelFrame extends JPanel {
    private static final long serialVersionUID=1L;
    private static final Color BACK=new Color(0x171f31), PANEL=new Color(0x222e43), INK=new Color(0xe9edf4), ACCENT=new Color(0x89d0c2);
    private final CardLayout cards=new CardLayout();
    private final JPanel screens=new JPanel(cards), choices=new JPanel(new GridLayout(0,1,7,7));
    private final JTextArea narrative=new JTextArea(9,55);
    private final JLabel heading=new JLabel(), chapter=new JLabel(), speaker=new JLabel(), status=new JLabel(" ");
    private final JProgressBar[] meters=new JProgressBar[4];
    private final ScenePanel art=new ScenePanel();
    private final JButton begin=button("New Story"), resume=button("Resume"), menuLoad=button("Load Game"),
        save=button("Save"), load=button("Load"), log=button("Journal"), settings=button("Reading Settings"), menu=button("Main Menu");
    private final Preferences prefs=Preferences.userNodeForPackage(VisualNovelFrame.class);
    private final Timer reveal;
    private String fullText="";
    private int cursor;
    private boolean hasGame;
    private final java.util.List<JButton> currentButtons=new java.util.ArrayList<>();
    /** Builds the menu and a responsive game window. */
    public VisualNovelFrame() {
        super(new BorderLayout());
        setMinimumSize(new Dimension(800,740));setPreferredSize(new Dimension(1050,920));
        reveal=new Timer(12,e->{cursor=Math.min(fullText.length(),cursor+3);narrative.setText(fullText.substring(0,cursor));if(cursor==fullText.length())((Timer)e.getSource()).stop();});
        narrative.setEditable(false);narrative.setLineWrap(true);narrative.setWrapStyleWord(true);
        narrative.setBackground(PANEL);narrative.setForeground(INK);narrative.setCaretColor(INK);
        narrative.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,prefs.getInt("fontSize",18)));
        narrative.setBorder(BorderFactory.createEmptyBorder(15,17,15,17));
        narrative.addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent e){finishText();}});
        heading.setFont(new Font(Font.SANS_SERIF,Font.BOLD,24));chapter.setForeground(ACCENT);speaker.setForeground(ACCENT);
        JPanel welcome=panel(new BorderLayout(30,30));welcome.setBorder(BorderFactory.createEmptyBorder(55,75,55,75));
        JPanel hero=panel(new GridLayout(0,1,0,10));
        JLabel title=new JLabel("FINALS WEEK");title.setFont(new Font(Font.SANS_SERIF,Font.BOLD,46));title.setForeground(INK);
        JLabel edition=new JLabel("PRESENTATION EDITION");edition.setForeground(ACCENT);hero.add(edition);hero.add(title);
        hero.add(new JLabel("Three days. Eight decisions. Your final exam."));
        hero.add(new JLabel("A focused 5–8 minute story • Five endings"));welcome.add(hero,BorderLayout.NORTH);
        ScenePanel cover=new ScenePanel();cover.setScene("cover","library","Maya");welcome.add(cover,BorderLayout.CENTER);
        JPanel options=panel(new GridLayout(0,2,10,10));resume.setEnabled(false);
        options.add(begin);options.add(resume);options.add(menuLoad);
        JButton guide=button("How to Play");guide.addActionListener(e->showMessage("Read each scene and choose how to respond.\nKnowledge, confidence, rest, and support shape the outcome.\nLocked choices tell you what preparation they need.\nHover over an option to see its effects.\n1–3: select an option. Space: finish text or continue.\nSave: three manual slots. Load: manual slots plus autosave.\nJournal: review your decisions and current goals.\nSettings: font size and optional text reveal.\nTry a different balance of choices to discover all five endings."));options.add(guide);
        JButton gallery=button("Ending Collection");gallery.addActionListener(e->showEndingCollection());options.add(gallery);
        JButton credits=button("About / Credits");credits.addActionListener(e->showMessage("Finals Week — Presentation Edition\nCS321 / Team 6\nNoah Bazzano • Trevor Hoang • Allorah Sanchez • Perry Shipley\n\nOriginal procedural Java2D artwork. No external assets required.\nA story about preparation, teamwork, and sustainable effort."));options.add(credits);
        welcome.add(options,BorderLayout.SOUTH);
        JPanel game=panel(new BorderLayout(10,10));game.setBorder(BorderFactory.createEmptyBorder(17,20,15,20));
        JPanel top=panel(new BorderLayout(8,8)),titles=panel(new GridLayout(0,1,0,5));titles.add(chapter);titles.add(heading);top.add(titles,BorderLayout.NORTH);
        JPanel bars=panel(new GridLayout(1,4,10,0));String[] names={"Knowledge","Support","Energy","Confidence"};
        for(int i=0;i<4;i++){JPanel box=panel(new BorderLayout());box.add(new JLabel(names[i]),BorderLayout.NORTH);meters[i]=new JProgressBar(0,i==0?45:25);meters[i].setStringPainted(true);meters[i].setForeground(ACCENT);meters[i].setBackground(PANEL);box.add(meters[i]);bars.add(box);}top.add(bars,BorderLayout.CENTER);game.add(top,BorderLayout.NORTH);
        JPanel center=panel(new BorderLayout(8,8));center.add(art,BorderLayout.NORTH);
        JPanel dialogue=panel(new BorderLayout(0,5));dialogue.add(speaker,BorderLayout.NORTH);
        JScrollPane scroll=new JScrollPane(narrative);scroll.setBorder(BorderFactory.createLineBorder(new Color(0x36435b)));dialogue.add(scroll,BorderLayout.CENTER);center.add(dialogue,BorderLayout.CENTER);choices.setOpaque(false);center.add(choices,BorderLayout.SOUTH);game.add(center,BorderLayout.CENTER);
        JPanel bottom=panel(new BorderLayout());JPanel actions=panel(new FlowLayout(FlowLayout.LEFT,7,3));
        for(JButton b:new JButton[]{save,load,log,settings,menu})actions.add(b);bottom.add(actions,BorderLayout.CENTER);bottom.add(status,BorderLayout.SOUTH);game.add(bottom,BorderLayout.SOUTH);
        screens.add(welcome,"menu");screens.add(game,"game");add(screens,BorderLayout.CENTER);styleLabels(screens);
        menu.addActionListener(e->{finishText();cards.show(screens,"menu");});resume.addActionListener(e->cards.show(screens,"game"));settings.addActionListener(e->readingSettings());
        for(int i=0;i<3;i++){final int index=i;String key="choice"+i;
            screens.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke((char)('1'+i)),key);
            screens.getActionMap().put(key,new AbstractAction(){@Override public void actionPerformed(ActionEvent e){if(choices.isShowing() && index<currentButtons.size() && currentButtons.get(index).isEnabled())currentButtons.get(index).doClick();}});
        }
        screens.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"continue");
        screens.getActionMap().put("continue",new AbstractAction(){@Override public void actionPerformed(ActionEvent e){if(reveal.isRunning())finishText();else if(choices.isShowing() && currentButtons.size()==1)currentButtons.get(0).doClick();}});
    }
    /** Hosts the view in a desktop window; component rendering also works headlessly. */
    public void showWindow(){
        JFrame window=new JFrame("Finals Week — Presentation Edition / Team 6");
        window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        window.addWindowListener(new WindowAdapter(){@Override public void windowClosing(WindowEvent e){
            if(!hasGame || confirm("Close Finals Week? Check that your latest scene was saved before exiting.")){reveal.stop();window.dispose();}
        }});
        window.setContentPane(this);window.pack();window.setMinimumSize(new Dimension(800,740));window.setLocationRelativeTo(null);window.setVisible(true);
    }
    private static JPanel panel(LayoutManager layout){JPanel p=new JPanel(layout);p.setBackground(BACK);return p;}
    private static JButton button(String text){JButton b=new JButton(text);b.setBackground(PANEL);b.setForeground(INK);b.setFocusPainted(false);b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0x495b77)),BorderFactory.createEmptyBorder(9,14,9,14)));return b;}
    private static void styleLabels(Container parent){for(Component c:parent.getComponents()){if(c instanceof JLabel && !((JLabel)c).getForeground().equals(ACCENT))c.setForeground(INK);if(c instanceof Container)styleLabels((Container)c);}}
    private void finishText(){reveal.stop();narrative.setText(fullText);narrative.setCaretPosition(0);}
    private void readingSettings(){
        JSpinner size=new JSpinner(new SpinnerNumberModel(narrative.getFont().getSize(),14,28,1));JCheckBox animate=new JCheckBox("Reveal text gradually (click text to finish)",prefs.getBoolean("animate",false));
        JPanel p=new JPanel(new GridLayout(0,1,8,8));p.add(new JLabel("Narrative font size"));p.add(size);p.add(animate);
        if(JOptionPane.showConfirmDialog(this,p,"Reading Settings",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){int value=(Integer)size.getValue();narrative.setFont(narrative.getFont().deriveFont((float)value));prefs.putInt("fontSize",value);prefs.putBoolean("animate",animate.isSelected());finishText();}
    }
    /** Wires global actions once. */
    public void registerActions(Runnable onNew,Runnable onSave,Runnable onLoad,Runnable onLog){
        begin.addActionListener(e->{if(!hasGame || confirm("Start a new story? Save your current run in a manual slot first if you want to keep it."))onNew.run();});
        save.addActionListener(e->onSave.run());load.addActionListener(e->onLoad.run());menuLoad.addActionListener(e->onLoad.run());log.addActionListener(e->onLog.run());
    }
    /** Displays the current scene and only the actions available in that scene. */
    public void render(StoryNode scene,GameState state,String summary,IntConsumer onChoice,Runnable onContinue,Runnable onRestart){
        hasGame=true;resume.setEnabled(true);reveal.stop();
        if(scene.isEnding())prefs.putBoolean("ending."+scene.getId(),true);heading.setText(scene.getTitle());chapter.setText(scene.getChapter().toUpperCase());speaker.setText(scene.getSpeaker());
        String[] keys={"knowledge","social","energy","confidence"};for(int i=0;i<4;i++){int value=state.getStatsSnapshot().get(keys[i]);meters[i].setValue(value);meters[i].setString(Integer.toString(value));}
        art.setScene(scene.getId(),scene.getLocation(),scene.getSpeaker());
        fullText=scene.getNarrative();cursor=0;narrative.setText("");if(prefs.getBoolean("animate",false))reveal.start();else finishText();
        choices.removeAll();currentButtons.clear();
        for(int i=0;i<scene.getChoices().size();i++){
            visualnovel.model.Choice option=scene.getChoices().get(i);final int index=i;boolean allowed=option.isAvailable(state.getStatsSnapshot());
            JButton b=button((i+1)+"   "+option.getText()+(allowed?"":"   [Requires "+requirements(option)+"]"));b.setEnabled(allowed);b.setHorizontalAlignment(SwingConstants.LEFT);b.setToolTipText("Effects: "+option.getStatChanges());b.addActionListener(e->{finishText();onChoice.accept(index);});choices.add(b);currentButtons.add(b);
        }
        if(!scene.getContinuation().isEmpty())addAction("Continue  →",onContinue);
        if(scene.isEnding())addAction("Start another story",()->{if(confirm("Replay from Monday? Your current ending remains in autosave until the new game starts."))onRestart.run();});
        long decisions=state.getHistory().stream().filter(h->!h.endsWith(": Continue")).count();
        status.setText("Decisions: "+Math.min(decisions,8)+" / 8   •   Autosave at every scene   •   1–3: choose   •   Space: continue");
        cards.show(screens,"game");choices.revalidate();choices.repaint();
    }
    private void showEndingCollection(){
        String[] ids={"honorsEnding","communityEnding","passedEnding","burnoutEnding","failedEnding"};
        String[] names={"Distinction — exceptional preparation with energy to spare", "Together — pass with strong support", "Forward — a steady passing finish", "Burnout — prepared, but running on empty", "Begin Again — a chance to rebuild"};
        StringBuilder text=new StringBuilder("FIVE WAYS THE WEEK CAN END\n\n");int count=0;
        for(int i=0;i<ids.length;i++){boolean found=prefs.getBoolean("ending."+ids[i],false);if(found)count++;text.append(found?"DISCOVERED: ":"UNDISCOVERED: ").append(names[i]).append("\n\n");}
        text.append(count).append(" / 5 discovered on this computer.\nDifferent habits lead to different conclusions.");showMessage(text.toString());
    }
    private static String requirements(visualnovel.model.Choice option){return option.getRequirements().entrySet().stream().map(e->(e.getKey().equals("social")?"support":e.getKey())+" "+e.getValue()).collect(java.util.stream.Collectors.joining(", "));}
    private void addAction(String text,Runnable action){JButton b=button(text);b.addActionListener(e->{finishText();action.run();});choices.add(b);currentButtons.add(b);}
    /** @return true only after an explicit confirmation */
    public boolean confirm(String text){return JOptionPane.showConfirmDialog(this,text,"Finals Week",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;}
    /** @return one-based selected slot, or zero on cancel */
    public int selectSlot(String action,String[] labels){Object chosen=JOptionPane.showInputDialog(this,"Choose a slot:",action,JOptionPane.PLAIN_MESSAGE,null,labels,labels[0]);if(chosen==null)return 0;for(int i=0;i<labels.length;i++)if(labels[i].equals(chosen))return i+1;return 0;}
    /** Shows a scrollable decision journal. */
    public void showHistory(java.util.List<String> history){JTextArea text=new JTextArea("YOUR WEEK\n\nBuild understanding. Protect rest. Ask for support.\n\n"+(history.isEmpty()?"Your story is just beginning.":String.join("\n\n",history)),20,65);text.setEditable(false);text.setLineWrap(true);text.setWrapStyleWord(true);text.setFont(narrative.getFont());JOptionPane.showMessageDialog(this,new JScrollPane(text),"Decision Journal",JOptionPane.PLAIN_MESSAGE);}
    /** Shows a status or error message. */
    public void showMessage(String text){JOptionPane.showMessageDialog(this,text);}
}
