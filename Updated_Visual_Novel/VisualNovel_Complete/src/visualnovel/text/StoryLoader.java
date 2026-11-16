package visualnovel.text;
import visualnovel.model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
/** Parses UTF-8 story resources; narrative content is kept outside Java source. */
public final class StoryLoader {
    /** Reads NODE and CHOICE records in the format documented in README.md.
     * @param input story stream (closed by this method)
     * @return validated story graph
     * @throws IOException if data is missing, malformed, or has invalid references
     */
    public Story load(InputStream input) throws IOException {
        if(input==null) throw new IOException("Story resource was not found.");
        Map<String,StoryNode> nodes=new LinkedHashMap<>();
        String start=null; String[] scene=null; List<Choice> choices=new ArrayList<>();
        int lineNumber=0;
        try(BufferedReader reader=new BufferedReader(new InputStreamReader(input,StandardCharsets.UTF_8))) {
            String line;
            while((line=reader.readLine())!=null) {
                lineNumber++;
                if(line.isBlank() || line.startsWith("#")) continue;
                String[] fields=line.split("\\|",-1);
                switch(fields[0]) {
                    case "START" -> {
                        if(fields.length!=2 || start!=null || fields[1].isBlank())
                            throw new IllegalArgumentException("Invalid or duplicate START.");
                        start=fields[1];
                    }
                    case "NODE" -> {
                        if(scene!=null) addNode(nodes,scene,choices);
                        if((fields.length!=5 && fields.length!=8) || fields[1].isBlank()
                            || !(fields[4].equals("normal") || fields[4].equals("exam") || fields[4].startsWith("continue:")))
                            throw new IllegalArgumentException("Invalid NODE.");
                        scene=fields; choices=new ArrayList<>();
                    }
                    case "CHOICE" -> {
                        if(scene==null || (fields.length!=4 && fields.length!=5) || fields[1].isBlank())
                            throw new IllegalArgumentException("Invalid CHOICE.");
                        Map<String,Integer> effects=new LinkedHashMap<>();
                        if(!fields[3].isEmpty()) for(String effect:fields[3].split(",",-1)) {
                            String[] pair=effect.split("=",-1);
                            if(pair.length!=2 || effects.putIfAbsent(pair[0],Integer.parseInt(pair[1]))!=null)
                                throw new IllegalArgumentException("Invalid or duplicate attribute effect.");
                        }
                        Map<String,Integer> requirements=new LinkedHashMap<>();
                        if(fields.length==5 && !fields[4].isEmpty()) for(String requirement:fields[4].split(",",-1)) {
                            String[] pair=requirement.split("=",-1);
                            if(pair.length!=2 || requirements.putIfAbsent(pair[0],Integer.parseInt(pair[1]))!=null)
                                throw new IllegalArgumentException("Invalid requirement.");
                        }
                        choices.add(new Choice(fields[1],fields[2],effects,requirements));
                    }
                    default -> throw new IllegalArgumentException("Unknown record: "+fields[0]);
                }
            }
            if(scene!=null) addNode(nodes,scene,choices);
            return new Story(start,nodes);
        } catch(IllegalArgumentException ex) {
            throw new IOException("Invalid story near line "+lineNumber+": "+ex.getMessage(),ex);
        }
    }
    private void addNode(Map<String,StoryNode> nodes,String[] data,List<Choice> choices) {
        if(data[4].startsWith("continue:") && (!choices.isEmpty() || data[4].length()==9))
            throw new IllegalArgumentException("Continue scenes need a destination and no choices.");
        StoryNode node=data[4].startsWith("continue:")
            ? new StoryNode(data[1],data[2],data[3],data[4].substring(9))
            : new StoryNode(data[1],data[2],data[3],choices,data[4].equals("exam"));
        if(data.length==8) node=new StoryNode(data[1],data[2],data[3].replace("\\n","\n"),choices,data[4].equals("exam"),data[4].startsWith("continue:")?data[4].substring(9):"",data[5],data[6],data[7]);
        if(nodes.putIfAbsent(node.getId(),node)!=null)
            throw new IllegalArgumentException("Duplicate scene: "+node.getId());
    }
}
