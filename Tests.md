**Test 1:** User log in

Steps:

    User launches application.
    User clicks OK on the welcome page.
    User chooses either customer or seller.
    User enters an existing username and password into login page.
    User selects the "Log in" button. 

Expected result: Application verifies the user's username and password and loads their homepage automatically. 

Test Status: Passed. 


**Test 2:** Failed log in

Steps:

    User launches application.
    User clicks OK on the welcome page.
    User chooses either customer or seller.
    User enters a username and password that doesn't exist yet into login page.
    User selects the "Log in" button. 

Expected result: Application prompts the user to create a new account or try logging in again.

    User selects create a new account.

Expected result: Application creates a new account with the previously entered username and password and loads the homepage automatically.

Test Status: Passed. 


**Test 3:** Viewing stores, adding a product to cart, view cart, and purchasing

Steps:

    (User's already logged in as a customer and the homepage is currently displayed)
    User selects "1. view store" from homepage.
    User selects the store "Midnights" from dropdown menu, then the product "Antihero", and chooses "ok" to add it to their cart.

Expected result: Application adds the product "Antihero" to the user's cart.

    User selects "5. view cart"
    
Expected result: GUI pop up of customer's shopping cart with "Antihero from store Midnights for 10.00: Everybody is a sexy baby" is displayed.

    User selects "3. purchase"

Expected result: Application purchases the item, removes it from user's cart, and adds the item to their buy history.

Test Status: Passed. 

**Test 4:** Removing item from shopping cart (with item in cart)

Steps:

    (User is already logged in as a customer, and homepage is displayed, and they have "Antihero" and "Betty" in cart)
    User selects "4. edit cart" from homepage.
    User selects "AntiHero" from dropdown and clicks ok
    
Expected result: If user selects "4. edit cart" again, Antihero will be not show up on the GUI pop up

**Test 5:** Removing item from shopping cart (with an empty cart)

Steps: 

    (User is already logged in a customer, and homepage is displayed, and they have nothing in their cart)
    User selects "4. edit cart" from homepage.
    
Expected result: GUI pop up with the message "your cart is empty!"

**Test 6:** Export to csv for customer 

Steps:
    
    (User is already logged in as a customer, and homepage is displayed, and they have already bought Antihero)
    User selects "7. export buy history to csv file"
    User enters file name that they want to export to {ex, buyhistoryfile}
    
 Expected result: If the user clicks on their file (ex. buyhistoryfile), they should see Antihero-Midnights

 **Test 7:** Export to csv for customer with no purchase history
 
 Steps:
    (User is already logged in as a customer, and homepage is displayed, and they have bought nothing from the website)
    User selects "7. export buy history to csv file"
    User eneters file name that they want to export to {ex, buyhistoryfile}

Expected result: GUI pop up with error message "There was a problem!"


**Test 8:** Viewing statistics for customer

Steps:

    (User is already logged in as a customer, and is on the homepage)
    User selects "6. view statistics" from the dropdown menu on the homepage.
    User chooses either option of what statistics they want to see.
    

Expected result: GUI pops up with the selected dashboard displayed and a drop down menu of sorting options. If the dashboard is empty, an error message will pop up instead. 

Test Status: Passed.

**Test 9:** Viewing statistics for seller

Steps:

    (User is already logged in as a seller, and is on the homepage)
    User selects "5. view statistics" from the dropdown menu on the homepage.
    User selects one of their stores (if the user has no stores an error message will be displayed)
    User selects either option of what statistics they want to see.
    

Expected result: GUI pops up with the selected dashboard displayed and a drop down menu of sorting options. If the dashboard is empty, an error message will pop up instead. 

Test Status: Passed.

**Test 10:** Sorting statistics

Steps:

    (User is already logged in as either a customer or a seller, and is viewing a list of statistics).
    User selects one of the sorting options from the dropdown menu (except for "Done").
    

Expected result: The exact same GUI will pop up, except the dashboard will be sorted to whatever the user selected. 

Test Status: Passed.

**Test 11:** Search for a product for customer
Steps:

    (User is already logged in as either a customer).
    User selects "2. search" from the dropdown menu on the homepage.
    User types in a word/phrase (for testing purposes the search is "the")
    User enters search
    

Expected result: A GUI with a drop menu of products that contain "the" 

Test Status: Passed.

**Test 12:** Search with blank input for customer
Steps:

    (User is already logged in as either a customer).
    User selects "2. search" from the dropdown menu on the homepage.
    User types in a word/phrase (for testing purposes the search is "")
    User enters search
    

Expected result: A GUI with a drop menu of all products

Test Status: Passed.

**Test 13:** Log out feature for customer

**Test 14:** Delete account for customer
    
