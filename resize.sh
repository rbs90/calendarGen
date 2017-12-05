#!/bin/bash
cd $1/generated/
mkdir -p thumb
echo Erstelle Thumbnails
for i in `seq 0 12`;
do
    if [ "$i" -lt "10" ]; then 
       i=0$i
    fi
    convert -thumbnail 1920 $i.png thumb/$i.jpg
done    
echo Erstelle Preview PDF
convert thumb/*.jpg cal_prev.pdf
