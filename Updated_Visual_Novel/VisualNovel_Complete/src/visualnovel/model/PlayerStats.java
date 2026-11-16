package visualnovel.model;
import java.util.Map;
import java.util.Locale;
/** Mutable player attributes; a new game starts rested enough to take the exam. */
public final class PlayerStats {
    private int knowledge, social, energy, confidence;
    /** Initializes the same baseline used by reset(). */
    public PlayerStats() { reset(); }
    /** Starts a new game with zero knowledge, social, and confidence, and four energy. */
    public void reset() { knowledge = social = confidence = 0; energy = 4; }
    /** Adds an amount to a named attribute.
     * @param statName knowledge, social, energy, or confidence (case insensitive)
     * @param amount signed change
     * @throws IllegalArgumentException if the name is unknown
     */
    public void changeStat(String statName, int amount) {
        switch (statName.toLowerCase(Locale.ROOT)) {
            case "knowledge" -> knowledge += amount;
            case "social" -> social += amount;
            case "energy" -> energy += amount;
            case "confidence" -> confidence += amount;
            default -> throw new IllegalArgumentException("Unknown stat: " + statName);
        }
    }
    /** Applies signed deltas; null means there are no changes.
     * @param changes attribute names mapped to deltas
     */
    public void applyChanges(Map<String,Integer> changes) {
        if (changes != null) changes.forEach(this::changeStat);
    }
    /** @return current knowledge */
    public int getKnowledge() { return knowledge; }
    /** @return current social support */
    public int getSocial() { return social; }
    /** @return current energy */
    public int getEnergy() { return energy; }
    /** @return current confidence */
    public int getConfidence() { return confidence; }
    /** @return immutable copy of all current attribute values */
    public Map<String,Integer> snapshot() {
        return Map.of("knowledge",knowledge,"social",social,"energy",energy,"confidence",confidence);
    }
    /** Restores absolute values, rather than adding them to the new-game baseline.
     * @param values complete snapshot containing all four attributes
     * @throws IllegalArgumentException if the snapshot is incomplete
     */
    public void restore(Map<String,Integer> values) {
        if (!values.keySet().equals(snapshot().keySet()) || values.values().stream().anyMatch(java.util.Objects::isNull))
            throw new IllegalArgumentException("A save must contain all four attributes.");
        knowledge=values.get("knowledge"); social=values.get("social");
        energy=values.get("energy"); confidence=values.get("confidence");
    }
    /** @return a compact attribute summary for display */
    public String toDisplayString() {
        return "Knowledge: " + knowledge + " | Social: " + social
            + " | Energy: " + energy + " | Confidence: " + confidence;
    }
}
