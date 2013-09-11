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
            System.out.println("type = " + type);

            //Check second ele of instruction
            subjName = tokens[1];
            System.out.println("subjName = " + subjName);

            //Check third ele of instruction
            objName = tokens[2];      
            System.out.println("objName = " + objName);
        }
        else {
            type = "BAD";
            System.out.println("type2 = " + type);
        }

        //If WRITE, Check fourth ele of instruction
        if (type.equals("WRITE")){
            if (tokens.length == 4){
                value = Integer.parseInt(tokens[3]);
                System.out.println("value = " + value);
            }
            else {
                type = "BAD";
                System.out.println("type2 = " + type);
            }
        }
    }

    public static void instrMethod() {
        System.out.println("InstructionObject!\n");
    }
}

class BadInstruction {
    public static void badInstruction() {
        System.out.println("BadInstruction!");
    }
}

class SecurityLevel{
    // static int level;
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

class Lookup {
    // map subjName and objName (from instrObj) 2 objects in the program
    private static Map<String, Subject> sMap = new HashMap<String, Subject>();
    private static Map<String, SecureObject> oMap = new HashMap<String, SecureObject>();

    public static void setSubj(String sSub, Subject sub) {
        sMap.put(sSub, sub);
    }
    public static void getSubj(String s) {
        //gets Subject object associated with name s
        sMap.get(s);
        System.out.println("looked up " + s + " as " + sMap.get(s) + "\n");
    }

    public static void setObj(String sObj, SecureObject obj) {
        oMap.put(sObj, obj);
    }
    public static void getObj(String o) {
        //gets Subject object associated with name s
        oMap.get(o);
        System.out.println("looked up " + o + " as " + oMap.get(o) + "\n");
    }
}

class ReferenceMonitor { 
    private static Map<Subject, Integer> subjectMap = new HashMap<Subject, Integer>();
    private static Map<SecureObject, Integer> objectMap = new HashMap<SecureObject, Integer>();

    public static void updateSubjectRM(Subject s, Integer level) {
        subjectMap.put(s, level);   
    }
    public static void updateObjectRM(SecureObject o, Integer level) {
        objectMap.put(o, level);   
    }

    //BLP
    public static void monitorInstruction(InstructionObject instrObj) {
        if (instrObj.type.equals("READ")){
            //SSP
            //ssp(lookup.getSubj(instrObj.subjName), lookup.getObj(instrObj.objName)) ;
        }
        else if (instrObj.type.equals("WRITE")){
            //*-Property
            //starProperty(instrObj.subjName)
        }
        else{
            System.out.println("BAD instruction send to RM");
        }

    }

    public static void ssp(Subject s, SecureObject o) {
        if (subjectMap.containsKey(s) && objectMap.containsKey(o)){
            //**replace comparison with dominates method that we need to write
            if (s.level >= o.level){
                //allow access

            }
            else {
                //Deny access
                System.out.println("This instruction violates SSP\n");
            }
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
        int low = 0;
        int high = 1;

        ReferenceMonitor rm = new ReferenceMonitor();
        //Lookup lookup = new Lookup();

        //Make subjects known the the secure system
        Subject lyle = new Subject();
        lyle.createSubject("Lyle", low);
        System.out.println(lyle.name + " " + lyle.level + "\n");
        //Hard association set in RM
        rm.updateSubjectRM(lyle, low);
        //Set in lookup
        //rm.lookup.setSubj(lyle.name, lyle);


        Subject hal = new Subject();
        hal.createSubject("Hal", high);
        System.out.println(hal.name + " " + hal.level + "\n");
        rm.updateSubjectRM(hal, high);
        //lookup.setSubj(hal.name, hal);

        //Make objects known to the secure system
        SecureObject lobj = new SecureObject();
        lobj.createNewObject("Lobj", low);
        System.out.println(lobj.name + " " + lobj.level + "\n");
        rm.updateObjectRM(lobj, low);

        SecureObject hobj = new SecureObject();
        hobj.createNewObject("Hobj", high);
        System.out.println(hobj.name + " " + hobj.level + "\n");
        rm.updateObjectRM(hobj, high);


        //Instructions are parsed from the list
        String s;
        while(inFile.hasNext()){
            s = inFile.nextLine();
            //Print line of input
            System.out.println(s);
            InstructionObject instrObj = new InstructionObject();
            instrObj.assignObjElements(s);
            //Print end of instruction object exclaimation
            instrObj.instrMethod();

            rm.monitorInstruction(instrObj);

        }

        System.out.println("\nSecureSystem!\n");

    }
}