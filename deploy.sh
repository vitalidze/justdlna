#!/bin/bash

#BRANCH=`git branch | sed -n '/\* /s///p'`
BRANCH=master
PAGES=gh-pages
VERSION=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | egrep -Ev '(^\[|Download\w+:)'`
ARTIFACT_ID=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.artifactId | egrep -Ev '(^\[|Download\w+:)'`

mvn clean assembly:assembly

git checkout $PAGES
# make directory for releases (if it doesn't yet exist)
mkdir -p release
# copy artifacts
cp -av target/$ARTIFACT_ID-$VERSION-bin.tar.gz target/$ARTIFACT_ID-$VERSION-bin.zip release/
# put latest version to the text file
echo $VERSION > release/latest
# add files do git index
git add release/$ARTIFACT_ID-$VERSION-bin.tar.gz release/$ARTIFACT_ID-$VERSION-bin.zip release/latest
# git commit
git commit -m "Release $VERSION"
git push origin $PAGES
# checkout previous branch
git checkout $BRANCH
