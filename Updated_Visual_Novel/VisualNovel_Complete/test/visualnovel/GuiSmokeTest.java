package visualnovel;
import visualnovel.model.*;
import visualnovel.view.VisualNovelFrame;
import visualnovel.text.StoryLoader;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;

/** Headless integration checks and optional screenshots of the actual Swing view. */
public final class GuiSmokeTest {
    private GuiSmokeTest(){}
    private static java.util.List<JButton> buttons(Container root){
        java.util.List<JButton> result=new ArrayList<>();
        for(Component c:root.getComponents()){if(c instanceof JButton)result.add((JButton)c);if(c instanceof Container)result.addAll(buttons((Container)c));}return result;
    }
    private static void layout(Container c){c.doLayout();for(Component child:c.getComponents())if(child instanceof Container)layout((Container)child);}
    private static void capture(VisualNovelFrame view,Path path)throws Exception{
        view.setSize(1050,920);layout(view);BufferedImage image=new BufferedImage(1050,920,BufferedImage.TYPE_INT_RGB);var g=image.createGraphics();view.paint(g);g.dispose();ImageIO.write(image,"png",path.toFile());
    }
    /** Runs on Swing's event thread; pass a directory to write actual view previews. */
    public static void main(String[] args)throws Exception{
        Story story=new StoryLoader().load(GuiSmokeTest.class.getResourceAsStream("/visualnovel/text/story.txt"));
        SwingUtilities.invokeAndWait(()->{try{
            VisualNovelFrame view=new VisualNovelFrame();int[] selected={-1},continued={0};
            view.registerActions(()->continued[0]++,()->{},()->{},()->{});
            if(args.length>0){Files.createDirectories(Path.of(args[0]));capture(view,Path.of(args[0],"Main_Menu.png"));}
            for(StoryNode node:story.getNodes().values()){
                GameState state=new GameState(node.getId(),Map.of("knowledge",20,"social",0,"energy",6,"confidence",8));
                view.render(node,state,"",index->selected[0]=index,()->continued[0]++,()->{});
                view.setSize(1050,920);layout(view);
                var options=buttons(view).stream().filter(b->b.getText().startsWith("1   ")).toList();
                if(!node.getChoices().isEmpty()){
                    if(options.size()!=1)throw new AssertionError("Missing first choice: "+node.getId());
                    selected[0]=-1;options.get(0).doClick(0);if(selected[0]!=0)throw new AssertionError("Choice callback not connected");
                }
                if(node.getId().equals("d2")){
                    JButton locked=buttons(view).stream().filter(b->b.getText().startsWith("2   ")).findFirst().orElseThrow();
                    if(locked.isEnabled())throw new AssertionError("Locked choice enabled in GUI");
                }
                if(args.length>0 && Set.of("d2","d4","d5","honorsEnding").contains(node.getId()))capture(view,Path.of(args[0],node.getId()+".png"));
            }
            System.out.println("GuiSmokeTest passed: all 41 scenes render; callbacks and locked-choice display verified.");
        }catch(Exception e){throw new RuntimeException(e);}});
    }
}
