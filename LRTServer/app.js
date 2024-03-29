
var express = require('express');
var mongoose = require('mongoose');
var async = require('async');

var mongoUri = process.env.MONGOLAB_URI || 'mongodb://localhost/lrt';

//object model for trace events
var tracePointSchema = new mongoose.Schema( 
{
    lineNumber: Number,
    fileName: String,
    timestamp: String,
    seq: Number,
    methodSig: String,
    traceId: String
});
var TracePoint = mongoose.model('TracePoint',tracePointSchema);

//object model for Trace
var traceSchema = new mongoose.Schema(
{
    osType: String, 
    osVersion: String,
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
    app.use(reqlogger);
    /* other middleware goes here */
});

function reqlogger(req, res, next) {
    console.log("Request: " + req.method + ' ' + req.url + ' body:' + JSON.stringify(req.body));
    next(); // Passing the request to the next handler in the stack.
}


app.get('/', function(req,res) {
    res.send("Hello! You got from the root.");
    console.log('GET /');
});

/**
 * Create a new trace for a particular user
 */
app.post('/users/:userId/traces', function(req,res) {
    var newTrace = new Trace(req.body);
    newTrace.userId = req.params.userId;

    newTrace.save(function(err) {
        if (err) {
            console.log(err);
            sendResponse(res,503,{error: 'fail'});
        } else {
            sendResponse(res,201,newTrace.toObject());
        }
    });
});

/**
 * Create new tracepoints for a particular 
 * instance of a trace
 * Takes an array of tracepoints as the POST body
 * so we can effeciently post more than one point at a time.
 */
app.post('/traces/:traceId/tracepoints', function(req,res) {
    var points = req.body;
    async.each(points,
        function(point, taskCallback) {
            var newPoint = new TracePoint(point);
            //set the traceId field from the URL.
            //this could be set in the tracepoint body itself too.

            newPoint.traceId = req.params.traceId;
            newPoint.save(function(err) {
                if (err) {
                    taskCallback(err);
                } else {
                    taskCallback();
                }
            });
        },
        function(err) {
            if (err) {
                console.log(err);
                sendResponse(res,503,err);
            } else {
                sendResponse(res,200,{msg:"Tracepoints created. TODO put hrefs in here"});
            }
        }
    );
});


/*
 * get a list of traces for a user
 *
 */
app.get('/users/:userId/traces', function (req,res) {
//merge the userid from the REST path into the query hash.
    var qs = req.query;
    qs["userId"] = req.params.userId;

    //the base query
    //all elements left in qs will tell mongo to match the document field exactly.
    //or for the date entry, it will match a range
    var query = Trace.find(qs);

    //execute the built up query
    //use the lean() option to return json objects, not heavyweight mongoose objects
    query.lean().exec(function(err,traces) {
        if (err){
            console.log("error with get list of traces");
            sendResponse(res,503,{error:"query failed"});
        } else {
            sendResponse(res,200,traces);
        }
    });
});

/*
 * get a trace by id
 *
 */
app.get('/traces/:traceId', function (req,res) {
    Trace.findOne({_id: req.params.traceId}, function (err, trace) {
        if (err) {
            console.log(err);
            sendResponse(res,503,{error: 'server error'});
        } else if (trace == null) {
            sendResponse(res,404,{error:'not found'});
        } else {
            sendResponse(res,200,trace.toObject());
        }
    });
});

/*
 * get a list of trace points for a trace 
 */
app.get('/traces/:traceId/tracepoints', function (req,res) {
    //TODO: we might want to support pagination for this one.
    //merge the userid from the REST path into the query hash.
    var qs = req.query;
    qs["traceId"] = req.params.traceId;

    //the base query
    //all elements left in qs will tell mongo to match the document field exactly.
    var query = TracePoint.find(qs);

    //execute the built up query
    //use the lean() option to return json objects, not heavyweight mongoose objects
    query.lean().exec(function(err,tracePoints) {
        if (err){
            console.log("error with get list of tracepoints");
            sendResponse(res,503,{error:"query failed"});
        } else {
            sendResponse(res,200,tracePoints);
        }
    });
});

/*
 * get a tracepoint by id
 */
app.get('/tracespoints/:tracePointId', function (req,res) {
    TracePoint.findOne({_id: req.params.tracePointId}, function (err, tp) {
        if (err) {
            console.log(err);
            sendResponse(res,503,{error: 'server error'});
        } else if (tp == null) {
            sendResponse(res,404,{error:'not found'});
        } else {
            sendResponse(res,200,tp.toObject());
        }
    });
});

//delete a trace and all of the tracepoints that it owns.
//app.delete('/traces/:traceId', function (req,res) {

//});


function sendResponse(res,statusCode,responseBodyObject) {
    res.writeHead(statusCode,  {'Content-Type': 'application/json'});
    var bodyString = JSON.stringify(responseBodyObject);
    res.write(bodyString);
    res.end();
    console.log("Response: " + statusCode + ' body:' + bodyString);
}


app.listen(app.get('port'));
console.log('express listening on port:' + app.get('port'));
