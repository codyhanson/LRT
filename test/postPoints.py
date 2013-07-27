#! /usr/bin/env python

import requests
import random
import sys
import datetime
from pprint import pprint
import json


traceId = sys.argv[1]
numpts = int(sys.argv[2])

if (sys.argv[3] == 'heroku'):
    api = 'http://lrtserver.herokuapp.com'
else:
    api = 'http://127.0.0.1:3000'

print 'Using api url: ' + api

points = []

for i in range(0,numpts):
    points.append(
        {
            'lineNumber': random.randint(0,500), 
            'fileName': 'aSourceFile.java', 
            'timestamp': 'a timestamp',
            'seq': random.randint(0,500),
            'methodSig': 'aMethod(int a, int b)' 
        }
    )
    print "added another point"



postBody = {'items': points}

print "post body:" 
pprint(postBody)

#headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
headers = {'Content-type': 'application/json'}
r = requests.post(api + '/traces/{0}/tracepoints'.format(traceId), data=json.dumps(postBody), headers=headers)

print "Response:" , r.status_code
print "Response Body:" , r.text

