const AWS = require('aws-sdk');

AWS.config.update({ region: 'us-east-1' });
const dynamoDB = new AWS.DynamoDB.DocumentClient();

exports.handler = async (event) => {
    const { title, artist, year } = JSON.parse(event.body);

    try {
        // Build the query
        let filterExpression = [];
        let expressionAttributes = {};

        if (title) {
            filterExpression.push('title = :title');
            expressionAttributes[':title'] = title;
        }
        if (artist) {
            filterExpression.push('artist = :artist');
            expressionAttributes[':artist'] = artist;
        }
        if (year) {
            filterExpression.push('year = :year');
            expressionAttributes[':year'] = year;
        }

        const params = {
            TableName: 'music',
            FilterExpression: filterExpression.join(' and '),
            ExpressionAttributeValues: expressionAttributes
        };

        // Perform the query
        const result = await dynamoDB.scan(params).promise();

        // Respond with the queried data
        return {
            statusCode: 200,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(result.Items)
        };

    } catch (error) {
        console.error(error);
        return {
            statusCode: 500,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ message: "Error querying the Music table", error: error.message })
        };
    }
};
