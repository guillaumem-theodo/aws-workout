## Enable S3 bucket versioning

- You can enable `S3 bucket Versioning`
- S3 Bucket versioning allows you to keep different versions of the object stored under a given Key
- S3 Bucket versioning allows you to keep the versions prior to a deletion
-

1️⃣ Prerequisite: 
- Create Two S3 buckets
- One bucket with Versioning enable
- The other bucket without Versioning enable
- Store different objects in the S3 buckets, with the same keys

🏁 Test S3 versioning

- ✅ List objects and versions in a Given S3 bucket
  
```bash
aws s3 ls s3://BUCKETNAME/KEY
aws s3api list-object-versions --bucket BUCKETNAME --prefix KEY
```

- ✅ Retrieve a given version of an object
```bash
aws s3api get-object --bucket BUCKETNAME --key KEY --version-id VERSION_ID ./tmp.txt
```


[Doc AWS](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Versioning.html)
