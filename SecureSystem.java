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
        }
        //If WRITE, Check fourth ele of instruction
        if (type.equals("WRITE")){
            if (tokens.length == 4){
                value = Integer.parseInt(tokens[3]);
            }
            else {
                type = "BAD";
            }
        }
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
    public String name;
    //temp is the value the subject most recently read
    public int temp;


    public Subject(String inName) {
        name = inName;
        //temp is initially zero
        temp = 0;
    }
}

class SecureObject {
    public String name;
    public int currentValue;

    public SecureObject(String inName) {
        //currentValue is initially zero
        currentValue = 0;
        name = inName;
    }
}

class ReferenceMonitor { 
    public static HashMap<String, Integer> rmMap = new HashMap<String, Integer>();

    //Map and arrays to associate string to Subject/SecureObject
    public static HashMap<String, Subject> subjMap = new HashMap<String, Subject>();
    public static HashMap<String, SecureObject> objMap = new HashMap<String, SecureObject>();

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
        if (subjMap.containsKey(instrObj.subjName) && objMap.containsKey(instrObj.objName)){
            if (instrObj.type.equals("READ")){
                //SSP
                ssp(instrObj.subjName, instrObj.objName) ;
            }
            else if (instrObj.type.equals("WRITE")){
                //*-Property
                starProperty(instrObj.subjName, instrObj.objName, instrObj.value);
            }
            else{
                System.out.println("Bad instruction");
            }
        }
        else {
            System.out.println("Bad instruction, unknown subject/object");
        }

    }

    //READ = SSP
    public static void ssp(String s, String o) {
        //System.out.println("SSP get subj level as " + getRM(s) + " and object level as " + getRM(o));

        if (SecurityLevel.dominates(getRM(s).intValue(), getRM(o).intValue())){
            //System.out.println("SSP allowed subj " + s +  " with level " + getRM(s) + " to read " + o +  " with level " + getRM(o) + "\n");
            objMan.read(subjMap.get(s), objMap.get(o));

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
            objMan.write(objMap.get(o), v);
        }
        else {
            System.out.println("This instruction violates *-Property");
        }
    }    

    /* Perform requests of the ReferenceMonitor */
    static class ObjectManager {

        //READ assign Subject.TEMP new value
        public void read(Subject s, SecureObject o){
            s.temp = o.currentValue;
            //System.out.println(s.name +" read " + o.name + " as " + o.currentValue);
        }

        //WRITE assign SecureObject.currentValue new value
        public void write(SecureObject o, int value){
            o.currentValue = value;
            //System.out.println(o.name +" was written as " + value);
        }
    }

}

/* Top level class */
class SecureSystem {

    public static Integer currentSubjArrayIndex = 0;
    public static Integer currentObjArrayIndex = 0;


    public static void main(String[] args) throws IOException{
        Scanner inFile = new Scanner(new FileReader("instructionList.txt"));

        int low  = SecurityLevel.LOW;
        int high = SecurityLevel.HIGH;

        ReferenceMonitor rm = new ReferenceMonitor();


        //====Make subjects known the the secure system
        //Lyle
        Subject lyle = new Subject("lyle");
        rm.updateRM("lyle", low);
        rm.subjMap.put("lyle", lyle);
        //Hal
        Subject hal = new Subject("hal");
        rm.updateRM("hal", high);
        rm.subjMap.put("hal", hal);

        //====Make objects known to the secure system
        //LObj
        SecureObject lobj = new SecureObject("lobj");
        rm.updateRM("lobj", low);
        rm.objMap.put("lobj", lobj);
        //HObj
        SecureObject hobj = new SecureObject("hobj");
        rm.updateRM("hobj", high);
        rm.objMap.put("hobj", hobj);



        //Instructions are parsed from the list
        String s;
        while(inFile.hasNext()){
            s = inFile.nextLine();
            //Print line of input
            System.out.println("\nInstruction line = " + s);
            InstructionObject instrObj = new InstructionObject();
            instrObj.assignObjElements(s);

            rm.monitorInstruction(instrObj);
            

            System.out.println("The current state is: ");
            System.out.println("    LObj has value: " + lobj.currentValue);
            System.out.println("    HObj has value: " + hobj.currentValue);
            System.out.println("    Lyle has recently read: " + lyle.temp);
            System.out.println("    Hal has recently read: " + hal.temp);

        }

        System.out.println("\nSecureSystem!\n");

    }
}