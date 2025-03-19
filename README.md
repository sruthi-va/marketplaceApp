# bAmazon.java

## Instructions on how to compile and run the project
Run server:
```
javac Product.java Store.java Seller.java Customer.java ShoppingCart.java Dashboard.java MyObjectOutputStream.java MarketPlace.java
java MarketPlace
```
Run client:
```
javac Client.java
java Client
```

## A detailed description of each class; includes the functionality included in the class, the testing done to verify it works properly, and its relationship to other classes in the project.
- MarketPlace.java
  - Is the server that processes inputs from the client, calling the methods from all other classes to combine their functionality. Extensive testing was performed by our team as each member tested all the actions that can be preformed by customer/seller, checking to see if it resulted in the right output, and making sure the marketplace could handle any mishaps such as a null answer received from the client.
- Client.java
  - Acts as the main interface that interacts with the user through the use of simple GUIs. Through easy to understand prompts, an user can easily navigate the marketplace and have access to roles unique to their identity as a customer or a seller. For instance, only customers can add products to their cart and buy it, while sellers can make stores or new products and view how well their store is doing by viewing statistics calculated by the dashboard. Extensive testing was performed by our team as each member tested the all the actions that can be preformed by customer/seller, checking to see if it displayed the right options.
- Customer.java
  - Holds all the methods for the customer like adding/removing items from their cart, purchasing items, viewing their buying history, export to csv, etc
- Dashboard.java
  - This class has the methods that sort and display information and statistics from product sales and customer logs. It includes methods that return ArrayLists of stores by number of products sold, stores by products bought by a particular customer, customers by number of products bought from a particular store, and products from a particular store by number of sales. There are also methods that given one of these ArrayLists, sorts it either alphabetically or by quantity. They can sort in either direction (ex. A-Z or Z-A). The MarketPlace class uses these methods to display these statistics to the terminal and gives the user the option to sort the statistics. The user will have different options for which statistics they can see depending on if they are a customer or seller. The Dashboard class methods have been tested extensively using a main method, and the implementation of Dashboard in MarketPlace has been extensively tested as well through running MarketPlace.
- MyObjectOutputStream.java
  - Created custom Object Output Stream so that it does not write headings when used repetedly.
- Product.java
  - Product object that holds product's name, description, price, store. Checks if other objects are equivalent to a current product.
- Seller.java
  - Has methods to process seller input, create/edit/delete stores/products, allows import/export to/from csv files. This class can list out all stores that a seller owns, and allows them to create, edit, or delete a store. This class also connects with the marketplace to update on which stores and products are available to buy.
- ShoppingCart.java
  - Has methods to add to, delete from, and modify customer shopping cart. It interacts with Customer.java and has been tested to make sure all its methods return the right outputs using the main method in ShoppingCart.java. It writes to a file shoppingcart.txt to make sure the data gets saved even when the server isn't running.
- Store.java
  - Store object that holds seller name and array of products in store. Allows seller to create or delete products within a specific store, and lists out all products that the store has.
