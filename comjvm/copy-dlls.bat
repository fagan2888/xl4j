mkdir "%1\target\%3-Ascii-%4"
mkdir "%1\target\%3-Unicode-%4"
copy /y "%1\target\%3-%4\%2.dll" "%1\target\%3-Ascii-%4"
copy /y "%1\target\%3-%4\%2.xml" "%1\target\%3-Ascii-%4"
copy /y "%1\target\%3-%4\*.h" "%1\target\%3-Ascii-%4"
copy /y "%1\target\%3-%4\%2.dll" "%1\target\%3-Unicode-%4"
copy /y "%1\target\%3-%4\%2.xml" "%1\target\%3-Unicode-%4"
copy /y "%1\target\%3-%4\*.h" "%1\target\%3-Unicode-%4"
