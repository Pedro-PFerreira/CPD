set mypath=%cd%
keytool -genkeypair -alias user2 -keyalg RSA -keysize 2048 -validity 365 -keystore clientkeystore.jks -storepass client
keytool -exportcert -alias user2 -keystore clientkeystore.jks -storepass client -file client.cert
keytool -importcert -alias user2 -file client.cert -keystore  %mypath%/../server/servertruststore.jts -storepass client
