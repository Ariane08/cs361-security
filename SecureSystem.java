//Secure System
import java.util.*;
import java.io.*;


class InstructionObject {
    public static String type;
    public static String subjName;
    public static String objName;
    public static int value; 

    public static void parseInput(Scanner in) throws IOException{
        String s;
        while(in.hasNext()){
            s = in.nextLine();
            //Print lines of input list
            System.out.println(s);
            String delim = " ";
            String[] tokens = s.split(delim);
            //Print tokens of parsed string
            System.out.println(Arrays.toString(tokens));
            type = tokens[0];
            subjName = tokens[1];
            objName = tokens[2];
            // if (type.toLowercase() == "write"){
            //     value = tokens[3];
            // }
        }

    }
    public static void instrFunction() {
        System.out.println("InstructionObject!");
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
    	instrObj.parseInput(inFile);
    	// SecureObject obj = new SecureObject();
    	// obj.objFunction();
    	// ObjectManager mObj = new ObjectManager();
    	// mObj.objManFunction();
        System.out.println("SecureSystem!");
    }
}