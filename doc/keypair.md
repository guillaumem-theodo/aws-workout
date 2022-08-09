### 🔑🔑 Create a Key Pair for your EC2 🔑🔑
In order to work with and to log into the EC2, you need to create an SSH keypair.
A key pair is a pair of private and public keys.
You will need to have the private key stored on your laptop (in the Workout root directory).
The public key need to be stored in AWS EC2 KeyPair Service.

#### 🚧 Howto to create and store the keypair:
In the Workout root directory 
  
```shell
./generate-keypair.sh
```

It will generate the private and public key files
  - private key file named `aws-workout-key-pair.pem`. Must be stored on your laptop in the Workout root directory.
  - public key file named `aws-workout-key-pair.pub`. Will be automatically uploaded in AWS. 

It will create a keypair in AWS named `aws-workout-key` and upload the public part of the key from your laptop.

#### 🚧 Enable SSH Agent Forwarding

Many TEST files rely on SSH and SSH Agent Forwarding (from your laptop to EC2 then to other EC2). 
Agent Forwarding is a way to SSH from servers to servers using the same credentials.
You need to enable **SSH Agent Forwarding** and to add the private key.

⚠️ SSH Agent Forwarding is not a good practice on PROD environments.

1) Enable SSH Agent Forwarding
```bash
vim ~/.ssh/config

Add:
Host *
  ForwardAgent yes
  AddKeysToAgent yes
```

2) Add the private key in agent forwarding
```bash
ssh-add -k aws-workout-key-pair.pem
```

2) You can check if the agent fowarding is set up using following command
```bash
ssh-add -L
```
