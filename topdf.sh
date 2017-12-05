#!/bin/bash
cd $1/generated/
echo "Erstelle High Qual PDF"
convert *.png cal.pdf

echo "Erstelle Low Qual PDF"

cd -
./resize.sh $1
