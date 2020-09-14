# EDCS dataset transformer
A small Java program to transform datasets from EDCS (Clauss-Slaby), removing certain elements and giving a bibliographical reference, a provenience and the transcription as a result. `#epigraphy #digitalepigraphy` 

# How to use 
You can use the jar file under linux by running `java -jar edcs-formatter.java`.
It is important that your source file from Epigraphische Datenbank Clauss - Slaby ([`manfredclauss.de`](http://www.manfredclauss.de/)) is a `.txt` file renamed to `transformieren.txt` which is in the same directory as the java jar file. The result will be written to `resultat.txt` (which is created if not yet existant or overwritten if already existant in the folder). 

The program will strip the dataset of some info/paragraphs, leaving you with only one line each for:
1. Bibliographical reference
2. Provenience 
3. Transcription 

This program was written for Manfred Hainzmann.

