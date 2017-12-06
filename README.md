# calendarGen
calendar creation tool based on java and imagemagick

## Requirements
imagemagick, Java

## Command line options
```
  -y --year   The year that is used for creation
  -i --input  The input folder for images and text-informations
```
## Holidays
Currently there are only holidays for Saxony, Germany (powered by spiketime.de). Could be easily changed in code for other german states.

## Folder and file structure
The input folder must contain 13 images named from 0 to 12, 0 is the title image. Currently only jpg and png format (<b>and only these endings!</b>) supported.
A text file named text.txt contains the image descriptions for the months 1 to 12, each description on a new line.

Output will be created in the 'generated'-folder in your input dir.

## Example
```
[rbs@durin][~/projects/calenderGen/artifact]% ls -lah kalender       
-rw-r--r-- 1 rbs rbs 1,8M Dez  5 21:55 0.png
-rw-r--r-- 1 rbs rbs 1,8M Dez  5 21:55 10.png
-rw-r--r-- 1 rbs rbs 519K Dez  5 21:55 11.jpg
-rw-r--r-- 1 rbs rbs 461K Dez  5 21:55 12.jpg
-rw-r--r-- 1 rbs rbs 234K Dez  5 21:55 1.jpg
-rw-r--r-- 1 rbs rbs 1,4M Dez  5 21:55 2.jpg
-rw-r--r-- 1 rbs rbs 158K Dez  5 21:55 3.jpg
-rw-r--r-- 1 rbs rbs 965K Dez  5 21:55 4.jpg
-rw-r--r-- 1 rbs rbs 504K Dez  5 21:55 5.jpg
-rw-r--r-- 1 rbs rbs 205K Dez  5 21:55 6.jpg
-rw-r--r-- 1 rbs rbs 515K Dez  5 21:55 7.jpg
-rw-r--r-- 1 rbs rbs 1,5M Dez  5 21:55 8.jpg
-rw-r--r-- 1 rbs rbs 363K Dez  5 21:55 9.jpg
-rw-r--r-- 1 rbs rbs  182 Dez  5 21:50 text.txt
[rbs@durin][~/projects/calenderGen/artifact]% java -jar calendarGen.jar -y 2018 -i kalender      
```

## Screenshot
![April](/doc/screenshot.jpg?raw=true "Example Screenshot")
