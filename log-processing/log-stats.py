__author__ = 'cris'

import sys
import json

log_file = sys.argv[1]

sum=0.0
sum_weighed=0.0

items=0

'''
We read from the file 

'''
with open(log_file,'r') as f:
    for line in f:
        data = json.loads(line)
        if data["clickPos"] >= 0:
            position = data["clickPos"] + 1 # this is because position starts from 0

            items = items + 1
            sum = sum + position

            suggestions = data["response"]["suggestions"]
            num_suggestions = len(suggestions)
            sum_weighed = sum_weighed + (position+num_suggestions)/float(num_suggestions)

print sum
print items

print "Average position of clicks", sum/items

