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
        System.out.println("InstructionObject type is received as = " + type);
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
        System.out.println("InstructionObject type is output as = " + type);

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


    public SecureSubject(String inName, int l) {
        name = inName;
        //temp is initially zero
        temp = 0;
        level = l;
    }

    public void HGenerateInstr(int parsedInt, ReferenceMonitor rm){
        if (parsedInt == 1){
            InstructionObject instrObj0 = new InstructionObject();
            instrObj0.assignObjElements("RUN HAL");
            rm.monitorInstruction(instrObj0);

            InstructionObject instrObj1 = new InstructionObject();
            instrObj1.assignObjElements("DESTROY HAL OBJ");
            rm.monitorInstruction(instrObj1);
            System.out.println("Hal communicated a 1 over CovertChannel");
        }
        else{
            InstructionObject instrObj2 = new InstructionObject();
            instrObj2.assignObjElements("RUN HAL");
            rm.monitorInstruction(instrObj2);

            InstructionObject instrObj3 = new InstructionObject();
            instrObj3.assignObjElements("CREATE HAL OBJ");
            rm.monitorInstruction(instrObj3);
            System.out.println("Hal communicated a 0 over CovertChannel");
        }
    }

    public void LGenerateInstr(ReferenceMonitor rm){
        InstructionObject instrObj0 = new InstructionObject();
        instrObj0.assignObjElements("CREATE LYLE OBJ");
        rm.monitorInstruction(instrObj0);

        InstructionObject instrObj1 = new InstructionObject();
        instrObj1.assignObjElements("WRITE LYLE OBJ 1");
        rm.monitorInstruction(instrObj1);

        InstructionObject instrObj2 = new InstructionObject();
        instrObj2.assignObjElements("READ LYLE OBJ");
        rm.monitorInstruction(instrObj2);

        InstructionObject instrObj3 = new InstructionObject();
        instrObj3.assignObjElements("DESTROY LYLE OBJ");
        rm.monitorInstruction(instrObj3);

        InstructionObject instrObj4 = new InstructionObject();
        instrObj4.assignObjElements("RUN LYLE");
        rm.monitorInstruction(instrObj4);
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
        //System.out.println("RM set " + s + " to level " + level);
    }
    public static Integer getRM(String s) {
        return (Integer)rmMap.get(s);
    }

    //=======================BLP
    public static void monitorInstruction(InstructionObject instrObj) {
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
                //System.out.println("Curren state of objMap in DESTROY " + objMan.objMap.entrySet() + "\n");
                //else = no-op
            }
            else if (instrObj.type.equals("RUN")){
                //check level of subject to run
                SecureSubject sub = subjMap.get(instrObj.subjName);
                System.out.println("Subject level in run = " + sub.level);

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
        //System.out.println("SSP get subj level as " + getRM(s) + " and object level as " + getRM(o));

        if (SecurityLevel.dominates(getRM(s).intValue(), getRM(o).intValue())){
            //System.out.println("SSP allowed subj " + s +  " with level " + getRM(s) + " to read " + o +  " with level " + getRM(o) + "\n");
            objMan.read(subjMap.get(s), objMan.objMap.get(o));

        }
        else {
            subjMap.get(s).temp = 0;
            System.out.println("This instruction violates SSP");
        }
    } 

    //WRITE = *-Property
    public static void starProperty(String s, String o, int v) {
        //System.out.println("*-Property get subj level as " + getRM(s) + " and object level as " + getRM(o));

        if (SecurityLevel.writeAccess(getRM(s).intValue(), getRM(o).intValue())){
            //System.out.println("*-Property allowed subj " + s +  " with level " + getRM(s) + " to write to " + o +  " with level " + getRM(o) + "\n");
            objMan.write(objMan.objMap.get(o), v);
        }
        else {
            System.out.println("This instruction violates *-Property");
        }
    }    

    /* Perform requests of the ReferenceMonitor */
    static class ObjectManager {
        public static HashMap<String, SecureObject> objMap = new HashMap<String, SecureObject>();

        //READ assign SecureSubject.TEMP new value
        public void read(SecureSubject s, SecureObject o){
            s.temp = o.currentValue;
            //System.out.println(s.name +" read " + o.name + " as " + o.currentValue);
        }

        //WRITE assign SecureObject.currentValue new value
        public void write(SecureObject o, int value){
            o.currentValue = value;
            //System.out.println(o.name +" was written as " + value);
        }

        public void create(SecureSubject s, String o){
            SecureObject so = new SecureObject(o, s.level);
            //Update rmMap to map stringName to int level of Securesubject
            updateRM(o, s.level);
            //update objMap to map stringName to newSecureObject
            objMap.put(o, so);
            System.out.println(s.name +" created " + o + " with level " + getRM(s.name));
            System.out.println("Curren state of objMap " + objMap.entrySet() + "\n");
        }
        
        public void destroy(SecureSubject s, String o){
            //update rmMap to not include destroyed object
            //System.out.println("Initial state of objMap before DESTROY of " + o + " = " + objMap.entrySet() + "\n");
            rmMap.remove(o);
            //update objMap to not include destroyed object
            objMap.remove(o);
            //System.out.println("Curren state of objMap after DESTROY of " + o + " = " + objMap.entrySet() + "\n");

            System.out.println(s.name +" destroyed " + o);
        }
    }

}

/* Top level class */
class CovertChannel {

    public static Integer currentSubjArrayIndex = 0;
    public static Integer currentObjArrayIndex = 0;

    public static void printState(SecureObject lobj, SecureObject hobj, SecureSubject lyle, SecureSubject hal){
        System.out.println("The current state is: ");
        System.out.println("    LObj has value: " + lobj.currentValue);
        System.out.println("    HObj has value: " + hobj.currentValue);
        System.out.println("    Lyle has recently read: " + lyle.temp);
        System.out.println("    Hal has recently read: " + hal.temp);
    }


    public static void main(String[] args) throws IOException{
        File inFile1 = new File("instructionList.txt");

        //if args[0] == "v"
            //then args[1] is a filename
        //else is a file name



        int low  = SecurityLevel.LOW;
        int high = SecurityLevel.HIGH;

        ReferenceMonitor rm = new ReferenceMonitor();


        //====Make Securesubjects known the the secure system
        //Lyle
        SecureSubject lyle = new SecureSubject("lyle", low);
        rm.updateRM("lyle", low);
        rm.subjMap.put("lyle", lyle);
        //Hal
        SecureSubject hal = new SecureSubject("hal", high);
        rm.updateRM("hal", high);
        rm.subjMap.put("hal", hal);

        //====****************************Make objects known to the secure system
        //LObj
        SecureObject lobj = new SecureObject("lobj", low);
        rm.updateRM("lobj", low);
        rm.objMan.objMap.put("lobj", lobj);
        //HObj
        SecureObject hobj = new SecureObject("hobj", high);
        rm.updateRM("hobj", high);
        rm.objMan.objMap.put("hobj", hobj);


        FileInputStream fis = new FileInputStream(inFile1);
        Reader isr = new InputStreamReader(fis, "US-ASCII");
        int fileSize = (int)inFile1.length();
        int[] bitsToByte = new int[8];
        String writeString = "";
        int initInt = 0;
        String bitsRead = "";
        int parsedInt = 0;
        int result = 0;
        char charRead = 'a';
        char charWrite = 'a';
        //while collects individual bytes from the input file
        while ((initInt = isr.read()) != -1) {
            System.out.println("\n New byte read from file_________");
            byte b =(byte)initInt;
            charRead = (char)(initInt & 0xFF);
            //System.out.print("initial char read from file = " + charRead + "\n");
            //System.out.println("init char read as int = " + b);
            if (b != -1){
                //bitsRead = string version of a byte from the input file 
                bitsRead = String.format("%8s", Integer.toBinaryString(b)).replace(' ', '0');
                System.out.println("bits of byte read = " + bitsRead);
                //***Parse the string of bits from the byte
                //***Need to do this before communicating to LowSubject
                for (int i = 0; i < bitsRead.length(); i++) {
                    parsedInt = Character.getNumericValue(bitsRead.charAt(i));

                    // sending bit by Hal kickoff
                    hal.HGenerateInstr(parsedInt, rm);
                    // call generator of instruction for Hal
                    // call generator of instruction for Lyle (always the same)

                    System.out.println("single parsed int = " + parsedInt + "\n====================");
                    bitsToByte[i] = parsedInt;
                }
    
                byte numberByte = (byte) Integer.parseInt(bitsRead, 2); // mode 2 = binary
                charWrite = (char)numberByte;
                //System.out.print("byte coverted back to char after processing = " + charWrite + "\n");
                //*System.out.println("numberByte should  equal num as int = " + numberByte);
            }
        }

        System.out.println("\nSecureSystem!\n");

    }
}