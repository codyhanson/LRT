#! /usr/bin/env python

import requests
import random
import sys
import json
from pprint import pprint



if (sys.argv[1] == 'heroku'):
    api = 'http://lrtserver.herokuapp.com'
else:
    api = 'http://127.0.0.1:3000'

print 'Using api url: ' + api


postBody = { 'trace': {
                'osType': 'Android', 
                'osVersion':'4.1',
                'userId': '123',
                'appName': 'lrtTester', 
                'appVersion':'1.0',
                'traceServiceVersion':'alpha'
                }
           }

print "post body:"
pprint(postBody)
                
headers = {'Content-type': 'application/json'}
r = requests.post(api + '/users/{0}/traces'.format('123'), data=json.dumps(postBody), headers=headers)
print "Response:" , r.status_code
print "Response Body:" , r.text
