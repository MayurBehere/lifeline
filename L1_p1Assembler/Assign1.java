import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

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

    static final Map<String, String> regtable = new HashMap<>() {
        {
            put("AREG", "01");
            put("BREG", "02");
            put("CREG", "03");
            put("DREG", "04");
        }
    };

    static final Map<String, Integer> symtab = new LinkedHashMap<>();
    static int symId = 1;
    static int LC = 0;

    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);
        List<String[]> finalOutput = new ArrayList<>();
        int sentenceCount = 0;

        System.out.println("Choose input mode: ");
        System.out.println("1. Enter lines manually");
        System.out.println("2. Read lines from a text file");
        System.out.print("Enter your choice (1 or 2): ");

        int choice = inputScanner.nextInt();
        inputScanner.nextLine();

        if (choice == 1) {
            System.out.println("Enter sentences (type 'exit' to stop):");
            String inp;
            while (!(inp = inputScanner.nextLine()).equalsIgnoreCase("exit")) {
                sentenceCount++;
                processLine(inp, sentenceCount, finalOutput);
            }
        } else if (choice == 2) {
            System.out.print("Enter the file path: ");
            // String filePath = inputScanner.nextLine();
            String filePath="file.txt";
            File file = new File(filePath);

            try (Scanner fileScanner = new Scanner(file)) {
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    sentenceCount++;
                    processLine(line, sentenceCount, finalOutput);
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + filePath);
            }
        } else {
            System.out.println("Invalid choice! Please restart the program.");
        }

        printFinalOutput(finalOutput);
        printSymbolTable();
        inputScanner.close();
    }

    private static void processLine(String sentence, int sentenceCount, List<String[]> finalOutput) {
        System.out.println("\nSentence " + sentenceCount + ":");
        Scanner lineScanner = new Scanner(sentence);

        String label = null;
        String instruction = null;
        String operand1 = null;
        String operand2 = null;

        int tokenIndex = 0;
        while (lineScanner.hasNext()) {
            String token = lineScanner.next();
            tokenIndex++;

            if (tokenIndex == 1 && !optab.containsKey(token)) {
                label = token;
                symtab.put(label, LC);
            } else if (optab.containsKey(token)) {
                instruction = token;
            } else if (regtable.containsKey(token)) {
                operand1 = token;
            } else {
                operand2 = token;
                if (!operand2.matches("\\d+") && !operand2.startsWith("='")) {
                    symtab.putIfAbsent(operand2, -1);
                }
            }
        }

        String[] instructionDetails = optab.getOrDefault(instruction, new String[] { "NULL", "NULL" });
        String opCode = instructionDetails[1] + "-" + instructionDetails[0];
        String resolvedOperand2 = resolveOperand(operand2);

        if (label != null) {
            System.out.println("  Label: " + label);
        }
        if (instruction != null) {
            // String[] instructionDetails = optab.get(instruction);
            System.out.println("  Instruction: " + instruction + " (Opcode: " + instructionDetails[0] + ", Class: "
                    + instructionDetails[1] + ")");
        }
        if (operand1 != null) {
            System.out.println("  Operand1: " + operand1
                    + (regtable.containsKey(operand1) ? " (Register Opcode: " + regtable.get(operand1) + ")" : ""));
        }
        if (operand2 != null) {
            System.out.println("  Operand2: " + operand2
                    + (regtable.containsKey(operand2) ? " (Register Opcode: " + regtable.get(operand2) + ")" : ""));
        }

        finalOutput.add(new String[] {
                instruction != null && instructionDetails[1].equals("AD") ? "NULL" : String.valueOf(LC),
                opCode,
                operand1 != null ? regtable.getOrDefault(operand1, "NULL") : "NULL",
                resolvedOperand2
        });

        if (instruction != null && instruction.equals("START") || instruction.equals("ORIGIN")) {
            LC = Integer.parseInt(operand2);
        } 
        // else if (instruction != null && instruction.equals("ORIGIN")) {
        //     LC = Integer.parseInt(operand2);
        // } 
        else if (instruction != null && instruction.equals("DS")) {
            LC += Integer.parseInt(operand2);
        } else if (instruction != null && !instruction.equals("END") ) {
            LC++;
        }

        // Write final output and symbol table to files
        writeToFile("finalOutput.txt", finalOutput);
        writeSymTabToFile("symtab.txt", symtab);

        lineScanner.close();
    }

    private static String resolveOperand(String operand) {
        if (operand == null)
            return "NULL";
        if (operand.matches("\\d+"))
            return "C-" + operand;
        if (operand.startsWith("='"))
            return "L-" + operand.substring(2, operand.length() - 1);
        if (symtab.containsKey(operand)) {
            if (symtab.get(operand) == -1) {
                symtab.put(operand, LC);
            }
            return "S-" + (new ArrayList<>(symtab.keySet()).indexOf(operand) + 1);
        }
        return "NULL";
    }

    private static void printFinalOutput(List<String[]> finalOutput) {
        System.out.println("\nLC\tOPCODE\tOP1\tOP2");
        for (String[] line : finalOutput) {
            System.out.printf("%s\t%s\t%s\t%s\n", (Object[]) line);
        }
    }

    private static void printSymbolTable() {
        System.out.println("\nSymbol Table:");
        System.out.println("ID\tNAME\tADDR");
        int id = 1;
        for (Map.Entry<String, Integer> entry : symtab.entrySet()) {
            System.out.printf("%d\t%s\t%s\n", id++, entry.getKey(), entry.getValue() == -1 ? "NULL" : entry.getValue());
        }
    }

    private static void writeToFile(String fileName, List<String[]> data) {
        try (PrintWriter writer = new PrintWriter(new File(fileName))) {
            for (String[] line : data) {
                writer.printf("%s\t%s\t%s\t%s\n", (Object[]) line);
            }
            System.out.println("Successfully written final output to " + fileName);
        } catch (FileNotFoundException e) {
            System.out.println("Error writing to file: " + fileName);
        }
    }

    private static void writeSymTabToFile(String fileName, Map<String, Integer> symtab) {
        try (PrintWriter writer = new PrintWriter(new File(fileName))) {
            int id = 1;
            for (Map.Entry<String, Integer> entry : symtab.entrySet()) {
                writer.printf("%d\t%s\t%s\n", id++, entry.getKey(), entry.getValue() == -1 ? "NULL" : entry.getValue());
            }
            System.out.println("Successfully written symbol table to " + fileName);
        } catch (FileNotFoundException e) {
            System.out.println("Error writing to file: " + fileName);
        }
    }
}
