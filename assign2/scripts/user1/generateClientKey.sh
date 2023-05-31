#!/bin/sh
keytool -genkeypair -alias clientkey -keyalg RSA -keysize 2048 -validity 365 -keystore clientkeystore.jks
keytool -importcert -alias servercert -file server.cer -keystore clientkeystore.jks
