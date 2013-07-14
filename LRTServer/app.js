
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

//object model for Trace
var traceSchema = new mongoose.Schema(
{
    osVersion: String,
    osType : String,
    userId : String,
    appName: String,
    appVersion: String,
    traceServiceVersion: String
}
);
var Trace = mongoose.model('Trace',traceSchema);


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

/**
 * Create a new trace for a particular user
 */
app.post('/users/:userId/traces', function(req,res) {


});

/**
 * Create new tracepoints for a particular 
 * instance of a trace
 * Takes an array of tracepoints as the POST body
 * so we can effeciently post more than one point at a time.
 */
app.post('/traces/:traceId/tracepoints', function(req,res) {


});


/*
 * get a list of traces for a user
 *
 */
app.get('/users/:userId/traces', function (req,res) {

});

/*
 * get a trace by id
 *
 */
app.get('/traces/:traceId', function (req,res) {

});

/*
 * get a list of trace points for a trace 
 */
app.get('/traces/:traceId/tracepoints', function (req,res) {
    //TODO: we might want to support pagination for this one.

});

/*
 * get a tracepoint by id
 */
app.get('/tracespoints/:tracePointId', function (req,res) {

});


app.listen(app.get('port'));
console.log('express listening on port:' + app.get('port'));
