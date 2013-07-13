
var express = require('express');
var mongoose = require('mongoose');

var mongoUrl = process.env.MONGOLAB_URI || 'mongodb://localhost/lrt';

//object model for trace events
var tracePointSchema = new mongoose.Schema( 
{
    lineNumber: Number,
    timestamp: Date,
    seq: Number,
    methodSig: String,
    traceId: String
    //some other shit

});
var TracePoint = mongoose.model('TracePoint',tracePointSchema);

//Setup Mongodb
mongoose.connect(mongoUri, function(err,res) {
    if (err) {
        console.log ('ERROR connecting to: ' + mongoUri + '. ' + err);
    } else {
        console.log ('Succeeded: connected to: ' + mongoUri );
    }
})



var app = express();

app.configure(function() {
    app.set('port', process.env.PORT || 3000)
    app.use(express.bodyParser());
    /* other middleware goes here */
});


app.get('/', function(req,res) {
    res.send("Hello! You got from the root.");
    console.log('GET /');
});

app.post('/',function(req,res) {

   console.log('post');
   console.log(req.body);
   res.send("Hello! You posted to the root.");

});


app.listen(app.get('port'));
console.log('express listening on port:' + app.get('port'));

