## Coding conventions

1) All Resources created in your account are named following this convention:
`$category-$workout-$type-$optional_number-$optional_comments`
   - net-101-vpc-1
   - net-102-subnet-2 and net-102-subnet-3
   - net-102-subnet-1-with-ssh
   - net-105-ec2-1
   - ...
2) All Resources create have a `Purpose` tag, so you can use Tag Manager to list them
3) All Terraform Resources have a logical id `$category-$workout-$type-$optional_number-$optional_comments`
    ```terraform
    resource "aws_vpc" "net-101-vpc" {
    
    }
    ```
    ```terraform
    resource "aws_route_table_association" "net-105-rt-association-subnet2" {
      route_table_id = aws_route_table.net-105-rt-2.id
      subnet_id = var.private_subnet_102_id
    }
    ```
   
4) All CDK Constructs have a logical id `$category-$workout-$type-$optional_number-$optional_comments`
    ```java
     CfnVPC.Builder.create(this, "net-101-vpc") .build();
    ```
    ```java
    CfnSubnetRouteTableAssociation.Builder
        .create(this, "net-105-rt-2-association-subnet")
                .routeTableId(routeTable.getAttrRouteTableId())
                .subnetId(privateSubnet.getAttrSubnetId())
                .build();
    ```
