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

public class BeaverBase {

    static String prompt = "beaverql> ";
    static String version = "v1.0b(example)";
    static String copyright = "©2018 Pat Dayton";
    static boolean isExit = false;
    static long pageSize = 512;

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
        //printCmd("SELECT * FROM <table_name>;");
        //printDef("Display all records in the table <table_name>.");
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
        ArrayList<String> commandTokens = new ArrayList<String>(Arrays.asList(userCommand.split(" ")));


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
                showTables();
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
            case "exit":
                isExit = true;
                break;
            case "quit":
                isExit = true;
                break;
            default:
                System.out.println("I didn't understand the command: \"" + userCommand + "\"");
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
                    beaverbaseTablesCatalog.write(0xFF);
                    beaverbaseTablesCatalog.write(0xFF);
                    beaverbaseTablesCatalog.write(0xFF);
                    beaverbaseTablesCatalog.write(0xFF);


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
    }


    /**
     *  Stub method for dropping tables
     *  @param dropTableString is a String of the user input
     */
    public static void dropTable(String dropTableString) {
            System.out.println("STUB: This is the dropTable method.");
            System.out.println("\tParsing the string:\"" + dropTableString + "\"");
    }



    /**
     *  Stub method for updating records
     *  @param updateString is a String of the user input
     */
    public static void parseUpdate(String updateString) {
            System.out.println("STUB: This is the dropTable method");
            System.out.println("Parsing the string:\"" + updateString + "\"");
    }

    /*parse query*/
    public static void parseQuery(String queryString) {
        System.out.println("\tParsing the string:\"" + queryString + "\"");
        ArrayList<String> columnList = new ArrayList<>(); //possibly just the wildcard *
        String tableName;
        String constraintColumn;
        String constraintValue;
        String constraintOperator;

        /*parse out all the values from the query*/
        ArrayList<String> fromSplit = new ArrayList<>(Arrays.asList(queryString.split("from")));
        //System.out.println("fromSplit = " + fromSplit.toString());
        String selectString = fromSplit.get(0);
        String fromString = fromSplit.get(1);
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
        ArrayList<String> selectSplit = new ArrayList<>(Arrays.asList(selectString.split(" ")));
        for (int i = 1; i < selectSplit.size(); i++) {
            columnList.add(selectSplit.get(i).replace(",", ""));
        }
        //System.out.println("columnList = " + columnList.toString());

        /*retrieve table information about columns*/
        ArrayList<String> columnListActual = getTableInformation(tableName, "columnList");
        //System.out.println("columnListActual = " + columnListActual.toString());
        ArrayList<String> notNullList = getTableInformation(tableName, "nullList");
        //System.out.println("notNullList = " + notNullList.toString());
        ArrayList<String> dataTypeList = getTableInformation(tableName, "dataTypeList");
        //System.out.println("dataTypeList = " + dataTypeList.toString());
        ArrayList<String> ordinalPositionList = getTableInformation(tableName, "ordinalPositionList");
        //System.out.println("ordinalPositionList = " + ordinalPositionList.toString());

        /*pass values on to be printed by printQueryResults*/
        printQueryResults(
            tableName,
            constraintColumn,
            constraintOperator,
            constraintValue,
            columnList,
            columnListActual,
            notNullList,
            dataTypeList,
            ordinalPositionList);

    }

    /*print query results from parsed query parameters*/
    public static void printQueryResults(
            String tableName,
            String constraintColumn,
            String constraintOperator,
            String constraintValue,
            ArrayList<String> columnList,
            ArrayList<String> columnListActual,
            ArrayList<String> notNullList,
            ArrayList<String> dataTypeList,
            ArrayList<String> ordinalPositionList
            ){

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

        for (int i = 0; i < leftInsertTokens.size()-1; i++)
            columnList.add(leftInsertTokens.get(i).replace(",", "").replace(")", ""));

        for (int i = 0; i < rightInsertTokens.size(); i++)
            valueList.add(rightInsertTokens.get(i).replace(",", "").replace(")", ""));

        //System.out.println("columnList = " + columnList.toString());
        //System.out.println("valuelist = " + valueList.toString());

        /*retrieve table information about columns*/
        ArrayList<String> columnListActual = getTableInformation(tableName, "columnList");
        //System.out.println("columnListActual = " + columnListActual.toString());
        ArrayList<String> notNullList = getTableInformation(tableName, "nullList");
        //System.out.println("notNullList = " + notNullList.toString());
        ArrayList<String> dataTypeList = getTableInformation(tableName, "dataTypeList");
        //System.out.println("dataTypeList = " + dataTypeList.toString());
        ArrayList<String> ordinalPositionList = getTableInformation(tableName, "ordinalPositionList");
        //System.out.println("ordinalPositionList = " + ordinalPositionList);

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
        //System.out.println("orderedValueList = " + orderedValueList.toString());
        //System.out.println("");

        /*determine payload length*/
        int payloadLength = 4 + 1 + columnList.size() -1;
        for (int i = 1; i < dataTypeList.size(); i++) { //loop through the dataTypeList and add appropriate amounts to payload length.
            switch (dataTypeList.get(i)) {
                case "TINYINT":
                    payloadLength+=1;
                    break;
                case "SMALLINT":
                    payloadLength+=2;
                    break;
                case "INT":
                    payloadLength+=4;
                    break;
                case "BIGINT":
                    payloadLength+=8;
                    break;
                case "REAL":
                    payloadLength+=4;
                    break;
                case "DOUBLE":
                    payloadLength+=8;
                    break;
                case "DATETIME":
                    payloadLength+=8;
                    break;
                case "DATE":
                    payloadLength+=8;
                    break;
                case "TEXT":
                    payloadLength+=orderedValueList.get(i).length();;
                    break;
                default:
                    System.out.println("There is an issue with this command \"" + dataTypeList.get(i) + "\"");
                    break;
            }
        }
        //System.out.println("payloadLength = " + payloadLength);

        /*connect to correct table page*/
        try{
            String tablePath = "data/user_data/"+tableName+".tbl";
            RandomAccessFile table = new RandomAccessFile(tablePath, "rw");
            table.seek(1);
            int recordCount = table.read();

            int lastRecordLocation;
            if (recordCount == 0) {
                lastRecordLocation = (int) pageSize;
            }
            else{
                table.seek(8+((recordCount-1)*2));
                lastRecordLocation = table.readShort();
            }

            /*update the record count*/
            recordCount++;
            table.seek(1);
            table.writeByte(recordCount);

            /*seek to correct location and write payload*/
            int newStartOfContent = lastRecordLocation - payloadLength;

            /*update start of content*/
            table.seek(2);
            table.writeShort(newStartOfContent);

            table.seek(newStartOfContent);
            table.writeInt(Integer.parseInt(orderedValueList.get(0))); //rowid
            /*first the serial typecodes*/
            for (int i = 1; i < dataTypeList.size(); i++) {
                switch (dataTypeList.get(i)) {
                    case "TINYINT":
                        table.write(0x4);
                        break;
                    case "SMALLINT":
                        table.write(0x5);
                        break;
                    case "INT":
                        table.write(0x6);
                        break;
                    case "BIGINT":
                        table.write(0x7);
                        break;
                    case "REAL":
                        table.write(0x8);
                        break;
                    case "DOUBLE":
                        table.write(0x9);
                        break;
                    case "DATETIME":
                        table.write(0xA);
                        break;
                    case "DATE":
                        table.write(0xB);
                        break;
                    case "TEXT":
                        table.write(0xC+orderedValueList.get(i).length());
                        break;
                    default:
                        System.out.println("There is an issue with this command \"" + dataTypeList.get(i) + "\"");
                        break;
                }
            }
            /*then the actual values*/
            for (int i = 1; i < dataTypeList.size(); i++) {
                switch (dataTypeList.get(i)) {
                    case "TINYINT":
                        table.writeByte(Integer.parseInt(orderedValueList.get(i)));
                        break;
                    case "SMALLINT":
                        table.writeShort(Integer.parseInt(orderedValueList.get(i)));
                        break;
                    case "INT":
                        table.writeInt(Integer.parseInt(orderedValueList.get(i)));
                        break;
                    case "BIGINT":
                        table.writeLong(Integer.parseInt(orderedValueList.get(i)));
                        break;
                    case "REAL":
                        table.writeDouble(Double.parseDouble(orderedValueList.get(i)));
                        break;
                    case "DOUBLE":
                        table.writeDouble(Double.parseDouble(orderedValueList.get(i)));
                        break;
                    case "DATETIME":
                        table.writeInt(Integer.parseInt(orderedValueList.get(i)));
                        break;
                    case "DATE":
                        table.writeInt(Integer.parseInt(orderedValueList.get(i)));
                        break;
                    case "TEXT":
                        table.write(orderedValueList.get(i).getBytes());
                        break;
                    default:
                        System.out.println("There is an issue with this command \"" + dataTypeList.get(i) + "\"");
                        break;
                }
            }



            /*add record location to list*/
            int recordLocationPosition = 8+((recordCount-1)*2);
            table.seek(recordLocationPosition);
            table.writeShort(newStartOfContent);

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
            //System.out.println("writing to beaverbase_tables.tbl");
            RandomAccessFile beaverbase_tables = new RandomAccessFile("data/catalog/beaverbase_tables.tbl", "rw");

            /*determine number of records*/
            beaverbase_tables.seek(1);
            int recordCount = beaverbase_tables.read();

            for (int i = 1; i <= recordCount; i++) {

                /*get location of next title*/
                beaverbase_tables.seek(8+((i-1)*2));
                int recordLocation = beaverbase_tables.readShort();

                /*get rowId*/
                beaverbase_tables.seek(recordLocation);
                int rowId = beaverbase_tables.readInt();

                /*get length of table name*/
                beaverbase_tables.seek(recordLocation+5);
                int tableNameLength = beaverbase_tables.readUnsignedByte()-0xC;

                /*read and print the table name*/
                byte[] tableName = new byte[tableNameLength];
                beaverbase_tables.read(tableName);
                String tableNameString = new String(tableName);
                System.out.println(rowId+"\t"+tableNameString);

            }
            beaverbase_tables.close();
            System.out.println("");

        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    /*create new table*/
    public static void parseCreateTable(String createTableString) {

        ArrayList<String> createTableTokens = new ArrayList<>(Arrays.asList(createTableString.split(" ")));

        String tableName = createTableTokens.get(1);

        /* Define table file name */
        String tableFileName = "data/user_data/"+tableName + ".tbl";


        /*1) Parse the COL_NAMES and DATA_TYPES from the createTableTokens Variable*/
        int payloadSize = 0; //number of bytes in the payload
        int numColumns = 0;
        ArrayList<Integer> recordPayloadHeader = new ArrayList<>();
        ArrayList<Integer> recordPayloadContent = new ArrayList<>();
        ArrayList<String> columnList = new ArrayList<>();
        ArrayList<String> columnDataTypeList = new ArrayList<>();
        ArrayList<String> isNullableList = new ArrayList<>();
        ArrayList<Integer> dataTypeList = new ArrayList<>();
        String columnName;

        for (int i = 0; i < createTableTokens.size()-1; i++) {

            boolean isNullable = (createTableTokens.get(i+1).replace(",", "").equals("not"));

            switch (createTableTokens.get(i).replace(",", "")) {
                case "tinyint":
                    /*create column name list*/
                    columnName = createTableTokens.get(i-1).replace(",", "");
                    columnList.add(columnName);
                    columnDataTypeList.add("TINYINT");

                    payloadSize+=1;

                    /*create is nullable list*/
                    if (isNullable) {
                        isNullableList.add("YES");
                    }
                    else{
                        isNullableList.add("NO");
                    }

                    /*create data type list*/
                    dataTypeList.add(0x04);

                    numColumns++;
                    break;

                case "smallint":
                    /*create column name list*/
                    columnName = createTableTokens.get(i-1).replace(",", "");
                    columnList.add(columnName);
                    columnDataTypeList.add("SMALLINT");

                    payloadSize+=2;

                    /*create is nullable list*/
                    if (isNullable) {
                        isNullableList.add("YES");
                    }
                    else{
                        isNullableList.add("NO");
                    }

                    /*create data type list*/
                    dataTypeList.add(0x05);

                    numColumns++;
                    break;

                case "int":
                    /*create column name list*/
                    columnName = createTableTokens.get(i-1).replace(",", "");
                    columnList.add(columnName);
                    columnDataTypeList.add("INT");

                    payloadSize+=4;

                    /*create is nullable list*/
                    if (isNullable) {
                        isNullableList.add("YES");
                    }
                    else{
                        isNullableList.add("NO");
                    }

                    /*create data type list*/
                    dataTypeList.add(0x06);

                    numColumns++;
                    break;

                case "bigint":
                    /*create column name list*/
                    columnName = createTableTokens.get(i-1).replace(",", "");
                    columnList.add(columnName);
                    columnDataTypeList.add("BIGINT");

                    payloadSize+=8;

                    /*create is nullable list*/
                    if (isNullable) {
                        isNullableList.add("YES");
                    }
                    else{
                        isNullableList.add("NO");
                    }

                    /*create data type list*/
                    dataTypeList.add(0x07);

                    numColumns++;
                    break;

                case "real":
                    /*create column name list*/
                    columnName = createTableTokens.get(i-1).replace(",", "");
                    columnList.add(columnName);
                    columnDataTypeList.add("REAL");

                    payloadSize+=4;

                    /*create is nullable list*/
                    if (isNullable) {
                        isNullableList.add("YES");
                    }
                    else{
                        isNullableList.add("NO");
                    }

                    /*create data type list*/
                    dataTypeList.add(0x08);

                    numColumns++;
                    break;

                case "double":
                    /*create column name list*/
                    columnName = createTableTokens.get(i-1).replace(",", "");
                    columnList.add(columnName);
                    columnDataTypeList.add("DOUBLE");

                    payloadSize+=8;

                    /*create is nullable list*/
                    if (isNullable) {
                        isNullableList.add("YES");
                    }
                    else{
                        isNullableList.add("NO");
                    }

                    /*create data type list*/
                    dataTypeList.add(0x09);

                    numColumns++;
                    break;

                case "datetime":
                    /*create column name list*/
                    columnName = createTableTokens.get(i-1).replace(",", "");
                    columnList.add(columnName);
                    columnDataTypeList.add("DATETIME");

                    payloadSize+=8;

                    /*create is nullable list*/
                    if (isNullable) {
                        isNullableList.add("YES");
                    }
                    else{
                        isNullableList.add("NO");
                    }

                    /*create data type list*/
                    dataTypeList.add(0x0A);

                    numColumns++;
                    break;

                case "date":
                    /*create column name list*/
                    columnName = createTableTokens.get(i-1).replace(",", "");
                    columnList.add(columnName);
                    columnDataTypeList.add("DATE");

                    payloadSize+=8;

                    /*create is nullable list*/
                    if (isNullable) {
                        isNullableList.add("YES");
                    }
                    else{
                        isNullableList.add("NO");
                    }

                    /*create data type list*/
                    dataTypeList.add(0x0B);

                    numColumns++;
                    break;

                case "text":
                    /*create column name list*/
                    columnName = createTableTokens.get(i-1).replace(",", "");
                    columnList.add(columnName);
                    columnDataTypeList.add("TEXT");

                    /*create data type list -- different because variable*/
                    String textColumnName = createTableTokens.get(i-1).replace(",","");
                    int textColumnNameLength = textColumnName.length();
                    payloadSize+=textColumnNameLength;
                    int serialTypeCode = 0x0C + textColumnNameLength;
                    dataTypeList.add(serialTypeCode);

                    /*create is nullable list*/
                    if (isNullable) {
                        isNullableList.add("YES");
                    }
                    else{
                        isNullableList.add("NO");
                    }

                    numColumns++;
                    break;

                default:
                    /* This word does not add to the */
                    break;
            }
        }
        recordPayloadHeader.add(0, numColumns); //add number of columns to the array list

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
            tableFile.write(0xFF);
            tableFile.write(0xFF);
            tableFile.write(0xFF);
            tableFile.write(0xFF);

            /*Close stream*/
            tableFile.close();
            //tableFile.writeInt(63); /*Why is this 63?*/
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
                rowId = getRowId("beaverbase_tables") + 1;
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
            int catalogTablesPayloadLength = 4 + 2 + tableName.length();

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
            beaverbase_tables.writeInt(rowId);
            beaverbase_tables.write(0x1); //number of columns in beaverbase_tables
            int textTypeCode = 0xC+(int)tableName.length();
            beaverbase_tables.writeByte((int)textTypeCode); //rowid is an INT --> Serial Typecode 6
            beaverbase_tables.write(tableName.getBytes());
            System.out.println(tableName+" created");

            /*close connection*/
            beaverbase_tables.close();

        }
        catch(Exception e) {
            System.out.println(e);
        }

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
                int catalogColumnsPayloadLength = 4 + 1 + 5 + tableName.length() + columnList.get(i).length() + columnDataTypeList.get(i).length() + 1 + isNullableList.get(i).length();

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
        int rowId = 0;
        try{
            tableName = "data/catalog/"+tableName+".tbl";
            RandomAccessFile table = new RandomAccessFile(tableName, "rw");
            table.seek(1);
            int recordCount = table.read();

            /*find location of most recent record*/
            table.seek(8+((recordCount-1)*2));
            int mostRecentRecordLocation = table.readShort();

            /*seek to most recent record*/
            table.seek(mostRecentRecordLocation);

            /*read the 4 byte rowId and update rowId*/
            rowId =  table.readInt();

            table.close();
        }
        catch(IOException e) {
            System.out.println(e);
        }

        return rowId;
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
            //System.out.println("recordCount = " + recordCount);

            /*loop through the records in the beaverbase_columnspage (linearly)*/
            for (int i = 1; i <= recordCount; i++) {

                /*find location of next record, read it*/
                table.seek(8+((i-1)*2));
                //System.out.println("seeking to: " + (8+((i-1)*2)) );
                int recordLocation = table.readShort();
                //System.out.println("recordLocation = " + recordLocation);

                /*locate, read, and save values of column types*/
                table.seek(recordLocation+5); //table name
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

}
