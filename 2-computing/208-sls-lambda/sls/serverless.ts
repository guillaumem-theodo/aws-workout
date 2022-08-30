import type { Serverless } from 'serverless/aws';

const serverlessConfiguration: Serverless = {
  service: 'cpu-208-sls-lambda'
  ,
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
    environment: {
      AWS_NODEJS_CONNECTION_REUSE_ENABLED: '1',
      BUCKET_NAME: "${env:BUCKET_NAME, 'anyname'}",
    },
    iamRoleStatements: [
      {
        Effect: 'Allow',
        Resource: [
            "arn:aws:s3:::${env:BUCKET_NAME, 'anyname'}",
          "arn:aws:s3:::${env:BUCKET_NAME, 'anyname'}/*"],
        Action: ['s3:ListBucket'],
      },
    ],
    lambdaHashingVersion: 20201221
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
