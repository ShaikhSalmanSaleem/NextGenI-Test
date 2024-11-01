Feature: Amazon Shopping Cart Validation

  Scenario: Search, Add to Cart, and Validate Product Price
    Given I navigate to "https://www.amazon.com"
    When I search for "toys"
    And I select two random products and add them to the cart
    Then I validate the price on the Search Results page, Product Details page, and Total Summary page