package visualnovel.model;
/** Owns one game's mutable state and progression rules, without GUI or file access. */
public final class StoryEngine {
    private final Story story;
    private PlayerStats stats;
    private String currentNodeId;
    private java.util.List<String> history=new java.util.ArrayList<>();
    /** Advances a narrative-only scene without applying choice effects. */
    public void advance() {
        String next=getCurrentNode().getContinuation();
        if(next.isEmpty()) throw new IllegalStateException("This scene has no continuation.");
        story.getNode(next); history.add(getCurrentNode().getTitle()+": Continue"); currentNodeId=next;
    }
    /** @return whether a current choice meets its minimum requirements */
    public boolean canChoose(int index) { return getCurrentNode().getChoices().get(index).isAvailable(stats.snapshot()); }
    /** Creates an independent game; instances permit testing and separate sessions.
     * @param story validated story graph
     */
    public StoryEngine(Story story) { this.story=story; startNewGame(); }
    /** Resets the game to the starting scene and baseline attributes. */
    public void startNewGame() { stats=new PlayerStats(); currentNodeId=story.getStartId(); history.clear(); }
    /** @return current immutable scene */
    public StoryNode getCurrentNode() { return story.getNode(currentNodeId); }
    /** @return a display summary without exposing mutable game attributes */
    public String getStatsDisplay() { return stats.toDisplayString(); }
    /** Selects a current option, applies effects, and resolves the next scene.
     * @param index zero-based index of a current choice
     * @throws IndexOutOfBoundsException if no such option is available
     */
    public void choose(int index) {
        StoryNode current=getCurrentNode();
        Choice choice=current.getChoices().get(index);
        if(!canChoose(index)) throw new IllegalArgumentException("Choice requirements are not met.");
        PlayerStats next=new PlayerStats(); next.restore(stats.snapshot());
        next.applyChanges(choice.getStatChanges());
        String destination=current.isExam() ? evaluateEnding(next) : choice.getNextNodeId();
        story.getNode(destination); // Validate before committing either part of the game state.
        history.add(current.getTitle()+": "+choice.getText());
        stats=next; currentNodeId=destination;
    }
    private String evaluateEnding(PlayerStats candidate) {
        int knowledge=candidate.getKnowledge(), confidence=candidate.getConfidence(), energy=candidate.getEnergy();
        if(knowledge<8 || confidence<4) return "failedEnding";
        if(energy<1) return "burnoutEnding";
        if(candidate.getSocial()>=5) return "communityEnding";
        if(knowledge>=14 && confidence>=8 && energy>=3) return "honorsEnding";
        return "passedEnding";
    }
    /** @return immutable snapshot for persistence */
    public GameState snapshot() { return new GameState(currentNodeId,stats.snapshot(),history); }
    /** Validates and restores a save without partially changing a running game.
     * @param state saved scene and absolute attribute values
     * @throws IllegalArgumentException for an unknown scene or incomplete attributes
     */
    public void restore(GameState state) {
        story.getNode(state.getCurrentNodeId());
        PlayerStats restored=new PlayerStats(); restored.restore(state.getStatsSnapshot());
        stats=restored; currentNodeId=state.getCurrentNodeId(); history=new java.util.ArrayList<>(state.getHistory());
    }
}
