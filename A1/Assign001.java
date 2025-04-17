import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
public class Assign1 {
    static final Map<String, String[]> optab = new HashMap<String, String[]>() {{
        put("DC", new String[]{"01", "DL"});
        put("DS", new String[]{"02", "DL"});
        put("START", new String[]{"01", "AD"});
        put("ORIGIN", new String[]{"02", "AD"});
        put("EQU", new String[]{"03", "AD"});
        put("LTORG", new String[]{"04", "AD"});
        put("END", new String[]{"05", "AD"});
        put("STOP", new String[]{"00", "IS"});
        put("ADD", new String[]{"01", "IS"});
        put("SUB", new String[]{"02", "IS"});
        put("MULT", new String[]{"03", "IS"});
        put("DIV", new String[]{"04", "IS"});
        put("MOVER", new String[]{"05", "IS"});
        put("MOVEM", new String[]{"06", "IS"});
        put("JMP", new String[]{"07", "IS"});
        put("BC", new String[]{"08", "IS"});
        put("READ", new String[]{"09", "IS"});
        put("PRINT", new String[]{"10", "IS"});
    }};
    static final Map<String, String> regtab = new HashMap<>() {{
        put("AREG", "01");
        put("BREG", "02");
        put("CREG", "03");
        put("DREG", "04");
    }};
    static final Map<String, Integer> symtab = new HashMap<>();
    static int locationCounter = 0;
    static List<String> intermediateCode = new ArrayList<>();

    public static void main(String[] args) {
        int sentenceCount = 0;
        Scanner sc = new Scanner(System.in);
        String filep = "file.txt"; // Input file path
        File file = new File(filep);
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                sentenceCount++;
                classifyAndProcess(line, sentenceCount);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filep);
        }

        // Print the symbol table and intermediate code
        System.out.println("\nSymbol Table:");
        symtab.forEach((key, value) -> System.out.println(key + " -> " + value));

        System.out.println("\nIntermediate Code:");
        intermediateCode.forEach(System.out::println);

        sc.close();
    }

    private static void classifyAndProcess(String sentence, int sentenceCount) {
        Scanner sc = new Scanner(sentence);
        String label = null;
        String instr = null;
        String opr1 = null;
        String opr2 = null;
        int tokenIndex = 0;

        while (sc.hasNext()) {
            String token = sc.next();
            tokenIndex++;

            if (tokenIndex == 1 && !optab.containsKey(token)) {
                label = token; // It's a label
                symtab.putIfAbsent(label, locationCounter); // Add label to symbol table
            } else if (optab.containsKey(token)) {
                instr = token; // It's an instruction
                if ("START".equals(instr)) {
                    opr1 = sc.hasNext() ? sc.next() : "0"; // Default to 0 if no operand
                    locationCounter = Integer.parseInt(opr1);
                    intermediateCode.add("(AD,01) (C," + opr1 + ")");
                    sc.close();
                    return;
                }
            } else if (instr != null && optab.get(instr)[1].equals("AD")) {
                opr2 = token;
            } else if (instr != null && optab.get(instr)[1].equals("DL")) {
                opr2 = token;
            } else if (regtab.containsKey(token)) {
                if (opr1 == null) {
                    opr1 = token;
                } else {
                    opr2 = token;
                }
            } else {
                if (opr1 == null) {
                    opr1 = token;
                } else {
                    opr2 = token;
                }
            }
        }


        if (instr != null) {
            String[] instructionDetails = optab.get(instr);
            String code = locationCounter +"  (" + instructionDetails[1] + "," + instructionDetails[0] + ")";
            if (opr1 != null) {
                code += " " + (regtab.containsKey(opr1) ? "(R," + regtab.get(opr1) + ")" : "(S," + addToSymTab(opr1) + ")");
            }
            if (opr2 != null) {
                code += " " + (regtab.containsKey(opr2) ? "(R," + regtab.get(opr2) + ")" : "(S," + addToSymTab(opr2) + ")");
            }
            intermediateCode.add(code);
            locationCounter++; 
        }

        sc.close();
    }

    private static int addToSymTab(String symbol) {
        if (!symtab.containsKey(symbol)) {
            symtab.put(symbol, 0);
        }
        return symtab.keySet().stream().collect(Collectors.toList()).indexOf(symbol) + 1;
    }
}