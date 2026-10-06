# romania-health-catalog
A way to browse the health catalog information published by Romania health authorities every month in a human readable way instead of XML

Goal: Practice acceptance-test-driven development on a realistic full-stack project — deliberately more than a CRUD app that AI can generate quickly.
The acceptance tests (Gherkin + Selenium + Cucumber) are written early and drive the implementation.

Background: The Romanian government publishes XML files containing large catalogs of information (see https://www.casmb.ro/casmb_nomenclatoare_siui.php (https://www.casmb.ro/casmb_nomenclatoare_siui.php)). Files are named like NomenclatoareFarmacii_xxxxxxxx.xml.zip. A sample file with a small amount of data is provided inside the BDD project for use in tests until you can work with the full official file.
Task: Given the contents of such a file, develop a full-stack application for exploring the information it contains. The application must at least allow:
- Uploading a file — the XML root element carries an attribute such as issueDate="2024-03-01T13:22:46" which identifies the XML file version.
- Listing already uploaded versions/files.
- Searching a specific version for information in a paginated way — at minimum, code and name/description must be shown as search results for drugs; you may also implement other object types present in the XML file.
- Viewing detailed information of an item (at minimum, drugs).
Technical constraints.
•	Database: in-memory/in-file database locally (H2, MapDB, …) or a free hosted database (PostgreSQL or any other, including NoSQL).
•	No restrictions on implementation technology.
•	Use Selenium and Cucumber to write e2e tests for the application; the e2e suite must upload the sample file (provided with the question, or created by trimming data from the official Romanian government file).
Guided details (expected architecture and practices).
•	Use DTOs for the APIs, mapped to database Entities — prefer automatic mapping (e.g. MapStruct, ModelMapper) over hand-written mappers.
•	Use a layered architecture: controllers call services/facades for business functionality, which access the database through repositories.
•	Ensure CORS is configured correctly so the frontend can call the backend without issues.
•	Apply best practices for the frontend, the backend, and the e2e tests alike.

Sample e2e Gherkin tests (starting point — extend as appropriate):
Feature: Romania Home page scenarios

  @Home @Versions
  Scenario: Available versions
    Given I am on Catalog 'Home' page
    When Looking into the list of available versions
    Then I find the list of available versions

  @Home @Import
  Scenario: Import new catalog
    Given I am on Catalog 'Home' page
    When I import new catalog with issue date '2024 - 3'
    Then I confirm '2024 - 3' gets added to the top of the list of available catalogs

  @Home @Search @Drugs
  Scenario: Search for a drug
    Given I am on Catalog 'Home' page
    When I search for 'NUTRIFLEX'
    Then I confirm at least 1 result
    And I can open drug search result

Deliverable: Public repository containing the full application (frontend + backend), the e2e test suite, and a README covering setup, database choice, architecture decisions, and how to run the app and the e2e suite.
