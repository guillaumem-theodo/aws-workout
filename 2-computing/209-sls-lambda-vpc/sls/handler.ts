import {S3} from 'aws-sdk';
import {APIGatewayProxyHandler} from 'aws-lambda';
import 'source-map-support/register';

const S3Client = new S3({signatureVersion: 'v4'});

async function getS3Object() {
    const params = {
        Bucket: process.env.BUCKET_NAME,
        Key: "test.txt"
    };

    //  List objects in S3 bucket
    try {
        const s3Object = await S3Client.getObject(params).promise();
        console.log(s3Object)
        return s3Object;
    } catch (e) {
        console.log(e)
    }
    return undefined;
}

export const listAllObjects: APIGatewayProxyHandler = async (event, _context) => {
    console.log(JSON.stringify(event));
    const s3Object = await getS3Object();

    return {
        statusCode: 200,
        body: JSON.stringify(s3Object || {message: 'No object found in s3 bucket'})
    }
}
