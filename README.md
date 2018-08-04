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

## Supported DDL and DML commands

```
SHOW TABLES; // Displays a list of all TABLES

CREATE TABLE; //Creates a new table schema, i.e. a new empty table

DROP TABLE _tableName_; // Remove a table schema and all of its contained database

INSERT INTO _tableName_ [_columnList_] VALUES _valueList_; // Inserts a single record into a table

DELETE FROM _tableName_ [WHERE _condition_]; // Deletes one or more records from a a table.

UPDATE _tableName_ SET _columnName_ = _value_ [WHERE condition]; // Modifies one or more records in a table.

EXIT; // Cleanly exits the program and saves all table information in non-volatile files to disk.
```

## Querying

BeaverBaseSQL supports SELECT-FROM-WHERE” style queries.

```

```

## Extra Features Implemented

- Delete was implemented
- All constraint based commands can use any logical operator (<, <=, =, !=, >, >=)

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
