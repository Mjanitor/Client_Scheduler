- The title of the application is "Client Scheduler."  The purpose of this application is to allow for appointment scheduling for a list of clients that works through a connected database.  These appointments and customers can be added, removed, and modified to fit the needs of the business.

- The author of this application is Michael Janitor and I can be reached at MJanito@wgu.edu.  The version of this application is 1.1 and the current date is 6/19/2023.

- The IDE is IntelliJ IDEA 2022.3.1 (Community Edition).  The Java JDK is version Java SE 19.0.1.  The JavaFX version is JavaFX-SDK-19.

- Directions:  Assuming that a local database has been set up, first navigate to Helper/JDBC and populate the private variables with the name of your database, its location, and the username and password of your database.  Next, depending on your IDE, you'll need to add the mySQL connector library and its location.  For example, mine was located in my local documents folder and the version was "mysql-connector-j-8.0.33."  After the database is hooked up and the jar file library added, simply run the ClientScheduler application class in your IDE of choice and it should populate correctly.

- My additional report was of a list of customers per country.  When the radio button is selected, it checks the list of customers' division ID and runs a report of all customers whose division ID fits within the bound of the selected country.  This isn't a new idea, but it seemed to make sense.  Sometimes it's important to check where everyone is located for the purposes of time-zone conversions for phone calls, and just to get an idea for potential business reports that need run.

- mySQL driver version number: mysql-connector-j-8.0.33