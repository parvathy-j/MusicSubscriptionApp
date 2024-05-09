const AWS = require('aws-sdk');
const dynamoDB = new AWS.DynamoDB.DocumentClient();

exports.handler = async (event) => {
    console.log("Received event:", event);  // Log the incoming event to see what data is being received

    const { email, password } = JSON.parse(event.body);
    console.log(`Login attempt for email: ${email}`); // Be cautious with logging sensitive information in production

    const params = {
        TableName: "login",
        Key: { email }
    };

    try {
        const result = await dynamoDB.get(params).promise();
        console.log("DynamoDB response:", result); // Check what is being returned from DynamoDB

        const user = result.Item;
        if (user) {
            console.log("User found, verifying password...");
            if (user.password === password) {  // Ensure passwords are handled securely in production
                return {
                    statusCode: 200,
                    headers: {
                        "Access-Control-Allow-Origin": "*",
                    },
                    body: JSON.stringify({
                        message: "User logged in successfully",
                        user_name: user.username,
                        email: user.email
                    })
                };
            } else {
                console.log("Password verification failed.");
                return {
                    statusCode: 401,
                    headers: {
                        "Access-Control-Allow-Origin": "*",
                    },
                    body: JSON.stringify({ message: "Invalid email or password" })
                };
            }
        } else {
            console.log("No user found with the given email.");
            return {
                statusCode: 404,
                headers: {
                    "Access-Control-Allow-Origin": "*",
                },
                body: JSON.stringify({ message: "User not found" })
            };
        }
    } catch (error) {
        console.error('Database error:', error);
        return {
            statusCode: 500,
            headers: {
                "Access-Control-Allow-Origin": "*",
            },
            body: JSON.stringify({ message: "An error occurred. Please try again." })
        };
    }
};
