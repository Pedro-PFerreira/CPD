set mypath=%cd%
keytool -genkeypair -alias user3 -keyalg RSA -keysize 2048 -validity 365 -keystore clientkeystore.jks -storepass client
keytool -exportcert -alias user3 -keystore clientkeystore.jks -storepass client -file client.cert
keytool -importcert -alias user3 -file client.cert -keystore  %mypath%/../server/servertruststore.jts -storepass client
