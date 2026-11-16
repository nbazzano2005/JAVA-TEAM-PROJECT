package visualnovel.persistence;
import visualnovel.model.GameState;
import java.io.*;
import java.nio.file.*;
import java.util.*;
/** Reads and writes a single properties save, without knowing about the GUI. */
public final class SaveManager {
    private final Path path;
    /** Sets the save location.
     * @param path save file path
     */
    public SaveManager(Path path) { this.path=path.toAbsolutePath(); }
    /** Writes a snapshot using a temporary file to avoid truncating an existing save.
     * @param state absolute game state
     * @throws IOException if writing or replacing the save fails
     */
    public void save(GameState state) throws IOException {
        Properties data=new Properties();
        data.setProperty("formatVersion","3");
        data.setProperty("campaign","finals-week-presentation-1");
        data.setProperty("savedAt",java.time.Instant.now().toString());
        data.setProperty("history.count",Integer.toString(state.getHistory().size()));
        for(int i=0;i<state.getHistory().size();i++) data.setProperty("history."+i,state.getHistory().get(i));
        data.setProperty("currentNodeId",state.getCurrentNodeId());
        state.getStatsSnapshot().forEach((key,value)->data.setProperty(key,value.toString()));
        Files.createDirectories(path.getParent());
        Path temp=Files.createTempFile(path.getParent(),"visualnovel-",".tmp");
        try {
            try(OutputStream out=Files.newOutputStream(temp)) { data.store(out,"Visual Novel Save"); }
            try { Files.move(temp,path,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE); }
            catch(AtomicMoveNotSupportedException ex) { Files.move(temp,path,StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(temp); }
    }
    /** @return whether the slot contains a file or other existing target */
    public boolean exists() { return Files.exists(path); }
    /** @return formatted save timestamp, or a legacy/missing label */
    public String getTimestamp() throws IOException {
        if(!Files.exists(path)) return "Empty slot";
        Properties data=new Properties();
        try(InputStream in=Files.newInputStream(path)) { data.load(in); }
        catch(IllegalArgumentException ex) { throw new IOException("Malformed save file.",ex); }
        String time=data.getProperty("savedAt");
        if(time==null) return "Legacy save (no timestamp)";
        try { return java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a z")
            .withZone(java.time.ZoneId.systemDefault()).format(java.time.Instant.parse(time)); }
        catch(java.time.DateTimeException ex) { throw new IOException("Invalid save timestamp.",ex); }
    }
    /** Reads a complete save; a missing save differs from an unreadable or damaged one.
     * @return empty when no save exists, otherwise the saved snapshot
     * @throws IOException if a save cannot be read or required fields are invalid
     */
    public Optional<GameState> load() throws IOException {
        Properties data=new Properties();
        try(InputStream in=Files.newInputStream(path)) { data.load(in); }
        catch(NoSuchFileException ex) { return Optional.empty(); }
        catch(IllegalArgumentException ex) { throw new IOException("Malformed save file.",ex); }
        try {
            String node=data.getProperty("currentNodeId");
            if(node==null || node.isBlank()) throw new IllegalArgumentException("Missing scene ID.");
            Map<String,Integer> stats=new HashMap<>();
            for(String key:List.of("knowledge","social","energy","confidence"))
                stats.put(key,Integer.parseInt(data.getProperty(key)));
            String version=data.getProperty("formatVersion","1");
            if(!version.equals("3") || !"finals-week-presentation-1".equals(data.getProperty("campaign")))
                throw new IllegalArgumentException("This save belongs to a different story edition. Start a new story for the Presentation Edition.");
            java.time.Instant.parse(data.getProperty("savedAt",""));
            int count=Integer.parseInt(data.getProperty("history.count","0"));
            if(count<0 || count>10000) throw new IllegalArgumentException("Invalid history length.");
            List<String> history=new ArrayList<>();
            for(int i=0;i<count;i++) {
                String item=data.getProperty("history."+i);
                if(item==null) throw new IllegalArgumentException("Incomplete decision history.");
                history.add(item);
            }
            return Optional.of(new GameState(node,stats,history));
        } catch(IllegalArgumentException | java.time.DateTimeException ex) { throw new IOException("Invalid or incomplete save data.",ex); }
    }
}
