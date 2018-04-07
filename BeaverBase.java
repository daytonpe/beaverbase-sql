/*
 * Pat Dayton
 * UTD CS6360 -- Davis
 * Project 2
 */

package beaverbase;

import java.io.RandomAccessFile;
import java.io.File;
import java.io.FileReader;
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
        System.out.println("Welcome to BeaverBaseLite"); // Display the string.
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
	 *  Stub method for executing queries
	 *  @param queryString is a String of the user input
	 */
	public static void parseQuery(String queryString) {
		System.out.println("STUB: This is the parseQuery method");
		System.out.println("\tParsing the string:\"" + queryString + "\"");
	}

	/**
	 *  Stub method for updating records
	 *  @param updateString is a String of the user input
	 */
	public static void parseUpdate(String updateString) {
		System.out.println("STUB: This is the dropTable method");
		System.out.println("Parsing the string:\"" + updateString + "\"");
	}

	public static long toHex(String arg) {
            return Long.parseLong(String.format("%x", new BigInteger(1, arg.getBytes())), 16);
//                return String.format("%x", new BigInteger(1, arg.getBytes()));
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

                    /*get title length*/
                    beaverbase_tables.seek(recordLocation+1);
                    int tableNameLength = beaverbase_tables.readByte()-0xC;

                    /*read and print the table name*/
                    byte[] tableName = new byte[tableNameLength];
                    beaverbase_tables.read(tableName);
                    String tableNameString = new String(tableName);
                    System.out.println(i+"\t"+tableNameString);

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

            ArrayList<String> createTableTokens = new ArrayList<String>(Arrays.asList(createTableString.split(" ")));

            String tableName = createTableTokens.get(1);

            /* Define table file name */
            String tableFileName = "data/user_data/"+tableName + ".tbl";


            /*1) Parse the COL_NAMES and DATA_TYPES from the createTableTokens Variable*/
            int payloadSize = 0; //number of bytes in the payload
            int numColumns = 0;
            ArrayList<Integer> recordPayloadHeader = new ArrayList<Integer>();
            ArrayList<Integer> recordPayloadContent = new ArrayList<Integer>();
            ArrayList<String> columnList = new ArrayList<String>();
            ArrayList<String> columnDataTypeList = new ArrayList<String>();
            ArrayList<String> isNullableList = new ArrayList<String>();

            ArrayList<Integer> dataTypeList = new ArrayList<Integer>();
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
                tableFile.setLength(pageSize);
                tableFile.seek(0);
                /*Leaf*/
                tableFile.write(0x0D);
                /*Num cols (starts with 0)*/
                tableFile.write(0x00);
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

                /*increment number of records*/
                beaverbase_tables.seek(1);
                int recordCount = beaverbase_tables.read();
                recordCount++;
                beaverbase_tables.seek(1);
                beaverbase_tables.write(recordCount);

                /*retrieve start of content*/
                beaverbase_tables.seek(2);
                int startOfContent = beaverbase_tables.readShort();
                //System.out.println("Start of Content: " + startOfContent);

                /*calculate payload length-- 2+c in this case -- 1 (columns not counting row_id + 1 (text) + column_name_length*/
                int catalogTablesPayloadLength = 2 + tableName.length();

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

                    /*find location of most recent record*/
                    beaverbase_columns.seek(8+((recordCount-1)*2));
                    int mostRecentRecordLocation = beaverbase_columns.readShort();

                    /*seek to most recent record*/
                    beaverbase_columns.seek(mostRecentRecordLocation);

                    /*read the 4 byte rowId and update rowId*/
                    rowId = beaverbase_columns.readInt();
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
}
