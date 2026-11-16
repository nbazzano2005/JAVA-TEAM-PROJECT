package visualnovel.model;
import java.util.Map;
import java.util.Objects;
/** Immutable player option, destination, and attribute effects. */
public final class Choice {
    private final String text, nextNodeId;
    private final Map<String,Integer> statChanges;
    private Map<String,Integer> requirements=Map.of();
    /** Creates an option with minimum stat requirements. */
    public Choice(String text,String next,Map<String,Integer> changes,Map<String,Integer> requirements) {
        this(text,next,changes); this.requirements=Map.copyOf(requirements);
    }
    /** @return immutable minimum requirements */
    public Map<String,Integer> getRequirements() { return requirements; }
    /** @return whether all requirements are satisfied */
    public boolean isAvailable(Map<String,Integer> stats) {
        return requirements.entrySet().stream().allMatch(e->stats.getOrDefault(e.getKey(),Integer.MIN_VALUE)>=e.getValue());
    }
    /** Creates a story option.
     * @param text button label
     * @param nextNodeId destination; empty only for an exam decision
     * @param statChanges signed attribute changes
     */
    public Choice(String text, String nextNodeId, Map<String,Integer> statChanges) {
        this.text=Objects.requireNonNull(text);
        this.nextNodeId=Objects.requireNonNull(nextNodeId);
        this.statChanges=Map.copyOf(statChanges);
    }
    /** @return button label */
    public String getText() { return text; }
    /** @return destination scene ID */
    public String getNextNodeId() { return nextNodeId; }
    /** @return immutable attribute deltas */
    public Map<String,Integer> getStatChanges() { return statChanges; }
}
