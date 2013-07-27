#! /usr/bin/env python

import requests
import random


api = 'http://127.0.0.1:3000'


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
