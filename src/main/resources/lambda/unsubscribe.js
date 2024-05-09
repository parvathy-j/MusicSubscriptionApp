const AWS = require('aws-sdk');

AWS.config.update({ region: 'us-east-1' });
const dynamoDB = new AWS.DynamoDB.DocumentClient();

exports.handler = async (event) => {
    const { email, title } = JSON.parse(event.queryStringParameters);
    const params = {
        TableName: 'subscriptions',
        Key: {
            email: email,
            title: title
        }
    };

    try {
        await dynamoDB.delete(params).promise();
        return {
            statusCode: 200,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ message: "Unsubscribed successfully" })
        };
    } catch (error) {
        console.error('Unsubscribe error:', error);
        return {
            statusCode: 500,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ message: "Failed to unsubscribe", error: error.message })
        };
    }
};
