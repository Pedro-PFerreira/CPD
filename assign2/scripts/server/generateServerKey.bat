set mypath=%cd%
keytool -genkeypair -alias serverkey -keyalg RSA -keysize 2048 -validity 365 -keystore serverkeystore.jks -storepass server
keytool -exportcert -alias serverkey -keystore serverkeystore.jks -storepass server -file server.cert
keytool -importcert -file server.cert -keystore %mypath%/../user1/clienttruststore.jts -storepass server
keytool -importcert -file server.cert -keystore %mypath%/../user2/clienttruststore.jts -storepass server
keytool -importcert -file server.cert -keystore %mypath%/../user3/clienttruststore.jts -storepass server
keytool -importcert -file server.cert -keystore %mypath%/../user4/clienttruststore.jts -storepass server
