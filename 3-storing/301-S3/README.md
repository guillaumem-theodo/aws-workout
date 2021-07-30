## Some S3 buckets

- S3 is an `object storage`
- Objects are stored in `S3 Buckets`
- Buckets are created in a Region (S3 is a global service, but buckets are regional)
- Objects are stored using a `Key`
- Keys can be a single string or a `path` like string

   
- 1️⃣ Create three S3 buckets in One region
- 2️⃣ Create a fourth S3 bucket in another region
- 3️⃣ Store objets in each bucket
- 4️⃣ Store objets in each bucket using path like string key `key1/key2/key3`

🏁 Test S3 buckets
- ✅ Test that you can list buckets using AWS CLI (`aws s3 ls`)
- ✅ Test that you can list objects in one bucket using AWS CLI (`aws s3 ls s3:\\BUCKETNAME`)
- ✅ Test that you can store (copy) objects in one bucket (`aws s3 cp`)

![Image of VPC](doc/301-S3.png)
