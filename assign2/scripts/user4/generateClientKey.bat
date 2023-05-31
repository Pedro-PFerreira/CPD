set mypath=%cd%
keytool -genkeypair -alias user4 -keyalg RSA -keysize 2048 -validity 365 -keystore clientkeystore.jks -storepass client
keytool -exportcert -alias user4 -keystore clientkeystore.jks -storepass client -file client.cert
keytool -importcert -alias user4 -file client.cert -keystore  %mypath%/../server/servertruststore.jts -storepass client
