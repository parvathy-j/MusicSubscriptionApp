const AWS = require('aws-sdk');

AWS.config.update({ region: 'us-east-1' });
const dynamoDB = new AWS.DynamoDB.DocumentClient();

exports.handler = async (event) => {
    const { email, title } = JSON.parse(event.body);
    const params = {
        TableName: 'subscriptions',
        Item: {
            email: email,
            title: title
        },
        ConditionExpression: 'attribute_not_exists(email) AND attribute_not_exists(title)'
    };

    try {
        await dynamoDB.put(params).promise();
        return {
            statusCode: 200,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ message: "Subscribed successfully" })
        };
    } catch (error) {
        console.error('Subscribe error:', error);
        return {
            statusCode: 500,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ message: "Failed to subscribe", error: error.message })
        };
    }
};
