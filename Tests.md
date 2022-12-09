Test 1: User log in

Steps:

    User launches application.
    User clicks OK on the welcome page.
    User chooses either customer or seller.
    User enters an existing username and password into login page.
    User selects the "Log in" button. 

Expected result: Application verifies the user's username and password and loads their homepage automatically. 

Test Status: Passed. 


Test 2: Failed log in

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


Test 3: Viewing stores, adding a product to cart, view cart, and purchasing

Steps:

    (User's already logged in as a customer and the homepage is currently displayed)
    User selects "1. view store" from homepage.
    User selects the store "Midnights", then the product "Antihero", and chooses the option to add it to their cart.

Expected result: Application adds the product "Antihero" to the user's cart.

    User selects "5. view cart"
    
Expected result: GUI pop up of customer's shopping cart with "Antihero from store Midnights for 10.00: Everybody is a sexy baby" is displayed.

    User selects "3. purchase"

Expected result: Application purchases the item, removes it from user's cart, and adds the item to their buy history.

Test Status: Passed. 

Test 4: Removing item from shopping cart (with item in cart)

Steps:

    (User is already logged in as a customer, and homepage is displayed, and they have "Antihero" and "Betty" in cart)
    User selects "4. edit cart" from homepage.
    User selects "AntiHero" from dropdown and clicks ok
    
Expected result: If user selects "4. edit cart" again, Antihero will be not show up on the GUI pop up

Test 5: Removing item from shopping cart (with an empty cart) **this is a bug need to fix**

Steps: 

    (User is already logged in a customer, and homepage is displayed, and they have nothing in their cart)
    User selects "4. edit cart" from homepage.
    
Expected result: GUI pop up with the message "your cart is empty!"

Test 6: Export to csv for customer **add condition if customer hasn't purchased anything**

Steps:
    
    (User is already logged in as a customer, and homepage is displayed, and they have already bought Antihero
    User selects "7. export buy history to csv file"
    User enters file name that they want to export to {ex, buyhistoryfile}
    
 Expected result: If the user clicks on their file (ex. buyhistoryfile), they should see Antihero-Midnights
    
