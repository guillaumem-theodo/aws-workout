import {S3} from 'aws-sdk';
import {APIGatewayProxyHandler} from 'aws-lambda';
import 'source-map-support/register';

const S3Client = new S3({signatureVersion: 'v4'});

async function listS3Objects() {
    const params = {
        Bucket: process.env.BUCKET_NAME,
    };

    //  List objects in S3 bucket
    return await S3Client.listObjectsV2(params).promise();
}

export const listAllObjects: APIGatewayProxyHandler = async (event, _context) => {
    const s3Objects = await listS3Objects();

    return {
        statusCode: 200,
        body: JSON.stringify(s3Objects || {message: 'No objects found in s3 bucket'})
    }
}
