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
            //known subject of the secure system?
            System.out.println("subjName = " + subjName);

            //Check third ele of instruction
            objName = tokens[2];
            //known object of the secure system?       
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
                //Print object variables
                System.out.println("value = " + value);
            }
            else {
                type = "BAD";
                System.out.println("type2 = " + type);
            }
        }
    }

    public static void instrFunction() {
        System.out.println("InstructionObject!\n");
    }
}

class BadInstruction {
    public static void badInstructionFunction() {
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

    //Handle the read/write requests of the subject
    // 
    public static void refMonFunction() {
        System.out.println("ReferenceMonitor!");
    }
}

class SecureSystem {



    public static void main(String[] args) throws IOException{
    	Scanner inFile = new Scanner(new FileReader("instructionList.txt"));

        // SecurityLevel low  = SecurityLevel.LOW;
        // SecurityLevel high = SecurityLevel.HIGH;

        int low = 0;
        int high = 1;

        Subject lyle = new Subject();
        lyle.createSubject("Lyle", low);
        System.out.println(lyle.name + " " + lyle.level + "\n");

        Subject hal = new Subject();
        hal.createSubject("Hal", high);
        System.out.println(hal.name + " " + hal.level + "\n");

        SecureObject lobj = new SecureObject();
        lobj.createNewObject("Lobj", low);
        System.out.println(lobj.name + " " + lobj.level + "\n");

        SecureObject hobj = new SecureObject();
        hobj.createNewObject("Hobj", high);
        System.out.println(hobj.name + " " + hobj.level + "\n");

        //Instructions are parsed from the list
        String s;
        while(inFile.hasNext()){
            s = inFile.nextLine();
            //Print line of input
            System.out.println(s);
            InstructionObject instrObj = new InstructionObject();
            instrObj.assignObjElements(s);
            //Print end of instruction object exclaimation
            instrObj.instrFunction();

        }

        System.out.println("\nSecureSystem!\n");

    }
}