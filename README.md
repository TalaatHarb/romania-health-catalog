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

## Implementation

### Modules
| Module | Stack | Purpose |
| --- | --- | --- |
| `romania-health-catalog-be` | Java 25, Spring Boot 4, Spring Data JPA, H2 (file), MapStruct | REST API, XML parsing and persistence |
| `romania-health-catalog-fe` | React 18, TypeScript, Vite, Bootstrap 5, Jest | Single page application |
| `romania-health-catalog-bdd` | Cucumber, Selenium, JUnit 5 | End-to-end acceptance tests |

### Every catalog object is stored and searchable
Uploading a catalog persists **all** object types found in the XML, not only drugs: countries, districts, cities,
streets, physicians, specialities, insurance houses, health departments, active substances, ATC codes, ICD-10
diagnostics, NHP programs, co-payment lists, holidays, business rules and more (see `CatalogItemType`).

In the UI, next to the version selector, the **Search in** dropdown lets you choose what to search. Each entry shows
how many objects of that type the selected version contains; empty types are disabled. Results are paginated and
selecting one shows all of its properties.

### Design decisions
- **Drugs keep a dedicated table** (`DrugEntity`) and endpoints because they have rich, typed details (prices, flags,
  validity) and existing consumers.
- **All other objects use one generic table** (`CatalogItemEntity`: type, code, name and a JSON `details` column).
  The `CatalogItemType` enum is the single registry that maps each XML collection to a label and to the properties
  used as code and name. Supporting a new XML type takes one enum constant, with no new entity, repository or
  endpoint.
- Re-uploading a version replaces its generic items (inside a single transaction) instead of duplicating them.
  Inserts are JDBC-batched (`hibernate.jdbc.batch_size`).
- Searches are case-insensitive and match the name **or** the code.
- Drug search is backed by the composite index `idx_drug_search (version_id, valid_to, name, code)`. A `LIKE '%term%'`
  can't seek into a B-tree, but the index restricts the scan to the active drugs of one version and evaluates the
  match on compact index entries instead of full rows. With ~870k drugs this takes a search from ~3.6s to ~0.3s.
  Very broad terms (e.g. a single letter) still scan all active drugs of the version. Hibernate (`ddl-auto: update`)
  creates the index automatically on existing databases.
- Item search is backed by `idx_catalog_item_search (version_id, type, name, code)`. The search query orders by the
  constant `version_id, type` before the requested `name` sort, so pages are read in index order instead of sorting
  every item of the type (first page of a 364k-item type: ~22s → ~0.4s). The superseded
  `idx_catalog_item_version_type` index is dropped at startup (`SchemaMaintenance`), since Hibernate never drops
  indexes and the database would keep choosing it.
- Search counts and the item-type counts use `count(*)`, which is answered from the indexes (a derived `count(id)` has
  to read every matching row). The contains pattern is built in Java (`SearchPatterns`) and bound as a single
  parameter, so the database doesn't rebuild it for every scanned row.

### REST API (base path `/backend/api/v1`)
| Method | Path | Description |
| --- | --- | --- |
| GET | `/versions` | Uploaded versions |
| POST | `/versions` | Upload a catalog (`multipart/form-data`, field `file`, `.xml` or `.zip`); requires the upload secret |
| GET | `/versions/{versionId}/drugs?searchTerm=&page=&size=&sort=` | Search active drugs by name or code |
| GET | `/drugs/{drugId}` | Drug details |
| GET | `/versions/{versionId}/item-types` | Searchable object types with their counts for a version |
| GET | `/versions/{versionId}/items?type=CITY&searchTerm=&page=&size=&sort=` | Search objects of a type by name or code |
| GET | `/items/{itemId}` | Object details |

OpenAPI docs: `http://localhost:8080/backend/swagger-ui/index.html`.

### Upload secret
Uploads are only accepted with the upload secret, sent as the `uploadSecret` header or the `uploadSecret` query
parameter. If both are sent, the header wins. A missing or wrong secret gets a `403` problem response
("Missing or invalid upload secret") and nothing is stored.

- The default secret is `UPLOAD_SECREET`. Override it with the `UPLOAD_SECRET` environment variable
  (property `health-catalog.upload-secret`). **Always override it in production.** An empty value disables uploads.
- The FE upload dialog has an "Upload secret" field pre-filled with the default. The real secret is never put
  into the FE bundle.

```shell
curl -H "uploadSecret: UPLOAD_SECREET" -F "file=@catalog.zip" http://localhost:8080/backend/api/v1/versions
```

### Running locally
```shell
# backend (http://localhost:8080/backend), H2 database stored in ./db
cd romania-health-catalog-be
mvn spring-boot:run

# frontend (http://localhost:5173)
cd romania-health-catalog-fe
npm install
npm run dev

# e2e tests (needs the backend, the frontend and Chrome)
cd romania-health-catalog-bdd
mvn test                                   # up to 4 browsers in parallel
mvn test "-Dbdd.parallelism=1"             # one scenario at a time
mvn test "-Dbrowser=firefox" "-Dsite.url=http://localhost:5173"
```

The search scenarios import the sample catalog themselves if it isn't available yet, so any scenario can run on
its own and in any order.
