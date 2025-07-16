       IDENTIFICATION DIVISION.                                         foobar
       PROGRAM-ID. EXAMPLE2.
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       COPY EXAMPLE2-COPY
           REPLACING 'John Doe' BY "Jane Doe"
                     NAME BY WS-NAME.
      * foobar foobar foobar foobar foobar foobar foobar foobar foobar foobar foobar foobar foobar foobar
       PROCEDURE DIVISION.
       DISPLAY WS-NAME.
       STOP RUN.
