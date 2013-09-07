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

class SecureObject {
    public static void objFunction() {
        System.out.println("Referenced an object!");
    }
}

class ObjectManager {
    public static void objManFunction() {
        System.out.println("ObjectManager!");
    }
}

class SecureSystem {
    public static void main(String[] args) throws IOException{
    	Scanner inFile = new Scanner(new FileReader("instructionList.txt"));


    	InstructionObject instrObj = new InstructionObject();
    	instrObj.getNewInstruction(inFile);

        System.out.println("\nSecureSystem!\n");
    }
}