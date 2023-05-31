set mypath=%cd%
keytool -genkeypair -alias user1 -keyalg RSA -keysize 2048 -validity 365 -keystore clientkeystore.jks -storepass client
keytool -exportcert -alias user1 -keystore clientkeystore.jks -storepass client -file client.cert
keytool -importcert -alias user1 -file client.cert -keystore  %mypath%/../server/servertruststore.jts -storepass client
