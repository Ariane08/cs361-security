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
                System.out.println("type3 = " + type);
            }
        }
    }

    public static void instrMethod() {
        System.out.println("=====================end of instruction=======\n");
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

    //Map and arrays to associate string to Subject/SecureObject
    public static HashMap<String, Integer> subjIndexMap = new HashMap<String, Integer>();
    public static HashMap<String, Integer> objIndexMap = new HashMap<String, Integer>();
    public static ArrayList<Subject> subjArray = new ArrayList<Subject>();
    public static ArrayList<SecureObject> objArray = new ArrayList<SecureObject>();

    public static ObjectManager objMan = new ObjectManager();

    //RM map handling
    public static void updateRM(String s, Integer level) {
        rmMap.put(s, level);
        System.out.println("RM set " + s + " to level " + level);
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
            ssp(instrObj.subjName, instrObj.objName) ;
        }
        else if (instrObj.type.equals("WRITE")){
            //*-Property
            starProperty(instrObj.subjName, instrObj.objName, instrObj.value);
        }
        else{
            System.out.println("Bad instruction\n");
        }

    }

    //READ = SSP
    public static void ssp(String s, String o) {
        //System.out.println("SSP get subj level as " + getRM(s) + " and object level as " + getRM(o));

        if (SecurityLevel.dominates(getRM(s).intValue(), getRM(o).intValue())){
            //allow access
            System.out.println("SSP allowed subj " + s +  " with level " + getRM(s) + " to read " + o +  " with level " + getRM(o) + "\n");

            System.out.println("SSP subjArray PRINT");
            for(int i = 0; i < subjArray.size(); i++) {   
                System.out.println(subjArray.get(i).name);
            } 

            //Tell ObectManager what to do
            //objMan.read(subjMap.get(s), objMap.get(o));
            Integer subjIndex = subjIndexMap.get(s);
            System.out.println("subjIndex = " + subjIndex + " = " + subjArray.get(subjIndex).name);
            // Integer objIndex = arrayIndexMap.get(o);
            // System.out.println("objIndex = " + objIndex + " = " + objArray.get(objIndex).name);
            //objMan.read(subjArray.get(subjIndex), objArray.get(objIndex));

        }
        else {
            System.out.println("This instruction violates SSP");
        }
    } 

    //WRITE = *-Property
    public static void starProperty(String s, String o, int v) {
        //System.out.println("*-Property get subj level as " + getRM(s) + " and object level as " + getRM(o));

        if (SecurityLevel.writeAccess(getRM(s).intValue(), getRM(o).intValue())){
            //allow access
            System.out.println("*-Property allowed subj " + s +  " with level " + getRM(s) + " to write to " + o +  " with level " + getRM(o) + "\n");
            
            //System.out.println("write is called with " + objMap.get(o).name);

            //Tell ObectManager what to do
            //objMan.write(objMap.get(o), v);
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
            System.out.println(s.name +" read " + o.name + " as " + o.currentValue);
        }

        //WRITE assign SecureObject.currentValue new value
        public void write(SecureObject o, int value){
            o.currentValue = value;
            System.out.println(o.name +" was written as " + value);
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

        for (int i = 0; i < 2; i++){
            //String str = "a" + String.valueOf(i);
            Subject subj = new Subject();
            currentSubjArrayIndex++;
            rm.subjArray.add(subj);
        }

        for (int i = 0; i < 2; i++){
            //String str = "a" + String.valueOf(i);
            SecureObject subj = new SecureObject();
            currentObjArrayIndex++;
            rm.objArray.add(subj);
        }

        //====Make subjects known the the secure system
        //Lyle
        rm.subjArray.get(0).createSubject("lyle", low);
        rm.updateRM("lyle", low);
        rm.subjIndexMap.put("lyle", 0);

        rm.subjArray.get(1).createSubject("hal", high);
        rm.updateRM("hal", high);
        rm.subjIndexMap.put("hal", 1);

        rm.objArray.get(0).createNewObject("lobj", low);
        rm.updateRM("lobj", low);
        rm.objIndexMap.put("lobj", 0);

        rm.objArray.get(1).createNewObject("hobj", high);
        rm.updateRM("hobj", high);
        rm.objIndexMap.put("hobj", 1);
        // Subject lyle = new Subject();
        // lyle.createSubject("lyle", low);
        // rm.updateRM("lyle", low);
        // rm.subjIndexMap.put("lyle", currentSubjArrayIndex);
        // currentSubjArrayIndex++;
        // rm.subjArray.add(lyle);
        // //Hal
        // Subject hal = new Subject();
        // hal.createSubject("hal", high);
        // rm.updateRM("hal", high);
        // rm.subjIndexMap.put("hal", currentSubjArrayIndex);
        // currentSubjArrayIndex++;
        // rm.subjArray.add(hal);

        // //====Make objects known to the secure system
        // //LObj
        // SecureObject lobj = new SecureObject();
        // lobj.createNewObject("lobj", low);
        // rm.updateRM("lobj", low);
        // rm.objIndexMap.put("lobj", currentObjArrayIndex);
        // currentObjArrayIndex++;
        // rm.objArray.add(lobj);
        // //HObj
        // SecureObject hobj = new SecureObject();
        // hobj.createNewObject("hobj", high);
        // rm.updateRM("hobj", high);
        // rm.objIndexMap.put("hobj", currentObjArrayIndex);
        // currentObjArrayIndex++;
        // rm.objArray.add(hobj);



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
            System.out.println("LObj has value: " + rm.objArray.get(0).name);
            System.out.println("HObj has value: " + rm.objArray.get(1).name);
            System.out.println("Lyle has recently read: " + rm.subjArray.get(0).name);
            System.out.println("Hal has recently read: " + rm.subjArray.get(1).name);

            //Print end of instruction divider
            instrObj.instrMethod();

        }

        System.out.println("\nSecureSystem!\n");

    }
}