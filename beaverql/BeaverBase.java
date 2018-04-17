/*
 * Pat Dayton
 * UTD CS6360 -- Davis
 * Project 2
 */

package beaverbase;

import java.io.RandomAccessFile;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.ArrayList;
import java.util.Arrays;
import static java.lang.System.out;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class BeaverBase {

    static String prompt = "beaverql> ";
    static String version = "v1.0b(example)";
    static String copyright = "©2018 Pat Dayton";
    static boolean isExit = false;
    static int pageSize = 512;
    static DateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss  ");
    static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /*
     *  The Scanner class is used to collect user commands from the prompt
     *  There are many ways to do this. This is just one.
     *
     *  Each time the semicolon (;) delimiter is entered, the userCommand
     *  String is re-populated.
     */
    static Scanner scanner = new Scanner(System.in).useDelimiter(";");

    /** ***********************************************************************
     *  Main method
     * @param args
     */
    public static void main(String[] args) {

        /* Display the welcome screen */
        splashScreen();

        /* Variable to collect user input from the prompt */
        String userCommand = "";

        while(!isExit) {
            System.out.print(prompt);
            /* toLowerCase() renders command case insensitive */
            userCommand = scanner.next().replace("\n", " ").replace("\r", "").trim().toLowerCase();
            // userCommand = userCommand.replace("\n", "").replace("\r", "");
            parseUserCommand(userCommand);
        }
        System.out.println("Exiting...");


    }

    /** ***********************************************************************
     *  Static method definitions
     */
    public static void splashScreen() {
            System.out.println(line("-",80));
    System.out.println("Welcome to BeaverBaseLite ~|=|>"); // Display the string.
            System.out.println("BeaverBaseLite Version " + getVersion());
            System.out.println(getCopyright());
            System.out.println("\nType \"help;\" to display supported commands.");
            System.out.println(line("-",80));
    }

    public static String line(String s,int num) {
            String a = "";
            for(int i=0;i<num;i++) {
                    a += s;
            }
            return a;
    }

    public static void printCmd(String s) {
            System.out.println("\n\t" + s + "\n");
    }

    public static void printDef(String s) {
            System.out.println("\t\t" + s);
    }

    public static void help() {
        out.println(line("*",80));

        out.println("SUPPORTED COMMANDS\n");
        out.println("All commands below are case insensitive\n");

        out.println("SHOW TABLES;");
        out.println("\tDisplay the names of all tables.\n");

        out.println("SELECT * FROM <table_name>;");
        out.println("\tDisplay all records in the table <table_name>;\n");

        out.println("SELECT <column_list> FROM <table_name> [WHERE <condition>];");
        out.println("\tDisplay table records whose optional <condition>");
        out.println("\tis <column_name> = <value>.\n");

        out.println("DROP TABLE <table_name>;");
        out.println("\tRemove table data (i.e. all records) and its schema.\n");

        out.println("UPDATE TABLE <table_name> SET <column_name> = <value> [WHERE <condition>];");
        out.println("\tModify records data whose optional <condition> is\n");

        out.println("VERSION;");
        out.println("\tDisplay the program version.\n");

        out.println("HELP;");
        out.println("\tDisplay this help information.\n");

        out.println("EXIT;");
        out.println("\tExit the program.\n");

        out.println(line("*",80));
    }

    public static String getVersion() {
            return version;
    }

    public static String getCopyright() {
            return copyright;
    }

    public static void displayVersion() {
            System.out.println("BeaverBaseLite Version " + getVersion());
            System.out.println(getCopyright());
    }

    public static void parseUserCommand (String userCommand) {

        /* commandTokens is an array of Strings that contains one token per array element
         * The first token can be used to determine the type of command
         * The other tokens can be used to pass relevant parameters to each command-specific
         * method inside each case statement */
        // String[] commandTokens = userCommand.split(" ");
        ArrayList<String> commandTokens = new ArrayList<>(Arrays.asList(userCommand.split(" ")));


        /*
        *  This switch handles a very small list of hardcoded commands of known syntax.
        *  You will want to rewrite this method to interpret more complex commands.
        */
        switch (commandTokens.get(0)) {
            case "select":
                parseQuery(userCommand);
                break;
            case "drop":
                dropTable(userCommand);
                break;
            case "create":
                parseCreateTable(userCommand);
                break;
            case "update":
                parseUpdate(userCommand);
                break;
            case "show":
                if (commandTokens.get(1).replace(",", "").replace(" ","").equals("tables")) {
                   showTables();
                } else{
                    System.out.println("Did you mean: SHOW TABLES;\n");
                }
                break;
            case "help":
                help();
                break;
            case "version":
                displayVersion();
                break;
            case "install":
                initializeDataStore();
                break;
            case "insert":
                parseInsert(userCommand);
                break;
            case "delete":
                parseDelete(userCommand);
                break;
            case "init":
                initialize();
                break;
            case "test1":
                test1();
                break;
            case "test2":
                test2();
                break;
            case "test3":
                test3();
                break;
            case "exit":
                isExit = true;
                break;
            case "quit":
                isExit = true;
                break;
            default:
                System.out.println("I didn't understand the command: \"" + userCommand + "\"\n");
                break;
        }
    }

    /**
     * This static method creates the BeaverBase data storage container
     * and then initializes two .tbl files to implement the two
     * system tables, beaverbase_tables and beaverbase_columns
     *
     *  WARNING! Calling this method will destroy the system database
     *           catalog files if they already exist.
     */
    static void initializeDataStore() {

            /** Create data directory at the current OS location to hold */
            try {
                    File dataDir1 = new File("data");
                    dataDir1.mkdir();
                    String[] oldTableFiles;
                    oldTableFiles = dataDir1.list();
                    for (int i=0; i<oldTableFiles.length; i++) {
                            File anOldFile1 = new File(dataDir1, oldTableFiles[i]);
                            anOldFile1.delete();
                    }

                    /*Don't know if I'll need to do deletes of these folders as well.*/
                    File dataDir2 = new File("data/catalog");
                    dataDir2.mkdir();
                    String[] oldTableFiles2;
                    oldTableFiles2 = dataDir2.list();
                    for (int i=0; i<oldTableFiles2.length; i++) {
                            File anOldFile2 = new File(dataDir2, oldTableFiles2[i]);
                            anOldFile2.delete();
                    }

                    File dataDir3 = new File("data/user_data");
                    dataDir3.mkdir();
                    String[] oldTableFiles3;
                    oldTableFiles3 = dataDir3.list();
                    for (int i=0; i<oldTableFiles3.length; i++) {
                            File anOldFile3 = new File(dataDir3, oldTableFiles3[i]);
                            anOldFile3.delete();
                    }

            }
            catch (SecurityException se) {
                    out.println("Unable to create data container directory");
                    out.println(se);
            }



            /** Create beaverbase_tables system catalog */
            try {
                    RandomAccessFile beaverbaseTablesCatalog = new RandomAccessFile("data/catalog/beaverbase_tables.tbl", "rw");
                    /* Initially, the file is one page in length */
                    beaverbaseTablesCatalog.setLength(pageSize);
                    /* Set file pointer to the beginnning of the file */
                    beaverbaseTablesCatalog.seek(0);
                    /* Write 0x0D to the page header to indicate that it's a leaf page.
                     * The file pointer will automatically increment to the next byte. */
                    beaverbaseTablesCatalog.write(0x0D);
                    /* Write 0x00 (although its value is already 0x00) to indicate there
                     * are no cells on this page */
                    beaverbaseTablesCatalog.write(0x00);

                    beaverbaseTablesCatalog.writeShort((int) pageSize);

                    /*write placeholder FF FF FF FF for the Right Page*/
                    beaverbaseTablesCatalog.writeInt(-1);

                    beaverbaseTablesCatalog.close();
            }
            catch (Exception e) {
                    out.println("Unable to create the database_tables file");
                    out.println(e);
            }

            /** Create beaverbase_columns systems catalog */
            try {
                    RandomAccessFile beaverbaseColumnsCatalog = new RandomAccessFile("data/catalog/beaverbase_columns.tbl", "rw");
                    /** At first the file is one page in length */
                    beaverbaseColumnsCatalog.setLength(pageSize);
                    beaverbaseColumnsCatalog.seek(0);       // Set file pointer to the beginnning of the file
                    /* Write 0x0D to the page header to indicate a leaf page. The file
                     * pointer will automatically increment to the next byte. */
                    beaverbaseColumnsCatalog.write(0x0D);
                    /* Write 0x00 (although its value is already 0x00) to indicate there
                     * are no cells on this page */
                    beaverbaseColumnsCatalog.write(0x00);

                    beaverbaseColumnsCatalog.writeShort((int) pageSize);

                    /*write placeholder FF FF FF FF for the Right Page*/
                    beaverbaseColumnsCatalog.write(0xFF);
                    beaverbaseColumnsCatalog.write(0xFF);
                    beaverbaseColumnsCatalog.write(0xFF);
                    beaverbaseColumnsCatalog.write(0xFF);

                    beaverbaseColumnsCatalog.close();
            }
            catch (Exception e) {
                out.println("Unable to create the database_columns file");
                out.println(e);
            }
            System.out.println("");
    }

    /**
     *  Stub method for updating records
     *  @param updateString is a String of the user input
     */
    public static void parseUpdate(String updateString) {
        String tableName;
        String changeColumn;
        String changeValue;
        String constraintValue;
        String constraintColumn;
        String constraintOperator;

        /*parse out all the values from the query*/
        ArrayList<String> fromSplit = new ArrayList<>(Arrays.asList(updateString.split("set")));
        //System.out.println("fromSplit = " + fromSplit.toString());

        String leftString = fromSplit.get(0);
        ArrayList<String> leftSplit = new ArrayList<>(Arrays.asList(leftString.split(" ")));
        //System.out.println("leftSplit = " + leftSplit);

        tableName = leftSplit.get(1).replace(" ", "");
        //System.out.println("tableName = " + tableName);

        /*if the table onto which we are performing a replace doesn't exist, break*/
        if(!tableExists(tableName)){
            System.out.println("\n"+tableName + " does not exist.\n");
            return;
        }

        ArrayList<String> whereSplit = new ArrayList<>(Arrays.asList(fromSplit.get(1).split("where")));
        //System.out.println("whereSplit = " + whereSplit);
        ArrayList<String> changeArray = new ArrayList<>(Arrays.asList(whereSplit.get(0).split(" ")));
        //System.out.println("changeArray = " + changeArray);
        changeColumn = changeArray.get(1).replace(" ", "").replace(",","");
        //System.out.println("changeColumn = " + changeColumn);
        changeValue = changeArray.get(3).replace(" ", "").replace(",","");
        //System.out.println("changeValue = " + changeValue);

        ArrayList<String> constraintArray = new ArrayList<>(Arrays.asList(whereSplit.get(1).split(" ")));
        constraintColumn = constraintArray.get(1).replace(" ", "").replace(",","");
        constraintOperator = constraintArray.get(2).replace(" ", "").replace(",","");
        constraintValue = constraintArray.get(3).replace(" ", "").replace(",","");
        //System.out.println("constraintArray = " + constraintArray);
        //System.out.println("constraintOperator = " + constraintOperator);
        //System.out.println("constraintColumn = " + constraintColumn);
        //System.out.println("constraintValue = " + constraintValue);

        /*retrieve table information about columns*/
        ArrayList<String> columnListActual = getTableInformation(tableName, "columnList");
        //System.out.println("columnListActual = " + columnListActual.toString());
        ArrayList<String> notNullList = getTableInformation(tableName, "nullList");
        //System.out.println("notNullList = " + notNullList.toString());
        ArrayList<String> dataTypeList = getTableInformation(tableName, "dataTypeList");
        //System.out.println("dataTypeList = " + dataTypeList.toString());

        /*validate query columns*/
        if(changeColumn.equals("rowid")){
            System.out.println("\nrowid is immutable\n");
        }
        else if (columnListActual.contains(changeColumn)) {
           /*pass values on to be printed by printQueryResults*/
            updateRecord(
                tableName,
                constraintColumn,
                constraintOperator,
                constraintValue,
                changeColumn,
                changeValue,
                columnListActual,
                notNullList,
                dataTypeList);
        }
        else{
            System.out.println("\ninvalid columns in query\n");
        }
    }

    /*update a record*/
    public static void updateRecord(
        String tableName,
        String constraintColumn,
        String constraintOperator,
        String constraintValue,
        String changeColumn,
        String changeValue,
        ArrayList<String> columnListActual,
        ArrayList<String> notNullList,
        ArrayList<String> dataTypeList
        ){

        /*first determine where constraint and change are in ordinal position*/
        int constraintOrdinalPosition = columnListActual.indexOf(constraintColumn)+1;
        int changeOrdinalPosition = columnListActual.indexOf(changeColumn)+1;

        int textConstraintLength = 0; //only used if type of constraint is TEXT

        try{
            RandomAccessFile table = new RandomAccessFile("data/user_data/"+tableName+".tbl", "rw");

            /*outer while loop searches through all the pages in the file*/
            table.seek(4);
            int pagePointer = table.readInt();
            int pageNumber = 1;
            int pageStart = 0;

            /*loop through each of the pages, flow control at the bottom*/
            while(true){

                /*determine number of records*/
                table.seek(pageStart+1);
                int recordCount = table.read();

                /*get a copy for looping purposes*/
                table.seek(pageStart+1);
                int originalRecordCount = table.read();

                /*
                recordCount will change if we delete a record. So we can't use it for looping through a page
                deleted record pointers will count in our looping value.
                */
                int recordsVisited = 0;
                int recordPointer = pageStart+8;
                String recordConstraintType = "";

                /*linear search records for those that match our query*/
                do{
                    /*get location of next title*/
                    table.seek(recordPointer);
                    int recordLocation = table.readShort();
                    recordPointer+=2;

                    /*checkpoint -- if record has been deleted, do not continue*/
                    if (recordLocation == -1)
                        continue;

                    recordsVisited++;

                    /*seek to record*/
                    table.seek(recordLocation);

                    /*save values that are always at same offsets*/
                    int recordPayloadLength = table.readShort();
                    int rowId = table.readInt();
                    int numColumns = table.readByte();

                    int recordConstraintOffset = 0;

                    /*determine data type of constraint*/
                    recordConstraintType = dataTypeList.get(constraintOrdinalPosition-1);

                    recordConstraintOffset = 7 + numColumns; //this will always be the same as a starting point
                    for (int j = 1; j < constraintOrdinalPosition-1; j++) {
                        recordConstraintOffset+=getContentSize(dataTypeList.get(j)); //accounted for all but the strings

                        int columnTypeByte = table.readByte();

                        if (columnTypeByte>0xB) { //if we find a TEXT type
                            recordConstraintOffset+= (columnTypeByte -0xC);
                        }
                    }
                    if(recordConstraintType.equals("text")){
                        textConstraintLength = table.readByte() - 0xC;
                    }
                    if(constraintColumn.equals("rowid")){
                        recordConstraintOffset = 2;
                    }

                    table.seek(recordLocation+recordConstraintOffset);

                    /*check if record matches where condition*/
                    boolean foundMatch = false;

                    if (recordConstraintType.equals("text")) {
                        byte[] temp = new byte[textConstraintLength];
                        table.read(temp);
                        String actualConstraintValue1 = new String(temp);
                        //System.out.println("actualConstraintValue1 =? constraintValue : " + actualConstraintValue1 +" =? "+constraintValue);
                        /* Test if we match constraint depending on constraint operator.
                        For TEXT data type only != and = are sensical */
                        switch (constraintOperator) {
                            case "=":
                                foundMatch = actualConstraintValue1.equals(constraintValue);
                                break;
                            case "!=":
                                foundMatch = !actualConstraintValue1.equals(constraintValue);
                                break;
                            default:
                                throw new Error("Invalid constraint "+constraintOperator);
                        }


                    }
                    else{
                        switch (recordConstraintType) {
                            case "tinyint":
                                int actualConstraintValue2 = table.readByte();
                                //System.out.println("actualConstraintValue2 = "+actualConstraintValue2);
                                foundMatch = checkIntConstraint(actualConstraintValue2, Integer.parseInt(constraintValue), constraintOperator);
                                break;
                            case "smallint":
                                int actualConstraintValue3 = table.readShort();
                                //System.out.println("actualConstraintValue3 = "+actualConstraintValue3);
                                foundMatch = checkIntConstraint(actualConstraintValue3, Integer.parseInt(constraintValue), constraintOperator);
                                break;
                            case "int":
                                //System.out.println("pointer: "+table.getFilePointer());
                                int actualConstraintValue4 = table.readInt();
                                //System.out.println("actualConstraintValue4 = "+actualConstraintValue4);
                                //System.out.println("actualConstraintValue4 =? constraintValue :" + actualConstraintValue4 +" =? "+constraintValue);
                                foundMatch = checkIntConstraint(actualConstraintValue4, Integer.parseInt(constraintValue), constraintOperator);
                                break;
                            case "bigint":
                                long actualConstraintValue5 = table.readLong();
                                //System.out.println("actualConstraintValue5 = "+actualConstraintValue5);
                                foundMatch = checkLongConstraint(actualConstraintValue5, Long.parseLong(constraintValue), constraintOperator);
                                break;
                            case "real":
                                float actualConstraintValue6 = table.readFloat();
                                //System.out.println("actualConstraintValue6 = "+actualConstraintValue6);
                                foundMatch = checkFloatConstraint(actualConstraintValue6, Float.parseFloat(constraintValue), constraintOperator);
                                break;
                            case "double":
                                //System.out.println("pointer: "+table.getFilePointer());
                                double actualConstraintValue7 = table.readDouble();
                                //System.out.println("actualConstraintValue7 = "+actualConstraintValue7);
                                //System.out.println("actualConstraintValue7 =? constraintValue :" + actualConstraintValue7 +" =? "+constraintValue);
                                foundMatch = checkDoubleConstraint(actualConstraintValue7, Double.parseDouble(constraintValue), constraintOperator);
                                break;
                            case "datetime":
                                long actualConstraintValue8 = table.readLong();
                                //System.out.println("actualConstraintValue8 = "+actualConstraintValue8);
                                foundMatch = checkLongConstraint(actualConstraintValue8, Long.parseLong(constraintValue), constraintOperator);
                                break;
                            case "date":
                                long actualConstraintValue9 = table.readLong();
                                //System.out.println("actualConstraintValue9 = "+actualConstraintValue9);
                                foundMatch = checkLongConstraint(actualConstraintValue9, Long.parseLong(constraintValue), constraintOperator);
                                break;
                            default:
                                throw new Error("Not a valid data type: "+recordConstraintType);
                        }
                    }

                    //System.out.println("foundMatch = " + foundMatch);

                    /*print the columns that match*/
                    if (foundMatch) {
                        ArrayList <Integer> dataTypesHexList = new ArrayList<>();

                        /* we will read the record types and record locations
                        concurrently so we know how many bytes to print and in
                        what format. Thus we need to initialize two starting positions */

                        /*first grab the rowid (useful if we are updating text so we can delete and insert*/
                        table.seek(recordLocation+2);
                        int recordRowId = table.readInt();

                        /*second where the data types are stored in single byte format*/
                        int dataTypesPointer = recordLocation+7;

                        /*third where the record payload actually starts*/
                        int recordPayloadPointer = recordLocation+7+numColumns;
                        //System.out.println("recordPayloadPointer = " + recordPayloadPointer);

                        int dataType = 0;

                        /*move through the values until you find the correct one to update*/
                        for (int j = 1; j < changeOrdinalPosition-1; j++) {

                            /*get the hex value of the datatype*/
                            table.seek(dataTypesPointer);

                            /*increment our pointers*/
                            dataTypesPointer++; //simple increment since all singly bytes
                            dataType = table.readByte();
                            //System.out.println("dataType = " + convertTypeCode(dataType));
                            if (dataType>0xB) { //more complicated when content size is variable
                                recordPayloadPointer+=dataType-0xC; //if text
                            }else{
                                recordPayloadPointer+=getContentSize(dataType); //if not text
                            }

                        }
                        /*need to increment the dataTypesPointer one extra time*/
                        table.seek(dataTypesPointer);
                        dataTypesPointer++;
                        dataType = table.readByte();

                        /*now we can seek to the correct key to change*/
                        table.seek(recordPayloadPointer);

                        switch (convertTypeCode(dataType)) {
                            case "tinyint":
                                table.writeByte(Integer.parseInt(changeValue));
                                break;
                            case "smallint":
                                table.writeShort(Integer.parseInt(changeValue));
                                break;
                            case "int":
                                table.writeInt(Integer.parseInt(changeValue));
                                break;
                            case "bigint":
                                table.writeLong(Long.parseLong(changeValue));;
                                break;
                            case "real":
                                table.writeFloat(Float.parseFloat(changeValue));
                                break;
                            case "double":
                                table.writeDouble(Double.parseDouble(changeValue));
                                break;
                            case "datetime":
                                table.writeLong(Long.parseLong(changeValue));
                                break;
                            case "date":
                                table.writeLong(Long.parseLong(changeValue));
                                break;
                            case "text":
                                /*since text size is variable, lets delete the record, and then reinsert the updated value*/
                                int newTextLength = changeValue.length();
                                int oldTextLength = dataType - 0xC;

                                /*figure out how much extra space we need to add to the new byte[] that will store our record*/
                                int wordSizeDifference = newTextLength - oldTextLength;

                                table.seek(recordLocation);
                                int recordPayload = table.readShort();
                                byte[] oldRecord = new byte[(7+numColumns + recordPayload)];
                                table.seek(recordLocation);
                                table.read(oldRecord);

                                byte[] newRecord = new byte[(7+numColumns + recordPayload + wordSizeDifference)];
                                int textStartIndex = recordPayloadPointer - recordLocation;

                                byte[] changeValueArr = changeValue.getBytes();

                                /*prep the new record payload*/
                                int newRecordPayloadInt = recordPayload + wordSizeDifference;
                                String newRecordPayloadString = String.valueOf(newRecordPayloadInt);

                                ByteBuffer b2 = ByteBuffer.allocate(4);
                                b2.putInt(newRecordPayloadInt);
                                byte[] newRecordPayloadArr = b2.array();

                                /*create a new byte[] where the text is updated (size might change)*/
                                int j = 0;
                                /*insert bytes from original array before TEXT*/
                                for (int i = 0; i < textStartIndex; i++) {
                                    newRecord[i] = oldRecord[j];
                                    j++;
                                }
                                /*insert bytes from new TEXT*/
                                for (int i = j; i <= newTextLength+j-1; i++)
                                    newRecord[i] = changeValueArr[i-j];

                                j+=oldTextLength;
                                /*insert bytes from original array after TEXT*/
                                for (int i = j; i < oldRecord.length; i++)
                                    newRecord[i+wordSizeDifference] = oldRecord[i];

                                for (int i = 2; i<4; i++)
                                    newRecord[i-2] = newRecordPayloadArr[i];

                                /*update the byte representing the updated TEXT*/
                                int newTextHeaderByte = newTextLength+0xC;
                                newRecord[6+changeOrdinalPosition-1] = (byte)newTextHeaderByte;

                                /*delete the old record before replacing it*/
                                deleteRecords(
                                    true,
                                    tableName,
                                    constraintColumn,
                                    constraintOperator,
                                    constraintValue,
                                    columnListActual,
                                    notNullList,
                                    dataTypeList,
                                    true);


                                /*if new word is bigger it will be reinserted at a new location*/
                                if (wordSizeDifference>0) {
                                    insertByteArray(newRecord, tableName, newRecord.length, false, recordLocation);
                                }
                                /*if not, it replaces the record in place*/
                                else{
                                    insertByteArray(newRecord, tableName, newRecord.length, true, recordLocation);
                                }
                                break;
                            default:
                                throw new Error("Not a valid data type: "+recordConstraintType);
                        }
                    }
                } while (recordsVisited < originalRecordCount);

                /*if the page we just visited has no following pages, close and return*/
                if(pagePointer == -1){
                    /*close out of table*/
                    table.close();
                    System.out.println();
                    return;
                }
                /*since we are reading through from first page to last we need to increment these at the end*/
                pageStart+=pageSize;
                table.seek(pageStart+4);
                pagePointer = table.readInt();
                pageNumber++;
            }
        }
        catch(IOException e) {
            System.out.println(e);
        }
        System.out.println();
    }

    /*parse query*/
    public static void parseQuery(String queryString) {
        //System.out.println("\tParsing the string:\"" + queryString + "\"");
        ArrayList<String> columnList = new ArrayList<>(); //possibly just the wildcard *
        String tableName;
        String constraintColumn = null;
        String constraintValue = null;
        String constraintOperator = null;
        boolean hasConstraint = false;

        /*parse out all the values from the query*/
        ArrayList<String> fromSplit = new ArrayList<>(Arrays.asList(queryString.split("from")));
        //System.out.println("fromSplit = " + fromSplit.toString());
        String selectString = fromSplit.get(0);
        String fromString = fromSplit.get(1);
        //System.out.println("fromString = " + fromString);

        /*check if there is a constraint in the query*/
        if (fromString.contains("where"))
        {
            hasConstraint = true;
            ArrayList<String> whereSplit = new ArrayList<>(Arrays.asList(fromString.split("where")));
            //System.out.println("whereSplit = " + whereSplit.toString());
            tableName = whereSplit.get(0).replace(" ","");
            //System.out.println("tableName = " + tableName);
            String constraintString = whereSplit.get(1);
            //System.out.println("constraintString = " + constraintString);
            ArrayList<String> constraintSplit = new ArrayList<>(Arrays.asList(constraintString.split("\\s+")));
            //System.out.println("constraintSplit = " + constraintSplit);
            constraintColumn = constraintSplit.get(1);
            //System.out.println("constraintColumn = " + constraintColumn);
            constraintOperator = constraintSplit.get(2);
            //System.out.println("constraintOperator = " + constraintOperator);
            constraintValue = constraintSplit.get(3);
            //System.out.println("constraintValue = " + constraintValue);
        }else{
            tableName = fromString.replace(" ", "");
        }

        ArrayList<String> selectSplit = new ArrayList<>(Arrays.asList(selectString.split(" ")));
        for (int i = 1; i < selectSplit.size(); i++) {
            columnList.add(selectSplit.get(i).replace(",", ""));
        }

        /*if the table from which we are qerying doesn't exist, break*/
        if(!tableExists(tableName)){
            System.out.println("\n"+tableName + " does not exist.\n");
            return;
        }

        /*retrieve table information about columns*/
        ArrayList<String> columnListActual = getTableInformation(tableName, "columnList");
        //System.out.println("Q columnListActual = " + columnListActual.toString());
        ArrayList<String> notNullList = getTableInformation(tableName, "nullList");
        //System.out.println("Q notNullList = " + notNullList.toString());
        ArrayList<String> dataTypeList = getTableInformation(tableName, "dataTypeList");
        //System.out.println("Q dataTypeList = " + dataTypeList.toString());
        ArrayList<String> ordinalPositionList = getTableInformation(tableName, "ordinalPositionList");
        //System.out.println("Q ordinalPositionList = " + ordinalPositionList.toString());

        /*if a wildcard is given, add all columns to the column list*/
        if (columnList.contains("*")) {
            columnList = columnListActual;
        }

        /*validate query columns*/
        if (validateQueryColumns(columnList, columnListActual)) {
           /*pass values on to be printed by printQueryResults*/
            printQueryResults(
                tableName,
                hasConstraint,
                constraintColumn,
                constraintOperator,
                constraintValue,
                columnList,
                columnListActual,
                notNullList,
                dataTypeList,
                ordinalPositionList);
        }
        else{
            System.out.println("invalid columns in query");
        }
    }

    /*ensure all columnList values are in column list actual*/
    public static boolean validateQueryColumns(ArrayList<String> columnList, ArrayList<String> columnListActual){
        for (int i = 0; i < columnList.size(); i++) {
            if (!columnListActual.contains(columnList.get(i))){
                return false;
            }
        }
        return true;
    }

    /*ensure column value (singular) is in column list actual*/
    public static boolean validateQueryColumns(String column, ArrayList<String> columnListActual){
        if (!columnListActual.contains(column)){
            return false;
        }
        return true;
    }

    /*given a string value of a data type, return the content size*/
    public static int getContentSize(String dataType){
        switch (dataType) {
            case "tinyint":
                return 1;
            case "smallint":
                return 2;
            case "int":
                return 4;
            case "bigint":
                return 8;
            case "real":
                return 4;
            case "double":
                return 8;
            case "datetime":
                return 8;
            case "date":
                return 8;
            case "text":
                return 0;
            default:
                throw new Error("Not a valid data type: "+dataType);
        }
    }

    /*given a string value of a data type, return the content size*/
    public static int getContentSize(int dataType){
        if (dataType>0xB) {
            return 0;
        }
        switch (dataType) {
            case 0x0:
                return 1;
            case 0x1:
                return 2;
            case 0x2:
                return 4;
            case 0x3:
                return 8;
            case 0x4:
                return 1;
            case 0x5:
                return 2;
            case 0x6:
                return 4;
            case 0x7:
                return 8;
            case 0x8:
                return 4;
            case 0x9:
                return 8;
            case 0xA:
                return 8;
            case 0xB:
                return 8;
            default:
                throw new Error("Not a valid data type: "+dataType);
        }
    }

    /*convert hex typecodes into string data types*/
    public static String convertTypeCode(int typeCode){
        if (typeCode>0xB) {
            return "text";
        }
        switch (typeCode) {
            case 0x0:
                return "null";
            case 0x1:
                return "null";
            case 0x2:
                return "null";
            case 0x3:
                return "null";
            case 0x4:
                return "tinyint";
            case 0x5:
                return "smallint";
            case 0x6:
                return "int";
            case 0x7:
                return "bigint";
            case 0x8:
                return "real";
            case 0x9:
                return "double";
            case 0xA:
                return "datetime";
            case 0xB:
                return "date";
            default:
                throw new Error("Not a valid type code: "+typeCode);
        }
    }

    /*given a subset of columns from query, return a boolean ArrayList of whether to print each column value*/
    public static ArrayList<Boolean> getBooleanPrintArray(ArrayList<String> columnList, ArrayList<String> columnListActual){
        ArrayList<Boolean> booleanPrintArray = new ArrayList<>();

        for (int i = 0; i < columnListActual.size(); i++) {
            if (columnList.contains(columnListActual.get(i))) {
                booleanPrintArray.add(true);
            }
            else{
                booleanPrintArray.add(false);
            }
        }
        return booleanPrintArray;
    }

    /*given two ints and a constraint type, determine if the constraint is met*/
    public static boolean checkIntConstraint(int value, int constraint, String operator){
        switch (operator){
            case "=":
                return value == constraint;
            case "!=":
                return value != constraint;
            case "<=":
                return value <= constraint;
            case "<":
                return value < constraint;
            case ">=":
                return value >= constraint;
            case ">":
                return value > constraint;
            default:
                throw new Error("Not a valid constraint operator: "+operator);
        }
    }

    /*given two doubles and a constraint type, determine if the constraint is met*/
    public static boolean checkDoubleConstraint(double value, double constraint, String operator){
        switch (operator){
            case "=":
                return value == constraint;
            case "!=":
                return value != constraint;
            case "<=":
                return value <= constraint;
            case "<":
                return value < constraint;
            case ">=":
                return value >= constraint;
            case ">":
                return value > constraint;
            default:
                throw new Error("Not a valid constraint operator: "+operator);
        }
    }

    /*given two doubles and a constraint type, determine if the constraint is met*/
    public static boolean checkFloatConstraint(float value, float constraint, String operator){
        switch (operator){
            case "=":
                return value == constraint;
            case "!=":
                return value != constraint;
            case "<=":
                return value <= constraint;
            case "<":
                return value < constraint;
            case ">=":
                return value >= constraint;
            case ">":
                return value > constraint;
            default:
                throw new Error("Not a valid constraint operator: "+operator);
        }
    }

    /*given two longs and a constraint type, determine if the constraint is met*/
    public static boolean checkLongConstraint(long value, long constraint, String operator){
        switch (operator){
            case "=":
                return value == constraint;
            case "!=":
                return value != constraint;
            case "<=":
                return value <= constraint;
            case "<":
                return value < constraint;
            case ">=":
                return value >= constraint;
            case ">":
                return value > constraint;
            default:
                throw new Error("Not a valid constraint operator: "+operator);
        }
    }

    /*print query results from parsed query parameters*/
    public static void printQueryResults(
        String tableName,
        boolean hasConstraint,
        String constraintColumn,
        String constraintOperator,
        String constraintValue,
        ArrayList<String> columnList,
        ArrayList<String> columnListActual,
        ArrayList<String> notNullList,
        ArrayList<String> dataTypeList,
        ArrayList<String> ordinalPositionList
        ){

        /*print header*/
        System.out.println();
        for (int i = 0; i < columnList.size(); i++) {
            System.out.print(String.format("%-16s" , columnList.get(i)));
        }
        System.out.println("");
        columnList.forEach((_item) -> {
            System.out.print("-----------------");
        });
        System.out.println("");

        int textConstraintLength = 0; //only used if type of constraint is TEXT

        /*determine constraint offset from start of each record*/

        /*first determine where constraint is in ordinal position*/
        int constraintOrdinalPosition = columnListActual.indexOf(constraintColumn)+1;

        try{
            RandomAccessFile table = new RandomAccessFile("data/user_data/"+tableName+".tbl", "rw");

            /*outer while loop searches through all the pages in the file*/
            table.seek(4);
            int pagePointer = table.readInt();
            int pageNumber = 1;
            int pageStart = 0;

            /*loop through each of the pages, flow control at the bottom*/
            while(true){

                /*determine number of records*/
                table.seek(pageStart+1);
                int recordCount = table.readByte();

                //TODO this will break if all the tables from the first page are deleted
                if (recordCount == 0) {
                    System.out.println("");
                    return;
                }

                /*
                recordCount will change if we delete a record. So we can't use it for looping through a page
                deleted record pointers will count in our looping value.
                */
                int recordsVisited = 0;
                int recordPointer = pageStart+8;
                String recordConstraintType = "";

                /*linear search records OF THIS PAGE for those that match our query*/
                do{

                    /*get location of next title*/
                    table.seek(recordPointer);
                    int recordLocation = table.readShort();
                    recordPointer+=2;

                    /*check if record has been deleted, if not increment the recordsVisited as we will now visit this record*/
                    if (recordLocation == -1)
                        continue;

                    recordsVisited++;

                    /*seek to record*/
                    table.seek(recordLocation);


                    /*save values that are always at same offsets*/
                    int recordPayloadLength = table.readShort();
                    int rowId = table.readInt();
                    int numColumns = table.readByte();

                    int recordConstraintOffset = 0;

                    if (hasConstraint){
                        /*determine data type of constraint*/
                        recordConstraintType = dataTypeList.get(constraintOrdinalPosition-1);

                        recordConstraintOffset = 7 + numColumns; //this will always be the same as a starting point
                        for (int j = 1; j < constraintOrdinalPosition-1; j++) {
                            recordConstraintOffset+=getContentSize(dataTypeList.get(j)); //accounted for all but the strings

                            int columnTypeByte = table.readByte();
                            //System.out.println("columnTypeByte = " + columnTypeByte);

                            if (columnTypeByte>0xB) { //if we find a TEXT type
                                recordConstraintOffset+= (columnTypeByte -0xC);
                                //System.out.println("adding "+(columnTypeByte -0xC));
                            }
                        }
                        if(recordConstraintType.equals("TEXT")){
                            textConstraintLength = table.readByte() -0xC;
                        }
                        if(constraintColumn.equals("rowid")){
                            recordConstraintOffset = 2;
                        }
                    }

                    table.seek(recordLocation+recordConstraintOffset);

                    /*check if record matches where condition*/
                    boolean foundMatch = false;

                    //System.out.println("recordConstraintType = " + recordConstraintType);

                    if (!hasConstraint){ //if it doesn't have any constraints, then everything is a match
                        foundMatch = true;
                    }
                    else if (recordConstraintType.equals("TEXT")) {
                        byte[] temp = new byte[textConstraintLength];
                        table.read(temp);
                        String actualConstraintValue1 = new String(temp);

                        /* Test if we match constraint depending on constraint operator.
                        For TEXT data type only != and = are sensical */
                        switch (constraintOperator) {
                            case "=":
                                foundMatch = actualConstraintValue1.equals(constraintValue);
                                break;
                            case "!=":
                                foundMatch = !actualConstraintValue1.equals(constraintValue);
                                break;
                            default:
                                throw new Error("Invalid constraint "+constraintOperator);
                        }
                    }
                    else{
                        switch (recordConstraintType) {
                            case "null":
                                foundMatch = false;
                                break;
                            case "tinyint":
                                int actualConstraintValue2 = table.readByte();
                                foundMatch = checkIntConstraint(actualConstraintValue2, Integer.parseInt(constraintValue), constraintOperator);
                                break;
                            case "smallint":
                                int actualConstraintValue3 = table.readShort();
                                foundMatch = checkIntConstraint(actualConstraintValue3, Integer.parseInt(constraintValue), constraintOperator);
                                break;
                            case "int":
                                int actualConstraintValue4 = table.readInt();
                                foundMatch = checkIntConstraint(actualConstraintValue4, Integer.parseInt(constraintValue), constraintOperator);
                                break;
                            case "bigint":
                                long actualConstraintValue5 = table.readLong();
                                foundMatch = checkDoubleConstraint(actualConstraintValue5, Double.parseDouble(constraintValue), constraintOperator);
                                break;
                            case "real":
                                float actualConstraintValue6 = table.readFloat();
                                foundMatch = checkFloatConstraint(actualConstraintValue6, Float.parseFloat(constraintValue), constraintOperator);
                                break;
                            case "double":
                                double actualConstraintValue7 = table.readDouble();
                                foundMatch = checkDoubleConstraint(actualConstraintValue7, Double.parseDouble(constraintValue), constraintOperator);
                                break;
                            case "datetime":
                                long actualConstraintValue8 = table.readLong();
                                foundMatch = checkLongConstraint(actualConstraintValue8, Long.parseLong(constraintValue), constraintOperator);
                                break;
                            case "date":
                                long actualConstraintValue9 = table.readLong();
                                foundMatch = checkLongConstraint(actualConstraintValue9, Long.parseLong(constraintValue), constraintOperator);
                                break;
                            default:
                                throw new Error("Not a valid data type: "+recordConstraintType);
                        }
                    }

                    //System.out.println("foundMatch = " + foundMatch);

                    /*print the columns that match*/
                    if (foundMatch) {

                        ArrayList <Boolean> booleanPrintList = getBooleanPrintArray(columnList, columnListActual);
                        ArrayList <Integer> dataTypesHexList = new ArrayList<>();
                        //System.out.println("booleanPrintList= " + booleanPrintList.toString());

                        /*since in a different place, we need to print rowid first if applicable*/
                        if (booleanPrintList.get(0)) {
                            System.out.print(rowId+"\t\t");
                        }

                        /* we will read the record types and record locations
                        concurrently so we know how many bytes to print and in
                        what format. Thus we need to initialize two starting positions */

                        /*first where the data types are stored in single byte format*/
                        int dataTypesPointer = recordLocation+7;
                        /*second where the record payload actually starts*/
                        int recordPayloadPointer = recordLocation+7+numColumns;


                        table.seek(recordPayloadPointer);
                        for (int j = 1; j < booleanPrintList.size(); j++) {

                            /*get the hex value of the datatype*/
                            table.seek(dataTypesPointer);
                            int dataType = table.readByte();

                            if (booleanPrintList.get(j)) {
                                /*print it based on what type it is*/
                                table.seek(recordPayloadPointer);
                                switch (convertTypeCode(dataType)) {
                                    case "null":
                                        System.out.print(String.format("%-16s" , "NULL"));
                                        break;
                                    case "tinyint":
                                        int printValue2 = table.readByte();
                                        System.out.print(String.format("%-16s" , printValue2));
                                        break;
                                    case "smallint":
                                        int printValue3 = table.readShort();
                                        System.out.print(String.format("%-16s" , printValue3));
                                        break;
                                    case "int":
                                        int printValue4 = table.readInt();
                                        System.out.print(String.format("%-16s" , printValue4));
                                        break;
                                    case "bigint":
                                        long printValue5 = table.readLong();
                                        System.out.print(String.format("%-16s" , printValue5));
                                        break;
                                    case "real":
                                        float printValue6 = table.readFloat();
                                        System.out.print(String.format("%-16s" , printValue6));
                                        break;
                                    case "double":
                                        double printValue7 = table.readDouble();
                                        System.out.print(String.format("%-16s" , printValue7));
                                        break;
                                    case "datetime":
                                        long printValue8 = table.readLong();
                                        System.out.print(String.format("%-16s" , datetimeFormat.format(printValue8)));
                                        break;
                                    case "date":
                                        long printValue9 = table.readLong();
                                        /*note that we are still storing the time part of the DATE value, just not displaying it*/
                                        System.out.print(String.format("%-16s" , dateFormat.format(printValue9)));
                                        break;
                                    case "text":
                                        byte[] temp = new byte[dataType-0xC];
                                        table.read(temp);
                                        String printValue10 = new String(temp);
                                        System.out.print(String.format("%-16s" , printValue10));
                                        break;
                                    default:
                                        throw new Error("Not a valid data type: "+recordConstraintType);
                                }
                            }

                            /*increment our pointers*/
                            dataTypesPointer++; //simple increment since all singly bytes
                            if (dataType>0xB) { //more complicated when content size is variable
                                recordPayloadPointer+=dataType-0xC; //if text
                            }else{
                                recordPayloadPointer+=getContentSize(dataType); //if not text
                            }
                        }
                        System.out.println();
                    }
                } while (recordsVisited < recordCount);

                /*if the page we just visited has no following pages, close and return*/
                if(pagePointer == -1){
                    /*close out of table*/
                    table.close();
                    System.out.println();
                    return;
                }
                /*since we are reading through from first page to last we need to increment these at the end*/
                pageStart+=pageSize;
                table.seek(pageStart+4);
                pagePointer = table.readInt();
                pageNumber++;
            }
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }

    /*insert into table*/
    public static void parseInsert(String insertString) {

        ArrayList<String> columnList = new ArrayList<>();
        ArrayList<String> valueList = new ArrayList<>();

        /*parse table name*/
        ArrayList<String> initialInsertTokens = new ArrayList<>(Arrays.asList(insertString.split("\\(")));
        ArrayList<String> leftInsertTokens = new ArrayList<>(Arrays.asList(initialInsertTokens.get(1).split(" ")));
        ArrayList<String> rightInsertTokens = new ArrayList<>(Arrays.asList(initialInsertTokens.get(2).split(" ")));

        String tableName = leftInsertTokens.get(leftInsertTokens.size()-1);

        /*if the table into which we are inserting doesn't exist, break*/
        if(!tableExists(tableName)){
            System.out.println("\n"+tableName + " does not exist.\n");
            return;
        }

        for (int i = 0; i < leftInsertTokens.size()-1; i++)
            columnList.add(leftInsertTokens.get(i).replace(",", "").replace(")", ""));

        for (int i = 0; i < rightInsertTokens.size(); i++)
            valueList.add(rightInsertTokens.get(i).replace(",", "").replace(")", ""));

        /*retrieve table information about columns*/
        ArrayList<String> columnListActual = getTableInformation(tableName, "columnList");
        ArrayList<String> isNullableList = getTableInformation(tableName, "nullList");
        ArrayList<String> dataTypeList = getTableInformation(tableName, "dataTypeList");

        /*ensure columns and null properties match*/
        if(columnList.size() != columnListActual.size())
            System.out.println("Incorrect number of columns supplied.");

        if(valueList.size() != columnListActual.size())
            System.out.println("Incorrect number of values supplied.");

        /*reorder columnList and valueList to match ordinal positions*/
        ArrayList<String> orderedValueList = new ArrayList<>();
        for (int i = 0; i < columnListActual.size(); i++) {
            for (int j = 0; j < columnList.size(); j++) {
                if (columnList.get(j).equals(columnListActual.get(i))) {
                    orderedValueList.add(valueList.get(j));
                }
            }
        }

        /*determine payload length*/
        int payloadLength = 2 + 4 + 1 + columnList.size() -1;
        for (int i = 1; i < dataTypeList.size(); i++) { //loop through the dataTypeList and add appropriate amounts to payload length.
            switch (dataTypeList.get(i)) {
                case "tinyint":
                    payloadLength+=1;
                    break;
                case "smallint":
                    payloadLength+=2;
                    break;
                case "int":
                    payloadLength+=4;
                    break;
                case "bigint":
                    payloadLength+=8;
                    break;
                case "real":
                    payloadLength+=4;
                    break;
                case "double":
                    payloadLength+=8;
                    break;
                case "datetime":
                    payloadLength+=8;
                    break;
                case "date":
                    payloadLength+=8;
                    break;
                case "text":
                    payloadLength+=orderedValueList.get(i).length();;
                    break;
                default:
                    System.out.println("There is an issue with this command \"" + dataTypeList.get(i) + "\"");
                    break;
            }
        }

        /*ensure there is space for payload*/
        if (!hasSpace(tableName, payloadLength))
            createPage(tableName); /*if not, extend the table*/

        /*connect to correct table page*/
        try{
            RandomAccessFile table = new RandomAccessFile("data/user_data/"+tableName+".tbl", "rw");

            /*read the page pointers and seek to the header of the newest page*/
            table.seek(4);
            int pagePointer = table.readInt();
            int pageNumber = 1;
            int pageStart = 0;

            while(pagePointer != -1){
                pageStart+=pageSize;
                table.seek(pageStart+4);
                pagePointer = table.readInt();
                pageNumber++;
            }

            /*increment recordCount*/
            table.seek(pageStart+1);
            int recordCount = table.readByte();
            recordCount++;
            table.seek(pageStart+1);
            table.writeByte(recordCount);

            /*save start of Content Location*/
            table.seek(pageStart+2);
            int startOfContent = table.readShort();

            /*seek to correct location and write payload*/
            int newStartOfContent = startOfContent - payloadLength;

            /*update start of content*/
            table.seek(pageStart+2);
            table.writeShort(newStartOfContent);

            int rowid = Integer.parseInt(orderedValueList.get(0));

            /*abortString is a delete command for this inserted*/
            String abortString = "delete from "+tableName+" where rowid = "+rowid;

            /*add record location to list*/
            int recordPointerPosition = pageStart+8;
            table.seek(recordPointerPosition);
            int recordPointer = table.readShort();
            while(recordPointer!=0){
                recordPointerPosition+=2;
                recordPointer = table.readShort();
            }
            table.seek(recordPointerPosition);
            table.writeShort(newStartOfContent);

            /*seek to the new records location and begin writing*/
            table.seek(newStartOfContent);
            table.writeShort(payloadLength - 6 - columnListActual.size()); //record payload length (not counting record header)
            table.writeInt(rowid); //rowid
            table.writeByte(columnListActual.size()-1);

            /*first the serial typecodes*/
            for (int i = 1; i < dataTypeList.size(); i++) {
                boolean isNull = orderedValueList.get(i).toLowerCase().replace(" ", "").replace(",", "").equals("null");
                boolean canBeNull = isNullableList.get(i).equals("YES");
                switch (dataTypeList.get(i)) {
                    case "tinyint":
                        if (isNull) {
                            if (canBeNull) {
                                table.write(0x0);
                            } else {
                                System.out.println("Cannot write NULL to type TINYINT NOT NULLABLE");
                                parseDelete(abortString); //delete what's been written so far
                                return;
                            }
                        } else {
                            table.write(0x4);
                        }
                        break;
                    case "smallint":
                        if (isNull) {
                            if (canBeNull) {
                                table.write(0x1);
                            } else {
                                System.out.println("Cannot write NULL to type SMALLINT NOT NULLABLE");
                                parseDelete(abortString); //delete what's been written so far
                                return;
                            }
                        } else {
                            table.write(0x5);
                        }
                        break;
                    case "int":
                        if (isNull) {
                            if (canBeNull) {
                                table.write(0x2);
                            } else {
                                System.out.println("Cannot write NULL to type INT NOT NULLABLE");
                                parseDelete(abortString); //delete what's been written so far
                                return;
                            }
                        } else {
                            table.write(0x6);
                        }
                        break;
                    case "bigint":
                        if (isNull) {
                            if (canBeNull) {
                                table.write(0x3);
                            } else {
                                System.out.println("Cannot write NULL to type BIGINT NOT NULLABLE");
                                parseDelete(abortString); //delete what's been written so far
                                return;
                            }
                        } else {
                            table.write(0x7);
                        }
                        break;
                    case "real":
                        if (isNull) {
                            if (canBeNull) {
                                table.write(0x2);
                            } else {
                                System.out.println("Cannot write NULL to type REAL NOT NULLABLE");
                                parseDelete(abortString); //delete what's been written so far
                                return;
                            }
                        } else {
                            table.write(0x8);
                        }
                        break;
                    case "double":
                        if (isNull) {
                            if (canBeNull) {
                                table.write(0x3);
                            } else {
                                System.out.println("Cannot write NULL to type DOUBLE NOT NULLABLE");
                                parseDelete(abortString); //delete what's been written so far
                                return;
                            }
                        } else {
                            table.write(0x9);
                        }
                        break;
                    case "datetime":
                        if (isNull) {
                            if (canBeNull) {
                                table.write(0x3);
                            } else {
                                System.out.println("Cannot write NULL to type DATETIME NOT NULLABLE");
                                parseDelete(abortString); //delete what's been written so far
                                return;
                            }
                        } else {
                            table.write(0xA);
                        }
                        break;
                    case "date":
                        if (isNull) {
                            if (canBeNull) {
                                table.write(0x3);
                            } else {
                                System.out.println("Cannot write NULL to type DATE NOT NULLABLE");
                                parseDelete(abortString); //delete what's been written so far
                                return;
                            }
                        } else {
                            table.write(0xB);
                        }
                        break;
                    case "text":
                        if (isNull){
                            System.out.println("Cannot write NULL to type TEXT");
                            parseDelete(abortString); //delete what's been written so far
                            return;
                        } else {
                            table.write(0xC+orderedValueList.get(i).length());
                            break;
                        }
                    default:
                        System.out.println("There is an issue with this command \"" + dataTypeList.get(i) + "\"");
                        break;
                }
            }

            /*then the actual values*/
            for (int i = 1; i < dataTypeList.size(); i++) {
                boolean isNull = orderedValueList.get(i).toLowerCase().replace(" ", "").replace(",", "").equals("null");
                switch (dataTypeList.get(i)) {
                    case "tinyint":
                        if(isNull){
                            table.writeByte(0);
                        } else{
                            table.writeByte(Integer.parseInt(orderedValueList.get(i)));
                        }
                        break;
                    case "smallint":
                        if(isNull){
                            table.writeShort(0);
                        } else {
                            table.writeShort(Integer.parseInt(orderedValueList.get(i)));
                        }
                        break;
                    case "int":
                        if (isNull) {
                            table.writeInt(0);
                        } else {
                            table.writeInt(Integer.parseInt(orderedValueList.get(i)));
                        }
                        break;
                    case "bigint":
                        if (isNull){
                            table.writeLong(0);
                        } else {
                            table.writeLong(Long.parseLong(orderedValueList.get(i)));
                        }
                        break;
                    case "real":
                        if (isNull){
                            table.writeFloat(0);
                        } else {
                            table.writeFloat(Float.parseFloat(orderedValueList.get(i)));
                        }
                        break;
                    case "double":
                        if (isNull){
                            table.writeDouble(0);
                        } else {
                            table.writeDouble(Double.parseDouble(orderedValueList.get(i)));
                        }
                        break;
                    case "datetime":
                        if (isNull){
                            table.writeLong(0);
                        } else {
                            table.writeLong(Long.parseLong(orderedValueList.get(i)));
                        }
                        break;
                    case "date":
                        if (isNull){
                            table.writeLong(0);
                        } else {
                            table.writeLong(Long.parseLong(orderedValueList.get(i)));
                        }
                        break;
                    case "text":
                        table.write(orderedValueList.get(i).getBytes());
                        break;
                    default:
                        System.out.println("There is an issue with this command \"" + dataTypeList.get(i) + "\"");
                        break;
                }

            }
            table.close();
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }

    /*insert byte array into table -- helper method for update*/
    public static void insertByteArray(
            byte[] record,
            String tableName,
            int recordPayloadLength,
            boolean shorterFlag,
            int originalAddress) {
        /*connect to correct table page*/
        int payloadLength = record.length;

        /*ensure there is space for payload*/
        if (!hasSpace(tableName, payloadLength))
            createPage(tableName); /*if not, extend the table*/

        try{
            String tablePath = "data/user_data/"+tableName+".tbl";
            RandomAccessFile table = new RandomAccessFile(tablePath, "rw");

            /*read the page pointers and seek to the header of the newest page*/
            table.seek(4);
            int pagePointer = table.readInt();
            int pageNumber = 1;
            int pageStart = 0;

            while(pagePointer != -1){
                pageStart+=pageSize;
                table.seek(pageStart+4);
                pagePointer = table.readInt();
                pageNumber++;
            }

            /*increment recordCount*/
            table.seek(pageStart+1);
            int recordCount = table.readByte();
            recordCount++;
            table.seek(pageStart+1);
            table.writeByte(recordCount);

            /*save start of Content Location*/
            table.seek(pageStart+2);
            int startOfContent = table.readShort();




            /*if the new text value is shorter, we insert it in the same spot*/
            if(shorterFlag){
                table.seek(originalAddress);
                table.write(record);
            }
            /*otherwise write switch up the header and write in new location*/
            else{
                /*seek to correct location and write payload*/
                int newStartOfContent = startOfContent - recordPayloadLength;

                /*update start of content*/
                table.seek(pageStart+2);
                table.writeShort(newStartOfContent);

                table.seek(newStartOfContent);
                table.write(record);

                /*add record location to list*/
                int recordPointerPosition = pageStart+8;
                table.seek(recordPointerPosition);
                int recordPointer = table.readShort();
                while(recordPointer!=0){
                    recordPointerPosition+=2;
                    recordPointer = table.readShort();
                }

                table.seek(recordPointerPosition);
                table.writeShort(newStartOfContent);
            }

            table.close();
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }

    /*show tables*/
    public static void showTables(){
        System.out.println("\nrowid\ttable_name\n" +"--------------------------");
        try{
            /*create access to tables file in catalog*/
            RandomAccessFile beaverbase_tables = new RandomAccessFile("data/catalog/beaverbase_tables.tbl", "rw");

            /*determine number of records*/
            beaverbase_tables.seek(1);
            int recordCount = beaverbase_tables.read();

            /*if record count == 0 then then we can just break*/
            if (recordCount == 0){
                System.out.println("");
                return;
            }

            /*
            recordCount will change if we delete a record. So we can't use it for looping through a page
            deleted record pointers will count in our looping value.
            */
            int recordsVisited = 0;

            int recordPointer = 8;

            do{

                //System.out.println("recordsVisited = " + recordsVisited);
                /*get location of next title*/
                beaverbase_tables.seek(recordPointer);
                int recordLocation = beaverbase_tables.readShort();

                recordPointer+=2;

                /*check if record has been deleted, if not increment the recordsVisited as we will now visit this record*/
                if (recordLocation == -1){
                    continue;
                }

                recordsVisited++;

                /*get rowId*/
                beaverbase_tables.seek(recordLocation+2);
                //System.out.println("recordLocation+2 = " + (recordLocation+2));
                int rowId = beaverbase_tables.readInt();

                /*get length of table name*/
                beaverbase_tables.seek(recordLocation+7);
                int tableNameLength = beaverbase_tables.readUnsignedByte()-0xC;

                /*read and print the table name*/
                byte[] tableName = new byte[tableNameLength];
                beaverbase_tables.read(tableName);
                String tableNameString = new String(tableName);
                System.out.println(rowId+"\t"+tableNameString);

            }while(recordsVisited < recordCount);
            beaverbase_tables.close();
            System.out.println("");

        }
        catch(IOException e) {
            System.out.println(e);
        }
        System.out.println();
    }

    /*parse out info from create table command*/
    public static void parseCreateTable(String createTableString) {
        /*initial parsing*/
        ArrayList<String> columnList = new ArrayList<>();
        ArrayList<String> columnDataTypeList = new ArrayList<>();
        ArrayList<String> isNullableList = new ArrayList<>();
        ArrayList<String> split1 = new ArrayList<>(Arrays.asList(createTableString.split("\\(")));
        ArrayList <String> split2 = new ArrayList<>(Arrays.asList(split1.get(0).split(" ")));
        String tableName = split2.get(1).replace(",", "").replace(" ", "");
        ArrayList <String> split3 = new ArrayList<>(Arrays.asList(split1.get(1).split(", ")));

        /*if the first attribute isn't rowid and/or doesn't say PRIMARY KEY, throw*/
        String primKey = split3.get(0).replace(")", "").trim().toLowerCase();
        if (!primKey.contains("primary key") || !primKey.contains("rowid")){
            System.out.println("\nrowid must be specified primary key.");
            return;
        }

        /*fill our lists with data about the table*/
        for (int i = 0; i < split3.size(); i++) {
            String temp = split3.get(i).replace(")", "").trim().toLowerCase();

            /*make sure no other keys are listed as primary key*/
            if (i != 0) {
               if (temp.contains("primary key") || temp.contains("rowid")){
                System.out.println("\nOnly rowid can be specified primary key.");
                return;
                }
            }


            ArrayList<String> tempArr = new ArrayList<>(Arrays.asList(temp.split(" ")));

            /*isNullable List*/
            if (temp.contains("not nullable")){
                isNullableList.add("NO");
            } else {
                isNullableList.add("YES");
            }

            /*columnList*/
            columnList.add(tempArr.get(0));

            /*columnDataTypeList*/
            columnDataTypeList.add(tempArr.get(1));
        }

        if (!isNullableList.contains("NO")) {
            System.out.println("\nNew tables must have one NOT NULLABLE attribute (Cannot be rowid).");
            return;
        }

        createTable(
            tableName,
            columnList,
            columnDataTypeList,
            isNullableList
        );
    }

    /*create new table*/
    public static void createTable(
            String tableName,
            ArrayList<String> columnList,
            ArrayList<String> columnDataTypeList,
            ArrayList<String> isNullableList) {

        /*if the table name is already in use, break*/
        if(tableExists(tableName)){
            System.out.println("\n"+tableName + " already exists. Choose another name.\n");
            return;
        }

        /* Define table file name */
        String tableFileName = "data/user_data/"+tableName + ".tbl";

        int numColumns = columnList.size();

        /*create a .tbl file to contain table data */
        try {
            /*  Create RandomAccessFile tableFile in read-write mode.
             *  Note that this doesn't create the table file in the correct directory structure
             */
            RandomAccessFile tableFile = new RandomAccessFile(tableFileName, "rw");
            /* Initially, the file is one page in length */
            tableFile.setLength(pageSize);

            tableFile.seek(0);

            /*Leaf*/
            tableFile.write(0x0D);

            /*Num cols (starts with 0)*/
            tableFile.write(0x00);

            /*Start of first record*/
            tableFile.writeShort((int) pageSize);

            /*write placeholder FF FF FF FF for the Right Page*/
            tableFile.writeInt(-1);

            /*Close stream*/
            tableFile.close();
        }
        catch(Exception e) {
            System.out.println(e);
        }

        /*update beaverbase_tables in catalog*/
        try{
            /*grab the file to write to*/
            RandomAccessFile beaverbase_tables = new RandomAccessFile("data/catalog/beaverbase_tables.tbl", "rw");
            beaverbase_tables.seek(0);
            int pageType = beaverbase_tables.read();

            //get record count and next rowId
            beaverbase_tables.seek(1);
            int recordCount = beaverbase_tables.read();
            int rowId = 1; //will be changed if recordCount is != 0
            if (recordCount != 0) {
                rowId = getRowId("beaverbase_tables");
            }

            /*increment number of records*/
            recordCount++;
            beaverbase_tables.seek(1);
            beaverbase_tables.write(recordCount);

            /*retrieve start of content*/
            beaverbase_tables.seek(2);
            int startOfContent = beaverbase_tables.readShort();
            //System.out.println("Start of Content: " + startOfContent);

            /*calculate payload length-- 2+c in this case -- 1 (columns not counting row_id + 1 (text) + column_name_length*/
            int catalogTablesPayloadLength = 2 + 4 + 2 + tableName.length();

            /*update the start of content*/
            int newStartOfContent = startOfContent - catalogTablesPayloadLength;
            //System.out.println("New Start of Content: "+ newStartOfContent);
            beaverbase_tables.seek(2);
            beaverbase_tables.writeShort(newStartOfContent);

            /*add the location of the new record to list*/
            int recordLocationPosition = 8+((recordCount-1)*2);
            beaverbase_tables.seek(recordLocationPosition);
            beaverbase_tables.writeShort(newStartOfContent);

            /*write payload*/
            int recordLocation = startOfContent - catalogTablesPayloadLength;
            beaverbase_tables.seek(recordLocation);
            beaverbase_tables.writeShort(catalogTablesPayloadLength - 8); //record payload length (not including record header)
            //System.out.println("catalogTablesPayloadLength - 8 = " + (catalogTablesPayloadLength - 8));
            beaverbase_tables.writeInt(rowId); //rowId
            //System.out.println("rowId = " + rowId);
            beaverbase_tables.write(0x1); //number of columns in beaverbase_tables
            int textTypeCode = 0xC+(int)tableName.length();
            beaverbase_tables.writeByte((int)textTypeCode); //rowid is an INT --> Serial Typecode 6
            beaverbase_tables.write(tableName.getBytes());
            System.out.println("");
            System.out.println(tableName+" table created\n");

            /*close connection*/
            beaverbase_tables.close();

        }
        catch(Exception e) {
            System.out.println(e);
        }

//        /*ensure there is space for payload*/
//        if (!hasSpace(tableName, payloadLength))
//            createPage(tableName); /*if not, extend the table*/

        /*update beaverbase_columns*/
        try{
            /*grab the file to write to*/
            RandomAccessFile beaverbase_columns = new RandomAccessFile("data/catalog/beaverbase_columns.tbl", "rw");
            beaverbase_columns.seek(0);
            int pageType = beaverbase_columns.read();

            /*
            Determine rowId starting point for each of these columns.
            Since the create table command does not make the user explicitly input a rowId for each column, the program
            must determine it automatically. We will find the rowId of the most recently inserted column and then increment
            from their for each new column of the newly created table.
            If number of records in beaverbase_columns is zero, we can simply start at 1
            */
            beaverbase_columns.seek(1);
            int recordCount = beaverbase_columns.read();
            int rowId = 1; //will be changed if recordCount is != 0
            if (recordCount != 0) {
                rowId = getRowId("beaverbase_columns") + 1;
            }

            /*insert each of the columns as rows in beaverbase_columns.tbl*/
            for (int i = 0; i < numColumns; i++) {
                /*increment number of records*/
                beaverbase_columns.seek(1);
                recordCount = beaverbase_columns.read();
                recordCount++;
                beaverbase_columns.seek(1);
                beaverbase_columns.write(recordCount);

                /*retrieve start of content*/
                beaverbase_columns.seek(2);
                int startOfContent = beaverbase_columns.readShort();

                /*calculate payload length -- rowId + numCols + colTypes + column names/types*/
                int catalogColumnsPayloadLength = 2 + 4 + 1 + 5 + tableName.length() + columnList.get(i).length() + columnDataTypeList.get(i).length() + 1 + isNullableList.get(i).length();

                /*update the start of content*/
                int newStartOfContent = startOfContent - catalogColumnsPayloadLength;
                beaverbase_columns.seek(2);
                beaverbase_columns.writeShort(newStartOfContent);

                /*add the location of the new record to list*/
                int recordLocationPosition = 8+((recordCount-1)*2);
                beaverbase_columns.seek(recordLocationPosition);
                beaverbase_columns.writeShort(newStartOfContent);

                /*seek to new start of content to write payload*/
                beaverbase_columns.seek(newStartOfContent);

                /*write payload*/
                int recordLocation = startOfContent - catalogColumnsPayloadLength;
                beaverbase_columns.seek(recordLocation);

                beaverbase_columns.writeShort(catalogColumnsPayloadLength - 12); //record payload length (not including record header)
                beaverbase_columns.writeInt(rowId); //rowId
                beaverbase_columns.writeByte(5); //numCols
                beaverbase_columns.writeByte(0xC+tableName.length());
                beaverbase_columns.writeByte(0xC+columnList.get(i).length());
                beaverbase_columns.writeByte(0xC+columnDataTypeList.get(i).length());
                beaverbase_columns.writeByte(0x4);
                beaverbase_columns.writeByte(0xC+isNullableList.get(i).length());
                beaverbase_columns.write(tableName.getBytes()); //tableName
                beaverbase_columns.write(columnList.get(i).getBytes()); //column_name
                beaverbase_columns.write(columnDataTypeList.get(i).getBytes()); //data type string
                beaverbase_columns.writeByte(i+1); //ordinalPosition
                beaverbase_columns.write(isNullableList.get(i).getBytes()); //isNullable
                rowId++;
            }

            /*close connection*/
            beaverbase_columns.close();

        }
        catch(Exception e) {
            System.out.println(e);
        }
}

    /*get most recent rowId -- only necessary for catalog .tbl files*/
    public static int getRowId(String tableName){
        int recordPointer = 1;
        int i = 0;
        try{
            tableName = "data/catalog/"+tableName+".tbl";
            RandomAccessFile table = new RandomAccessFile(tableName, "rw");

            do{
                /*seek to the next record pointer
                so long as it's not a 0, it's either a real record pointer
                or a -1 where a record used to be (and thus that rowid has
                already been claimed
                */
                table.seek(8 + (2*i));
                recordPointer = table.readShort();
                i++;


            } while (recordPointer != 0);

            table.close();

        }
        catch(IOException e) {
            System.out.println(e);
        }
        return i;
    }

    /*get table information from beaverbase_columns. requests can be dataTypeList, columnList, nullList, ordinalPositionList*/
    public static ArrayList<String>  getTableInformation(String tableName, String request){
        ArrayList<String> nullList = new ArrayList<>();
        ArrayList<String> dataTypeList = new ArrayList<>();
        ArrayList<String> columnList = new ArrayList<>();
        ArrayList<String> ordinalPositionList = new ArrayList<>();

        //System.out.println("tableName = " + tableName);
        try{
            RandomAccessFile table = new RandomAccessFile("data/catalog/beaverbase_columns.tbl", "rw");
            table.seek(1);
            int recordCount = table.read();



            /*
            recordCount will change if we delete a record. So we can't use it for looping through a page
            deleted record pointers will count in our looping value.
            */
            int recordsVisited = 0;

            /*loop through the records in the beaverbase_columnspage (linearly)*/
            while(recordsVisited < recordCount){
                //System.out.println("recordsVisited = " + recordsVisited);
                recordsVisited++;
                /*get location of next title*/
                table.seek(8+((recordsVisited-1)*2));
                int recordLocation = table.readShort();
                //System.out.println("recordLocation = " + recordLocation

                /*checkpoint -- if record has been deleted, do not continue*/
                if (recordLocation == -1)
                    continue;

                /*locate, read, and save values of column types*/
                table.seek(recordLocation+7); //table name
                //System.out.println("SEEKING: "+(recordLocation+7));
                int tableNameLength = table.readByte()-0xC;
                //System.out.println("tableNameLength = " + tableNameLength);
                int columnNameLength = table.readByte()-0xC;
                //System.out.println("columnNameLength = " + columnNameLength);
                int dataTypeLength = table.readByte()-0xC;
                //System.out.println("dataTypeLength = " + dataTypeLength);
                int ordinalPositionType = table.readByte();
                //System.out.println("ordinalPositionType = " + ordinalPositionType);
                int isNullableLength = table.readByte()-0xC;
                //System.out.println("isNullableLength = " + isNullableLength);

                /*read table name associated with this column*/
                byte[] readTableName = new byte[tableNameLength];
                table.read(readTableName);
                String readTableNameString = new String(readTableName);
                //System.out.println("readTableNameString = " + readTableNameString);

                /*check if table name is same as table for which we are looking*/
                //System.out.println(tableName.equals(readTableNameString));

                if (tableName.equals(readTableNameString)){
                    /*if it matches, add everything to the data arrayLists*/

                    /*column name*/
                    byte[] readColumnName = new byte[columnNameLength];
                    table.read(readColumnName);
                    String columnName = new String(readColumnName);
                    //System.out.println("columnName = " + columnName);

                    /*data type*/
                    byte[] readDataType = new byte[dataTypeLength];
                    table.read(readDataType);
                    String dataType = new String(readDataType);
                    //System.out.println("dataType = " + dataType);

                    /*ordinal position*/
                    int ordinalPosition = table.readByte();
                    //System.out.println("ordinalPosition = " + ordinalPosition);

                    /*is nullable*/
                    byte[] readIsNullable = new byte[isNullableLength];
                    table.read(readIsNullable);
                    String isNullable = new String(readIsNullable);
                    //System.out.println("isNullable = " + isNullable);

                    /*make sure that each of our lists are big enough*/
                    while(ordinalPosition > nullList.size()){
                        nullList.add(null);
                        dataTypeList.add(null);
                        columnList.add(null);
                        ordinalPositionList.add(null);
                    }

                    /*save to array lists in ordinal positions*/
                    dataTypeList.set(ordinalPosition-1, dataType);
                    columnList.set(ordinalPosition-1, columnName);
                    nullList.set(ordinalPosition-1, isNullable);
                    ordinalPositionList.set(ordinalPosition-1, String.valueOf(ordinalPosition));
                }
            }
            table.close();
        }
        catch(IOException e) {
            System.out.println(e);
        }
        switch (request) {
            case "dataTypeList":
                return dataTypeList;
            case "columnList":
                return columnList;
            case "nullList":
                return nullList;
            case "ordinalPositionList":
                return ordinalPositionList;
            default:
                System.out.println("You have entered an invalid request: \"" + request + "\"");
        }
        return null;
    }

    /*parse delete*/
    public static void parseDelete(String deleteString){
        boolean hasConstraint = false;
        String tableName;
        String constraintColumn = null;
        String constraintOperator = null;
        String constraintValue = null;

        /*check if there is a constraint in the query*/
        if (deleteString.contains("where"))
        {
            hasConstraint = true;
            ArrayList<String> whereSplit = new ArrayList<>(Arrays.asList(deleteString.split("where")));
            //System.out.println("whereSplit = " + whereSplit.toString());

            /*parse constraint*/
            ArrayList<String> rightSplit = new ArrayList<>(Arrays.asList(whereSplit.get(1).split(" ")));
            constraintColumn = rightSplit.get(1).replace(" ", "");
            //System.out.println("constraintColumn = " + constraintColumn);
            constraintOperator = rightSplit.get(2).replace(" ", "");
            //System.out.println("constraintOperator = " + constraintOperator);
            constraintValue = rightSplit.get(3).replace(" ", "");
            //System.out.println("constraintValue = " + constraintValue);

            /*parse tableName*/
            ArrayList<String> leftSplit = new ArrayList<>(Arrays.asList(whereSplit.get(0).split(" ")));
            //System.out.println("leftSplit = " + leftSplit.toString());
            tableName = leftSplit.get(2).replace(" ", "");
            //System.out.println("tableName = " + tableName);

        } else{
            /*we delete everything!*/
            ArrayList<String> deleteStringSplit = new ArrayList<>(Arrays.asList(deleteString.split(" ")));
            tableName = deleteStringSplit.get(2).replace(" ", "");
            //System.out.println("tableName = " + tableName);
        }

        /*if the table from which we are deleting doesn't exist, break*/
        if(!tableExists(tableName)){
            System.out.println("\n"+tableName + " does not exist.\n");
            return;
        }

        /*retrieve table information about columns*/
        ArrayList<String> columnListActual = getTableInformation(tableName, "columnList");
        //System.out.println("columnListActual = " + columnListActual.toString());
        ArrayList<String> notNullList = getTableInformation(tableName, "nullList");
        //System.out.println("notNullList = " + notNullList.toString());
        ArrayList<String> dataTypeList = getTableInformation(tableName, "dataTypeList");
        //System.out.println("dataTypeList = " + dataTypeList.toString());



        /*validate query columns*/
        if (!hasConstraint || validateQueryColumns(constraintColumn, columnListActual)) {
           /*pass values on to be printed by printQueryResults*/
            deleteRecords(
                hasConstraint,
                tableName,
                constraintColumn,
                constraintOperator,
                constraintValue,
                columnListActual,
                notNullList,
                dataTypeList,
                false);
        }
        else{
            System.out.println("\ninvalid columns in query\n");
        }
    }

    /*delete records from passed delete parameters*/
    public static void deleteRecords(
        boolean hasConstraint,
        String tableName,
        String constraintColumn,
        String constraintOperator,
        String constraintValue,
        ArrayList<String> columnListActual,
        ArrayList<String> notNullList,
        ArrayList<String> dataTypeList,
        boolean updateShorterTextFlag
        ){

        /*first determine where constraint and change are in ordinal position*/
        int constraintOrdinalPosition = columnListActual.indexOf(constraintColumn)+1;

        int textConstraintLength = 0; //only used if type of constraint is TEXT

        try{

            RandomAccessFile table;

            /*if we are deleting from the drop table method, we will be deleting from catalog rather than user_data*/
            if (tableName.equals("beaverbase_columns") || tableName.equals("beaverbase_tables")) {
                table = new RandomAccessFile("data/catalog/"+tableName+".tbl", "rw");
            }
            else{
                table = new RandomAccessFile("data/user_data/"+tableName+".tbl", "rw");
            }

            /*outer while loop searches through all the pages in the file*/
            table.seek(4);
            int pagePointer = table.readInt();
            int pageNumber = 1;
            int pageStart = 0;

            /*loop through each of the pages, flow control at the bottom*/
            while(true){

                /*determine number of records*/
                table.seek(pageStart+1);
                int recordCount = table.read();

                /*get a copy for looping purposes*/
                table.seek(pageStart+1);
                int originalRecordCount = table.read();

                /*
                recordCount will change if we delete a record. So we can't use it for looping through a page
                deleted record pointers will count in our looping value.
                */
                int recordsVisited = 0;
                int recordPointer = pageStart+8;
                String recordConstraintType = "";


                /*linear search records for those that match our query*/
                do{
                    table.seek(recordPointer);
                    int recordLocation = table.readShort();

                    recordPointer+=2;
                    /*checkpoint -- if record has been deleted, do not continue*/
                    if (recordLocation == -1)
                        continue;
                    recordsVisited++;

                    /*seek to record*/
                    table.seek(recordLocation);

                    /*save values that are always at same offsets*/
                    int recordPayloadLength = table.readShort();
                    int rowId = table.readInt();
                    int numColumns = table.readByte();

                    int recordConstraintOffset = 0;

                    /*if there are constraints, we must have a way to match against the constraint*/
                    boolean foundMatch = false;

                    /*check each of the entries to see if they match the constraint*/
                    if(hasConstraint){
                        /*determine data type of constraint*/
                        recordConstraintType = dataTypeList.get(constraintOrdinalPosition-1);

                        recordConstraintOffset = 7 + numColumns; //this will always be the same as a starting point
                        for (int j = 1; j < constraintOrdinalPosition-1; j++) {

                            recordConstraintOffset+=getContentSize(dataTypeList.get(j)); //accounted for all but the strings

                            int columnTypeByte = table.readByte();
                            //System.out.println("columnTypeByte = " + columnTypeByte);

                            if (columnTypeByte>0xB) { //if we find a TEXT type
                                recordConstraintOffset+= (columnTypeByte -0xC);
                                //System.out.println("adding "+(columnTypeByte -0xC));
                            }
                        }
                        if(recordConstraintType.equals("text")){
                            textConstraintLength = table.readByte() - 0xC;
                        }
                        if(constraintColumn.equals("rowid")){
                            recordConstraintOffset = 2;
                        }

                        table.seek(recordLocation+recordConstraintOffset);

                        if (recordConstraintType.equals("text")) {
                            byte[] temp = new byte[textConstraintLength];
                            table.read(temp);
                            String actualConstraintValue1 = new String(temp);

                            /* Test if we match constraint depending on constraint operator.
                            For TEXT data type only != and = are sensical */
                            switch (constraintOperator) {
                                case "=":
                                    foundMatch = actualConstraintValue1.equals(constraintValue);
                                    break;
                                case "!=":
                                    foundMatch = !actualConstraintValue1.equals(constraintValue);
                                    break;
                                default:
                                    throw new Error("Invalid constraint "+constraintOperator);
                            }
                        }
                        else{
                            switch (recordConstraintType) {
                                case "tinyint":
                                    int actualConstraintValue2 = table.readByte();
                                    foundMatch = checkIntConstraint(actualConstraintValue2, Integer.parseInt(constraintValue), constraintOperator);
                                    break;
                                case "smallint":
                                    int actualConstraintValue3 = table.readShort();
                                    foundMatch = checkIntConstraint(actualConstraintValue3, Integer.parseInt(constraintValue), constraintOperator);
                                    break;
                                case "int":
                                    int actualConstraintValue4 = table.readInt();
                                    foundMatch = checkIntConstraint(actualConstraintValue4, Integer.parseInt(constraintValue), constraintOperator);
                                    break;
                                case "bigint":
                                    long actualConstraintValue5 = table.readLong();
                                    foundMatch = checkLongConstraint(actualConstraintValue5, Long.parseLong(constraintValue), constraintOperator);
                                    break;
                                case "real":
                                    float actualConstraintValue6 = table.readFloat();
                                    foundMatch = checkFloatConstraint(actualConstraintValue6, Float.parseFloat(constraintValue), constraintOperator);
                                    break;
                                case "double":
                                    double actualConstraintValue7 = table.readDouble();
                                    foundMatch = checkDoubleConstraint(actualConstraintValue7, Double.parseDouble(constraintValue), constraintOperator);
                                    break;
                                case "datetime":
                                    long actualConstraintValue8 = table.readLong();
                                    foundMatch = checkLongConstraint(actualConstraintValue8, Long.parseLong(constraintValue), constraintOperator);
                                    break;
                                case "date":
                                    long actualConstraintValue9 = table.readLong();
                                    foundMatch = checkLongConstraint(actualConstraintValue9, Long.parseLong(constraintValue), constraintOperator);
                                    break;
                                default:
                                    throw new Error("Not a valid data type: "+recordConstraintType);
                            }
                        }
                    } else {
                        foundMatch = true;
                    }

                    /*if we found a match, delete the record and update the header*/
                    if (foundMatch){
                        /*delete record*/
                        table.seek(recordLocation);
                        byte[] blankArray = new byte[7 + numColumns + recordPayloadLength];
                        table.write(blankArray);

                        /*update pointer*/
                        /*TODO this is ugly, but it works for now.*/
                        if (!updateShorterTextFlag) {
                            recordPointer-=2;
                            table.seek(recordPointer);
                            table.writeShort(-1);
                            recordPointer+=2;
                        }

                        /*update record count*/
                        recordCount--;
                        table.seek(pageStart + 1);
                        table.writeByte(recordCount);
                    }
                } while (recordsVisited < originalRecordCount);

                /*if the page we just visited has no following pages, close and return*/
                if(pagePointer == -1){
                    /*close out of table*/
                    table.close();
                    System.out.println();
                    return;
                }
                /*since we are reading through from first page to last we need to increment these at the end*/
                pageStart+=pageSize;
                table.seek(pageStart+4);
                pagePointer = table.readInt();
                pageNumber++;
            }
        }
        catch(IOException e) {
            System.out.println(e);
        }
        System.out.println();
    }

    /*drop table*/
    public static void dropTable(String dropTableString){

        /*parse out the table name*/
        ArrayList<String> fromSplit = new ArrayList<>(Arrays.asList(dropTableString.split(" ")));
        String tableName = fromSplit.get(2).replace(" ", "");
        //System.out.println("tableName = " + tableName);

        /*delete files associated with this tableName*/
        File file = new File("data/user_data/"+tableName+".tbl");

        if(file.delete())
        {
            System.out.println("\nFile deleted successfully");
        }
        else
        {
            System.out.println("\nFailed to delete the file");
        }

        /*delete relevent records from beaverbase tables*/
        /*first set up our parameters for deleteRecords*/
        ArrayList<String> columnListActualTables = new ArrayList<>();
        columnListActualTables.add("rowid");
        columnListActualTables.add("table_name");

        //System.out.println("columnListActualTables = " + columnListActualTables.toString());

        ArrayList<String> notNullListTables = new ArrayList<>();
        notNullListTables.add("YES");
        notNullListTables.add("YES");

        ArrayList<String> dataTypeListTables = new ArrayList<>();
        dataTypeListTables.add("int");
        dataTypeListTables.add("text");


        /*then actually purging from beaverbase_tables*/
        deleteRecords(
            true, //has constraint
            "beaverbase_tables",
            "table_name", //constraintColumn
            "=", //constraintOperator
            tableName, //constraintValue
            columnListActualTables,
            notNullListTables,
            dataTypeListTables,
            false
        );

        /*delete relevent records from beaverbase_columns*/
        /*first set up our parameters for deleteRecords*/
        ArrayList<String> columnListActualColumns = new ArrayList<>();
        columnListActualColumns.add("rowid");
        columnListActualColumns.add("table_name");
        columnListActualColumns.add("column_name");
        columnListActualColumns.add("data_type");
        columnListActualColumns.add("ordinal_position");
        columnListActualColumns.add("is_nullable");

        ArrayList<String> notNullListColumns = new ArrayList<>();
        for (int i = 0; i < 6; i++)
           notNullListColumns.add("YES");

        ArrayList<String> dataTypeListColumns = new ArrayList<>();
        dataTypeListColumns.add("int");
        dataTypeListColumns.add("text");
        dataTypeListColumns.add("text");
        dataTypeListColumns.add("text");
        dataTypeListColumns.add("tinyint");
        dataTypeListColumns.add("text");

        /*then actually purging from beaverbase_columns*/
        deleteRecords(
            true, //has constraint
            "beaverbase_columns",
            "table_name", //constraintColumn
            "=", //constraintOperator
            tableName, //constraintValue
            columnListActualColumns,
            notNullListColumns,
            dataTypeListColumns,
            false
        );

    }

    /*check if table exists*/
    public static boolean tableExists(String tableName){

        try{
            /*create access to tables file in catalog*/
            RandomAccessFile beaverbase_tables = new RandomAccessFile("data/catalog/beaverbase_tables.tbl", "rw");

            /*determine number of records*/
            beaverbase_tables.seek(1);
            int recordCount = beaverbase_tables.read();

            /*if record count == 0 then then we can just break*/
            if (recordCount == 0){
                System.out.println("");
                return false;
            }

            /*
            recordCount will change if we delete a record. So we can't use it for looping through a page
            deleted record pointers will count in our looping value.
            */
            int recordsVisited = 0;

            int recordPointer = 8;

            do{

                /*get location of next title*/
                beaverbase_tables.seek(recordPointer);
                int recordLocation = beaverbase_tables.readShort();
                recordPointer+=2;

                /*check if record has been deleted, if not increment the recordsVisited as we will now visit this record*/
                if (recordLocation == -1)
                    continue;

                recordsVisited++;

                /*get length of table name*/
                beaverbase_tables.seek(recordLocation+7);
                int tableNameLength = beaverbase_tables.readUnsignedByte()-0xC;

                /*read and print the table name*/
                byte[] tableNameArr = new byte[tableNameLength];
                beaverbase_tables.read(tableNameArr);
                String tableNameString = new String(tableNameArr);

                /*if we find the table name that was passed, it exists*/
                if(tableNameString.equals(tableName)){
                    return true;
                }

            } while (recordsVisited < recordCount);

            beaverbase_tables.close();

        } catch (IOException e) {
            System.out.println(e);
        }

        return false;
    }

    /*
    * method to visualize byte arrays from
    * https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    */
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /*reinstall database, create t1 table and insert texas_county data*/
    public static void initialize(){
        initializeDataStore();
        String createTexasCounties = "create t1 ( "
                + "rowid int primary key, "
                + "name text not nullable, "
                + "area double, "
                + "population bigint not nullable, "
                + "counselors tinyint, "
                + "zip int, "
                + "parks smallint, "
                + "avg_age real, "
                + "founded datetime, "
                + "holiday date"
                + " )";

        String insert1  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (1, archer, 150.4, null, 7, 75111, 2, 43.21, 687943532123, 1531618670000)";
        String insert2  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (2, dallas, 345.6, 2987678, 8, 75112, 299, 41.31, 687943532123, 1531618670000)";
        String insert3  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (3, jack, 534.3, 5476, 8, 75113, 23, 36.90, 687943532123, 1531618670000)";
        String insert4  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (4, montague, 789.3, 10292, 7, 75114, 13, 38.23, 687943532321, 1531618670000)";
        String insert5  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (5, anderson, 150.4, 9972, 9, 75115, 98, 33.87, 687943533212, 1531618670000)";
        String insert6  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (6, bexar, 150.4, 1900000, 6, 75116, 4, 34.67, 687943531232, 1531618670000)";
        String insert7  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (7, collin, 345.6, 910000, 7, 75117, 89, 37.80, 687943532312, 1531618670000)";
        String insert8  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (8, tarrant, 534.3, 2000000, 4, 75118, 43, 36.09, 687943532123, 1531618670000)";
        String insert9  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (9, williamson, 789.3, 510000, 3, 75119, 25, 31.95, 687943532321, 1531618670000)";
        String insert10 = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (10, travis, 150.4, 1200000, 7, 75120, 65, 29.17, 687943532123, 1531618670000)";
        String insert11 = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (11, comal, 150.4, 130000, 8, 75121, 2, 28.21, 687943532234, 1531618670000)";
        String insert12 = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (12, nueces, 345.6, 360000, 3, 75122, 20, 40.15, 687943532342, 1531618670000)";
        String insert13 = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (13, hudspeth, 534.3, 3400, 7, 75123, 10, 44.99, 687943532345, 1531618670000)";
        String insert14 = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (14, coryell, 789.3, 76000, 3, 75124, 17, 50.00, 687943536452, 1531618670000)";
        String insert15 = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (15, hays, 150.4, 190000, 5, 75125, 13, 43.12, 687943534562, 1531618670000)";
        String insert16 = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (16, glasscock, 150.4, 1300, 7, 75125, 87, 27.54, 687943535672, 1531618670000)";
        String insert17 = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (17, wilbarger, 150.4, 190000, 4, 75126, 7, 29.21, 687943538762, 1531618670000)";
        String insert18 = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (18, frio, 150.4, 19000, 7, 75127, 6, 33.12, 687943539082, 1531618670000)";

        String query = "select * from t1";

        parseCreateTable(createTexasCounties);
        parseInsert(insert1);
//        parseInsert(insert2);
//        parseInsert(insert3);
//        parseInsert(insert4);
//        parseInsert(insert5);
//        parseInsert(insert6);
//        parseInsert(insert7);
//        parseInsert(insert8);
//        parseInsert(insert9);
//        parseInsert(insert10);
//        parseInsert(insert11);
//        parseInsert(insert12);
//        parseInsert(insert13);
//        parseInsert(insert14);
//        parseInsert(insert15);
//        parseInsert(insert16);
//        parseInsert(insert17);
//        parseInsert(insert18);
//        parseQuery(query);

    }

    /*clean and sort headers -- delete -1 values, order per rowid*/
    public static void cleanHeaders(String tableName){
        try{
            RandomAccessFile table = new RandomAccessFile("data/user_data/"+tableName+".tbl", "rw");
            table.seek(8);
            int recordPointer = table.readShort();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /*check if there is space to insert a new record and it's corresponding record pointer, leaving 2 zeros as buffer*/
    public static boolean hasSpace(String tableName, int recordLength){
        try{
            RandomAccessFile table = new RandomAccessFile("data/user_data/"+tableName+".tbl", "rw");

            /*read the page pointers and seek to the header of the newest page*/
            /*read the page pointers and seek to the header of the newest page*/
            table.seek(4);
            int pagePointer = table.readInt();
            int pageNumber = 1;
            int pageStart = 0;

            while(pagePointer != -1){
                pageStart+=pageSize;
                table.seek(pageStart+4);
                pagePointer = table.readInt();
                pageNumber++;
            }

            /*read start of content on newest page*/
            table.seek(pageStart+2);
            int startOfContent = table.readShort();

            /*traverse through the record pointers on newest page find the first 00 00 SHORT (empty space)*/
            int recordPointerPosition = pageStart+8;
            table.seek(recordPointerPosition);
            int recordPointer = table.readShort();

            while(recordPointer!=0){
                recordPointerPosition+=2;
                recordPointer = table.readShort();
            }

            /*subtract to find remaining space. Must be larger than recordLength + 4 for space to be available.*/
            int spaceRemaining = startOfContent - recordPointerPosition;

            if (spaceRemaining > recordLength+4){ //4 because we want a two byte buffer after our record location array
                return true;
            }
        } catch (IOException e) {
            System.out.println("e");
        }
        return false;
    }

    public static void test1(){
        String createTexasCounties = "create t2 ( "
                + "rowid int primary key, "
                + "name text not nullable, "
                + "area double, "
                + "population bigint, "
                + "counselors tinyint, "
                + "zip int, "
                + "parks smallint, "
                + "avg_age real, "
                + "founded datetime, "
                + "holiday date"
                + " )";

        String insert1  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t2 (1, archer, 150.4, 8809, 7, 75111, 2, 43.21, 687943532123, 1531618670000)";
        String insert2  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t2 (2, dallas, 345.6, 2987678, 8, 75112, null, 41.31, 687943532123, 1531618670000)";
        String insert3  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t2 (3, jack, 534.3, 5476, 8, 75113, 23, 36.90, 687943532123, 1531618670000)";
        String insert4  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t2 (4, montague, 789.3, 10292, 7, 75114, 13, null, 687943532321, null)";
        String insert5  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t2 (5, anderson, 150.4, 9972, 9, 75115, 98, 33.87, 687943533212, 1531618670000)";
        String insert6  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t2 (6, bexar, 150.4, 1900000, 6, 75116, 4, 34.67, 687943531232, 1531618670000)";
        String insert7  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t2 (7, collin, 345.6, 910000, 7, 75117, 89, 37.80, 687943532312, 1531618670000)";
        String insert8  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t2 (8, tarrant, 534.3, 2000000, 4, 75118, 43, 36.09, 687943532123, 1531618670000)";
        String insert9  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t2 (9, williamson, 789.3, 510000, 3, 75119, 25, 31.95, null, 1531618670000)";
        String insert10 = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t2 (10, travis, 150.4, 1200000, 7, 75120, 65, 29.17, 687943532123, 1531618670000)";
        String insert11 = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t2 (11, comal, 150.4, 130000, 8, 75121, 2, 28.21, 687943532234, 1531618670000)";
        String insert12 = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t2 (12, nueces, 345.6, 360000, 3, null, 20, 40.15, 687943532342, 1531618670000)";
        String insert13 = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t2 (13, hudspeth, 534.3, 3400, 7, 75123, 10, 44.99, 687943532345, 1531618670000)";
        String insert14 = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t2 (14, coryell, 789.3, 76000, 3, null, 17, 50.00, 687943536452, 1531618670000)";
        String insert15 = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t2 (15, hays, 150.4, 190000, 5, 75125, 13, 43.12, 687943534562, null)";
        String insert16 = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t2 (16, glasscock, 150.4, 1300, 7, 75125, 87, 27.54, 687943535672, 1531618670000)";
        String insert17 = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t2 (17, wilbarger, 150.4, null, 4, 75126, 7, 29.21, 687943538762, 1531618670000)";
        String insert18 = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t2 (18, frio, 150.4, 19000, 7, 75127, 6, 33.12, null, 1531618670000)";

        String query = "select * from t2";

        parseCreateTable(createTexasCounties);
        parseInsert(insert1);
        parseInsert(insert2);
        parseInsert(insert3);
        parseInsert(insert4);
        parseInsert(insert5);
        parseInsert(insert6);
        parseInsert(insert7);
        parseInsert(insert8);
        parseInsert(insert9);
        parseInsert(insert10);
        parseInsert(insert11);
        parseInsert(insert12);
        parseInsert(insert13);
        parseInsert(insert14);
        parseInsert(insert15);
        parseInsert(insert16);
        parseInsert(insert17);
        parseInsert(insert18);
        parseQuery(query);
    }

    public static void test2(){
        initializeDataStore();
    }

    public static void test3(){
         String insert19  = "insert into table (rowid, name, area, population, counselors, zip, parks, avg_age, founded, holiday) t1 (1, archer, 150.4, null, 7, 75111, 2, 43.21, 687943532123, 1531618670000)";
         parseInsert(insert19);
    }

    /*add one pagelength worth of bytes to a .tbl file*/
    public static void createPage(String tableName){
        try{
            RandomAccessFile table = new RandomAccessFile("data/user_data/"+tableName+".tbl", "rw");

            /*initialize byte[] we will use to extend our page*/
            byte[] b = new byte[(int)pageSize];
            for (int i = 0; i < b.length; i++) {
                b[i] = 0; //initialize everything to 0;
            }

            /*read the page pointers and seek to the header of the newest page*/
            table.seek(4);
            int pagePointer = table.readInt();
            int pageNumber = 1;
            int pageStart = 0;

            while(pagePointer != -1){
                pageStart+=pageSize;
                table.seek(pageStart+4);
                pagePointer = table.readInt();
                pageNumber++;
            }
            pageNumber++;

            /*rewrite the old rightmost page's Right Page section in the header to point to our new rightmost page*/
            int newPageStart = pageStart+pageSize;
            table.seek(pageStart+4);
            table.writeInt(newPageStart);

            /*seek to the end of the table as it was and add *pageSize* of bytes*/
            table.seek(newPageStart);
            table.write(b);

            /*seek back to page start and write the header*/
            table.seek(newPageStart);
            table.writeByte(0xD); //all interior pages for now
            table.writeByte(0); //number of records on this page
            table.writeShort((short)(pageNumber*(int)pageSize)); //start of Content --TODO sloppy conversions
            table.writeInt(-1); // right page

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
