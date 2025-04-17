import java.io.*;
import java.util.*;

public class Lab2 {

 public static void main(String[] args) {
 try {
 // File paths for input files
 String finalOutputFile = "finalOutput.txt";
 String symtabFile = "symtab.txt";

 // Read input files
 List<String> finalOutputLines = readFile(finalOutputFile);
 Map<Integer, Integer> symtab = readSymtab(symtabFile);

 // Process pass 2
 List<String> machineCode = generateMachineCode(finalOutputLines, symtab);

 // Print the output
 System.out.println("Final Machine Code:");
 // System.out.println("LC\tOPCODE\tOP1\tOP2");
 for (String line : machineCode) {
 System.out.println(line);
 }
 } catch (Exception e) {
 e.printStackTrace();
 }
 }

 // Read a file line by line
 private static List<String> readFile(String fileName) throws IOException {
 List<String> lines = new ArrayList<>();
 try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
 String line;
 while ((line = reader.readLine()) != null) {
 lines.add(line.trim());
 }
 }
 return lines;
 }

 // Read symtab file and convert to a map
 private static Map<Integer, Integer> readSymtab(String fileName) throws IOException {
 Map<Integer, Integer> symtab = new HashMap<>();
 try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
 String line;
 while ((line = reader.readLine()) != null) {
 String[] parts = line.split("\\s+");
 if (parts.length == 3) {
 int id = Integer.parseInt(parts[0]);
 int address = Integer.parseInt(parts[2]);
 symtab.put(id, address);
 }
 }
 }
 return symtab;
 }

 // Generate machine code based on the finalOutput and symtab
 private static List<String> generateMachineCode(List<String> finalOutput, Map<Integer, Integer> symtab) {
 List<String> machineCode = new ArrayList<>();

 for (String line : finalOutput) {
 String[] tokens = line.split("\\s+", 4); // Split into up to 4 tokens
 if (tokens.length < 4)
 continue; // Skip malformed lines

 String lc = tokens[0].equals("NULL") ? "" : tokens[0];
 String opcode = tokens[1];
 String op1 = tokens[2].equals("NULL") ? "" : tokens[2];
 String op2 = tokens[3].equals("NULL") ? "" : tokens[3];

 String resolvedOpcode = resolveOpcode(opcode);
 String resolvedOp1 = op1;
 String resolvedOp2 = resolveOperand(op2, symtab);

 // Skip line if opcode is AD-* (Assembler Directive)
 if (opcode.startsWith("AD")) {
 machineCode.add(""); // Print blank line
 continue;
 }

 // Check if opcode is DL-* (Declarative Statement)
 if (opcode.startsWith("DL")) {
 machineCode.add(String.format("%-8s-\t-\t%-8s", lc, resolvedOp2)); //  LC and resolvedOp2 
   continue;
 }

 // Add formatted machine code to the list
 machineCode.add(String.format("%s\t%s\t%s\t%s", lc, resolvedOpcode, resolvedOp1, resolvedOp2));
 }

 return machineCode;
 }

 // Resolve opcode by extracting numeric value (e.g., IS-08 -> 08)
 private static String resolveOpcode(String opcode) {
 if (opcode.contains("-")) {
 return opcode.split("-")[1];
 }
 return opcode;
 }

 // Resolve operand: lookup symtab for symbols or extract constant value
 private static String resolveOperand(String operand, Map<Integer, Integer> symtab) {
 if (operand.startsWith("S-")) {
 int symbolId = Integer.parseInt(operand.split("-")[1]);
 return symtab.getOrDefault(symbolId, 0).toString();
 } else if (operand.startsWith("C-")) {
 return "00"+operand.split("-")[1];
 }
 return operand;
 }
}
