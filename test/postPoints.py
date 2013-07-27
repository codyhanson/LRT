#! /usr/bin/env python

import requests
import random
import sys
import datetime

api = 'http://127.0.0.1:3000'

traceId = sys.argv[1]
numpts = int(sys.argv[2])


#for i in range(0,numpts):
#    postBody.append({
#        'lineNumber': random.randint(0,500), 
#        'timestamp': 'a timestamp',
#        'seq': random.randint(0,500),
#        'methodSig': 'aMethod(int a, int b)' 
#        }
#    )
postBody = {
    'lineNumber': random.randint(0,500), 
    'timestamp': 'a timestamp',
    'seq': random.randint(0,500),
    'methodSig': 'aMethod(int a, int b)' 
    }


r = requests.post(api + '/traces/{0}/tracepoints'.format(traceId), postBody)
print "Response:" , r.status_code
print "Response Body:" , r.text

