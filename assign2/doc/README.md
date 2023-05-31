# Distributed computing project

## Group Members

- Henrique Santos Ferreira (up202007459)
- Pedro Pereira Ferreira (up202004986)
- Pedro Manuel Costa Aguiar Botelho Gomes (up202006086)

## Instructions

- There is some specific insturctions to run the project for Linux and for Windows.

### Linux

- When you're a running in a Linux environment, it is possible to use the Makefile commands to do that.

1) To make a server, open a terminal and run the command:

```makefile
make server
```
2) To make a client, open another terminal and run the command:

```makefile
make server
```

- Then a login interface will appear. You can choose between user<-num->, where num =1|2|3|4. For all of them, the password is client.

3) When fininshed, you can do the command ```make clean``` to remove the files in the out folder.

### Windows

- When using Windows, it is not possible to use the MakeFile, without installing chocolatey package. However, here are some alternative commands to run the project:

1) Create an out folder, if it does not already exists;

2) Run the following command on a terminal:

```
javac -d ./out ./src/*.java
```

3) To make a server, run the following command in a terminal:

```
javac -cp out/ TimeServer 8000
```

4) To make a client, run the following command in a terminal:

```
javac -cp out/ TimeClient localhost 8000
```

- The login interface will appear and the valid usernames and passwords are the same of the ones used on Linux

- To terminate both of them, you can use ```CTRL + C``` on the respective terminal.