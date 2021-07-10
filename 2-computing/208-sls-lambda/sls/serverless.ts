import type { Serverless } from 'serverless/aws';

const serverlessConfiguration: Serverless = {
  service: {
    name: 'service-208-sls-lambda',
  },
  frameworkVersion: '2',
  custom: {
    webpack: {
      webpackConfig: './webpack.config.js',
      includeModules: true
    }
  },
  // Add the serverless-webpack plugin
  plugins: ['serverless-webpack'],
  provider: {
    name: 'aws',
    runtime: 'nodejs12.x',
    region: 'eu-west-1',
    apiGateway: {
      minimumCompressionSize: 1024,
    },
/*    vpc: {
      securityGroupIds: ["${opt:sg}"],
      subnetIds: ["${opt:subnet}"],
    },*/
    environment: {
      AWS_NODEJS_CONNECTION_REUSE_ENABLED: '1',
      BUCKET_NAME: "${opt:bucket_name}",
    },
    iamRoleStatements: [
      {
        Effect: 'Allow',
        Resource: [
            'arn:aws:s3:::unique-name-s3-bucket-1-208',
          'arn:aws:s3:::unique-name-s3-bucket-1-208/*'],
        Action: ['s3:ListBucket'],
      },
    ],
  },
  functions: {
    hello: {
      handler: 'handler.listAllObjects',
      events: [
        {
          http: {
            method: 'get',
            path: 'demo-208',
          }
        }
      ]
    }
  }
}

module.exports = serverlessConfiguration;
