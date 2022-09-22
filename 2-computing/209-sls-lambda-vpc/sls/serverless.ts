import type {Serverless} from 'serverless/aws';

const serverlessConfiguration: Serverless = {
  service: {
    name: 'service-209-sls-lambda-vpc',
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
    environment: {
    }
  },
  functions: {
    listAllObjectsInS3: {
      handler: 'handler.listAllObjects',
      events: [
        {
          http: {
            method: 'get',
            path: 'demo-209',
          }
        }
      ]
    }
  }
}

module.exports = serverlessConfiguration;
