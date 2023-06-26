package com.example.clientscheduler;

/**
 * Lambda class that enables quick production of an error message for various reasons, such as no selection,
 * overlaps, and others.  I found that the lambda implementation was much more efficient as instead of creating a
 * new error ALERT for each necessary time, I can simply call this lambda.
 */
public interface UTC {
    /**
     * Lambda function that enables quick production of an error message for various reasons, such as no selection,
     * overlaps, and others.  I found that the lambda implementation was much more efficient as instead of creating a
     * new error ALERT for each necessary time, I can simply call this lambda.
     */
    void showError(String title, String content);
}
