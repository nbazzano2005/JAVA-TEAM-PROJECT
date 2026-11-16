package visualnovel.controller;
import visualnovel.model.*;
import visualnovel.persistence.SaveManager;
import visualnovel.view.VisualNovelFrame;
import java.io.IOException;
import java.nio.file.Path;
/** Coordinates model, view, timestamped save slots, and history. */
public final class GameController {
    private final StoryEngine engine;
    private final SaveManager[] slots;
    private final VisualNovelFrame view;
    private boolean playing;
    /** Connects services; the first slot preserves the original savegame.properties location. */
    public GameController(StoryEngine engine,SaveManager firstSlot,VisualNovelFrame view) {
        this.engine=engine; this.view=view;
        slots=new SaveManager[]{firstSlot,new SaveManager(Path.of("savegame2.properties")),new SaveManager(Path.of("savegame3.properties")),new SaveManager(Path.of("autosave.properties"))};
        view.registerActions(this::startNewGame,this::saveGame,this::loadGame,()->view.showHistory(engine.snapshot().getHistory()));
    }
    private void startNewGame() { engine.startNewGame(); playing=true; autoSave(); refresh(); }
    private void refresh() { view.render(engine.getCurrentNode(),engine.snapshot(),engine.getStatsDisplay(),this::choose,this::advance,this::startNewGame); }
    private void choose(int index) { engine.choose(index); autoSave(); refresh(); }
    private void advance() { engine.advance(); autoSave(); refresh(); }
    private void autoSave() {
        try { slots[3].save(engine.snapshot()); }
        catch(IOException ex) { view.showMessage("Autosave failed. Your game is still running: "+ex.getMessage()); }
    }
    private int select(String action) {
        int count=action.equals("Save Game")?3:4;
        String[] labels=new String[count];
        for(int i=0;i<count;i++) {
            try { labels[i]=(i==3?"Autosave":"Slot "+(i+1))+" - "+slots[i].getTimestamp(); }
            catch(IOException ex) { labels[i]="Slot "+(i+1)+" - Unreadable save"; }
        }
        return view.selectSlot(action,labels)-1;
    }
    private void saveGame() {
        int index=select("Save Game"); if(index<0) return;
        try {
            // Also permit replacing a damaged save after explicit confirmation.
            if(slots[index].exists() && !view.confirm("Overwrite slot "+(index+1)+"?")) return;
            slots[index].save(engine.snapshot()); view.showMessage("Saved: "+slots[index].getTimestamp());
        } catch(IOException ex) { view.showMessage("Unable to save: "+ex.getMessage()); }
    }
    private void loadGame() {
        int index=select("Load Game"); if(index<0) return;
        try {
            var state=slots[index].load();
            if(state.isEmpty()) { view.showMessage("This slot is empty."); return; }
            if(playing && !view.confirm("Load this save and replace the current progress?")) return;
            engine.restore(state.get()); playing=true; refresh();
        } catch(IOException | IllegalArgumentException ex) { view.showMessage("Unable to load: "+ex.getMessage()); }
    }
}
