## A Simple EC2 with a User-Data Script

When bootstrapping an EC2, a **User-Data** script can be executed.  
The script is launched ONLY at first boot-time. It is not executed on RESTART.

## Your mission

1️⃣ Create a networking infrastructure (VPC, subnets, RouteTable, SecurityGroup, Internet Access)
- Use VPC and Subnets from `1-networking/101` and `1-networking/102` tutorials
- Add an Internet Gateway and accesses to/from internet (see `1-networking/104` tutorial)
- In a new Security group, Allow ports:
  - SSH only **from your IP**
  - ICMP only **from your IP**
  - 👉 HTTP from everywhere

2️⃣ Start a new EC2 with a **User-Data script**
- In the EC2 definition, add a User-Data script.
- In the script you can update the linux packages
- In the script you can install a service (e.g. Apache httpd)

Example of script to be used:
```
sudo su
yum update -y
yum install -y httpd
systemctl start httpd
systemctl enable httpd
echo "Hello World from $(hostname -f)" > /var/www/html/index.html
```

<div align="center">
<img src="./doc/202-user-data.png" width="800" alt="EC2">
</div>
<br>

## Your success
🏁 Once installed, the EC2 should be able to serve your html page.
- ✅ Test that you can see the webpage from your browser
- ✅ Use Curl to test the page

You can use following commands to check your mission success
```shell
./launch.sh 2-computing/202-user-data/TEST-curl.sh
./launch.sh 2-computing/202-user-data/TEST-ssh-public-ec2.sh
```

## Materials
[Doc AWS](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/user-data.html)


