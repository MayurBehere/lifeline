import java.io.*;
import java.util.*;

public class macrop2 {

    static class ALAEntry {
        String index;
        String formalArg;
        String actualArg;
        String macroName;

        public ALAEntry(String index, String formalArg, String actualArg, String macroName) {
            this.index = index;
            this.formalArg = formalArg;
            this.actualArg = actualArg;
            this.macroName = macroName;
        }

        @Override
        public String toString() {
            return String.format("%-10s %-15s %-15s %-10s", index, formalArg, actualArg, macroName);
        }
    }

    static class MacroDefinition {
        String macroName;
        List<String> formalParams;
        List<String> bodyLines;

        public MacroDefinition(String macroName, List<String> formalParams, List<String> bodyLines) {
            this.macroName = macroName;
            this.formalParams = formalParams;
            this.bodyLines = bodyLines;
        }
    }

    static Map<String, Integer> mntMapping = new HashMap<>();
    static TreeMap<Integer, String> mdtMapping = new TreeMap<>();
    static Map<String, List<ALAEntry>> macroALA = new HashMap<>();
    static Map<String, MacroDefinition> macroDefinitions = new HashMap<>();

    public static void main(String[] args) {
        String mntFile = "MNT.txt";
        String mdtFile = "MDT.txt";
        String alaFile = "ALA.txt";
        String intermediateFile = "Intermediate.txt";
        String finalOutputFile = "FinalOutput.txt";

        loadMNT(mntFile);
        loadMDT(mdtFile);
        loadALA(alaFile);

        buildMacroDefinitions();

        processIntermediate(intermediateFile, finalOutputFile);

        System.out.println("Pass 2 completed successfully.");
        System.out.println("Final output written to " + finalOutputFile);
    }

    private static void loadMNT(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                String[] tokens = line.split("\\s+");
                if (tokens.length < 3)
                    continue;
                String macroName = tokens[1];
                int mdtIndex = Integer.parseInt(tokens[2]);
                mntMapping.put(macroName, mdtIndex);
            }
        } catch (IOException e) {
            System.err.println("Error reading MNT file: " + e.getMessage());
        }
    }

    private static void loadMDT(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                String[] tokens = line.split("\\s+", 2);
                if (tokens.length < 2)
                    continue;
                int index = Integer.parseInt(tokens[0]);
                String instruction = tokens[1];
                mdtMapping.put(index, instruction);
            }
        } catch (IOException e) {
            System.err.println("Error reading MDT file: " + e.getMessage());
        }
    }

    private static void loadALA(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                String[] tokens = line.split("\\s+");
                if (tokens.length < 4)
                    continue;
                String index = tokens[0];
                String formalArg = tokens[1];
                String actualArg = tokens[2];
                String macroName = tokens[3];
                ALAEntry entry = new ALAEntry(index, formalArg, actualArg, macroName);
                macroALA.computeIfAbsent(macroName, k -> new ArrayList<>()).add(entry);
            }
        } catch (IOException e) {
            System.err.println("Error reading ALA file: " + e.getMessage());
        }
    }

    private static void buildMacroDefinitions() {
        for (String macroName : mntMapping.keySet()) {
            int startIndex = mntMapping.get(macroName);
            String headerLine = mdtMapping.get(startIndex);
            if (headerLine == null) {
                System.err.println("No MDT entry found for macro " + macroName);
                continue;
            }

            List<String> formalParams = new ArrayList<>();
            List<ALAEntry> alaEntries = macroALA.get(macroName);
            if (alaEntries != null) {
                for (ALAEntry entry : alaEntries) {
                    formalParams.add(entry.formalArg);
                }
            }

            List<String> bodyLines = new ArrayList<>();
            int currentIndex = startIndex + 1;
            while (true) {
                String mdtLine = mdtMapping.get(currentIndex);
                if (mdtLine == null)
                    break;
                if (mdtLine.equalsIgnoreCase("MEND"))
                    break;
                bodyLines.add(mdtLine);
                currentIndex++;
            }
            MacroDefinition def = new MacroDefinition(macroName, formalParams, bodyLines);
            macroDefinitions.put(macroName, def);
        }
    }

    private static void processIntermediate(String intermediateFile, String finalOutputFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(intermediateFile));
             PrintWriter writer = new PrintWriter(new FileWriter(finalOutputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty()) {
                    writer.println();
                    continue;
                }

                String[] tokens = trimmedLine.split("\\s+", 2);
                String possibleMacro = tokens[0];
                if (macroDefinitions.containsKey(possibleMacro)) {

                    System.out.printf("Macro Call Encountered: %s", possibleMacro);
                    System.out.println();
                    MacroDefinition def = macroDefinitions.get(possibleMacro);

                    List<String> actualArgs = new ArrayList<>();
                    if (tokens.length == 2) {
                        String argsPart = tokens[1].trim();
                        String[] argsTokens = argsPart.split(",");
                        for (String arg : argsTokens) {
                            arg = arg.trim();
                            if (!arg.isEmpty())
                                actualArgs.add(arg);
                        }
                    }

                    Map<String, String> placeholderMapping = new HashMap<>();
                    List<ALAEntry> alaEntries = macroALA.get(possibleMacro);
                    if (alaEntries != null) {
                        for (int i = 0; i < alaEntries.size(); i++) {

                            String actual = (i < actualArgs.size()) ? actualArgs.get(i) : "";

                            placeholderMapping.put(alaEntries.get(i).index, actual);
                        }
                    }

                    for (String bodyLine : def.bodyLines) {
                        String expandedLine = replacePlaceholders(bodyLine, placeholderMapping);
                        writer.println(expandedLine);
                    }

                } else {
                    writer.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error processing intermediate file: " + e.getMessage());
        }
    }

    private static String replacePlaceholders(String line, Map<String, String> mapping) {
        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            line = line.replace(entry.getKey(), entry.getValue());
        }
        return line;
    }
}

