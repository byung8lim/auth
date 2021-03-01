#!/bin/bash

BASE=`pwd`
if [ $# -lt 1 ];then
  echo "Docker Image name is not defined"
  exit 1
fi
IMG_NAME=$1

VER=`sudo docker images | awk '/foodcalorie/{print $2+0.1}'`
if [ -z "$VER" ];then
  VER="0.1"
fi
mvn clean package -DskipTests=true

if [ ! -d "target" ];then
  echo "build error 'cause target directory was not created"
  exit 1
fi

BUILD_IMG=`find target -type f -name '$IMG_NAME*.jar'`
if [ ! -z "$BUILD_IMG" ];then
  echo "SW Package was not created"
  exit 1
fi
sudo docker build -t kube-01.bd.com:5000/fitness/$IMG_NAME:$VER .
sudo docker push kube-01.bd.com:5000/fitness/$IMG_NAME:$VER
