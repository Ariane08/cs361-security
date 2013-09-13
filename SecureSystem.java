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

        if (tokens.length == 3 || tokens.length == 4){
            //Check first ele of instruction
            type = tokens[0].toUpperCase();
            if (!type.equals("WRITE") && !type.equals("READ")){
                type = "BAD";
            }
            //System.out.println("type = " + type);

            //Check second ele of instruction
            subjName = tokens[1];
            //System.out.println("subjName = " + subjName);

            //Check third ele of instruction
            objName = tokens[2];      
            //System.out.println("In assignObjElements! objName = " + objName);
        }
        else {
            type = "BAD";
            System.out.println("type2 = " + type);
        }

        //If WRITE, Check fourth ele of instruction
        if (type.equals("WRITE")){
            if (tokens.length == 4){
                value = Integer.parseInt(tokens[3]);
                System.out.println("WRITE value = " + value);
            }
            else {
                type = "BAD";
                System.out.println("type2 = " + type);
            }
        }
    }

    public static void instrMethod() {
        System.out.println("=====================end of instruction=======\n");
    }
}

class BadInstruction {
    public static void badInstruction() {
        System.out.println("BadInstruction!");
    }
}

class SecurityLevel{
    // static final int level;
    // public final static void LOW(){
    //     level = 0;
    // }
    // public final static void HIGH(){
    //     level = 1;
    // }
    //final static SecurityLevel LOW = 0;
}

class Subject {
    public static String name;
    //temp is the value the subject most recently read
    public static int temp;
    public static int level; // NEED TO CHANGE INT!!!

    public static void createSubject(String inName, int inLevel) {
        //get and set name
        name = inName;
        //temp is initially zero
        temp = 0;
        level = inLevel;
        //updateRM();
    }

    public static void read() {
        //temp is updated to the value of the obj
        //should get this value by communicating with the ObjectManager
        temp = 0;
    }

    public static void write() {
        //subject updates to the value of the obj
        //does this by communicating with the ObjectManager
        temp = 0;
    }

    public static void subjFunction() {
        System.out.println("Referenced a subject!");
    }
}

class SecureObject {
    public static String name;
    public static int currentValue;
    public static int level; // NEED TO CHANGE INT!!! 

    public static void createNewObject(String inName, int inLevel) {
        //currentValue is initially zero
        currentValue = 0;
        name = inName;
        level = inLevel;
    }

    public static void objFunction() {
        System.out.println("Referenced an object!");
    }
}

class ObjectManager {
    // Perform requests of the ReferenceMonitor
    public static void objManFunction() {
        System.out.println("ObjectManager!");
    }
}

class ReferenceMonitor { 
    //public static Map<String, Integer> rmMap = new HashMap<String, Integer>();
    //public static Map<String, Integer> objectMap = new HashMap<String, Integer>();
    public static HashMap<String, Integer> rmMap = new HashMap<String, Integer>();

    //RM map handling
    public static void updateRM(String s, Integer level) {
        rmMap.put(s, level);
        System.out.println("Updated RM with " + s + " " + level + " and rmMap.get(s) = " + rmMap.get(s) + "\n");
    }
    public static Integer getRM(String s) {
        //gets integer level of subject held by the RM
        //rmMap.get(s);
        System.out.println("rmMap.get(" + s + ") = " + (Integer)rmMap.get(s) + "\n");
        return (Integer)rmMap.get(s);
    }

    //BLP
    public static void monitorInstruction(InstructionObject instrObj) {
        if (instrObj.type.equals("READ")){
            //SSP
            System.out.println("called SSP with " + instrObj.subjName + " " + instrObj.objName);
            ssp(instrObj.subjName, instrObj.objName) ;
        }
        else if (instrObj.type.equals("WRITE")){
            //*-Property
            //starProperty(instrObj.subjName)
        }
        else{
            System.out.println("BAD instruction send to RM\n");
        }

    }

    public static void ssp(String s, String o) {
        // System.out.println("allowed subj " + s +  "with level " + getSubjRM(s));
        // System.out.println("to read " + o +  "with level " + getObjRM(o) + "\n" );
        //**replace comparison with dominates method that we need to write
        System.out.println("SSP get subj level as " + getRM(s) + " and object level as " + getRM(o));

        // if (getRM(s).intValue() >= getRM(o).intValue()){
        //     //allow access
        //     System.out.println("allowed subj " + s +  "with level " + getSubjRM(s));
        //     System.out.println("to read " + o +  "with level " + getObjRM(o) + "\n" );
        // }
        // else {
        //     //Deny access
        //     System.out.println("This instruction violates SSP\n");
        // }
        // System.out.println("ssp!");
    } 

    public static void starProperty() {
        System.out.println("starProperty!");
    }    

    public static void refMonFunction() {
        System.out.println("ReferenceMonitor!");
    }

}

//Top level class?
class SecureSystem {

    public static void main(String[] args) throws IOException{
    	Scanner inFile = new Scanner(new FileReader("instructionList.txt"));

        // SecurityLevel low  = SecurityLevel.LOW;
        // SecurityLevel high = SecurityLevel.HIGH;
        int low = 1;
        int high = 2;

        ReferenceMonitor rm = new ReferenceMonitor();

        //Make subjects known the the secure system
        Subject lyle = new Subject();
        lyle.createSubject("Lyle", low);
        //System.out.println("\nCreated subject = " + lyle.name + " " + lyle.level + "\n");
        //Hard association set in RM
        rm.updateRM("Lyle", low);

        Subject hal = new Subject();
        hal.createSubject("Hal", high);
        //System.out.println("Created subject = " + hal.name + " " + hal.level + "\n");
        rm.updateRM("Hal", high);

        //Make objects known to the secure system
        SecureObject lobj = new SecureObject();
        lobj.createNewObject("LObj", low);
        //System.out.println("Created object = " + lobj.name + " " + lobj.level + "\n");
        rm.updateRM("LObj", low);

        SecureObject hobj = new SecureObject();
        hobj.createNewObject("HObj", high);
        //System.out.println("Created object = " + hobj.name + " " + hobj.level + "\n");
        rm.updateRM("HObj", high);


        //Instructions are parsed from the list
        String s;
        while(inFile.hasNext()){
            s = inFile.nextLine();
            //Print line of input
            System.out.println("Instruction line = " + s);
            InstructionObject instrObj = new InstructionObject();
            instrObj.assignObjElements(s);

            rm.monitorInstruction(instrObj);

            //Print end of instruction divider
            instrObj.instrMethod();

        }

        System.out.println("\nSecureSystem!\n");

    }
}