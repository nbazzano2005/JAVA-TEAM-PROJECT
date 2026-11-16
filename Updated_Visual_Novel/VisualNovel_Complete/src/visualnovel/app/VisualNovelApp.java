package visualnovel.app;
import visualnovel.controller.GameController;
import visualnovel.model.*;
import visualnovel.persistence.SaveManager;
import visualnovel.text.StoryLoader;
import visualnovel.view.VisualNovelFrame;
import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
/** Application entry point; creates and connects the MVC components. */
public final class VisualNovelApp {
    /** Prevents instances because this class only supplies the JVM entry point. */
    private VisualNovelApp() { }
    /** Starts the application on Swing's event dispatch thread.
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> {
            try {
                Story story=new StoryLoader().load(VisualNovelApp.class.getResourceAsStream("/visualnovel/text/story.txt"));
                VisualNovelFrame view=new VisualNovelFrame();
                new GameController(new StoryEngine(story),new SaveManager(Path.of("savegame.properties")),view);
                view.showWindow();
            } catch(IOException ex) { JOptionPane.showMessageDialog(null,"Unable to start: "+ex.getMessage()); }
        });
    }
}
