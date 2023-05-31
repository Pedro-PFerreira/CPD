#!/bin/sh
keytool -genkeypair -alias serverkey -keyalg RSA -keysize 2048 -validity 365 -keystore serverkeystore.jks
keytool -exportcert -alias serverkey -keystore serverkeystore.jks -file server.cer
