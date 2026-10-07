#Author: talaatharb
Feature: Romania Home page scenarios

  @Home @Versions
  Scenario: Available versions
    Given I am on Catalog 'Home' page
    When Looking into the list of available versions
    Then I find the list of available versions

  @Home @Import
  Scenario: Import new version
    Given I am on Catalog 'Home' page
    When I import new catalog with issue date '2024 - 3'
    Then I confirm '2024 - 3' gets added to the top of the list of available catalogs

  @Home @Import @UploadSecret
  Scenario: Import is rejected without the right upload secret
    Given I am on Catalog 'Home' page
    When I try to import catalog with issue date '2024 - 3' using upload secret 'WRONG_SECRET'
    Then I see the upload error 'Missing or invalid upload secret'
    
  @Home @Search @Drugs
  Scenario: Search for a drug
    Given I am on Catalog 'Home' page
    And catalog version '2024 - 3' is available
    When I search for 'NUTRIFLEX'
    Then I confirm at least 1 result
    And I can open drug search result

  @Home @Search @Drugs
  Scenario: Search for a drug by choosing drugs explicitly
    Given I am on Catalog 'Home' page
    And catalog version '2024 - 3' is available
    When I search for 'NUTRIFLEX' in 'Drugs'
    Then I confirm at least 1 result
    And I can open drug search result

  @Home @Search @SearchTypes
  Scenario Outline: All catalog objects can be searched
    Given I am on Catalog 'Home' page
    And catalog version '2024 - 3' is available
    Then I can choose to search in '<type>'

    Examples:
      | type                |
      | Drugs               |
      | Cities              |
      | Streets             |
      | Districts           |
      | Physicians          |
      | Countries           |
      | Specialities        |
      | Insurance houses    |
      | ICD-10 diagnostics  |
      | Holidays            |

  @Home @Search @Items
  Scenario Outline: Search for <type> in the catalog
    Given I am on Catalog 'Home' page
    And catalog version '2024 - 3' is available
    When I search for '<searchTerm>' in '<type>'
    Then I confirm at least 1 result
    And I can open search result showing '<searchTerm>'

    Examples:
      | type                | searchTerm       |
      | Cities              | Rupea            |
      | Streets             | Posada           |
      | Districts           | ARAD             |
      | Physicians          | COSTEA           |
      | Countries           | GEORGIA          |
      | Specialities        | ORTODONTIE       |
      | Insurance houses    | Vrancea          |
      | ICD-10 diagnostics  | Hipoparatiroidia |
      | Holidays            | Craciun          |
