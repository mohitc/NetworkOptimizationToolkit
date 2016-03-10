import xml.etree.ElementTree as ET
import random
import sys

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

demandValues = []
#demands are in root[1]
for demand in root[1]:
	demandValues.append(demand[2].text)

random.shuffle(demandValues)

for i in range(len(demandValues)):
	root[1][i][2].text = demandValues[i]

tree.write(outfile)
