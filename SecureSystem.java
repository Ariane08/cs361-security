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
            //System.out.println("objName = " + objName);
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
    private static Map<String, Integer> subjectMap = new HashMap<String, Integer>();
    private static Map<String, Integer> objectMap = new HashMap<String, Integer>();

    //RM map handling
    public static void updateSubjRM(String s, Integer level) {
        subjectMap.put(s, level);
        System.out.println("Updated subjRM with " + s + " " + level + "\n");   
    }
    public static Integer getSubjRM(String s) {
        //gets integer level of subject held by the RM
        subjectMap.get(s);
        System.out.println("looked up " + s + " as " + subjectMap.get(s) + "\n");
        return subjectMap.get(s);
    }
    public static void updateObjRM(String o, Integer level) {
        objectMap.put(o, level);   
        System.out.println("Updated RM with " + o + " " + level + "\n");
    }
    public static Integer getObjRM(String o) {
        //gets Subject object associated with name s
        objectMap.get(o);
        System.out.println("looked up " + o + " as " + objectMap.get(o) + "\n");
        return objectMap.get(o);
    }

    //BLP
    public static void monitorInstruction(InstructionObject instrObj) {
        if (instrObj.type.equals("READ")){
            //SSP
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
        if (getSubjRM(s) >= getObjRM(o)){
            //allow access
            System.out.println("allowed subj " + s +  "with level " + getSubjRM(s));
            System.out.println("to read " + o +  "with level " + getObjRM(o) + "\n" );
        }
        else {
            //Deny access
            System.out.println("This instruction violates SSP\n");
        }
        System.out.println("ssp!");
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
        rm.updateSubjRM("Lyle", low);

        Subject hal = new Subject();
        hal.createSubject("Hal", high);
        //System.out.println("Created subject = " + hal.name + " " + hal.level + "\n");
        rm.updateSubjRM("Hal", high);

        //Make objects known to the secure system
        SecureObject lobj = new SecureObject();
        lobj.createNewObject("Lobj", low);
        //System.out.println("Created object = " + lobj.name + " " + lobj.level + "\n");
        rm.updateObjRM("Lobj", low);

        SecureObject hobj = new SecureObject();
        hobj.createNewObject("Hobj", high);
        //System.out.println("Created object = " + hobj.name + " " + hobj.level + "\n");
        rm.updateObjRM("Hobj", high);


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