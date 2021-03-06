import xml.etree.ElementTree as ET
import random
import sys
import math

if len(sys.argv) < 3 or len(sys.argv) > 4:
	print 'Usage: python randomizer.py input.xml output.xml [seed]'
	exit(-1)

infile = sys.argv[1]
outfile = sys.argv[2]
if len(sys.argv) == 4:
	random.seed(sys.argv[3])

ET.register_namespace('', "http://sndlib.zib.de/network")
tree = ET.parse(infile)
root = tree.getroot()

#demands are in root[1]
for demand in root[1]:
	value = float(demand[2].text)
	randomizedValue = value + random.normalvariate(0, math.sqrt(3*value))
#	print "Value is "+str(value)+", randomized to "+str(randomizedValue)+", difference is "+str(value - randomizedValue)
	if (randomizedValue < 0):
		randomizedValue = 1.0;
	demand[2].text = str(randomizedValue)

tree.write(outfile)
