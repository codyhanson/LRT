
var express = require('express');

//Setup Mongo
/*var mongo = require('mongodb').MongoClient
var mgdb = null;

mongo.connect(process.env.MONGOLAB_URI, function(err, db) {
    if(err) {
        console.log('Error connecting to Mongo');
        throw err;
    }
    mgdb = db;
    console.log('MongoLab connected');
})
*/



var app = express();

app.configure(function() {
    app.set('port', process.env.PORT || 3000);
    /* other middleware goes here */
});


app.get('/', function(req,res) {
    res.send("Hello! You got from the root.");
    console.log('GET /');
});

app.post('/',function(req,res) {

    console.log('post');

   res.send("Hello! You posted to the root.");

});


app.listen(app.get('port'));
console.log('express listening on port:' + app.get('port'));

/*
process.on('SIGINT', function () {
  console.log('Got SIGINT. Cleanup stuff .');
});
*/

/*
var WebSocketServer = require('ws').Server
  , wss = new WebSocketServer({port: 8080});

wss.on('connection', function(ws) {
    ws.on('message', function(message) {
        console.log('received: %s', message);
    });
    ws.send('something');
});

console.log('websocket server listening on 8080...');
*/




