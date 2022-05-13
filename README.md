# urls-to-screenshots
Generate full page screenshots by taking list of URLs as input

# Prerequisites:
- Java
- Maven
- TestNG (install it in Eclipse if you run tests from Eclipse)

# Steps to Configure:
1. Add the URLs in an excel(.xlsx) file.
2. Give names to the excel file tab and the file.
3. Place this .xlsx file in sr/main/resources filder.
4. Open the URLtoScreenshot.java file > "readExcel" method (line 107) > Add file name and tab name as parameter values (2nd & 3rd parameters).

`readExcel(filePath, "URLsDocument.xlsx", "urls", 0, "failedURLs_1.txt", 1);`

# Results:
- You can get the created screenshots in target/screenshots folder.
- Invalid screenshots (whose length is more than 5000px) can be found in target/skipped_screenshots.

# Parameter - Index:
- In "URLtoScreenshot.Java" file, readExcel method contains a parameter "Index".
- Index is nothing but row number of the starting URL in the excel document.
- Default value of the index is '1'. No need to change it unless there is a script failure. 
- Incase the script is failed after generating 150 URLs, then we need to change the index value as 151 and restart the script. 
- This updated value skip the completed URLs and start with the 151st URL.

## Parameter - FileSeq:
- We use this parameter to maintain sequence number to all the URLs from different files within a batch.
- In "URLtoScreenshot.Java" file, readExcel method contains a parameter "fileSeq".
- Default value is 0.
- When we need to run 2 excel files parallelly and each file contains 1000 URLs, then 'fileSeq' value for the first test should be '0'. For the 2nd test the value should be '1001'.
- When we need to run 4 excel files parallelly/sequencially, then we should give '0' to first test, 1001 to 2nd test, 2001 to 1st test 2nd run and 3001 to 2nd test 2nd run. 