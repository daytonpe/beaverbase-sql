# BeaverBaseSQL -- SQL ENGINE

## Overview
The goal of this project is to implement a (very) rudimentary database engine that is loosely based on a hybrid between MySQL and SQLite. Your implementation should operate entirely from the command line and API calls (no GUI).

Database supports actions on a single table at a time, no joins or nested query functionality is implemented. Like MySQL's InnoDB data engine (SDL), program uses file-per-table approach to physical storage. Each database table is physically stored as a separate single file. Each table file is subdivided into logical sections of fixed equal size call pages. Therefore, each table file size is exact increments of the global page_size attribute, i.e. all data files must share the same page_size attribute. Page size is 512 Bytes.

---

## Setup

Use the following commands to get up and running. BeaverBaseSQL behaves similar to other SQL shells. See specifications below, as features are lite.

```
$cd beaverbase
$javac BeaverBase.java
$cd ..
$java -cp . beaverbase.BeaverBase
```

---

## Seed Data

Use seed functions to quickly display the features of BeaverBaseSQL. Use `INSTALL` command to drop all records and schemas to restore to default.

```
TXINIT; // to initialize with Texas, USA counties

WAINIT; // to initialize with Washington, USA counties
```

Creates a table representing data of Texas/Washington counties and inserts example records.
- Utilizes all data types
- Creates multiple pages
- Does NOT utilize null values

---

## Supported DDL and DML Commands

```
SHOW TABLES; // Displays a list of all TABLES

CREATE TABLE; //Creates a new table schema, i.e. a new empty table

DROP TABLE _tableName_; // Remove a table schema and all of its contained database

INSERT INTO _tableName_ [_columnList_] VALUES _valueList_; // Inserts a single record into a table

DELETE FROM _tableName_ [WHERE _condition_]; // Deletes one or more records from a a table.

UPDATE _tableName_ SET _columnName_ = _value_ [WHERE condition]; // Modifies one or more records in a table.

EXIT; // Cleanly exits the program and saves all table information in non-volatile files to disk.
```

---

## Querying

BeaverBaseSQL supports SELECT-FROM-WHERE” style queries. For example

```
SELECT rowid, name, population FROM wa_counties WHERE counselors != 7;
```

All constraint based commands can use any logical operator (<, <=, =, !=, >, >=)

---

## Create Table

Create a table schema

```
CREATE TABLE _tableName_ (
_row_id_ INT PRIMARY KEY, _columnName2_ _dataType2_ [NOT NULL], _columnName3_ _dataType3_ [NOT NULL], ...
);
```

Create the table schema information for a new table. In other words, add appropriate entries to the system beaverbase_tables and beaverbase_columns tables that define the described CREATE TABLE and create the associated .tbl data file.

Note that unlike the official SQL specification, a BeaverBase table PRIMARY KEY must be (a) a single column, (b) the first column listed in the CREATE statement, (c) named row_id, and (d) an INT data type. This requirement greatly simplifies the implementation. In most commercial databases a unique key, which is an INT data type, is automatically created in the background if you do not explicitly create the PRIMARY KEY as a single column INT. In commercial databases this “default” key is usually called the rowid or row_id. In the BeaverBase implementation, the row_id is explicit and required.

If a column is a primary key, its beaverbase_columns.COLUMN_KEY attribute is “PRI”, otherwise, it is NULL. If a column is defined as NOT NULL, then its beaverbase_columns.IS_NULLABLE attribute is “NO”, otherwise, it is “YES”.

FOREIGN KEY constraint are not supported, since multi-table queries (i.e. Joins) are not supported in BeaverBase.

---
## Data Types Supported

Replace the “Serial Type Codes Of The Record Format” table in SQLite document with the following table. The VARINT data type is not supported. Note that there are different sizes of NULL values in BeaverBase. This is to accommodate updating the field to a non-NULL value without increasing the size of the cell payload.

| Serial TypeCode | Database Data Type Name | Content Size (bytes) | Description
| :------------- | :-------------: |:-------------: | :------------- |
|0x00|NULL|2|Value is a 1-byte NULL (used for NULL TINYINT)|
|0x01|NULL|2|Value is a 2-byte NULL (used for NULL SMALLINT)|
|0x02|NULL|4|Value is a 4-byte NULL (used for NULL INT or REAL)|
|0x03|NULL|8|Value is a 8-byte NULL (used for NULL DOUBLE, DATETIME, or DATE|
|0x04|TINYINT|1|Value is a big-endian 1-byte twos-complement integer.|
|0x05|SMALLINT|2|Value is a big-endian 2-byte twos-complement integer.|
|0x06|INT|4|Value is a big-endian 4-byte twos-complement integer.|
|0x07|BIGINT|8|Value is an big-endian 8-byte twos-complement integer.|
|0x08|REAL|4|A big-endian single precision IEEE 754 floating point number|
|0x09|DOUBLE|8|A big-endian double precision IEEE 754 floating point number|
|0x0A|DATETIME|8|A big-endian unsigned LONG integer that represents the specified number of milliseconds since the standard base time known as "the epoch”. It should display as a formatted string string:  YYYY-MM-DD_hh:mm:ss, e.g. 2016-03-23_13:52:23.|
|0x0B|DATE|8|A datetime whose time component is 00:00:00, but does not display.|
|0x0C + n|TEXT||Value is a string in ASCI encoding (range 0x00-0x7F) of length n. For the purposes of this database you may consider that the empty string is a NULL value, i.e. empty strings do not exist. The null terminator is not stored.|

---

## Caveats, Quirks, Comments

There are still some minor issues, mostly UI/UX with the system, but it should accomplish most,
if not all, the tasks assigned in the document.

* For example Parsing was greatly improved from my first version, but please try commands with different spacing if receiving errors. For example, constraint operators (<, <=, =, etc.) must have a space on both sides.

* After 50+ hours spent on this project I feel that I have a much better understanding of database systems.

* With that large amount of time spent, if you find any issues or concerns that I can be of assistance with
decoding, please reach out (pxd170130). I'd love to explain any decisions I made in the code.

* See screenshots to troubleshoot syntax if issues arise.

---
