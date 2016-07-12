## Java Kata ##

This repository contains an exercise in writing Java code to load and query the contents of two data files into in-memory data structures and efficiently query the data. It also contains an exercise in documenting the requirements, approach, design considerations, and assumptions.

- [Stated Requirements](#stated-requirements)
  * [High-Level](#high-level)
  * [Functional Requirements](#functional-requirements)
    + [Input File: Organizational Hierarchy](#input-file-organizational-hierarchy)
    + [Input File: User Data](#input-file-user-data)
    + [Output File: Report](#output-file-report)
    + [Internal API Contract](#internal-api-contract)
  * [Non-Functional Requirements](#non-functional-requirements)
- [Solution Approach](#solution-approach)
- [Design Considerations / Implementation Rationale](#design-considerations--implementation-rationale)
- [Assumptions](#assumptions)
- [Build / Usage](#build--usage)
  * [Vagrant Machine](#vagrant-machine)

## Stated Requirements ##

### High-Level ###

Create a tool, using Java, that reads two files into memory and produce summary statistics. One file consists of an organizational hierarchy (ie. a [K-ary tree](https://en.wikipedia.org/wiki/K-ary_tree), or [Directed Acrylic Graph (DAG)](https://en.wikipedia.org/wiki/Directed_acyclic_graph) with in-bound edges limited to one) and the other file consists of user data that will be associated to the organizational hierarchy.  

### Functional Requirements ###

#### Input File: Organizational Hierarchy ####
The organizational hierarchy file will consist of records with the following format:

    orgId, parentOrgId, name

#####Example Input:#####

	1, null, Foo
	2, 1, Bar
	4, 3, Baz
	5, 2, Qux
	3, null, Xyzzy

Each record represents one organization in the organizational hierarchy.  The `orgId` field represents the unique identifier for each organization in the hierarchy.  The `parentOrgId` field contains the unique identifier of the parent organization in the hierarchy. If `parentOrgId` is null, the organization should be treated as a root organization.  The `name` field contains the name for the organization.

#### Input File: User Data ####

The user data file will consist of records with the following format:

    userId, orgId, numFiles, numBytes

#####Example Input:#####

	1, 1, 10, 200

Each record represents one user with associated data.  The `userId` field represent the unique identifier for each user. The `orgId` represents the organization in the hierarchy that the user is a member of.  The `numFiles` and `numBytes` fields represent the number of files associated with the user and the number of bytes contained in those files, respectively.

#### Output File: Report ####

The output file is expected to be a plain text file. Each line should represent a single organization, including its unique identifier and summary statistics.  Each line should be indented to indicate its place in the hierarchy/tree structure.  The order of the lines should be in "tree order" (see Assumptions below).

#####Output Format:#####

    orgId, totalNumUsers, totalNumFiles, totalNumBytes

#####Example Output (see Assumptions below)#####

	- 1, 1, 10, 200
      - 2, 0, 0, 0
        - 5, 0, 0, 0
    - 3, 0, 0, 0
      - 4, 0, 0, 0       

#### Internal API Contract ####

    - OrgCollection
      - Org getOrg(orgId)
      // return list of orgs below this node in the tree;
      // inclusive = include orgId within list
      - List<Org> getOrgTree(orgId, boolean inclusive)
    - Org
      - int getTotalNumUsers() // return total number of users in this org and any children
      - int getTotalNumFiles() // return total number of files in this org and any children
      - int getTotalNumBytes() // return total number of bytes in this org and any children
      - List<Org> getChildOrgs() // return the immediate children of the org

### Non-Functional Requirements ###

- No dependencies other than the JVM (although a test framework such as JUnit or TestNG is permitted)
- Source code must be available.
- Must implement a few executable tests.
- Must provide directions on:
	- How to compile and run tests
	- How to create & execute a test of the tool with new data files
- Document the thought process:
	- Were any assumptions made and what are they?
	- What algorithms were used, why, and what alternatives are there?
	- If a data file with 500 million rows were provided, what changes would you make?
		- Where/how would the code likely break given this amount of data? 

## Solution Approach ##

- The console application will require two command line arguments containing the location of the data files.  Failure to provide these arguments will result in an error being written to `stderr`.  If either of the files specified in the arguments do not exist, an error will be written to `stderr`.

- Organization Hierarchy data files will be processed one record at a time into [POJOs](https://en.wikipedia.org/wiki/Plain_Old_Java_Object) before further processing to make sure the input data is valid.  Any lines that are not able to be parsed will be ignored and an error will be written to `stderr`.

- The organizational hierarchy will be loaded one organization at a time, in the order provided by the file, into a simple Org POJO.

	- The POJO will maintain a LinkedList of child organizations that can be added to as the file is being loaded, essentially forming a top-down [K-ary tree](https://en.wikipedia.org/wiki/K-ary_tree) structure. This will be useful for supporting query use cases such as summing the total number of users under an organization recursively by traversing the structure to compute totals. Since all children (N) need to be visited to compute totals, ordering is unimportant because [addition (summation) is a commutative operation](https://en.wikipedia.org/wiki/Addition), and complete iteration of a [LinkedList is has O(N) time-complexity](https://en.wikipedia.org/wiki/Linked_list), this is a sufficient data structure.

	- The POJO instance will be recorded into a HashMap by its unique identifier for quick retrieval by its identifier [[average time complexity of O(1) and worst-case space-complexity of O(n)](https://en.wikipedia.org/wiki/Hash_table)].

	- If the POJO contains a non-null `parentOrgId`, the HashMap will be used to lookup the parent organization and add the current organization to its list of children. If the parent organization cannot be found at the moment the record is being processed, it will be stored in a temporary [MultiMap](https://en.wikipedia.org/wiki/Multimap) (constructed using a HashMap and Collection) to be processed when the parent organization becomes available.

	- If the POJO contains a null `parentOrgId`, the POJO will be recorded into a LinkedList. This will support iteration of all root organizations (and their children by recursive iteration) for global summary statistics.

- The user data will be loaded one record at a time, in the order provided by the file, into a local stack variables and then pushed into the appropriate organization.

	- The POJO's `orgId` will be used to lookup the associated organization and roll-up the user's data into the Org POJO.

- Statistical output will by default be written to `stdout`. To meet the requirement of writing output to a file, the output can easily be piped to a file.  This also enables the tools output to be piped into other commands and is useful during debugging & ad-hoc analysis sessions. 

## Design Considerations / Implementation Rationale ##

- Although a console application was selected, an web application or web API could have been implemented to expose the same functionality.  Given the requirements do not stipulate certain usage patterns, a console application was selected due to it being a simpler approach.  The code can and should be written to allow the primary processing logic to be lifted out of the console application and into a web-accessible code base with little effort by not tying the core processing and reporting to console specific APIs (eg. `println` calls against `stdout` and `stderr`, direct calls to local file system, etc).

- APIs are generally more usable and maintainable when their inputs are as least restrictive as possible and return values are as specific (or derived) as feasible. An example in this exercise would be the `DataLoader` class. Rather than having `load` method overloads that exclusively take only file names or file streams as input arguments, the primary overload of the `load` method takes iterators of text lines as the expected input argument types.  Only the console application makes the input more restrictive to requiring file names.  This will make it simple to write unit tests without having to involve the file system and allow the logic to later be used where files are not the means of input without having to heavily modify the code.  Convenience, or helper, overloads that convert file-specific arguments into iterators can (and have) be provided. 

- All processing warnings and errors will be written to `stderr` rather than `stdout`.  By default `stderr` is usually piped to a console for viewing during debugging & ad-hoc analysis sessions, but this will also allow warnings and errors to be monitored by other tools more robustly and not impact downstream processing of successfully computed statistics by other tools.

- Implied in the *example* organization hierarchy input data file is the requirement to allow parent organizations to be referenced before they are defined (eg not in "tree order" [see Assumptions below]). However, this will require additional memory and processing time to resolve these references as the data file is being loaded.  If possible, upstream systems should try to or be required to order input data in "tree order" so as to eliminate this additional processing overhead.

- Parsing user data into POJOs is unnecessary since there are no requirements that require the user data to be maintained. However, the data should still be parsed into typed variables to support input validation and further processing by the Org POJO.

	It could be argued that it may be better to parse user data them into a POJO for consistency sake and to avoid committing the ["Data Clump" code smell](https://sourcemaking.com/refactoring/smells/data-clumps). However, this would create a potentially high number of POJO objects that would need to be collected by the garbage collector almost immediately after their construction.  Memory allocation and collection can often become a serious bottleneck to performing data processing as quickly and efficiently as possible.  Since the code complexity cost of parsing into local stack variables is low and the "Data Clump" smell would be fairly localized, a User POJO will not be created.

- When adding users and child organizations to their parent organization, summary statistics could have been pre-calculated and stored in memory along side the other organization data.  This would increase overall space complexity and increase time complexity during the write operations, with the trade-off being that read operations time complexity would improve.   
	
	When designing information retreival systems, use cases need to be analyzed to see if they are write-heavy, read-heavy, or well balanced.  Many information retreival systems are read-heavy, so additional processing time can be spent during writes to pre-construct structures and pre-compute values that will make reads more performant in future workloads.  This can be referred to as "workload tuning" and often involves space & time complexity tradeoffs.

- When recursively traversing the organizational hierarchy, if the hierarchy is deep enough it could cause a [stack overflow](https://en.wikipedia.org/wiki/Stack_overflow), caused by a limited amount of address space [memory] available for the call stack (see Asumptions below).  

	An alternative to using traditional recursive methods is to use some form of iteration (eg. `while` or `for` loop) while maintaining an external stack and some running state to build up results. This can enable the code to handle larger volumes of data, but can sometimes make the code lengthier, as well as more difficult to read, understand, debug, and modify. This could also increase space-complexity since more memory could be consumed by maintaining an external stack.

	Another alternative is to use a language that has a compiler with [tail recursion](https://en.wikipedia.org/wiki/Tail_call) optimization and write recursive methods that only use tail recursion. The compiler will emit code that is not succeptible to stack overflows if the source code only uses tail recursion.

- Although the non-functional requirements specify that 3rd-party libraries are not permitted, the functional requirements lend itself to the usage of an embedded key-value database.  Examples include LevelDb, LMDB, and RocksDb.  Usage of an embedded key-value database would not excessively complicate the application since it would require no additional out-of-process services to be available.  

	Additionally, it would permit much larger datasets to be handled by this tool, since the embedded databases handle the responsibility of swapping data pages between memory and disk and maintaining any retrieval structures (eg. trees, hashtables, bloom filters).  If it is expected that data files exceeding available memory would need to be processed by this tool, alternative data structures would preferred to quickly and efficiently page data back and forth between memory and disk while still allowing for efficient queries. Examples of data structures that would provide this capability: B-tree, B+tree, LSM tree, etc...

- Immutable, persistent data structures could be used to allow online copy-on-write mutations and concurrent queries.  However, nothing in the requirements stipulates the need for online mutations. Given the requirements, the only way to mutate input data is to alter files and provide it to the tool for another full processing pass.  Additionally, since there aren't motivating requirements, such data structures would add unnecessary code complexity to the code base.

- Not all classes have direct test fixtures (eg. `TextLineIterable`). Utility code like this should generally be heavily tested. However, since this is an exercise and the utility code will be indirectly tested by its usage in other executable tests, direct test fixtures were not created.

- Not all classes have rigorous input / output testing (eg. null/negative arguments into methods tested).  Some, but not all classes have these kinds of tests; since this is an exercise, these types of tests are implemented for demonstrative purposes.

- Rather than using a `String` for the observation type in `Result<T, TObservation>` when loading the data files, a more structured type could be implemented.  This would allow calling code to distinguish from errors and warnings, or potentially other types of observations.  Given the requirements, this approach was not pursued, but represents a potential enhancement.

- `LinkedList` was chose over `ArrayList` or `Vector` because the use cases benefited more from the insertion time-complexity of O(1). The operation where `ArrayList`/`Vector` would beat `LinkedList`, random access by index at O(1) time complexity, was not needed to support the use cases in the requirements.  Additionally, `ArrayList` and `Vector` have a worst-case space complexity of O(2 * N), which is less ideal than `LinkedList`'s O(N) space complexity.

- `IllegalArgumentException` is used heavily, whereas some custom exception types may be warranted (eg. `DuplicateDataException`, `InvalidDataException`, `CorruptDataException`, etc).


## Assumptions ##

- Source code can be made available by publishing a GitHub repository and sharing it's location.

- A simple console application that takes command line arguments is sufficient to meet requirements.

- As part of the exercise, "minor" stated technical requirements can be bent to demonstrate ability to consider possible alternatives and communicate potential problems with initial requirements. Examples:

	- Org API was changed to include an accessor `getId()` to return the organization's unique identifier.

	- Org API was changed to return a `long` from `getTotalNumBytes()` because of the increased likelihood of integer overflow given organizations containing users with large number of bytes stored in files.
	
	- OrgCollection API was changed to include a method `getRootOrgs` to return a list of all root organizations for reporting purposes.

- 3rd-party libraries cannot be used for implementation code. However, 3rd-party libraries & tools can be used build, testing, packaging, and deployment activities.

- Data file records are newline separated and record fields are comma separated. Whitespace before and after field values can be truncated.  Windows-style line endings should be permitted (carriage return + line feed).

- All numbers represented in data files are presumed to be non-negative numbers, including identifiers and number of files/bytes. Identifiers are further assumed to be greater than or equal to one (1). Any lines containing numbers breaking this invariant will be ignored and an error will be written to `stderr`.

- Organizations in input files are not guaranteed to be provided in top-down fashion (ie. parent organizations occur before their children in the file).  If organization records contain references to parent organizations that do not exist anywhere in the data file, those records will not show up in the summary statistics report, but will be accessible for `OrgCollection` lookup methods.

- Organization names are assumed to contain only alphanumeric and space characters. If data files are provided that violate this expectation, the offending lines will ignored and logged to `stderr`.

- Organization names are not queried or consumed in any of the requirements. During the loading phase, they will be validated, but not stored in the Org POJO.

- Organization Hierarchy files will not be so deep as to cause a stack overflow when using recursion to walk the structure. 

- Organization data files contain unique identifiers. However, if this is not the case, any records containing duplicate identifiers will be ignored and an error will be logged to `stderr`.

- The total number of users or files in an organization hierarchy will not exceed the upper-bound of what a signed 32-bit integer can represent (ie. `int` in Java code).     

- User data will not reference organization identifiers that do not exist in the provided Organizational Hierarchy data. If this happens, the offending user line will be ignored and an error logged to `stderr`.

- User data files contains *presumably* unique identifiers for users, although there are no requirements to "lookup" users by their identifier or report user level details.  The identifier will be parsed to ensure that it is numeric data, however, the uniqueness of the identifiers will not validated.

- Users are permitted to have zero files with zero bytes or a non-zero number of files with zero bytes, but cannot have zero files with a non-zero number of bytes. Breaking this invariant will cause the user data to be ignored and an error being written to `stderr`.

- Multiple organizations in the hierarchy file can serve as root organizations (i.e. organizations that have no parent)

- Output of stats can be written to the `stdout` and still meet requirements, since `stdout` output can be piped to a file.  

- Output indentation character(s) is not specified in requirements.  Two spaces followed by a dash and a space for each indentation will be used.  This is still easily machine readable, but also makes human consumption easier.

- Outputting in "tree order" is assumed to be [depth-first, pre-order](https://en.wikipedia.org/wiki/Tree_traversal). The reason for this assumption is it would yield text output that visually would look like the tree it is projected from and enable easy stack-oriented traversal by downstream machine processing.

- Workload is expected to be somewhat evenly weighted between read and write operations; ie. data will be loaded into memory, processed, written to a file, and then unloaded (application will exit).

- Data files will not contain large numbers of errors. If that were not the case, the Result<T, TObservation> class used when loading the data files could end up holding large numbers of errors in memory. To address this, rather than a pull-style API, a push-style API could be implemented to allow errors to be pushed to the caller (eg. [callback](https://en.wikipedia.org/wiki/Callback_(computer_programming))) as they occurred rather than be collected in memory.

- It is assumed that all data can fit within available memory.  


## Build / Usage ##

In order to build and run this project you need to have the following prerequisites installed (tested on `Ubuntu x64 14.04` patched as of `2016-07-07`):

- Java 8 OpenJDK (tested with version `1.8.0_91`)
- Gradle 2.13

Clone this repository into a directory on your local machine. Navigate into the new project directory and run the `run-example.sh` script found at the root of the repository.  This should compile, test, and run the `javakata` program with the example input mentioned in the requirements section.  You should see output similar (if not identical) to the example output mentioned in the requirements section.

You can also run the `build.sh` script directly (it is called by the `run-example.sh` script) found at the root to build and test the `javakata` program without running the example input files through the program.

Both scripts will create an executable at the `build/javakata.run` path.  You can run this directly by providing two arguments indicating the location of the org & user data files. For example, running from the repository root, the following command with execute the program with the provided example input:

	build/javakata.run testfiles/example-org-file testfiles/example-user-file

If you would like to save all output to a file:

	build/javakata.run testfiles/example-org-file testfiles/example-user-file > output.txt 2>&1

If you would like to save all output to files, separating normal and error output to separate files:

	build/javakata.run testfiles/example-org-file testfiles/example-user-file 2>> error.txt 1>> output.txt

These commands can be used as templates to process different files, eg. there are larger test files in the `testfiles` directory.

### Vagrant Machine ###

A [Vagrant](https://www.vagrantup.com/) machine has been included in the repository with an environment containing the prerequisites already installed.  To make use of the Vagrant machine, make sure the following prerequisites are installed:

- Vagrant 1.8.4
- Virtual Box 5.0.24
	- NOTE: Virtual Box is known to have difficulties running at the same time Hyper-V is installed (on Windows) or when Parallels is running on OS X.

At the root of the repository, type `vagrant up`. This will bring up the Vagrant machine. NOTE: This may take a few minutes, especially the first time.  Once the command completes, type `vagrant ssh`. This will connect your terminal to the running Vagrant machine.  Navigate to the `/vagrant` directory and now you will be in the root of the project repository.  Use the instructions above to build, test, and run the `javakata` program.  Any changes made to the source code (on the host machine or in the Vagrant machine) can be re-compiled and run from the VM.

	vagrant up
	# wait a few minutes
	vagrant ssh
	cd /vagrant
	./run-example.sh
	# javakata program will be compiled, tests run, and example input files processed

## License ##

Copyright © 2016, [Jordan Terrell](https://github.com/iSynaptic).
Released under the [MIT license](https://github.com/iSynaptic/java-kata/blob/master/LICENSE).