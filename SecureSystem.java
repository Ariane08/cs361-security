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
        //Print tokens of parsed string
        //System.out.println(Arrays.toString(tokens));

        type = tokens[0];
        type = type.toLowerCase();
        subjName = tokens[1];
        objName = tokens[2];
        //Print object variables
        System.out.println("type = " + type);
        System.out.println("subjName = " + subjName);
        System.out.println("objName = " + objName);
        
        if (type.equals("write")){
            value = Integer.parseInt(tokens[3]);
            //Print object variables
            System.out.println("value = " + value);
        }

    }

    public static void getNewInstruction(Scanner in) throws IOException{
        String s;
        while(in.hasNext()){
            s = in.nextLine();
            //Print line of input
            System.out.println(s);

            assignObjElements(s);

            //Print end of instruction object exclaimation
            instrFunction();

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

class Subject {
    public static String name;
    //temp is the value the subject most recently read
    public static int temp; 

    public static void makeNewSubject() {
        //get and set name
        name = "harry";
        //temp is initially zero
        temp = 0;
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

    public static void makeNewObject() {
        //currentValue is initially zero
        currentValue = 0;
    }

    public static void objFunction() {
        System.out.println("Referenced an object!");
    }
}

class ObjectManager {

    //Handle the read/write requests of the subject

    public static void objManFunction() {
        System.out.println("ObjectManager!");
    }
}

class ReferenceMonitor {
    public static void refMonFunction() {
        System.out.println("ReferenceMonitor!");
    }
}

class SecureSystem {
    public static void main(String[] args) throws IOException{
    	Scanner inFile = new Scanner(new FileReader("instructionList.txt"));

        //Instructions are parsed from the list
    	InstructionObject instrObj = new InstructionObject();
    	instrObj.getNewInstruction(inFile);

        System.out.println("\nSecureSystem!\n");
    }
}