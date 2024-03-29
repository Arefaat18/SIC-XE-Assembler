package assembler;

import static assembler.Operation.labels;
import static assembler.Operation.symbolTable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class Operation {

    public Operation(){}

    String name, opcode, format;
    static HashMap<String,Operation> Optable = new HashMap<>();
    static LinkedHashMap<String, String> symbolTable = new LinkedHashMap<String, String>();
    static ArrayList<Instruction> array = new ArrayList<Instruction>();
    static ArrayList<String> labels = new ArrayList<String>();
    static ArrayList<String> directives = new ArrayList<String>();
    String registers = "ABXFSTL, ";
    String hexaCharacters = "0123456789ABCDEF";
    String[] regOperations = {"ADDR","COMPR","DIVR","MULR","RMO","SUBR","TIXR"};
    static ArrayList<Instruction> programListing = new ArrayList<Instruction>();
    String[] errors = { "","misplaced label","missing or misplaced operation mnemonic","missing or misplaced operand field","duplicate label definition","this statement can’t have a label","this statement can’t have an operand","wrong g operation prefix","unrecognized operation code","undefined symbol in operand","not a hexadecimal string","can’t be format 4 instruction","illegal address for a register","missing END statement"};

    public Operation(String name, String opcode, String format) throws FileNotFoundException {
        this.name = name;
        this.opcode = opcode;
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpcode() {
        return opcode;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void readSrcFile() throws FileNotFoundException {

        File file = new File("srcfile.txt");
        Scanner sc = new Scanner(file);
        String line = new String();
        while(sc.hasNextLine())
        {
            line=sc.nextLine();
            if(line.startsWith(".")){
                continue;
            }
            String label = line.substring(0,8);
            if(!label.isEmpty())
                labels.add(label);
            String operation;
            String operand;
            if(line.length()>=15){
                operation = line.substring(9,15);
                operand = line.substring(17);
            }
            else
            {
                operation = line.substring(9);
                operand = "";
            }
            
            Instruction instruction = new Instruction(label,operation,operand);
            array.add(instruction);
        }

            
        }
        

    

    public void readOpFile() throws FileNotFoundException {
        File file = new File("Operations.txt");
        Scanner sc = new Scanner(file);
        while(sc.hasNext())
        {
            String opName = sc.next();
            String opCode = sc.next();
            String format = sc.next();
            String newOpCode = convertToBin(opCode);
            Operation operation = new Operation(opName.toUpperCase(),newOpCode,format);
            Optable.put(opName,operation);
        }
        
        file = new File("Directives.txt");
        sc = new Scanner(file);
        while(sc.hasNext()){
            directives.add(sc.next());
        }
    }

    public String convertToBin(String hexa){
        int x = Integer.parseInt(hexa,16);
        String binary = Integer.toBinaryString(x);
        while (binary.length()<8){
            binary="0"+binary;
        }
        return binary;
    }

    public void Pass1() throws FileNotFoundException, UnsupportedEncodingException{
        String hexaAddress = array.get(0).getOperand();
        int address = Integer.parseInt(hexaAddress,16);
        PrintWriter writer = new PrintWriter("LISTFILE.txt", "UTF-8");
        
        writer.println("Program Listing");
        writer.println("");
        boolean format4 = false;
        boolean notRegister=false;
        String r1;
        String r2;
        String comma;
        String temp;
        String[] lbls = new String[labels.size()];
       for(int f=0; f<labels.size();f++)
                {
                    lbls[f]=labels.get(f).trim();
                }
      
        for(int i=0;i<array.size();i++){
            Instruction instruction = array.get(i);
            String operation = instruction.getOperation().trim().toUpperCase();
            instruction.address = Integer.toHexString(address);
            programListing.add(instruction);
            format4 = false;
            temp=instruction.operand.trim();
            
            
   
            writer.println(programListing.get(i).getAddress().toUpperCase()+ "\t" +
                               programListing.get(i).getLabel()+ "\t" +
                               programListing.get(i).getOperation()+ "\t" +
                               programListing.get(i).getOperand());
        
            for(int j=0;j<regOperations.length;j++){
                if(operation.trim().equals(regOperations[j]))
                {
                    if(!operation.trim().equals("TIXR")){
                    temp = instruction.operand.trim();
                        r1=temp.substring(0,1);
                        comma=temp.substring(1,2);
                        r2=temp.substring(2);
                         if(!registers.contains(r1) || !registers.contains(comma) || !registers.contains(r2) )
                             writer.println("ERROR: " + errors[12]);
                    }
                    else
                    {
                        temp = instruction.operand.trim();
                        r1=temp.substring(0,1);
                        if(!registers.contains(r1))
                             writer.println("ERROR: " + errors[12]);
                    }
                }
            }
            
            if(instruction.operand.startsWith("#") || instruction.operand.startsWith("@") ||instruction.operand.startsWith("*"))
            {   
                temp = instruction.operand.substring(1);
            }
            if(!isNumeric(temp) && !Arrays.asList(regOperations).contains(instruction.operation.trim()) && !instruction.operation.trim().equals("CLEAR") && !instruction.operation.trim().equals("BYTE") && !instruction.operation.trim().equals("WORD")){
                if(instruction.operation.trim().equals("LDCH") || instruction.operation.trim().equals("STCH"))
                {
                    if(temp.trim().contains(","))
                        temp=temp.substring( 0 , temp.indexOf(","));
                }
                if(!Arrays.asList(lbls).contains(temp))
                    writer.println("ERROR: " + errors[9]);
                }
            
            if(operation.startsWith("+"))
            {
                operation = operation.substring(1);
                format4=true;
                Operation x = Optable.get(operation.toUpperCase().trim());
                if(!x.format.equals("3"))
                    writer.println("ERROR: " + errors[11]);
                
            }
            if(!instruction.label.trim().equals("") && instruction.label.startsWith(" "))
            {
                writer.println("ERROR: " + errors[1]);
            }
            if(instruction.operation.startsWith(" "))
                writer.println("ERROR: " + errors[2]);
            if(!instruction.operation.trim().equals("RSUB") )
                if(instruction.operand.startsWith(" ") || instruction.operand.trim().isEmpty())
                    writer.println("ERROR: " + errors[3]);
             
            if(!instruction.getLabel().trim().equals("")){
                if(symbolTable.containsKey(instruction.label))
                    writer.println("ERROR: " + errors[4]);
                if(instruction.operation.trim().toUpperCase().equals("END") || instruction.operation.trim().toUpperCase().equals("ORG"))
                    writer.println("ERROR: " + errors[5]);
                symbolTable.put(instruction.label,instruction.address);
                
            }
          
            if(instruction.operation.toUpperCase().trim().equals("RSUB") && !instruction.operand.trim().isEmpty()){
                
                writer.println("ERROR: " + errors[6]);
                
                
            }
            
            
       
         
            if(Optable.get(operation.toUpperCase().trim())!=null ){
               Operation x = Optable.get(operation.toUpperCase().trim());
               if(x.format.equals("1")){
                 instruction.address = Integer.toHexString(address);
                   address +=1;  
               }
               else if(x.format.equals("2")){
                   instruction.address = Integer.toHexString(address);
                   address +=2;
                   if(format4 == true)
                       writer.println("ERROR: " + errors[7]);
               }
               else if(x.format.equals("3")){
                   if(format4 == false){
                   instruction.address = Integer.toHexString(address);
                   address +=3;
                   }
                   else{
                       instruction.address = Integer.toHexString(address);
                   address +=4;
                   }
               }
              
               
            }
            else if(directives.contains(operation.toUpperCase().trim())){
                if(operation.trim().equals("RESW"))
                {
                    int value = Integer.parseInt(instruction.operand);
                    value *= 3;
                    instruction.address = Integer.toHexString(address);
                    address += value;
                    
                }
                else if(operation.trim().equals("RESB"))
                {
                    int value = Integer.parseInt(instruction.operand);
                    instruction.address = Integer.toHexString(address);
                    address += value;
                    
                }
                else if(operation.trim().equals("WORD"))
                {
                    int value = 3;
                    instruction.address = Integer.toHexString(address);
                    address += value;
                    
                }
                else if(operation.trim().equals("BYTE"))
                {
                    if(instruction.operand.startsWith("X"))
                    {
                    int value = (instruction.operand.length() -3)/2;
                    instruction.address = Integer.toHexString(address);
                    address += value;
                    temp=instruction.operand.substring(2,instruction.operand.length()-1);
                    if(!isHexa(temp))
                        writer.println("ERROR: " + errors[10]);
                   
                    }
                    if(instruction.operand.startsWith("C"))
                    {
                    int value = (instruction.operand.length() -3);
                    instruction.address = Integer.toHexString(address);
                    address += value;
                   
                    }
                }
                else{
                    instruction.address = Integer.toHexString(address);
                }
                
            }
            
            else{
               writer.println("ERROR: " + errors[8]);
            }
    
        }
        boolean hasEnd=false;
        for(int i=0;i<array.size();i++){
            
            if(array.get(i).operation.contains("END"))
                hasEnd=true;
        }
        if(hasEnd == false)
           writer.println("ERROR: " + errors[13]); 

        writer.println("");
        writer.println("Symbol Table");
        writer.println("");
        Set<Entry<String,String>> hashSet=Operation.symbolTable.entrySet();
        for(Entry entry:hashSet ) {

            writer.println(entry.getKey()+"\t"+entry.getValue().toString().toUpperCase());
        }
        writer.println("");
                writer.close();

    }
private static boolean isHexa(String number) {
    if ( number.length() == 0 || 
         (number.charAt(0) != '-' && Character.digit(number.charAt(0), 16) == -1))
        return false;
    if ( number.length() == 1 && number.charAt(0) == '-' )
        return false;

    for ( int i = 1 ; i < number.length() ; i++ )
        if ( Character.digit(number.charAt(i), 16) == -1 )
            return false;
    return true;
}
public static boolean isNumeric(String strNum) {
    try {
        double d = Double.parseDouble(strNum);
    } catch (NumberFormatException | NullPointerException nfe) {
        return false;
    }
    return true;
}

}

