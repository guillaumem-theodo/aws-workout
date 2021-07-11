## A Simple EC2 with a User-Data Script

When bootstrapping an EC2, a **User-Data** script can be executed.

The script is launched ONLY at first boot-strapping. It is not executed on RESTART.

1️⃣ Create a VPC, a subnet (or use `1-networking/101` and `1-networking/102` ones)
- Add an internet gateway and access to/from internet (see `1-networking/104`)
- Allow ports:
  - SSH only from your IP
  - ICMP only from your IP
  - 👉 HTTP from everywhere

2️⃣ Start a new EC2 with a User-Data script
- In the EC2 definition, add a User-Data script.
- In the script you can update the linux packages
- In the script you can install a service (e.g. apache httpd)

Example of script to be used:
```
sudo su
yum update -y
yum install -y httpd
systemctl start httpd
systemctl enable httpd
echo "Hello World from $(hostname -f)" > /var/www/html/index.html
```

🏁 Once installed, the EC2 should be able to serve your html page.

[Doc AWS](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/user-data.html)

![Image of VPC](./doc/202-user-data.png)


