package visualnovel.model;
import java.util.Map;
/** Immutable save snapshot, independent of Swing and file storage. */
public final class GameState {
    private final String currentNodeId;
    private final Map<String,Integer> statsSnapshot;
    private java.util.List<String> history=java.util.List.of();
    /** Captures scene, attributes, and the ordered decision log. */
    public GameState(String id,Map<String,Integer> stats,java.util.List<String> history) {
        this(id,stats); this.history=java.util.List.copyOf(history);
    }
    /** @return immutable decision log */
    public java.util.List<String> getHistory() { return history; }
    /** Captures a game.
     * @param currentNodeId current scene
     * @param statsSnapshot absolute attribute values
     */
    public GameState(String currentNodeId,Map<String,Integer> statsSnapshot) {
        this.currentNodeId=java.util.Objects.requireNonNull(currentNodeId);
        this.statsSnapshot=Map.copyOf(statsSnapshot);
    }
    /** @return current scene */
    public String getCurrentNodeId() { return currentNodeId; }
    /** @return immutable absolute attribute values */
    public Map<String,Integer> getStatsSnapshot() { return statsSnapshot; }
}
