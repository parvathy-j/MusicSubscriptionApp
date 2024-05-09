const AWS = require('aws-sdk');
const dynamo = new AWS.DynamoDB.DocumentClient();
const tableName = "login";

exports.handler = async (event) => {
    console.log("Received event:", JSON.stringify(event));

    let body;
    try {
        body = JSON.parse(event.body);
    } catch (e) {
        console.error('Error parsing JSON:', e);
        return {
            statusCode: 400,
            body: JSON.stringify({ message: "Invalid JSON format" })
        };
    }

    const { email, password, username } = body;

    if (!email || !username || !password) {
        return {
            statusCode: 400,
            body: JSON.stringify({ message: "Missing required fields" })
        };
    }

    try {
        // Check if the user already exists
        const exists = await dynamo.get({
            TableName: tableName,
            Key: { email }
        }).promise();

        if (exists.Item) {
            return {
                statusCode: 400,
                body: JSON.stringify({ message: "Email already exists" })
            };
        }

        // Insert new user
        await dynamo.put({
            TableName: tableName,
            Item: {
                email,
                username,
                password // Consider hashing the password for security purposes
            }
        }).promise();

        return {
            statusCode: 200,
            body: JSON.stringify({ message: "User registered successfully" })
        };
    } catch (error) {
        console.error('DynamoDB error: ', error);
        return {
            statusCode: 500,
            body: JSON.stringify({ message: "Failed to register user" })
        };
    }
};
