#!/bin/bash


ignored=0
for file in pom.xml file.sh ;
do
  ignored_additions=$(git --no-pager  diff --numstat | grep $file | cut -f 1)
  ignored_deletions=$(git --no-pager  diff --numstat | grep $file | cut -f 2)

  ignored=$((ignored + ignored_additions + ignored_deletions))
done
echo $ignored

