import java.io.*;
import java.util.*;

public class macrop1 {

    static class MNTEntry {
        int mntIndex;
        String macroName;
        int mdtIndex;

        public MNTEntry(int mntIndex, String macroName, int mdtIndex) {
            this.mntIndex = mntIndex;
            this.macroName = macroName;
            this.mdtIndex = mdtIndex;
        }

        public String toString() {
            return String.format("%-10d %-10s %-10d", mntIndex, macroName, mdtIndex);
        }
    }

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

    static class MDTEntry {
        int mdtIndex;
        String instruction;

        public MDTEntry(int mdtIndex, String instruction) {
            this.mdtIndex = mdtIndex;
            this.instruction = instruction;
        }

        @Override
        public String toString() {
            return String.format("%-10d %s", mdtIndex, instruction);
        }
    }

    static List<MNTEntry> mntTable = new ArrayList<>();
    static List<ALAEntry> alaTable = new ArrayList<>();
    static List<MDTEntry> mdtTable = new ArrayList<>();

    static int mntIndexCounter = 1;
    static int mdtIndexCounter = 1;
    static Map<String, String> currentALA = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the full path of the input file: ");
        String filePath = scanner.nextLine();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;
            boolean inMacroDefinition = false;
            boolean headerProcessed = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (line.equalsIgnoreCase("MACRO")) {
                    inMacroDefinition = true;
                    headerProcessed = false;
                    currentALA.clear();
                    continue;
                }

                if (inMacroDefinition) {
                    if (!headerProcessed) {
                        String[] parts = line.split("\\s+");
                        String macroName = parts[0];
                        int startingMDTIndex = mdtIndexCounter;
                        mntTable.add(new MNTEntry(mntIndexCounter++, macroName, startingMDTIndex));

                        if (parts.length > 1) {
                            String params = line.substring(macroName.length()).trim();
                            String[] formalArgs = params.split(",");
                            int argCount = 1;
                            for (String arg : formalArgs) {
                                arg = arg.trim();
                                if (!arg.isEmpty()) {
                                    String alaIndex = "#" + argCount;
                                    currentALA.put(arg, alaIndex);
                                    alaTable.add(new ALAEntry(alaIndex, arg, null, macroName));
                                    argCount++;
                                }
                            }
                        }

                        mdtTable.add(new MDTEntry(mdtIndexCounter++, line));
                        headerProcessed = true;
                    } else {
                        if (line.equalsIgnoreCase("MEND")) {
                            mdtTable.add(new MDTEntry(mdtIndexCounter++, line));
                            inMacroDefinition = false;
                        } else {
                            String processedLine = replaceFormals(line, currentALA);
                            mdtTable.add(new MDTEntry(mdtIndexCounter++, processedLine));
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
            return;
        }

        printMNTTable();
        printALA();
        printMDT();
    }

    private static String replaceFormals(String line, Map<String, String> alaMapping) {
        for (Map.Entry<String, String> entry : alaMapping.entrySet()) {
            String formalArg = entry.getKey();
            String alaIndex = entry.getValue();
            System.out.println(alaIndex+formalArg);
            line = line.replaceAll(formalArg, alaIndex);
        }
       
        System.out.println("The line is replaced which is "+line);
        return line;
    }

    private static void printMNTTable() {
        System.out.println(String.format("%-10s %-10s %-10s", "MNT Index", "Name", "MDT Index"));
        for (MNTEntry entry : mntTable) {
            System.out.println(entry);
        }
    }

    private static void printALA() {
        System.out.println(String.format("%-10s %-15s %-15s %-10s", "Index", "Formal Arg", "Actual Arg", "Macro Name"));
        for (ALAEntry entry : alaTable) {
            System.out.println(entry);
        }
    }

    private static void printMDT() {
        System.out.println(String.format("%-10s %s", "MDT Index", "Instruction"));
        for (MDTEntry entry : mdtTable) {
            System.out.println(entry);
        }
    }
}
// IN OUTPUT I WANT MDT MNT ALA AND THE CODE FROM START TO END WITHOUT MACRO
