//Secure Sytem
import java.util.*;
import java.io.*;


class InstructionObject {

    public static void parseInput(Scanner in) throws IOException{
        if (in.hasNext())
            System.out.println("yay");
        String s;
        while(in.hasNext()){
            s = in.nextLine();
            System.out.println(s);
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

        // if (inFile.hasNext())
        //     System.out.println("yay");
    	InstructionObject instrObj = new InstructionObject();
    	instrObj.parseInput(inFile);
    	// SecureObject obj = new SecureObject();
    	// obj.objFunction();
    	// ObjectManager mObj = new ObjectManager();
    	// mObj.objManFunction();
        System.out.println("SecureSystem!");
    }
}