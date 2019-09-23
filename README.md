# BEAM (Basic Encryption Algorithm Module)
Created 2019 by Philip Heyse

## Description
BEAM is a light weight Java library to encrypt and decrypt data.
Security level of the encryption: _"A government agency can break it. Your Mom can't."_ Unless your Mom works for a government agency or is a hacker ;-)

## Approach
BEAM combines three mechanisms for encryption:
1. From the provided password a longer password is generated
2. Bytes are shifted depending on the value of the password bytes.
3. Blocks are created and the position of the bytes in the block depends on the password bytes. Remaining bytes in the block are filled with random data.


## License
Apace V2



## Usage examples

### Encrypt and decrypt strings
```java
Beam beam = new Beam("myPassword");
String encryptedText = beam.encrypt("my text"); // creates a string such as 8087ED5FBB314E9F84B968AED20089...
String decryptedText = beam.decrypt(encryptedText); // produces "my text" again 
```

### Encrypt and decrypt byte arrays
```java
Beam beam = new Beam("myPassword");
byte[] myBytes = {(byte)10, (byte)11, (byte)12};
byte[] encryptedBytes =  beam.encrypt(myBytes);
byte[] decryptedBytes =  beam.decrypt(encryptedBytes);
```

### Encrypt stream
```java
Beam beam = new Beam("myPassword");
beam.encrypt(inputStreamWithPlainData, encryptedOutputStream, null);
```

### Decrypt stream
```java
Beam beam = new Beam("myPassword");
beam.decrypt(inputStreamWithEncryptedData, decryptedOutputStream, null);
```

### Password as byte array
```java
byte[] myPasswordBytes = {(byte)0, (byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6, (byte)7};
Beam beam = new Beam(myPasswordBytes);
```

### Password providing the block length
```java
int blockLength = 64;
Beam beam = new Beam("myPassword", blockLength);
```

## Including via Maven
```xml
[...]
		<dependency>
			<groupId>de.bright-side.beam</groupId>
			<artifactId>beam</artifactId>
			<version>1.0.1</version>
		</dependency>
[...]
```

## Change History
Version 1.0.0 (2019-08-20)
- first version 

Version 1.0.1 (2019-09-12)
- removed dependency with "javax.xml.bind.DatatypeConverter" which is included in the JDK/JRE but is missing on Android
