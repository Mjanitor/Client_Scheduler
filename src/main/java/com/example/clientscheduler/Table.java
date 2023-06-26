package com.example.clientscheduler;

/**
 * Lambda class of no parameters that simplifies display of the dynamic table.  Class is set in the "Table"
 * interface file.  It checks if any radio buttons are selected and modifies the result set accordingly but then
 * queries the database for all listed results and iterates over loops to build out the table so long as more results
 * exist.  This vastly improves my previous method implementation because I can piece out the function and allow for
 * a more streamlined version of table production.
 */
public interface Table {

    /**
     * Lambda function of no parameters that simplifies display of the dynamic table.  Class is set in the "Table"
     * interface file.  It checks if any radio buttons are selected and modifies the result set accordingly but then
     * queries the database for all listed results and iterates over loops to build out the table so long as more results
     * exist.  This vastly improves my previous method implementation because I can piece out the function and allow for
     * a more streamlined version of table production.
     */
    void dynamicTable();
}
