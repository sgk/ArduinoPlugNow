#!/bin/sh

ARDUINODEV=../Arduino

rm -rf com
javac -d . -cp $ARDUINODEV/app/pde.jar:$ARDUINODEV/core/core.jar:$ARDUINODEV/app/lib/RXTXcomm.jar *.java
cp Resources_ja.properties com/ppona/plugnow
jar -cf PlugNow.jar com
rm -rf com
