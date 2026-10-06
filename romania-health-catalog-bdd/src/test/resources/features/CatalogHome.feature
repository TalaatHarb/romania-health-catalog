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
    
  @Home @Search @Drugs
  Scenario: Search for a drug
    Given I am on Catalog 'Home' page
    When I search for 'NUTRIFLEX'
    Then I confirm at least 1 result
    And I can open drug search result