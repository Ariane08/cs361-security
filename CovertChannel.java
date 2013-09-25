//Secure System by Olga and Shaelyn
import java.util.*;
import java.io.*;

/*InstructionObject collects data from a single instruction line
and makes it available to the rest of the secure system */
class InstructionObject {
    public static String type;
    public static String subjName;
    public static String objName;
    public static int value; 

    public static void assignObjElements(String s){
        String delim = " ";
        String[] tokens = s.split(delim);

        
        type = tokens[0].toUpperCase();
        if (!type.equals("WRITE") && !type.equals("READ") && !type.equals("CREATE") && !type.equals("DESTROY") && !type.equals("RUN")){
            type = "BAD";
        }
        subjName = tokens[1].toLowerCase();
        if (!type.equals("RUN"))
            objName = tokens[2].toLowerCase();      
        
        //If WRITE, Check fourth ele of instruction
        if (type.equals("WRITE")){
            if (tokens.length == 4){
                value = Integer.parseInt(tokens[3]);
            }
            else {
                type = "BAD";
            }
        }

    }
}

class SecurityLevel{
    static final int LOW = 1;
    static final int HIGH = 2;

    public static boolean dominates(int sLevel, int oLevel){
        if (sLevel >= oLevel){
            return true;
        }
        return false;
    }

    public static boolean writeAccess(int sLevel, int oLevel){
        if (oLevel >= sLevel){
            return true;
        }
        return false;
    }
}

class SecureSubject {
    public String name;
    //temp is the value the Securesubject most recently read
    public int temp;
    public int level;
    String lowSubStr = "";
    String instruction = "";
    BufferedWriter bwCovert;

    public SecureSubject(String inName, int l, BufferedWriter bw1) {
        name = inName;
        //temp is initially zero
        temp = 0;
        level = l;
        bwCovert = bw1;
    }

    public void HGenerateInstr(int parsedInt, ReferenceMonitor rm, BufferedWriter[] bw) throws IOException {
        if (parsedInt == 1){
            InstructionObject instrObj0 = new InstructionObject();
            instruction = "RUN HAL";
            if (bw[0]!=null)
                bw[0].write(instruction.concat("\n"));
            instrObj0.assignObjElements(instruction);
            rm.monitorInstruction(instrObj0);

            InstructionObject instrObj1 = new InstructionObject();
            instruction = "DESTROY HAL OBJ";
            if (bw[0]!=null)
                bw[0].write(instruction.concat("\n"));
            instrObj1.assignObjElements(instruction);
            rm.monitorInstruction(instrObj1);
            //System.out.println("Hal communicated a 1 over CovertChannel");
        }
        else{
            InstructionObject instrObj2 = new InstructionObject();
            instruction = "RUN HAL";
            if (bw[0]!=null)
                bw[0].write(instruction.concat("\n"));
            instrObj2.assignObjElements(instruction);
            rm.monitorInstruction(instrObj2);

            InstructionObject instrObj3 = new InstructionObject();
            instruction = "CREATE HAL OBJ";
            if (bw[0]!=null)
                bw[0].write(instruction.concat("\n"));
            instrObj3.assignObjElements(instruction);
            rm.monitorInstruction(instrObj3);
            //System.out.println("Hal communicated a 0 over CovertChannel");
        }
    }

    public void LGenerateInstr (ReferenceMonitor rm, BufferedWriter[] bw) throws IOException {
        InstructionObject instrObj0 = new InstructionObject();
        instruction = "CREATE LYLE OBJ";
        if (bw[0]!=null)
            bw[0].write(instruction.concat("\n"));
        instrObj0.assignObjElements(instruction);
        rm.monitorInstruction(instrObj0);

        InstructionObject instrObj1 = new InstructionObject();
        instruction = "WRITE LYLE OBJ 1";
        if (bw[0]!=null)
            bw[0].write(instruction.concat("\n"));
        instrObj1.assignObjElements(instruction);
        rm.monitorInstruction(instrObj1);

        InstructionObject instrObj2 = new InstructionObject();
        instruction = "READ LYLE OBJ";
        if (bw[0]!=null)
            bw[0].write(instruction.concat("\n"));
        instrObj2.assignObjElements(instruction);
        rm.monitorInstruction(instrObj2);

        InstructionObject instrObj3 = new InstructionObject();
        instruction = "DESTROY LYLE OBJ";
        if (bw[0]!=null)
            bw[0].write(instruction.concat("\n"));
        instrObj3.assignObjElements(instruction);
        rm.monitorInstruction(instrObj3);

        InstructionObject instrObj4 = new InstructionObject();
        instruction = "RUN LYLE";
        if (bw[0]!=null)
            bw[0].write(instruction.concat("\n"));
        instrObj4.assignObjElements(instruction);
        rm.monitorInstruction(instrObj4);
    }

    public void readBits(int bit) throws IOException{
        lowSubStr = lowSubStr.concat(String.valueOf(bit));
        //System.out.println("lowSubStr = " + lowSubStr);
        if (lowSubStr.length() == 8){
            byte numberByte = (byte) Integer.parseInt(lowSubStr, 2); // mode 2 = binary
            char charWrite = (char)numberByte;
            //System.out.println("***************Char read = " + charWrite);
            lowSubStr = "";
            bwCovert.write(Character.toString(charWrite));
            //System.out.println("Tried to write char to file " + Character.toString(charWrite) + "!!!!!!!!!!!!!!!!!!!");
        }
    }
}

class SecureObject {
    public String name;
    public int currentValue;
    public int level;

    public SecureObject(String inName, int l) {
        //currentValue is initially zero
        currentValue = 0;
        name = inName;
        level = l;
    }
}

class ReferenceMonitor { 
    public static HashMap<String, Integer> rmMap = new HashMap<String, Integer>();
    public static HashMap<String, SecureSubject> subjMap = new HashMap<String, SecureSubject>();
    public static ObjectManager objMan = new ObjectManager();

    //RM map handling
    public static void updateRM(String s, Integer level) {
        rmMap.put(s, level);
    }
    public static Integer getRM(String s) {
        return (Integer)rmMap.get(s);
    }

    //=======================BLP
    public static void monitorInstruction(InstructionObject instrObj) throws IOException {
        if (subjMap.containsKey(instrObj.subjName)){
            if (instrObj.type.equals("READ")){
                //SSP
                executeRead(instrObj.subjName, instrObj.objName) ;
            }
            else if (instrObj.type.equals("WRITE")){
                //*-Property
                starProperty(instrObj.subjName, instrObj.objName, instrObj.value);
            }
            else if (instrObj.type.equals("CREATE")){
                // new objects added with security level equal to the level of creating Securesubject
                if (!objMan.objMap.containsKey(instrObj.objName)){
                    objMan.create(subjMap.get(instrObj.subjName), instrObj.objName);
                }
                //else = no-op
            }
            else if (instrObj.type.equals("DESTROY")){
                // eliminates an object if the Securesubject has right access and object exists
                if (objMan.objMap.containsKey(instrObj.objName) && SecurityLevel.writeAccess(subjMap.get(instrObj.subjName).level, objMan.objMap.get(instrObj.objName).level)){
                    objMan.destroy(subjMap.get(instrObj.subjName), instrObj.objName);
                }
                //else = no-op
            }
            else if (instrObj.type.equals("RUN")){
                //check level of subject to run
                SecureSubject sub = subjMap.get(instrObj.subjName);
                //System.out.println("Subject level in run = " + sub.level);
                if (sub.level == 1){
                    //System.out.println("Lyle read " + sub.temp);
                    sub.readBits(sub.temp);
                }
            }
            else{
                System.out.println("Bad instruction " + instrObj.type + " from " + instrObj.subjName);
            }
        }
        else {
            System.out.println("Bad instruction, unknown Securesubject/object");
        }

    }

    //READ = SSP
    public static void executeRead(String s, String o) {
        if (SecurityLevel.dominates(getRM(s).intValue(), getRM(o).intValue())){
            objMan.read(subjMap.get(s), objMan.objMap.get(o));
        }
        else {
            subjMap.get(s).temp = 0;
            //System.out.println("This instruction violates SSP");
        }
    } 

    //WRITE = *-Property
    public static void starProperty(String s, String o, int v) {
        if (SecurityLevel.writeAccess(getRM(s).intValue(), getRM(o).intValue())){
            objMan.write(objMan.objMap.get(o), v);
        }
        else {
            //System.out.println("This instruction violates *-Property");
        }
    }    

    /* Perform requests of the ReferenceMonitor */
    static class ObjectManager {
        public static HashMap<String, SecureObject> objMap = new HashMap<String, SecureObject>();

        //READ assign SecureSubject.TEMP new value
        public void read(SecureSubject s, SecureObject o){
            s.temp = o.currentValue;
        }

        //WRITE assign SecureObject.currentValue new value
        public void write(SecureObject o, int value){
            o.currentValue = value;
        }

        public void create(SecureSubject s, String o){
            SecureObject so = new SecureObject(o, s.level);
            //Update rmMap to map stringName to int level of Securesubject
            updateRM(o, s.level);
            //update objMap to map stringName to newSecureObject
            objMap.put(o, so);
            //System.out.println(s.name +" created " + o + " with level " + getRM(s.name));
        }
        
        public void destroy(SecureSubject s, String o){
            //update rmMap to not include destroyed object
            rmMap.remove(o);
            //update objMap to not include destroyed object
            objMap.remove(o);
            //System.out.println(s.name +" destroyed " + o);
        }
    }

}

/* Top level class */
class CovertChannel {

    public static Integer currentSubjArrayIndex = 0;
    public static Integer currentObjArrayIndex = 0;
    public static File inFile1;
    public static boolean vPresent = false;
    public static BufferedWriter[] bWarray = new BufferedWriter[1];

    public static void printState(SecureObject lobj, SecureObject hobj, SecureSubject lyle, SecureSubject hal){
        System.out.println("The current state is: ");
        System.out.println("    LObj has value: " + lobj.currentValue);
        System.out.println("    HObj has value: " + hobj.currentValue);
        System.out.println("    Lyle has recently read: " + lyle.temp);
        System.out.println("    Hal has recently read: " + hal.temp);
    }


    public static void main(String[] args) throws IOException{

        if (args[0].equals("v")){
            System.out.println("args[0] = " + args[0]);
            inFile1 = new File(args[1]);
            System.out.println("args[1] = " + args[1]);
            vPresent = true;
            File logFile = new File("log.txt");
            FileWriter fw = new FileWriter(logFile);
            BufferedWriter bw = new BufferedWriter(fw);
            bWarray[0] = bw;
        }
        else{
            inFile1 = new File(args[0]);
            System.out.println("args[0] = " + args[0]);
        }
        String fName = inFile1.getName();
        int pos = fName.lastIndexOf(".");
        if (pos > 0) {
            fName = fName.substring(0, pos);
        }

        int low  = SecurityLevel.LOW;
        int high = SecurityLevel.HIGH;

        ReferenceMonitor rm = new ReferenceMonitor();

        File notCovertFile = new File(fName.concat(".out"));
        System.out.println("Output file name is = " + notCovertFile.getName());
        FileWriter fw1 = new FileWriter(notCovertFile);
        BufferedWriter bw1 = new BufferedWriter(fw1);

        //====Make Securesubjects known the the secure system
        //Lyle
        SecureSubject lyle = new SecureSubject("lyle", low, bw1);
        rm.updateRM("lyle", low);
        rm.subjMap.put("lyle", lyle);
        //Hal
        SecureSubject hal = new SecureSubject("hal", high, bw1);
        rm.updateRM("hal", high);
        rm.subjMap.put("hal", hal);


        FileInputStream fis = new FileInputStream(inFile1);
        Reader isr = new InputStreamReader(fis, "US-ASCII");

        


        String writeString = "";
        int initInt = 0;
        String bitsRead = "";
        int parsedInt = 0;
        int result = 0;

        //while collects individual bytes from the input file
        while ((initInt = isr.read()) != -1) {
            byte b =(byte)initInt;

            if (b != -1){
                //bitsRead = string version of a byte from the input file 
                bitsRead = String.format("%8s", Integer.toBinaryString(b)).replace(' ', '0');
                
                for (int i = 0; i < bitsRead.length(); i++) {
                    parsedInt = Character.getNumericValue(bitsRead.charAt(i));
                    //System.out.println("single parsed int = " + parsedInt);
                    hal.HGenerateInstr(parsedInt, rm, bWarray);
                    lyle.LGenerateInstr(rm, bWarray);

                    //System.out.println("===================");
                }
            }
        }
        if (vPresent){
            bWarray[0].flush();
            bWarray[0].close();
        }
        bw1.flush();
        bw1.close();
        System.out.println("\nCovertChannel!\n");

    }
}