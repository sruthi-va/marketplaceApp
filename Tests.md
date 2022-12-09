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


Test 3: Viewing, adding a product to cart, and purchasing

Steps:

    (User's already logged in as a customer and the homepage is currently displayed.)
    User selects "view store" from homepage.
    User selects the store "Midnights", then the product "Antihero", and chooses the option to add it to their cart.

Expected result: Application adds the product "Antihero" to the user's cart.

    User selects "purchase."

Expected result: Application purchases the item, removes it from user's cart, and adds the item to their buy history.

Test Status: Passed. 