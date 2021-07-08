#!/bin/bash
sudo su
yum update -y
yum install -y httpd
systemctl start httpd
systemctl enable httpd

public_ip=$(curl 169.254.169.254/latest/meta-data/public-ipv4)

echo "Hello World from $(hostname -f) - PUBLIC IP: $public_ip" > /var/www/html/index.html
