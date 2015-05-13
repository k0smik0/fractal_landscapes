#!/bin/sh

#java -cp libs/stdlib.jar:bin IFS 100000 < barnsley.txt

#java -cp bin net.iubris.fractal_landscapes.ifs.standalone.IFS

java -jar build/ifs-felt-app.jar $1
