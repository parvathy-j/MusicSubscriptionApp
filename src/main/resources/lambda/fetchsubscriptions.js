const AWS = require('aws-sdk');

AWS.config.update({ region: 'us-east-1' });
const dynamoDB = new AWS.DynamoDB.DocumentClient();

exports.handler = async (event) => {
    const email = event.queryStringParameters.email;
    const params = {
        TableName: 'scubscriptions',
        KeyConditionExpression: 'email = :email',
        ExpressionAttributeValues: {
            ':email': email
        }
    };

    try {
        const data = await dynamoDB.query(params).promise();
        return {
            statusCode: 200,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data.Items)
        };
    } catch (error) {
        console.error('Fetch subscriptions error:', error);
        return {
            statusCode: 500,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ message: "Failed to fetch subscriptions", error: error.message })
        };
    }
};
