
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Properties;

/*
 * CS321 Interactive Branching Story - MVP
 *
 * This game is about a student preparing for a final exam.
 * The player's choices change their stats and story path.
 */
public class ClassStoryGame extends JFrame {

    private GameState game = new GameState();

    private static final String SAVE_FILE = "savegame.properties";

    // Stores the player's current progress and statistics
    private static class GameState {
        String scene = "intro";

        int knowledge = 10;
        int relaxation = 50;
        int stagnation = 20;

        int examBonus = 0;
        int examScore = 0;

        boolean studiedFirst = false;
        String secondChoice = "";
    }

    // Sets up the game window
    public ClassStoryGame() {
        setTitle("Finals Week - CS321 Visual Novel");

        setSize(850, 600);
        setMinimumSize(new Dimension(650, 500));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        showMainMenu();

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClassStoryGame());
    }

    // Displays the main menu
    private void showMainMenu() {
        JPanel menu = new JPanel(new BorderLayout(15, 15));

        menu.setBorder(
            BorderFactory.createEmptyBorder(65, 80, 65, 80)
        );

        JLabel title = new JLabel(
            "FINALS WEEK",
            SwingConstants.CENTER
        );

        title.setFont(new Font("SansSerif", Font.BOLD, 34));
        menu.add(title, BorderLayout.NORTH);

        JTextArea description = createTextArea(
            "A choose-your-own-adventure story about surviving a college class.\n\n"
            + "Will you study enough to pass the final, "
            + "or will your choices catch up with you?"
        );

        description.setBackground(menu.getBackground());
        menu.add(description, BorderLayout.CENTER);

        JPanel menuButtons = new JPanel(
            new GridLayout(3, 1, 0, 12)
        );

        // Starts a new game
        addButton(menuButtons, "New Game", () -> {
            game = new GameState();
            showStory();
        });

        // Loads previous progress
        addButton(menuButtons, "Load Game", this::loadGame);

        // Closes the game
        addButton(menuButtons, "Exit", () -> dispose());

        menu.add(menuButtons, BorderLayout.SOUTH);

        setContentPane(menu);
        revalidate();
        repaint();
    }

    // Displays the current story scene
    private void showStory() {
        JPanel screen = new JPanel(new BorderLayout(12, 12));

        screen.setBorder(
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        );

        // Top of the screen
        JPanel top = new JPanel(new GridLayout(2, 1, 0, 8));

        JLabel heading = new JLabel(
            getSceneTitle(),
            SwingConstants.CENTER
        );

        heading.setFont(new Font("SansSerif", Font.BOLD, 25));
        top.add(heading);

        // Displays player statistics
        JLabel stats = new JLabel(
            "Knowledge: " + game.knowledge
            + "    |    Relaxation: " + game.relaxation
            + "    |    Academic Stagnation: " + game.stagnation,
            SwingConstants.CENTER
        );

        stats.setFont(new Font("SansSerif", Font.BOLD, 14));

        top.add(stats);
        screen.add(top, BorderLayout.NORTH);

        // Displays the story dialogue
        JTextArea dialogue = createTextArea(getSceneText());

        dialogue.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        screen.add(new JScrollPane(dialogue), BorderLayout.CENTER);

        // Bottom section for choices
        JPanel bottom = new JPanel(new BorderLayout(0, 12));

        JPanel choices = new JPanel(
            new GridLayout(0, 1, 0, 7)
        );

        // Determines which choices are available
        switch (game.scene) {

            case "intro":
                addButton(
                    choices,
                    "Study at the library",
                    () -> makeFirstChoice(true)
                );

                addButton(
                    choices,
                    "Relax at the park",
                    () -> makeFirstChoice(false)
                );
                break;

            case "library":
            case "park":
                addButton(
                    choices,
                    "Continue to next week",
                    () -> changeScene("week2")
                );
                break;

            case "week2":
                addButton(
                    choices,
                    "Practice programming problems",
                    () -> makeSecondChoice("practice")
                );

                addButton(
                    choices,
                    "Spend time with friends",
                    () -> makeSecondChoice("friends")
                );

                // Review session only appears if the player studied
                if (game.studiedFirst) {
                    addButton(
                        choices,
                        "Attend the professor's review session",
                        () -> makeSecondChoice("office")
                    );
                }
                break;

            case "practice":
            case "friends":
            case "office":
                addButton(
                    choices,
                    "Continue to exam week",
                    () -> changeScene("examPrep")
                );
                break;

            case "examPrep":
                addButton(
                    choices,
                    "Take a practice final (+10 exam bonus)",
                    () -> prepareForExam(true)
                );

                addButton(
                    choices,
                    "Get some rest before the exam (+10 relaxation)",
                    () -> prepareForExam(false)
                );
                break;

            case "exam":
                addButton(
                    choices,
                    "Take the final exam",
                    this::takeFinalExam
                );
                break;

            case "pass":
            case "fail":
                addButton(
                    choices,
                    "Play Again",
                    () -> {
                        game = new GameState();
                        showStory();
                    }
                );
                break;
        }

        bottom.add(choices, BorderLayout.CENTER);

        // Save, load, and main menu buttons
        JPanel controls = new JPanel(
            new FlowLayout(FlowLayout.RIGHT)
        );

        addButton(controls, "Save Game", this::saveGame);
        addButton(controls, "Load Game", this::loadGame);
        addButton(controls, "Main Menu", this::showMainMenu);

        bottom.add(controls, BorderLayout.SOUTH);
        screen.add(bottom, BorderLayout.SOUTH);

        setContentPane(screen);
        revalidate();
        repaint();
    }

    // Creates an area for displaying text
    private JTextArea createTextArea(String text) {
        JTextArea area = new JTextArea(text);

        area.setFont(new Font("SansSerif", Font.PLAIN, 17));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setFocusable(false);

        return area;
    }

    // Makes a button and gives it an action
    private void addButton(
        JPanel panel,
        String label,
        Runnable action
    ) {
        JButton button = new JButton(label);

        button.setFont(
            new Font("SansSerif", Font.PLAIN, 15)
        );

        button.addActionListener(event -> action.run());

        panel.add(button);
    }

    // Changes to another story scene
    private void changeScene(String nextScene) {
        game.scene = nextScene;
        showStory();
    }

    // Handles the first player decision
    private void makeFirstChoice(boolean study) {
        game.studiedFirst = study;

        if (study) {
            // Studying improves knowledge
            changeStats(30, -10, -10);
            changeScene("library");
        } else {
            // Relaxing helps relaxation but increases stagnation
            changeStats(0, 25, 15);
            changeScene("park");
        }
    }

    // Handles the second player decision
    private void makeSecondChoice(String choice) {
        game.secondChoice = choice;

        if (choice.equals("practice")) {
            changeStats(30, -10, -10);

        } else if (choice.equals("friends")) {
            changeStats(-5, 25, 20);

        } else if (choice.equals("office") && game.studiedFirst) {
            changeStats(20, -5, -15);

        } else {
            return;
        }

        changeScene(choice);
    }

    // Handles the player's exam preparation choice
    private void prepareForExam(boolean practiceFinal) {
        if (practiceFinal) {
            game.examBonus = 10;
            changeStats(0, -10, 0);
        } else {
            game.examBonus = 0;
            changeStats(0, 10, 0);
        }

        changeScene("exam");
    }

    // Updates player statistics
    private void changeStats(
        int knowledgeChange,
        int relaxationChange,
        int stagnationChange
    ) {
        game.knowledge = keepInRange(
            game.knowledge + knowledgeChange
        );

        game.relaxation = keepInRange(
            game.relaxation + relaxationChange
        );

        game.stagnation = keepInRange(
            game.stagnation + stagnationChange
        );
    }

    // Keeps statistics between 0 and 100
    private static int keepInRange(int number) {
        return Math.max(0, Math.min(100, number));
    }

    // Calculates whether the player passes or fails
    private void takeFinalExam() {
        int score = game.knowledge
                  + game.examBonus
                  + game.relaxation / 5
                  - game.stagnation / 3;

        game.examScore = keepInRange(score);

        // A score of 60 or higher is passing
        if (game.examScore >= 60) {
            changeScene("pass");
        } else {
            changeScene("fail");
        }
    }

    // Gets the title of each scene
    private String getSceneTitle() {
        switch (game.scene) {
            case "intro":
                return "First Day of Class";

            case "library":
                return "The Library";

            case "park":
                return "An Afternoon at the Park";

            case "week2":
                return "One Week Until the Final";

            case "practice":
                return "Programming Practice";

            case "friends":
                return "Game Night";

            case "office":
                return "Professor's Review Session";

            case "examPrep":
                return "The Night Before the Exam";

            case "exam":
                return "Final Exam Day";

            case "pass":
                return "ENDING: You Passed!";

            case "fail":
                return "ENDING: You Failed";

            default:
                return "Finals Week";
        }
    }

    // Gets the dialogue for the current scene
    private String getSceneText() {
        switch (game.scene) {

            case "intro":
                return "Professor: 'Welcome to computer science! "
                     + "Your final exam is in two weeks.'\n\n"
                     + "After class, you look at your calendar. "
                     + "You have some free time today. "
                     + "You can study now, or take a break.\n\n"
                     + "What will you do?";

            case "library":
                return "You spend the afternoon reading your textbook "
                     + "and testing Java code.\n\n"
                     + "You feel more prepared, but you are a little tired.\n\n"
                     + "Knowledge +30 | Relaxation -10 | Stagnation -10";

            case "park":
                return "You take a walk at the park and enjoy "
                     + "a relaxing afternoon.\n\n"
                     + "You feel refreshed, but you have not "
                     + "made progress studying.\n\n"
                     + "Relaxation +25 | Stagnation +15";

            case "week2":
                if (game.studiedFirst) {
                    return "Professor: 'I noticed you started "
                         + "preparing early. You're welcome "
                         + "to attend my extra review session.'\n\n"
                         + "One week remains. You can practice "
                         + "on your own, relax with friends, "
                         + "or use the review session you unlocked "
                         + "by studying earlier.";
                }

                return "Professor: 'The final is only a week away. "
                     + "Make sure you prepare!'\n\n"
                     + "You took a break last week, and the "
                     + "professor's extra review session "
                     + "is not available to you. "
                     + "You can still practice independently "
                     + "or spend time with friends.";

            case "practice":
                return "You work through sample programs, "
                     + "fix errors, and complete practice problems.\n\n"
                     + "Knowledge +30 | Relaxation -10 | Stagnation -10";

            case "friends":
                return "Your friends invite you to a game night. "
                     + "You decide to join them.\n\n"
                     + "It's fun, but your study time disappears.\n\n"
                     + "Knowledge -5 | Relaxation +25 | Stagnation +20";

            case "office":
                return "You attend the professor's review session "
                     + "and ask about difficult topics.\n\n"
                     + "Because you studied early, "
                     + "you understand the explanations.\n\n"
                     + "Knowledge +20 | Relaxation -5 | Stagnation -15";

            case "examPrep":
                String previous;

                if (game.studiedFirst) {
                    previous = "You began preparing by studying at the library.";
                } else {
                    previous = "You began the semester's final stretch "
                             + "with a trip to the park.";
                }

                return previous
                     + "\n\nYour more recent choice was: "
                     + describeSecondChoice()
                     + ".\n\n"
                     + "The final exam is tomorrow. "
                     + "Do you take a practice final "
                     + "or get a good night's sleep?";

            case "exam":
                return "You walk into class and sit down "
                     + "for the final exam.\n\n"
                     + "Your result will depend on Knowledge, "
                     + "Relaxation, Academic Stagnation, "
                     + "and any exam preparation bonus.\n\n"
                     + "You need a score of 60 or higher to pass. Ready?";

            case "pass":
                return "Professor: 'Congratulations! "
                     + "You passed the class!'\n\n"
                     + "Final exam score: "
                     + game.examScore
                     + "/100\n\n"
                     + "Your studying and decisions helped you succeed.";

            case "fail":
                return "Professor: 'Unfortunately, "
                     + "you did not pass the final exam.'\n\n"
                     + "Final exam score: "
                     + game.examScore
                     + "/100\n\n"
                     + "Try again and make different decisions. "
                     + "More preparation may help!";

            default:
                return "The story could not find the current scene.";
        }
    }

    // Gives a description of an earlier choice
    private String describeSecondChoice() {
        if (game.secondChoice.equals("practice")) {
            return "practicing programming";
        }

        if (game.secondChoice.equals("friends")) {
            return "spending time with friends";
        }

        if (game.secondChoice.equals("office")) {
            return "attending the professor's review";
        }

        return "none yet";
    }

    // Saves the current progress to a file
    private void saveGame() {
        Properties save = new Properties();

        save.setProperty("scene", game.scene);
        save.setProperty("knowledge", String.valueOf(game.knowledge));
        save.setProperty("relaxation", String.valueOf(game.relaxation));
        save.setProperty("stagnation", String.valueOf(game.stagnation));

        save.setProperty("examBonus", String.valueOf(game.examBonus));
        save.setProperty("examScore", String.valueOf(game.examScore));

        save.setProperty(
            "studiedFirst",
            String.valueOf(game.studiedFirst)
        );

        save.setProperty("secondChoice", game.secondChoice);

        try (FileOutputStream file = new FileOutputStream(SAVE_FILE)) {
            save.store(file, "CS321 Visual Novel Save Game");

            JOptionPane.showMessageDialog(
                this,
                "Game saved to " + SAVE_FILE
            );

        } catch (IOException error) {
            JOptionPane.showMessageDialog(
                this,
                "Could not save game: " + error.getMessage()
            );
        }
    }

    // Loads the player's previous save
    private void loadGame() {
        Properties save = new Properties();

        try (FileInputStream file = new FileInputStream(SAVE_FILE)) {
            save.load(file);

            GameState loaded = new GameState();

            loaded.scene = save.getProperty("scene", "");

            loaded.knowledge = Integer.parseInt(
                save.getProperty("knowledge", "")
            );

            loaded.relaxation = Integer.parseInt(
                save.getProperty("relaxation", "")
            );

            loaded.stagnation = Integer.parseInt(
                save.getProperty("stagnation", "")
            );

            loaded.examBonus = Integer.parseInt(
                save.getProperty("examBonus", "")
            );

            loaded.examScore = Integer.parseInt(
                save.getProperty("examScore", "")
            );

            loaded.studiedFirst = Boolean.parseBoolean(
                save.getProperty("studiedFirst", "false")
            );

            loaded.secondChoice = save.getProperty("secondChoice", "");

            // Checks that the loaded data is valid
            if (!isValidScene(loaded.scene) || !areStatsValid(loaded)) {
                throw new IOException("Invalid saved game data");
            }

            game = loaded;
            showStory();

            JOptionPane.showMessageDialog(
                this,
                "Game loaded successfully!"
            );

        } catch (FileNotFoundException error) {
            JOptionPane.showMessageDialog(
                this,
                "No save file found. Start a new game first."
            );

        } catch (IOException | NumberFormatException error) {
            JOptionPane.showMessageDialog(
                this,
                "Could not load game: " + error.getMessage()
            );
        }
    }

    // Makes sure loaded stats are within their limits
    private boolean areStatsValid(GameState loaded) {
        return loaded.knowledge >= 0 && loaded.knowledge <= 100
            && loaded.relaxation >= 0 && loaded.relaxation <= 100
            && loaded.stagnation >= 0 && loaded.stagnation <= 100
            && loaded.examBonus >= 0 && loaded.examBonus <= 10
            && loaded.examScore >= 0 && loaded.examScore <= 100;
    }

    // Checks whether the saved scene exists
    private boolean isValidScene(String scene) {
        switch (scene) {
            case "intro":
            case "library":
            case "park":
            case "week2":
            case "practice":
            case "friends":
            case "office":
            case "examPrep":
            case "exam":
            case "pass":
            case "fail":
                return true;

            default:
                return false;
        }
    }
}
