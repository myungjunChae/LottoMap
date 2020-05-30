const fs = require('fs');
const AWS = require('aws-sdk');
const BUCKET_NAME = 'lottomap';
const s3 = new AWS.S3({   });
const uploadFile = (fileName) => {
    const fileContent = fs.readFileSync(fileName);
    const params = {
        Bucket: BUCKET_NAME,
        Key: 'data.json',
        Body: fileContent   
    };
    s3.upload(params, function(err, data) {
        if (err) { throw err; }
        console.log(`File uploaded successfully. ${data.Location}`);
    });
};

uploadFile('../data/data.json'); 