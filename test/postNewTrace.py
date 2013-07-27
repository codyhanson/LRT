#! /usr/bin/env python

import requests
import random
import sys



if (sys.argv[1] == 'heroku'):
    api = 'http://lrtserver.herokuapp.com'
else:
    api = 'http://127.0.0.1:3000'

print 'Using api url: ' + api


postBody = {
        'osType': 'Android', 
        'osVersion':'4.1',
        'userId': '123',
        'appName': 'lrtTester', 
        'appVersion':'1.0',
        'traceServiceVersion':'alpha'
        }
            
r = requests.post(api + '/users/{0}/traces'.format('123'), postBody)
print "Response:" , r.status_code
print "Response Body:" , r.text
