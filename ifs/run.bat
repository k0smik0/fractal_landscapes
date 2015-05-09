JAVA_HOME="C:\Program Files\Java\jdk1.7.0_06\bin"
REM %JAVA_HOME%\java -cp libs/stdlib.jar:bin IFS 100000 < barnsley.txt

%JAVA_HOME%\java -jar build\ifs-felt-app.jar %1
