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
            type = tokens[0].toUpperCase();
            if (!type.equals("WRITE") && !type.equals("READ")){
                type = "BAD";
            }
            subjName = tokens[1].toLowerCase();
            objName = tokens[2].toLowerCase();      
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

class Subject {
    public static String name;
    //temp is the value the subject most recently read
    public static int temp;
    public static int level;

    public static void createSubject(String inName, int inLevel) {
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
}

class ReferenceMonitor { 
    public static HashMap<String, Integer> rmMap = new HashMap<String, Integer>();

    //RM map handling
    public static void updateRM(String s, Integer level) {
        rmMap.put(s, level);
        System.out.println("Updated RM with " + s + " " + level + " and rmMap.get(s) = " + rmMap.get(s));
    }
    public static Integer getRM(String s) {
        //gets integer level of subject held by the RM
        //System.out.println("rmMap.get(" + s + ") = " + (Integer)rmMap.get(s));
        return (Integer)rmMap.get(s);
    }

    //=======================BLP
    public static void monitorInstruction(InstructionObject instrObj) {
        if (instrObj.type.equals("READ")){
            //SSP
            System.out.println("called SSP with " + instrObj.subjName + " " + instrObj.objName);
            ssp(instrObj.subjName, instrObj.objName) ;
        }
        else if (instrObj.type.equals("WRITE")){
            //*-Property
            starProperty(instrObj.subjName, instrObj.objName);
        }
        else{
            System.out.println("BAD instruction sent to RM!!!\n");
        }

    }

    //READ = SSP
    public static void ssp(String s, String o) {
        System.out.println("SSP get subj level as " + getRM(s) + " and object level as " + getRM(o));

        if (SecurityLevel.dominates(getRM(s).intValue(), getRM(o).intValue())){
            //allow access
            System.out.println("\nSSP allowed subj " + s +  " with level " + getRM(s) + " to read " + o +  " with level " + getRM(o) + "\n");
            //Tell ObectManager what to do
            //ObjectManager.read(s, o);
        }
        else {
            System.out.println("This instruction violates SSP");
        }
    } 

    //WRITE = *-Property
    public static void starProperty(String s, String o) {
        System.out.println("SSP get subj level as " + getRM(s) + " and object level as " + getRM(o));

        if (SecurityLevel.writeAccess(getRM(s).intValue(), getRM(o).intValue())){
            //allow access
            System.out.println("\n*-Property allowed subj " + s +  " with level " + getRM(s) + " to write to " + o +  " with level " + getRM(o) + "\n");
        }
        else {
            System.out.println("This instruction violates *-Property");
        }
    }    


    class ObjectManager {
        // Perform requests of the ReferenceMonitor

        //READ assign Subject.TEMP new value
        // public void read(String s, String o){
        //     s.TEMP = o.currentValue;
        // }

        //WRITE assign SecureObject.currentValue new value

        public void objManFunction() {
            System.out.println("ObjectManager!");
        }
    }

}

//Top level class
class SecureSystem {

    public static void main(String[] args) throws IOException{
    	Scanner inFile = new Scanner(new FileReader("instructionList.txt"));

        int low  = SecurityLevel.LOW;
        int high = SecurityLevel.HIGH;

        ReferenceMonitor rm = new ReferenceMonitor();

        //Make subjects known the the secure system
        Subject lyle = new Subject();
        lyle.createSubject("lyle", low);
        //System.out.println("\nCreated subject = " + lyle.name + " " + lyle.level + "\n");
        rm.updateRM("lyle", low);

        Subject hal = new Subject();
        hal.createSubject("hal", high);
        //System.out.println("Created subject = " + hal.name + " " + hal.level + "\n");
        rm.updateRM("hal", high);

        //Make objects known to the secure system
        SecureObject lobj = new SecureObject();
        lobj.createNewObject("lobj", low);
        //System.out.println("Created object = " + lobj.name + " " + lobj.level + "\n");
        rm.updateRM("lobj", low);

        SecureObject hobj = new SecureObject();
        hobj.createNewObject("hobj", high);
        //System.out.println("Created object = " + hobj.name + " " + hobj.level + "\n");
        rm.updateRM("hobj", high);


        //Instructions are parsed from the list
        String s;
        while(inFile.hasNext()){
            s = inFile.nextLine();
            //Print line of input
            System.out.println("\nInstruction line = " + s);
            InstructionObject instrObj = new InstructionObject();
            instrObj.assignObjElements(s);

            //if (type = BAD) 
            //   BadInsruction bio = new BadInstruction();
            //    bio.set(instruObj)

            //if (type != BAD)
            rm.monitorInstruction(instrObj);

            //Print end of instruction divider
            instrObj.instrMethod();

        }

        System.out.println("\nSecureSystem!\n");

    }
}