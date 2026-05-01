# Ideas

This file is a lightweight parking lot for ideas that are not yet ready to become OpenSpec changes.
Use it for questions, possible future directions, design sketches, and product thoughts that need more exploration.

When an idea becomes concrete enough to describe as a bounded change, promote it into `openspec/changes/<change-name>/` with a proposal, design, specs, and tasks.
Until then, ideas here are notes only and do not represent current system behavior or committed future work.

## Candidate ideas

### Cosmetic

- webapp:
  - /opsx-propose: cosmetic fix to webapp: combine the three download buttons into one, with a choices drop down.  Make download as json the default.  
  - /opsx-propose: cosmetic fix to webapp: make the status line the same colour as the menu bar, add appropriate spaces between the labels
  - /opsx-propose: cosmetic fix to webapp: provide a toggle on the status bar to switch to dark mode.
  - /opsx-propose: cosmetic fix to webapp: change 'Account' label on the menu bar to be the username logged in
  - /opsx-propose: cosmetic fix to webapp: improve the login dialog, use the space better.  Also, there are redundant lobels saying "Login", remove the clutter 
  - /opsx-propose: cosmetic fix to webapp: after logging in, place the focus on the selection table
  - /opsx-propose: cosmetic fix to webapp: on the selection table, allow space to be used to toggle the selection.  Ensure that the up/down + l/r arrows are useful/behave appropriately 

### New features


- /opsx-propose: next, we need to think about mapping the logical identifiers in the command/audit tables to the physical tables.  
 
  ```
  IF NOT EXISTS (SELECT 1 FROM sys.schemas WHERE name = '_util')
      EXEC('CREATE SCHEMA _util');
  
  IF OBJECT_ID(N'_util.LogicalTypeTableMapping', N'U') IS NULL
  BEGIN
      CREATE TABLE _util.LogicalTypeTableMapping (
          logicalTypeName NVARCHAR(255) NULL,
          qualifiedName NVARCHAR(255) NOT NULL    -- qualified table name
      );
  END;
  ```
  a logical type typically maps to a single table, but there could be multiple rows, eg if there's inheritance with NEW_TABLE defined for both super and subclass tables.

  Extend the fixtures to set up these tables and to populate them with appropriate data.  


- /opsx-propose: extend the library so that it can perform comparisons multi-threaded, each table in its own thread, rather than one at a time.  To support this, there will (I imagine) need to be a connection pool / DataSource.  The size of this pool should be specified as a cli argument, or read from a config property
- /opsx-propose: extend the library so that it can provide progress, and update the CLI to use this, by printing out as each table is compared.  I would imagine that the library will allow a callback to be registered, and the CLI registers an appropriate implementation.
- /opsx-propose: extend the webapp so that it can provide feedback to the user as the comparison progresses.  I would imagine the webapp could register a listener, and then use a Vaadin capability to show progress in the status bar
- /opsx-propose: Add a new output format of yaml.  Support this in the CLI, and in the webapp provide a new download button to download as YAML also.
- /opsx-propose: provide an "IgnoreColumnAdvisor" SPI for the responsibility fo determining if a column should be ignored in the comparison.  This SPI should be in the -api module.  Have the core library take a @Inject'd List<IgnoreColumnAdvisor>, so that more than one implementation can be consulted.  Rework the current implementations as separate services/beans, called something like IgnoreColumnAdvisorForIdentityColumns, and IgnoreColumnAdvisorForUuidColumns, and IgnoreColumnAdvisorForTimestamps.  For each of these implementations, add a config property (using @ConfigurationProperties so is typesafe) that can enable/disable the advisor, defaulting to eanabled.  
- /opsx-propose: add a new IgnoreColumnAdvisorUsingExtendedProperties, have it check for the presence of an "sqlcomparer.ignored" metadata attribute set to a truthy value (as per sp_extendedproperty); if so then ignore the property. 
- auto select from command/audit
  - drop down from/to of commands
  - query the audits
  - from audit targets infer entities
  - map entities to tables
- /opsx-propose: use spring native to compile the cli into an exe.  Put this behind a profile, only built on demand (I believe it can take some time to run).  Update README to explain how to build, using this profile.  Update README to explain how to run (instead of using that shell script).
- create a docker image out of the webapp.

### Programming Style

- /opsx-propose: followed preferred programming style: use @ConfigurationProperties for all config properties, so that these are typesafe.
- /opsx-propose: followed preferred programming style: don't hardcode package names, as we see in '@SpringBootApplication(scanBasePackages = "com.danhaywood.sqlcomparer.cli")'.  Instead, use an appropriate class (create one if necessary) and use the .class literal.
- /opsx-propose: followed preferred programming style: clean up HomePageConnectionStatusPlaywrightSuccessTest, externalize DDL into a resources file.   Do this elsewhere if necessary.
- /opsx-propose: followed preferred programming style: convert PlaywrightSqlServerFixture to be a regular service/bean, don't use static methods
- /opsx-propose: refactor the playwright tests to use page objects.  Put the page objects into their own maven module, update maven module dependencies accordingly.
- /opsx-propose: followed preferred programming style: use lombok @Getter, @Setter and @RequiredArgsConstructor to remove boilerplate.  Don't use other lombok conventions, though.
